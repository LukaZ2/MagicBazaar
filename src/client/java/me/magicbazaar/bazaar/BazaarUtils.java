package me.magicbazaar.bazaar;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import me.magicbazaar.MagicBazaarClient;
import me.magicbazaar.http.HttpHandler;
import me.magicbazaar.item.ItemNameMapping;
import me.magicbazaar.item.ItemUtils;
import net.minecraft.item.ItemStack;
import org.apache.http.client.HttpResponseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

public class BazaarUtils {

    public static final Pattern ITEM_HEADER_REGEX = Pattern.compile("(BUY|SELL) (.+)");
    public static final Pattern OFFER_AMOUNT_REGEX = Pattern.compile("(Offer|Order) amount: (.+)x.*?");
    public static final Pattern PRICE_REGEX = Pattern.compile("Price per unit: (.+) coins.*?");
    public static final Pattern FILLED_REGEX = Pattern.compile("Filled: .+/.+ 100%!.*?");

    public static Lock bazaarCacheLock = new ReentrantLock();

    @Getter
    private static JsonObject bazaarCache;

    private static void refreshCache() {
        JsonObject response;
        try {
            response = HttpHandler.get(MagicBazaarClient.SKYBLOCK_BAZAAR_URL);
        } catch (HttpResponseException e) {
            MagicBazaarClient.warn("Bazaar refresh failed with code: " + e.getStatusCode() + ", Message: \"" + e.getMessage() + "\".");
            return;
        } catch (IOException e) {
            MagicBazaarClient.warn("Bazaar refresh failed unexpectedly, check logs.");
            MagicBazaarClient.LOGGER.error(e.getMessage(), e);
            return;
        }

        try {
            bazaarCacheLock.lock();
            bazaarCache = response.get("products").getAsJsonObject();
            synchronized (MagicBazaarClient.getLocalBazaarState().getLocalEntries()) {
                scanEntries(MagicBazaarClient.getLocalBazaarState().getLocalEntries(), MagicBazaarClient.getConfigSync().OFFER_UPDATE_NOTIFY);
            }
        } finally {
            bazaarCacheLock.unlock();
        }
    }

    private static void refreshCacheLoop() {
        while (true) {
            refreshCache();
            try {
                Thread.sleep(MagicBazaarClient.getConfigSync().BAZAAR_API_REFRESH_INTERVAL);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void refreshCacheLoopAsync() {
        new Thread(BazaarUtils::refreshCacheLoop).start();
    }

    public static LocalBazaarEntry parseItem(ItemStack itemStack) {
        if (itemStack.getName().getString().isBlank()) return null;
        Matcher headerMatcher = ITEM_HEADER_REGEX.matcher(itemStack.getName().getString());
        if (!headerMatcher.matches()) return null;

        LocalBazaarEntry.Type type;
        String itemId;
        String itemName = headerMatcher.group(2);
        try {
            type = LocalBazaarEntry.Type.valueOf(headerMatcher.group(1));
            itemId = ItemUtils.getMappedId(itemName);
        } catch (IllegalArgumentException e) {
            return null;
        } catch (ItemNameMapping.NotFoundException e) {
            MagicBazaarClient.warn(e.getMessage());
            return null;
        }

        int amount = 0;
        double price = 0;
        for (String line : ItemUtils.getLoreRaw(itemStack)) {
            if (FILLED_REGEX.matcher(line).matches()) return null;

            try {
                Matcher amountMatcher = OFFER_AMOUNT_REGEX.matcher(line);
                if (amountMatcher.matches()) amount = Integer.parseInt(amountMatcher.group(2).replaceAll(",", ""));

                Matcher priceMatcher = PRICE_REGEX.matcher(line);
                if (priceMatcher.matches()) price = Double.parseDouble(priceMatcher.group(1).replaceAll(",", ""));

            } catch (IllegalArgumentException e) {
                MagicBazaarClient.warn("String -> Number parse failed for \"" + itemId + "\": \"" + e.getMessage() + "\".");
                return null;
            }
        }

        return new LocalBazaarEntry(itemId, itemName, amount, type, price);
    }

    public static void scanEntries(List<LocalBazaarEntry> entries) {
        scanEntries(entries, false);
    }
    public static void scanEntries(List<LocalBazaarEntry> entries, boolean notify) {

        try {
            bazaarCacheLock.lock();
            if (bazaarCache == null) return;

            List<LocalBazaarEntry> skip = new ArrayList<>();

            for (LocalBazaarEntry entry : entries) {
                if(skip.contains(entry)) continue;

                JsonElement element = getBazaarCache().get(entry.getItemId());
                if (element == null) {
                    MagicBazaarClient.warn("No data found for product: \"" + entry.getItemId() + "\".");
                    return;
                }
                JsonObject bazaarItemInfo = getBazaarCache().get(entry.getItemId()).getAsJsonObject();
                JsonArray orderSummary = null;
                switch (entry.getType()) {
                    case BUY -> orderSummary = bazaarItemInfo.get("sell_summary").getAsJsonArray(); //yes this is correct
                    case SELL -> orderSummary = bazaarItemInfo.get("buy_summary").getAsJsonArray();
                }

                List<LocalBazaarEntry> similarEntries = entries.stream().filter(e -> e.isSimilar(entry)).toList();

                int outbidAmount = 0;
                int matchedAmount = 0;
                double topPrice = entry.getPrice();

                for (JsonElement orderElement : orderSummary.asList()) {
                    JsonObject orderObject = orderElement.getAsJsonObject();
                    double price = orderObject.get("pricePerUnit").getAsDouble();
                    if (entry.getType() == LocalBazaarEntry.Type.BUY && price < entry.getPrice()) continue;
                    if (entry.getType() == LocalBazaarEntry.Type.SELL && price > entry.getPrice()) continue;

                    int amount = orderObject.get("amount").getAsInt();
                    int orders = orderObject.get("orders").getAsInt();

                    if (entry.getPrice() == price) {
                        if(orders > similarEntries.size()) matchedAmount += amount;
                        continue;
                    }

                    outbidAmount += amount;
                    topPrice = entry.getType() == LocalBazaarEntry.Type.BUY ? Math.max(topPrice, price) : Math.min(topPrice, price);
                }

                if(notify && entry.getState() == LocalBazaarEntry.State.TOP) {
                    if(outbidAmount > 0 || matchedAmount > 0) MagicBazaarClient.sendMessage("Your order for \"" + entry.getItemName() + "\" is no longer on top.");
                }

                for(LocalBazaarEntry similarEntry : similarEntries) {
                    similarEntry.setTopPrice(topPrice);
                    similarEntry.setOutbidAmount(outbidAmount);
                    similarEntry.setMatchedAmount(matchedAmount);
                }

                skip.addAll(similarEntries);

            }
        } finally {
            bazaarCacheLock.unlock();
        }
    }
}

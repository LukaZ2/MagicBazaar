package me.magicbazaar.bazaar;

import me.magicbazaar.MagicBazaarClient;
import me.magicbazaar.item.ItemNameMapping;
import me.magicbazaar.item.ItemUtils;
import net.minecraft.item.ItemStack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BazaarUtils {

    public static final Pattern ITEM_HEADER_REGEX = Pattern.compile("(BUY|SELL) (.+)");
    public static final Pattern OFFER_AMOUNT_REGEX = Pattern.compile("(Offer|Order) amount: (\\d+)x.*?");
    public static final Pattern PRICE_REGEX = Pattern.compile("Price per unit: (.+) coins.*?");
    public static final Pattern FILLED_REGEX = Pattern.compile("Filled: \\d+/\\d+ 100%!.*?");

    public static LocalBazaarEntry parseItem(ItemStack itemStack) {
        if(itemStack.getName().getString().isBlank()) {
            return null;
        }
        Matcher headerMatcher = ITEM_HEADER_REGEX.matcher(itemStack.getName().getString());
        if(!headerMatcher.matches()) {
            MagicBazaarClient.sendMessage(itemStack.getName().getString());
            return null;
        }

        LocalBazaarEntry.Type type;
        String itemId;
        try {
            type = LocalBazaarEntry.Type.valueOf(headerMatcher.group(1));
            itemId = ItemUtils.getMappedId(headerMatcher.group(2));
        } catch(IllegalArgumentException e) {
            return null;
        } catch (ItemNameMapping.NotFoundException e) {
            MagicBazaarClient.warn(e.getMessage());
            return null;
        }

        int amount = 0;
        double price = 0;
        for(String line : ItemUtils.getLoreRaw(itemStack)) {
            if(FILLED_REGEX.matcher(line).matches()) {
                return null;
            }

            try {
                Matcher amountMatcher = OFFER_AMOUNT_REGEX.matcher(line);
                if (!amountMatcher.matches()) continue;
                amount = Integer.parseInt(amountMatcher.group(2));

                Matcher priceMatcher = PRICE_REGEX.matcher(line);
                if (!priceMatcher.matches()) continue;
                price = Double.parseDouble(priceMatcher.group(1).replaceAll(",", ""));
            } catch (IllegalArgumentException e) {
                MagicBazaarClient.warn("String -> Number parse failed for \"" + itemId + "\": \"" + e.getMessage() + "\".");
                return null;
            }
        }
        return new LocalBazaarEntry(itemId, amount, type, price);
    }
}

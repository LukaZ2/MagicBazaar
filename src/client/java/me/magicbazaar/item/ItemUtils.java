package me.magicbazaar.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.magicbazaar.MagicBazaarClient;
import me.magicbazaar.http.HttpHandler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.io.IOException;
import java.util.*;

public class ItemUtils {

    private static final List<ItemNameMapping> mappings = new ArrayList<>();

    public static String getMappedId(String displayName) throws ItemNameMapping.NotFoundException {
        synchronized (mappings) {
            for (ItemNameMapping mapping : mappings) {
                if (!mapping.getDisplayName().equals(displayName)) continue;
                return mapping.getItemId();
            }
        }
        throw new ItemNameMapping.NotFoundException("No mapping for: \"" + displayName + "\".");
    }

    private static void loadEnchantMappings() {
        mappings.addAll(getUltimateEnchantmentMappings("FLASH", 1, 5));
    }

    private static boolean loadMappings() throws IOException {
        JsonObject response = HttpHandler.get(MagicBazaarClient.SKYBLOCK_ITEMS_URL);
        if (!response.get("success").getAsBoolean()) return false;

        JsonArray items = response.get("items").getAsJsonArray();

        synchronized (mappings) {
            for (JsonElement itemElement : items) {
                JsonObject item = itemElement.getAsJsonObject();
                mappings.add(new ItemNameMapping(item.get("id").getAsString(), item.get("name").getAsString()));
            }
            loadEnchantMappings();
        }

        return true;
    }

    public static void loadMappingsAsync() {
        loadMappingsAsync(5);
    }

    public static void loadMappingsAsync(int retries) {
        new Thread(() -> {
            for (int i = 0; i < retries; i++) {
                try {
                    Thread.sleep(1000);
                    if (loadMappings()) return;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public static List<Text> getLore(ItemStack itemStack) {
        return itemStack.getOrDefault(DataComponentTypes.LORE, LoreComponent.DEFAULT).styledLines();
    }
    public static List<String> getLoreRaw(ItemStack itemStack) {
        return getLore(itemStack).stream().map(Text::getString).toList();
    }

    public static final Map<Integer, String> numerals = new HashMap<>();
    static {
        //only till 399
        numerals.put(100, "C");
        numerals.put(90, "XC");
        numerals.put(50, "L");
        numerals.put(40, "XL");
        numerals.put(10, "X");
        numerals.put(9, "IX");
        numerals.put(5, "V");
        numerals.put(4, "IV");
        numerals.put(1, "I");
    }
    private static Map.Entry<Integer, String> getNearestNumeral(int n) {
        if(n <= 0) return null;
        return numerals.entrySet().stream()
                .filter(e -> e.getKey() <= n)
                .max(Comparator.comparingInt(Map.Entry::getKey))
                .orElse(null);
    }

    public static String toRomanNumeral(int n) {
        var entry = getNearestNumeral(n);
        if(entry == null) return "";
        return entry.getValue() + toRomanNumeral(n-entry.getKey());
    }
    public static String formatCapitals(String s) {
        var stringBuilder = new StringBuilder(s.toLowerCase());
        boolean capitalize = true;
        for(int i = 0; i < s.length(); i++) {
            char c = stringBuilder.charAt(i);
            if(capitalize) {
                capitalize = false;
                stringBuilder.setCharAt(i, Character.toUpperCase(c));
                continue;
            }
            if(c == ' ') capitalize = true;
        }
        return stringBuilder.toString();
    }

    public static List<ItemNameMapping> getUltimateEnchantmentMappings(String id, int min, int max) {
        return getEnchantMappings("ULTIMATE_" + id, id.replace("_", " "),min ,max);
    }

    public static List<ItemNameMapping> getEnchantMappings(String id, int min, int max) {
        return getEnchantMappings(id, id.replace("_", " "), min, max);
    }

    public static List<ItemNameMapping> getEnchantMappings(String id, String name, int min, int max) {
        var mappings = new ArrayList<ItemNameMapping>();
        for(int i = min; i <= max; i++) {
            var mapping = new ItemNameMapping("ENCHANTMENT_" + id + "_" + i, formatCapitals(name) + " " + toRomanNumeral(i));
            mappings.add(mapping);
        }
        return mappings;
    }

}

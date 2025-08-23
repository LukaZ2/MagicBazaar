package me.magicbazaar.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.magicbazaar.MagicBazaarClient;
import me.magicbazaar.api.HttpHandler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    private static boolean loadMappings() throws IOException {
        JsonObject response = HttpHandler.get(MagicBazaarClient.SKYBLOCK_ITEMS_URL);
        if (!response.get("success").getAsBoolean()) return false;

        JsonArray items = response.get("items").getAsJsonArray();

        synchronized (mappings) {
            for (JsonElement itemElement : items) {
                JsonObject item = itemElement.getAsJsonObject();
                mappings.add(new ItemNameMapping(item.get("id").getAsString(), item.get("name").getAsString()));
            }
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

}

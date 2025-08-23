package me.magicbazaar.bazaar;

import lombok.Getter;
import me.magicbazaar.MagicBazaarClient;
import me.magicbazaar.item.ItemNameMapping;
import me.magicbazaar.item.ItemUtils;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.slot.Slot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class LocalBazaarState {

    @Getter
    private final List<LocalBazaarEntry> localEntries;

    public LocalBazaarState() {
        this.localEntries = new ArrayList<>();
    }

    public void parse(Stream<ItemStack> itemStackStream) {
        synchronized (localEntries) {
            localEntries.clear();
//            StreamSupport.stream(inventory.spliterator(), false)
//            handler.getStacks().stream()
            itemStackStream
                    .filter(i -> i != ItemStack.EMPTY)
                    .map(BazaarUtils::parseItem)
                    .filter(Objects::nonNull)
                    .forEach(localEntries::add);
        }
    }
}

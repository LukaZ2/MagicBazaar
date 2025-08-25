package me.magicbazaar.bazaar;

import lombok.Getter;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class LocalBazaarState {

    @Getter
    private final List<LocalBazaarEntry> localEntries;

    public LocalBazaarState() {
        this.localEntries = new ArrayList<>();
    }

    public void parse(Stream<ItemStack> itemStackStream) {
        synchronized (localEntries) {
            localEntries.clear();
            itemStackStream
                    .filter(i -> i != ItemStack.EMPTY)
                    .map(BazaarUtils::parseItem)
                    .filter(Objects::nonNull)
                    .forEach(localEntries::add);
            localEntries.sort((e1, e2) -> {
                if(e1.getType() == e2.getType()) return 0;
                if(e1.getType() == LocalBazaarEntry.Type.BUY) return 1;
                return -1;
            });
            BazaarUtils.scanEntries(localEntries);
        }
    }
}

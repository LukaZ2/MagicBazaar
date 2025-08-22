package me.magicbazaar.menu;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;

public interface IContainerMatcher {
    boolean match(GenericContainerScreen screen);
}

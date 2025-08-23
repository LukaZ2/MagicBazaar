package me.magicbazaar.menu;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;

public interface IContainerMatcher {
    boolean match(Text title, ScreenHandler screenHandler);
}

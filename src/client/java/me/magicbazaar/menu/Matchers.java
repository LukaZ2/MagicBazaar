package me.magicbazaar.menu;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Matchers {

    public static IContainerMatcher titleEquals(String title) {
        return screen -> screen.getTitle().getString().equals(title);
    }
    public static IContainerMatcher titleContains(String text) {
        return screen -> screen.getTitle().getString().contains(text);
    }
    public static IContainerMatcher rowsEquals(int rows) {
        return screen -> screen.getScreenHandler().getRows() == rows;
    }
    public static IContainerMatcher always() {
        return screen -> true;
    }
    public static IContainerMatcher all(IContainerMatcher... matchers) {
        return screen -> Arrays.stream(matchers).allMatch(matcher -> matcher.match(screen));
    }

}

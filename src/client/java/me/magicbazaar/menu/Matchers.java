package me.magicbazaar.menu;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Matchers {

    public static IContainerMatcher titleEquals(String title) {
        return screen -> screen.getTitle().getString().equals(title);
    }
    public static IContainerMatcher titleContains(String text) {
        return screen -> screen.getTitle().getString().contains(text);
    }
    public static IContainerMatcher titleStarts(String text) {
        return screen -> screen.getTitle().getString().startsWith(text);
    }
    public static IContainerMatcher titleEnds(String text) {
        return screen -> screen.getTitle().getString().endsWith(text);
    }
    public static IContainerMatcher titleRegexMatches(Pattern pattern) {
        return screen -> pattern.matcher(screen.getTitle().getString()).matches();
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
    public static IContainerMatcher any(IContainerMatcher... matchers) {
        return screen -> Arrays.stream(matchers).anyMatch(matcher -> matcher.match(screen));
    }

}

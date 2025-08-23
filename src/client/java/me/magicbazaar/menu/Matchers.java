package me.magicbazaar.menu;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.screen.GenericContainerScreenHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Matchers {

    public static IContainerMatcher titleEquals(String text) {
        return (title, handler) -> title.getString().equals(text);
    }
    public static IContainerMatcher titleContains(String text) {
        return (title, handler) -> title.getString().contains(text);
    }
    public static IContainerMatcher titleStarts(String text) {
        return (title, handler) -> title.getString().startsWith(text);
    }
    public static IContainerMatcher titleEnds(String text) {
        return (title, handler) -> title.getString().endsWith(text);
    }
    public static IContainerMatcher titleRegexMatches(Pattern pattern) {
        return (title, handler) -> pattern.matcher(title.getString()).matches();
    }

    public static IContainerMatcher isContainerScreen() {
        return (title, handler) -> handler instanceof GenericContainerScreenHandler;
    }
    public static IContainerMatcher rowsEquals(int rows) {
        return (title, handler) -> {
            if(!(handler instanceof GenericContainerScreenHandler containerScreenHandler)) return false;
            return containerScreenHandler.getRows() == rows;
        };
    }
    public static IContainerMatcher always() {
        return (title, handler) -> true;
    }
    public static IContainerMatcher all(IContainerMatcher... matchers) {
        return (title, handler) -> Arrays.stream(matchers).allMatch(matcher -> matcher.match(title, handler));
    }
    public static IContainerMatcher any(IContainerMatcher... matchers) {
        return (title, handler) -> Arrays.stream(matchers).anyMatch(matcher -> matcher.match(title, handler));
    }

}

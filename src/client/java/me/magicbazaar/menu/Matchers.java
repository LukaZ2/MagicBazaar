package me.magicbazaar.menu;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;

import java.util.ArrayList;
import java.util.List;

public class Matchers {

    public static TitleStringEquals titleEquals(String title) {
        return new TitleStringEquals(title);
    }
    public static TitleStringContains titleContains(String text) {
        return new TitleStringContains(text);
    }
    public static RowsEquals rowsEquals(int rows) {
        return new RowsEquals(rows);
    }

    public static All all(IContainerMatcher... matchers) {
        return new All(new ArrayList<>(List.of(matchers)));
    }
    public static Always always() {
        return new Always();
    }

    public static class TitleStringEquals implements IContainerMatcher {

        private final String title;

        public TitleStringEquals(String title) {
            this.title = title;
        }

        @Override
        public boolean match(GenericContainerScreen screen) {
            return screen.getTitle().getString().equals(title);
        }
    }

    public static class TitleStringContains implements IContainerMatcher {
        private final String text;

        public TitleStringContains(String text) {
            this.text = text;
        }

        @Override
        public boolean match(GenericContainerScreen screen) {
            return screen.getTitle().getString().contains(text);
        }
    }

    public static class RowsEquals implements IContainerMatcher {
        private final int rows;

        public RowsEquals(int rows) {
            this.rows = rows;
        }

        @Override
        public boolean match(GenericContainerScreen screen) {
            return screen.getScreenHandler().getRows() == rows;
        }
    }

    public static class All implements IContainerMatcher {
        private final List<IContainerMatcher> matchers;

        public All(List<IContainerMatcher> matchers) {
            this.matchers = matchers;
        }

        @Override
        public boolean match(GenericContainerScreen screen) {
            return matchers.stream().allMatch(m -> m.match(screen));
        }
    }

    public static class Always implements IContainerMatcher {

        @Override
        public boolean match(GenericContainerScreen screen) {
            return true;
        }
    }
}

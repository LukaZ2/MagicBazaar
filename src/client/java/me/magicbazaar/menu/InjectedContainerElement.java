package me.magicbazaar.menu;

import lombok.Getter;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;

import java.util.ArrayList;
import java.util.List;

public class InjectedContainerElement {
    private final List<IContainerMatcher> matchers;

    @Getter
    private final List<Drawable> drawables;

    public InjectedContainerElement(List<IContainerMatcher> matchers, List<Drawable> drawables) {
        this.matchers = matchers;
        this.drawables = drawables;
    }
    public InjectedContainerElement(List<IContainerMatcher> matchers, Drawable drawable) {
        this(matchers, new ArrayList<>(List.of(drawable)));
    }
    public InjectedContainerElement(IContainerMatcher matcher, Drawable drawable) {
        this(new ArrayList<>(List.of(matcher)), new ArrayList<>(List.of(drawable)));
    }
    public InjectedContainerElement(IContainerMatcher matcher, List<Drawable> drawables) {
        this(new ArrayList<>(List.of(matcher)), drawables);
    }

    public boolean match(GenericContainerScreen screen) {
        return matchers.stream().anyMatch(m -> m.match(screen));
    }
}

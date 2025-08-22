package me.magicbazaar.menu;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;

import java.util.ArrayList;
import java.util.List;

public class ContainerInjector {

    private static List<InjectedContainerElement> elements = new ArrayList<>();

    public static void register(InjectedContainerElement element) {
        elements.add(element);
    }

    public static List<Drawable> handleNewContainer(GenericContainerScreen screen) {
        List<Drawable> drawables = new ArrayList<>();
        for(InjectedContainerElement element : elements) {
            if(!element.match(screen)) continue;
            drawables.addAll(element.getDrawables());
        }
        return drawables;
    }

}

package me.magicbazaar.menu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContainerInjector {

    @Getter
    @AllArgsConstructor
    public static class InjectedContainerScreenEntry {
        private IContainerMatcher matcher;
        private Class<? extends InjectedContainerScreen<?>> screenClazz;
        private Class<? extends ScreenHandler> screenHandlerClazz;
    }

    private final static List<InjectedContainerScreenEntry> entries = new ArrayList<>();

    public static void register(IContainerMatcher matcher, Class<? extends InjectedContainerScreen<?>> screenClazz, Class<? extends ScreenHandler> screenHandlerClazz) {
        entries.add(new InjectedContainerScreenEntry(matcher, screenClazz, screenHandlerClazz));
    }

    public static InjectedContainerScreenEntry match(Text title, ScreenHandler screenHandler) {
        return entries.stream()
                .filter(entry -> entry.getMatcher().match(title, screenHandler))
                .findFirst()
                .orElse(null);
    }

}

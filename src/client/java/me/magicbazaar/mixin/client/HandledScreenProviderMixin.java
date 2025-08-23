package me.magicbazaar.mixin.client;

import me.magicbazaar.MagicBazaarClient;
import me.magicbazaar.bazaar.BazaarOrdersScreen;
import me.magicbazaar.menu.ContainerInjector;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Mixin(HandledScreens.Provider.class)
public interface HandledScreenProviderMixin<T extends ScreenHandler> {

    @Inject(method = "open", at = @At("HEAD"), cancellable = true)
    private void open(Text name, ScreenHandlerType<T> type, MinecraftClient client, int id, CallbackInfo cb) {
        if (client.player == null) return;
        T currentHandler = type.create(id, client.player.getInventory());
        ContainerInjector.InjectedContainerScreenEntry entry = ContainerInjector.match(name, currentHandler);
        if (entry == null) return;

        try {
            var screenConstructor = entry.getScreenClazz().getConstructor(entry.getScreenHandlerClazz(), PlayerInventory.class, Text.class);
            Constructor<? extends ScreenHandler> screenHandlerConstructor;
            switch (currentHandler) {
                case GenericContainerScreenHandler genericContainerScreenHandler -> {
                    screenHandlerConstructor = entry.getScreenHandlerClazz().getConstructor(ScreenHandlerType.class, Integer.TYPE, PlayerInventory.class, Inventory.class, Integer.TYPE);

                    client.player.currentScreenHandler = screenHandlerConstructor.newInstance(type, id,
                            client.player.getInventory(),
                            genericContainerScreenHandler.getInventory(),
                            genericContainerScreenHandler.getRows());
                }
                default -> {
                    throw new NotImplementedException("No container injection definition for handler: \"" + currentHandler.getClass() + "\"");
                }
            }
            client.setScreen(screenConstructor.newInstance(entry.getScreenHandlerClazz().cast(client.player.currentScreenHandler), client.player.getInventory(), name));
            cb.cancel();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

}

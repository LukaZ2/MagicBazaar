package me.magicbazaar;

import me.magicbazaar.commands.MagicBazaarCommand;
import me.magicbazaar.menu.ContainerInjector;
import me.magicbazaar.menu.InjectedContainerElement;
import me.magicbazaar.menu.Matchers;
import me.magicbazaar.menu.injections.BazaarOrdersElement;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.text.Text;

public class MagicBazaarClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register(new MagicBazaarCommand());
        registerContainerInjections();
    }

    public void registerContainerInjections() {
        ContainerInjector.register(new InjectedContainerElement(Matchers.always(), new BazaarOrdersElement()));
    }

    public static void sendMessage(String msg) {
        sendMessage(Text.literal(msg));
    }

    public static void sendMessage(Text msg) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        mc.inGameHud.getChatHud().addMessage(msg);
    }
}
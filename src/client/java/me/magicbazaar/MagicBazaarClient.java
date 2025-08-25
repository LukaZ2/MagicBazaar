package me.magicbazaar;

import com.google.gson.Gson;
import lombok.Getter;
import me.magicbazaar.bazaar.BazaarOrdersScreen;
import me.magicbazaar.bazaar.BazaarUtils;
import me.magicbazaar.bazaar.LocalBazaarState;
import me.magicbazaar.commands.MagicBazaarCommand;
import me.magicbazaar.config.MagicBazaarConfig;
import me.magicbazaar.item.ItemUtils;
import me.magicbazaar.menu.ContainerInjector;
import me.magicbazaar.menu.IContainerMatcher;
import me.magicbazaar.menu.InjectedContainerScreen;
import me.magicbazaar.menu.Matchers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MagicBazaarClient implements ClientModInitializer, ClientTickEvents.StartTick {

    public static final String MOD_ID = "magicbazaar";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final String CONFIG_PATH = "MagicBazaar/config.json";
    public static final String SKYBLOCK_ITEMS_URL = "https://api.hypixel.net/v2/resources/skyblock/items";
    public static final String SKYBLOCK_BAZAAR_URL = "https://api.hypixel.net/v2/skyblock/bazaar";

    public static final IContainerMatcher BAZAAR_CONTAINER_MATCHER = Matchers.titleEnds("Bazaar Orders");

    @Getter
    private static MagicBazaarConfig config;

    @Getter
    private static LocalBazaarState localBazaarState;

    @Override
    public void onInitializeClient() {
        loadConfig();
        ClientCommandRegistrationCallback.EVENT.register(new MagicBazaarCommand());
        ClientTickEvents.START_CLIENT_TICK.register(this);
        registerContainerInjections();

        ItemUtils.loadMappingsAsync();
        localBazaarState = new LocalBazaarState();

        BazaarUtils.refreshCacheLoopAsync();
    }

    public void loadConfig() {
        File configFile = new File(CONFIG_PATH);
        if(!configFile.exists()) {
            try {
                configFile.getParentFile().mkdir();
                configFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            config = new Gson().fromJson(new FileReader(configFile), MagicBazaarConfig.class);
            if(config == null) config = new MagicBazaarConfig();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerContainerInjections() {
        ContainerInjector.register(Matchers.all(Matchers.isContainerScreen(), Matchers.titleEnds("Bazaar Orders")), BazaarOrdersScreen.class, BazaarOrdersScreen.Handler.class);
    }

    public static MagicBazaarConfig getConfigSync() {
        return getConfig().sync();
    }

    private static final Queue<Text> msgQueue = new LinkedList<>();

    @Override
    public void onStartTick(MinecraftClient minecraftClient) {
        Text text;
        synchronized (msgQueue) {
            while ((text = msgQueue.poll()) != null) sendMessage(text);
        }
    }

    public static void sendMessage(String msg) {
        sendMessage(Text.literal(msg));
    }

    public static void sendMessage(Text msg) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.inGameHud == null) return;
        if(!mc.isOnThread()) {
            synchronized (msgQueue) {
                msgQueue.add(msg);
            }
        }
        mc.inGameHud.getChatHud().addMessage(msg);
    }

    public static void warn(String msg) {
        warn(Text.literal(msg).withColor(Colors.WHITE));
    }

    public static void warn(Text text) {
        sendMessage(Text.literal("WARNING: ").withColor(Colors.RED).append(text));
    }

}
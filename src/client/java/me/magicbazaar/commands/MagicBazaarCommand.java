package me.magicbazaar.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.magicbazaar.MagicBazaarClient;
import me.magicbazaar.bazaar.LocalBazaarEntry;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

public class MagicBazaarCommand implements ClientCommandRegistrationCallback {
    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> commandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        commandDispatcher.register(ClientCommandManager.literal("magicbazaar").executes(this::run));
    }

    public int run(CommandContext<FabricClientCommandSource> context) {
        StringBuilder sb = new StringBuilder();
        sb.append("Begin\n");
        for (LocalBazaarEntry entry : MagicBazaarClient.getLocalBazaarState().getLocalEntries()) {
            sb.append("----------\n");
            sb.append("ID: ").append(entry.getItemId()).append("\n");
            sb.append("Type: ").append(entry.getType().toString()).append("\n");
            sb.append("Amount: ").append(entry.getAmount()).append("\n");
            sb.append("Price: ").append(entry.getPrice()).append("\n");
        }
        sb.append("End");
        MagicBazaarClient.sendMessage(sb.toString());
        return 1;
    }
}

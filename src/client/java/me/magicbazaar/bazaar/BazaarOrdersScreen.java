package me.magicbazaar.bazaar;

import me.magicbazaar.MagicBazaarClient;
import me.magicbazaar.menu.IContainerMatcher;
import me.magicbazaar.menu.InjectedContainerScreen;
import me.magicbazaar.menu.Matchers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

import java.util.List;
import java.util.stream.StreamSupport;

public class BazaarOrdersScreen extends InjectedContainerScreen.GenericContainer<BazaarOrdersScreen.Handler> {

    public BazaarOrdersScreen(Handler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawText(textRenderer, "Bazaar Orders", 0, 0, Colors.WHITE, true);
        renderOrders(context, mouseX, mouseY, deltaTicks);
    }

    public void renderOrders(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        synchronized (MagicBazaarClient.getLocalBazaarState().getLocalEntries()) {
            List<LocalBazaarEntry> entries = MagicBazaarClient.getLocalBazaarState().getLocalEntries();
            for (int i = 0; i < entries.size(); i++) {
                int y = (i+1)*(textRenderer.fontHeight+1);
                LocalBazaarEntry entry = entries.get(i);

                MutableText text = Text.literal(entry.getType().toString()).withColor(entry.getType() == LocalBazaarEntry.Type.BUY ? Colors.GREEN : Colors.YELLOW);
                text.append(Text.literal(" - ").withColor(Colors.WHITE));

                MutableText name = Text.literal(entry.getItemName());
                int nameColor = Colors.CYAN;
                if(entry.getOutbidAmount() > 0) nameColor = Colors.RED;
                else if(entry.getMatchedAmount() > 0) nameColor = Colors.PURPLE;
                text.append(name.withColor(nameColor));

                text.append(Text.literal(" x" + entry.getAmount()).withColor(Colors.WHITE));

                if(entry.totalAmountBeforeTop() > 0) {
                    MutableText status = Text.literal(" (").withColor(Colors.WHITE);
                    status.append(Text.literal("x" + entry.getTotalAmountBeforeTopString()).withColor(Colors.CYAN));
                    status.append(Text.literal(" " + entry.getTopPriceString()).withColor(Colors.YELLOW));
                    status.append(Text.literal(" -> ").withColor(Colors.WHITE));
                    status.append(Text.literal(entry.getRoundedPercentageFromTop()).withColor(Colors.GREEN));
                    status.append(Text.literal(")").withColor(Colors.WHITE));
                    text.append(status);
                }

                context.drawText(textRenderer, text, 1, y, Colors.WHITE, true);
            }
        }
    }

    public static class Handler extends GenericContainerScreenHandler {

        public Handler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, Inventory inventory, int rows) {
            super(type, syncId, playerInventory, inventory, rows);
        }

        @Override
        public void updateSlotStacks(int revision, List<ItemStack> stacks, ItemStack cursorStack) {
            super.updateSlotStacks(revision, stacks, cursorStack);
            MagicBazaarClient.getLocalBazaarState().parse(stacks.stream().limit(getInventory().size()));
        }

        @Override
        public void sendContentUpdates() {
            super.sendContentUpdates();
        }
    }

}

package me.magicbazaar.bazaar;

import me.magicbazaar.MagicBazaarClient;
import me.magicbazaar.menu.IContainerMatcher;
import me.magicbazaar.menu.InjectedContainerScreen;
import me.magicbazaar.menu.Matchers;
import net.minecraft.client.MinecraftClient;
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
        context.drawText(MinecraftClient.getInstance().textRenderer, "TESTETSTETSET", 0, 0, Colors.WHITE, true);
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

    }

}

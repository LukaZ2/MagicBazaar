package me.magicbazaar.menu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public abstract class InjectedContainerScreen<T extends ScreenHandler> extends HandledScreen<T> {

    public InjectedContainerScreen(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    public static abstract class GenericContainer<T extends GenericContainerScreenHandler> extends InjectedContainerScreen<T> {

        private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/generic_54.png");
        private final int rows;

        public GenericContainer(T handler, PlayerInventory inventory, Text title) {
            super(handler, inventory, title);
            int i = 222;
            int j = 114;
            this.rows = handler.getRows();
            this.backgroundHeight = 114 + this.rows * 18;
            this.playerInventoryTitleY = this.backgroundHeight - 94;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
            super.render(context, mouseX, mouseY, deltaTicks);
            this.drawMouseoverTooltip(context, mouseX, mouseY);
        }

        @Override
        protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
            int i = (this.width - this.backgroundWidth) / 2;
            int j = (this.height - this.backgroundHeight) / 2;
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, this.backgroundWidth, this.rows * 18 + 17, 256, 256);
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j + this.rows * 18 + 17, 0.0F, 126.0F, this.backgroundWidth, 96, 256, 256);
        }
    }

}

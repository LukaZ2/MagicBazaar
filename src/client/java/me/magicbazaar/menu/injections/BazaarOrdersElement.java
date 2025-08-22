package me.magicbazaar.menu.injections;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.util.Colors;

public class BazaarOrdersElement implements Drawable {
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        context.drawText(MinecraftClient.getInstance().textRenderer, "TEEST", 0, 0, Colors.BLUE, true);
    }
}

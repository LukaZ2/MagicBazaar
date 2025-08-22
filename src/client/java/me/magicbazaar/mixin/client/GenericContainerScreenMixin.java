package me.magicbazaar.mixin.client;

import me.magicbazaar.menu.ContainerInjector;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GenericContainerScreen.class)
public abstract class GenericContainerScreenMixin extends HandledScreen<GenericContainerScreenHandler> {

    public GenericContainerScreenMixin(GenericContainerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "<init>(Lnet/minecraft/screen/GenericContainerScreenHandler;Lnet/minecraft/entity/player/PlayerInventor;Lnet/minecraft/text/Text;)V", at = @At("TAIL"))
    public void constructor(CallbackInfo cb) {
        ContainerInjector.handleNewContainer(((GenericContainerScreen) (Object)this)).forEach(this::addDrawable);
    }
}

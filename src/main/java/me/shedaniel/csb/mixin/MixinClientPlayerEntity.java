package me.shedaniel.csb.mixin;

import me.shedaniel.csb.gui.CSBSettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalClientPlayerEntity.class)
public class MixinClientPlayerEntity {
    
    @Shadow protected Minecraft minecraft;
    
    @Inject(method = "sendChat", at = @At("HEAD"), cancellable = true)
    public void sendChatMessage(String message, CallbackInfo ci) {
        String[] split = message.toLowerCase().split(" ");
        if (split.length > 0 && split[0].contentEquals("/csbconfig")) {
            minecraft.openScreen(new CSBSettingsScreen(minecraft.screen instanceof ChatScreen ? null : minecraft.screen));
            ci.cancel();
        }
    }
    
}

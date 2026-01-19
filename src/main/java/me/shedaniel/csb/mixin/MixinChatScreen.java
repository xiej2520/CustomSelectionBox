package me.shedaniel.csb.mixin;

import me.shedaniel.csb.gui.CSBSettingsScreen;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class MixinChatScreen extends Screen {
    
    @Shadow protected TextFieldWidget chatField;
    
    protected MixinChatScreen() {
        super();
    }
    
    @Inject(method = "keyPressed",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;openScreen(Lnet/minecraft/client/gui/screen/Screen;)V", ordinal = 1),
            cancellable = true)
    public void keyPressed(char chr, int key, CallbackInfo ci) {
        String[] split = this.chatField.getText().trim().toLowerCase().split(" ");
        if (split.length > 0 && split[0].contentEquals("/csbconfig") && minecraft.screen instanceof CSBSettingsScreen) {
            ci.cancel();
        }
    }
    
}

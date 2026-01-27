package me.shedaniel.csb.mixin;

import me.shedaniel.csb.CSBConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Entity.class)
public abstract class MixinEntity {
    @Shadow
    public abstract boolean equals(Object object);

    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
    public void isGlowing(CallbackInfoReturnable<Boolean> cir) {
        HitResult target = Minecraft.getInstance().crosshairTarget;
        if (CSBConfig.isEntityEnabled() && target != null && this.equals(target.entity)) {
            cir.setReturnValue(true);
        }
    }
}

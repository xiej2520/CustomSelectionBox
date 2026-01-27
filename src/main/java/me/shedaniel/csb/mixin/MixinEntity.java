package me.shedaniel.csb.mixin;

import me.shedaniel.csb.CSBConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
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
        HitResult target = MinecraftClient.getInstance().crosshairTarget;
        if (CSBConfig.isEntityEnabled() && target instanceof EntityHitResult && this.equals(((EntityHitResult) target).getEntity())) {
            cir.setReturnValue(true);
        }
    }
}

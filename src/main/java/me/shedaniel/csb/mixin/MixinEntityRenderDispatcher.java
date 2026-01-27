package me.shedaniel.csb.mixin;

import me.shedaniel.csb.CSBConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.world.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class MixinEntityRenderDispatcher {
    @Shadow
    protected abstract void renderHitbox(Entity entity, double dx, double dy, double dz, float yaw, float tickDelta);

    @Inject(method = "render(Lnet/minecraft/entity/Entity;DDDFFZ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/EntityRenderer;postRender(Lnet/minecraft/entity/Entity;DDDFF)V"
            ))
    public void renderTargetHitbox(Entity entity, double dx, double dy, double dz, float yaw, float tickDelta, boolean skipHitbox, CallbackInfo ci) {
        HitResult target = Minecraft.getInstance().crosshairTarget;
        if (CSBConfig.isEntityEnabled() && target != null && target.entity == entity) {
            try {
                renderHitbox(entity, dx, dy, dz, yaw, tickDelta);
            } catch (Throwable t) {
                throw new CrashException(CrashReport.of(t, "Rendering entity hitbox in world (CSB)"));
            }
        }
    }

}

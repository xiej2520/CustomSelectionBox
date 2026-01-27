package me.shedaniel.csb.mixin;

import me.shedaniel.csb.CSBConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class MixinEntityRenderDispatcher {
    @Shadow
    private void renderHitbox(MatrixStack matrixStack, VertexConsumer vertexConsumer, Entity entity, float f) { }

    @Shadow
    private World world;

    @Inject(method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V"
            ))
    public <E extends Entity> void renderTargetHitbox(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        HitResult target = MinecraftClient.getInstance().crosshairTarget;
        if (CSBConfig.isEntityEnabled() && target instanceof EntityHitResult && entity.equals(((EntityHitResult) target).getEntity())) {
            try {
                renderHitbox(matrices, vertexConsumers.getBuffer(RenderLayer.getLines()), entity, tickDelta);
            } catch (Throwable t) {
                throw new CrashException(CrashReport.create(t, "Rendering entity hitbox in world (CSB)"));
            }
        }
    }

}

package me.shedaniel.csb.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.shedaniel.csb.CSBConfig;
import me.shedaniel.csb.api.CSBRenderer;
import me.shedaniel.csb.gui.CSBInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.world.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.HitResult;
import net.minecraft.world.InteractionResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static me.shedaniel.csb.CSB.HSBtoRGB;
import static me.shedaniel.csb.CSB.RENDERERS;

@SuppressWarnings("unused")
@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer implements CSBInfo {

    @Unique private boolean render = false;
    @Unique private float r = 0f;
    @Unique private float g = 0f;
    @Unique private float b = 0f;
    @Unique private float a = 0f;
    @Unique private float blinkingAlpha = 0f;
    @Shadow private ClientWorld world;

    @Shadow
    public static void renderOutlineShape(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float r, float g, float b, float a) { }

    @Shadow
    @Final
    private Minecraft minecraft;

    @WrapOperation(method = "renderBlockOutline",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/world/WorldRenderer;renderOutlineShape(Lnet/minecraft/util/math/Box;FFFF)V"
            ))
    private void onRenderOutlineShape(Box shape, float r, float g, float b, float a, Operation<Void> original) {
        if (!CSBConfig.isEnabled()) {
            original.call(shape, r, g, b, a);
        } else {
            render = true;
        }
    }

    @Inject(method = "renderWorldBorder", at = @At(value = "RETURN"))
    private void renderCustomOutline(Entity camera, float tickDelta, CallbackInfo ci) {
        if (render) {
            r = CSBConfig.getRed();
            g = CSBConfig.getGreen();
            b = CSBConfig.getBlue();
            a = CSBConfig.getAlpha();
            if (CSBConfig.rainbow) {
                final double millis = System.currentTimeMillis() % 10000L / 10000.0f;
                final int color = HSBtoRGB((float) millis, 0.8f, 0.8f);
                r = (color >> 16 & 255) / 255.0f;
                g = (color >> 8 & 255) / 255.0f;
                b = (color & 255) / 255.0f;
            }
            blinkingAlpha = CSBConfig.getBlinkSpeed() > 0 ?
                    CSBConfig.getBlinkAlpha() * (float) Math.abs(Math.sin(System.currentTimeMillis() / 100.0D * CSBConfig.getBlinkSpeed()))
                    : CSBConfig.getBlinkAlpha();
            HitResult hitResult = minecraft.crosshairTarget;
            BlockPos blockPos = hitResult.getPos();
            for (CSBRenderer renderer : RENDERERS) {
                InteractionResult result = Objects.requireNonNull(renderer.render(world, camera, hitResult, tickDelta));
                if (result != InteractionResult.PASS) {
                    break;
                }
            }
            render = false;
        }
    }

    @Override
    public float getOutlineRed() {
        return r;
    }

    @Override
    public float getOutlineGreen() {
        return g;
    }

    @Override
    public float getOutlineBlue() {
        return b;
    }

    @Override
    public float getOutlineAlpha() {
        return a;
    }

    @Override
    public float getInnerRed() {
        return r;
    }

    @Override
    public float getInnerGreen() {
        return g;
    }

    @Override
    public float getInnerBlue() {
        return b;
    }

    @Override
    public float getInnerAlpha() {
        return blinkingAlpha;
    }
}

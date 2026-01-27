package me.shedaniel.csb.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import me.shedaniel.csb.CSBConfig;
import me.shedaniel.csb.api.CSBRenderer;
import me.shedaniel.csb.gui.CSBInfo;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;

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
    @Shadow private final Long2ObjectMap<SortedSet<BlockBreakingInfo>> blockBreakingProgressions;

    protected MixinWorldRenderer(Map<Integer, BlockBreakingInfo> miningProgress, Long2ObjectMap<SortedSet<BlockBreakingInfo>> blockBreakingProgressions) {
        this.blockBreakingProgressions = blockBreakingProgressions;
    }

    @Shadow
    private static void drawShapeOutline(
            MatrixStack matrixStack, VertexConsumer vertexConsumer, VoxelShape voxelShape, double d, double e, double f, float g, float h, float i, float j
    ) {}

    @Shadow
    @Final
    private MinecraftClient client;

    @WrapOperation(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;drawBlockOutline(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/entity/Entity;DDDLnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"
            ))
    private void onRenderOutlineShape(WorldRenderer instance, MatrixStack matrixStack, VertexConsumer vertexConsumer, Entity entity, double d, double e, double f, BlockPos blockPos, BlockState blockState, Operation<Void> original) {
        if (!CSBConfig.isEnabled()) {
            original.call(instance, matrixStack, vertexConsumer, entity, d, e, f, blockPos, blockState);
        } else {
            render = true;
        }
    }

    @Inject(method = "render", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/WorldRenderer;renderWorldBorder(Lnet/minecraft/client/render/Camera;)V",
            shift = At.Shift.AFTER
    ))
    private void renderCustomOutline(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline,
                                     Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
        if (render) {
            r = CSBConfig.getRed();
            g = CSBConfig.getGreen();
            b = CSBConfig.getBlue();
            a = CSBConfig.getAlpha();
            if (CSBConfig.isRainbow()) {
                final double millis = System.currentTimeMillis() % 10000L / 10000.0f;
                final int color = HSBtoRGB((float) millis, 0.8f, 0.8f);
                r = (color >> 16 & 255) / 255.0f;
                g = (color >> 8 & 255) / 255.0f;
                b = (color & 255) / 255.0f;
            }
            blinkingAlpha = CSBConfig.getBlinkSpeed() > 0 ?
                    CSBConfig.getBlinkAlpha() * (float) Math.abs(Math.sin(System.currentTimeMillis() / 100.0D * CSBConfig.getBlinkSpeed()))
                    : CSBConfig.getBlinkAlpha();
            BlockHitResult hitResult = (BlockHitResult) client.crosshairTarget;
            if (hitResult != null) {
                BlockPos blockPos = hitResult.getBlockPos();
                for (CSBRenderer renderer : RENDERERS) {
                    SortedSet<BlockBreakingInfo> infos = this.blockBreakingProgressions.get(blockPos.asLong());
                    if (infos != null && !infos.isEmpty()) {
                        float progress = infos.last().getStage();
                        ActionResult result = Objects.requireNonNull(renderer.render(world, camera, hitResult, tickDelta, progress / 10.0F));
                        if (result != ActionResult.PASS) {
                            break;
                        }
                    } else {
                        ActionResult result = Objects.requireNonNull(renderer.render(world, camera, hitResult, tickDelta, 0.0F));
                    }
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

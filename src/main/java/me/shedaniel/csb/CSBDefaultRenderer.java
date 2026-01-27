package me.shedaniel.csb;

import me.shedaniel.csb.api.CSBRenderer;
import net.minecraft.block.*;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.client.render.vertex.BufferBuilder;
import net.minecraft.client.render.vertex.DefaultVertexFormat;
import net.minecraft.client.render.vertex.Tesselator;
import net.minecraft.client.render.world.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.HitResult;
import net.minecraft.world.InteractionResult;
import org.lwjgl.opengl.GL11;

import java.util.Optional;


public class CSBDefaultRenderer implements CSBRenderer {

    @Override
    public double getPriority() {
        return 1050d;
    }

    @Override
    public InteractionResult render(ClientWorld world, Entity camera, HitResult hitResult, float tickDelta, float breakProgress) {
        double dx = camera.prevX + (camera.x - camera.prevX) * tickDelta;
        double dy = camera.prevY + (camera.y - camera.prevY) * tickDelta;
        double dz = camera.prevZ + (camera.z - camera.prevZ) * tickDelta;

        GlStateManager.pushMatrix();
        // using shape.moved(-dx, -dy, -dz) looks better at edges and less z-fighting
        //GlStateManager.translated(-dx, -dy, -dz);

        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture();

        GlStateManager.depthMask(false);
        if (CSBConfig.isShowHidden()) {
            GlStateManager.disableDepthTest();
        }
        // avoid z-fighting with outline and blinking block
        GlStateManager.enablePolygonOffset();
        GlStateManager.polygonOffset(-1.0F, -1.0F);

        GL11.glLineWidth(getOutlineThickness());

        BlockPos blockPos = hitResult.getPos();
        BlockState blockState = world.getBlockState(blockPos);

        Box originalShape = blockState.getOutlineShape(world, blockPos);
        if (CSBConfig.isLinkBlocks()) {
            Box[] shapes = adjustShapeByLinkedBlocks(world, blockState, blockPos, originalShape);
            for (Box shape : shapes) {
                drawSelectionBox(shape.moved(-dx, -dy, -dz), breakProgress);
            }
        } else {
            drawSelectionBox(originalShape.moved(-dx, -dy, -dz), breakProgress);
        }

        GlStateManager.disablePolygonOffset();
        if (CSBConfig.isShowHidden()) {
            GlStateManager.enableDepthTest();
        }
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();

        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        GlStateManager.popMatrix();

        return InteractionResult.SUCCESS;
    }

    private void drawSelectionBox(Box shape, float breakProgress) {
        float blinkAlpha = CSBConfig.getBreakAnimation() == CSBConfig.BreakAnimation.ALPHA ? breakProgress : getInnerAlpha();

        if (CSBConfig.getBreakAnimation() == CSBConfig.BreakAnimation.DOWN) {
            double dy = (shape.maxY - shape.minY) * breakProgress;
            shape = shape.shrink(0, -dy, 0).moved(0, -dy, 0);
        } else if (CSBConfig.getBreakAnimation() == CSBConfig.BreakAnimation.SHRINK) {
            double dx = (shape.maxX - shape.minX) * breakProgress;
            double dy = (shape.maxY - shape.minY) * breakProgress;
            double dz = (shape.maxZ - shape.minZ) * breakProgress;
            shape = shape.shrink(-dx, -dy, -dz).moved(-dx / 2, -dy / 2, -dz / 2);
        }

        // expand to avoid z-fighting for outlines and blinking block
        drawOutlinedBoundingBox(shape.expand(0.002), getOutlineRed(), getOutlineGreen(), getOutlineBlue(), getOutlineAlpha());
        drawBlinkingBlock(shape.expand(0.002), getInnerRed(), getInnerGreen(), getInnerBlue(), blinkAlpha);
    }

    private void drawOutlinedBoundingBox(Box voxelShapeIn, float red, float green, float blue, float alpha) {
        double minX = voxelShapeIn.minX;
        double minY = voxelShapeIn.minY;
        double minZ = voxelShapeIn.minZ;
        double maxX = voxelShapeIn.maxX;
        double maxY = voxelShapeIn.maxY;
        double maxZ = voxelShapeIn.maxZ;

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuffer();

        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        WorldRenderer.addVerticesForOutlineShape(buffer, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
        tesselator.end();
    }

    private void drawBlinkingBlock(Box voxelShapeIn, float red, float green, float blue, float alpha) {
        double minX = voxelShapeIn.minX;
        double minY = voxelShapeIn.minY;
        double minZ = voxelShapeIn.minZ;
        double maxX = voxelShapeIn.maxX;
        double maxY = voxelShapeIn.maxY;
        double maxZ = voxelShapeIn.maxZ;

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuffer();

        buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        WorldRenderer.addVerticesForShape(buffer, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
        tesselator.end();
    }

    private Box[] adjustShapeByLinkedBlocks(ClientWorld world, BlockState state, BlockPos pos, Box shape) {
        Block block = state.getBlock();
        Optional<Box> other = Optional.empty();
        try {
            if (block instanceof ChestBlock) {
                // chests aren't actually linked for breaking ¯\_('')_/¯
                // technically all chests continuously adjacent to each other get combined pre-1.13, but good enough
                Direction facing = state.get(ChestBlock.FACING);
                for (Direction d : Direction.Plane.HORIZONTAL) {
                    BlockPos offsetPos = pos.offset(d);
                    BlockState anotherChestState = world.getBlockState(pos.offset(d));
                    if (anotherChestState.getBlock().equals(block)
                            && anotherChestState.get(ChestBlock.FACING) == facing) {
                        other = Optional.ofNullable(anotherChestState.getOutlineShape(world, offsetPos));
                        break;
                    }
                }
            } else if (block instanceof DoorBlock) {
                if (world.getBlockState(pos.up(1)).getBlock().equals(block)) {
                    BlockState otherState = world.getBlockState(pos.up(1));
                    if (otherState.get(DoorBlock.FACING).equals(state.get(DoorBlock.FACING))
                            && otherState.get(DoorBlock.HINGE).equals(state.get(DoorBlock.HINGE))) {
                        other = Optional.ofNullable(otherState.getOutlineShape(world, pos.up(1)));
                    }
                }
                if (world.getBlockState(pos.down(1)).getBlock() == block) {
                    BlockState otherState = world.getBlockState(pos.down(1));
                    if (otherState.get(DoorBlock.FACING).equals(state.get(DoorBlock.FACING))
                            && otherState.get(DoorBlock.HINGE).equals(state.get(DoorBlock.HINGE))) {
                        other = Optional.ofNullable(otherState.getOutlineShape(world, pos.down(1)));
                    }
                }
            } else if (block instanceof BedBlock) {
                Direction direction = state.get(HorizontalFacingBlock.FACING);
                BlockState otherState = world.getBlockState(pos.offset(direction));
                if (state.get(BedBlock.PART).equals(BedBlock.Part.FOOT) && otherState.getBlock().equals(block)) {
                    if (otherState.get(BedBlock.PART).equals(BedBlock.Part.HEAD)) {
                        other = Optional.ofNullable(
                                otherState.getOutlineShape(world, pos)
                                        .moved(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ())
                        );
                    }
                }
                otherState = world.getBlockState(pos.offset(direction.getOpposite()));
                direction = direction.getOpposite();
                if (state.get(BedBlock.PART).equals(BedBlock.Part.HEAD) && otherState.getBlock().equals(block)) {
                    if (otherState.get(BedBlock.PART).equals(BedBlock.Part.FOOT)) {
                        other = Optional.ofNullable(
                                otherState.getOutlineShape(world, pos)
                                        .moved(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ())
                        );
                    }
                }
            } else if (block instanceof PistonBaseBlock && state.get(PistonBaseBlock.EXTENDED)) {
                // Piston Base
                Direction direction = state.get(FacingBlock.FACING);
                BlockState otherState = world.getBlockState(pos.offset(direction));
                if (otherState.get(PistonHeadBlock.TYPE).equals(block == Blocks.PISTON ? PistonHeadBlock.Type.DEFAULT : PistonHeadBlock.Type.STICKY)
                        && direction.equals(otherState.get(FacingBlock.FACING))) {
                    other = Optional.ofNullable(
                            otherState.getOutlineShape(world, pos).moved(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ())
                    );
                }
            } else if (block instanceof PistonHeadBlock) {
                // Piston Arm
                Direction direction = state.get(FacingBlock.FACING);
                BlockState otherState = world.getBlockState(pos.offset(direction.getOpposite()));
                if (otherState.getBlock() instanceof PistonBaseBlock && direction == otherState.get(FacingBlock.FACING) && otherState.get(PistonBaseBlock.EXTENDED)) {
                    other = Optional.ofNullable(
                            otherState.getOutlineShape(world, pos.offset(direction.getOpposite()))
                                    .moved(direction.getOpposite().getOffsetX(), direction.getOpposite().getOffsetY(), direction.getOpposite().getOffsetZ())
                    );
                }
            }
        } catch (Exception ignored) {

        }
        return other.map(box -> new Box[] { shape, box }).orElseGet(() -> new Box[] { shape });
    }
}

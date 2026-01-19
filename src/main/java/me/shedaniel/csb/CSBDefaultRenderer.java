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
    public InteractionResult render(ClientWorld world, Entity camera, HitResult hitResult, float tickDelta) {
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GL11.glLineWidth(getOutlineThickness());
        GlStateManager.disableTexture();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepthTest();

        BlockPos blockPos = hitResult.getPos();
        BlockState blockState = world.getBlockState(blockPos);

        double dx = camera.prevX + (camera.x - camera.prevX) * tickDelta;
        double dy = camera.prevY + (camera.y - camera.prevY) * tickDelta;
        double dz = camera.prevZ + (camera.z - camera.prevZ) * tickDelta;

        if (CSBConfig.isAdjustBoundingBoxByLinkedBlocks()) {
            Box originalShape = blockState.getOutlineShape(world, blockPos);
            Box[] shapes = adjustShapeByLinkedBlocks(world, blockState, blockPos, originalShape);
            for (Box shape : shapes) {
                Box offsetShape = shape.expand(0.002F).moved(-dx, -dy, -dz);
                drawOutlinedBoundingBox(offsetShape, getOutlineRed(), getOutlineGreen(), getOutlineBlue(), getOutlineAlpha());
                drawBlinkingBlock(offsetShape, dx, dy, dz, getInnerRed(), getInnerGreen(), getInnerBlue(), getInnerAlpha());
            }
        } else {
            Box shape = blockState.getOutlineShape(world, blockPos);
            Box offsetShape = shape.expand(0.002F).moved(-dx, -dy, -dz);
            drawOutlinedBoundingBox(offsetShape, getOutlineRed(), getOutlineGreen(), getOutlineBlue(), getOutlineAlpha());
            drawBlinkingBlock(offsetShape, dx, dy, dz, getInnerRed(), getInnerGreen(), getInnerBlue(), getInnerAlpha());
        }


        GlStateManager.enableDepthTest();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();


        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        return InteractionResult.SUCCESS;
    }

    private void drawOutlinedBoundingBox(Box voxelShapeIn, float red, float green, float blue, float alpha) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuffer();
        //RenderSystem.pushMatrix();
        //RenderSystem.enableBlend();
        //RenderSystem.enableDepthTest();
        //RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        //RenderSystem.depthMask(false);
        //RenderSystem.color4f(red, green, blue, alpha);
        //RenderSystem.defaultAlphaFunc();
        //RenderSystem.enableAlphaTest();
        //RenderSystem.disableCull();
        //RenderSystem.disableTexture();
        //RenderSystem.lineWidth(getOutlineThickness());

        buffer.begin(3, DefaultVertexFormat.POSITION_COLOR);
        //voxelShapeIn.forEachEdge((k, l, m, n, o, p)  -> {
        //    buffer.vertex( (float)(k + xIn), (float)(l + yIn), (float)(m + zIn)).next();
        //    buffer.vertex( (float)(n + xIn), (float)(o + yIn), (float)(p + zIn)).next();
        //});
        double minX = voxelShapeIn.minX;
        double maxX = voxelShapeIn.maxX;
        double minY = voxelShapeIn.minY;
        double maxY = voxelShapeIn.maxY;
        double minZ = voxelShapeIn.minZ;
        double maxZ = voxelShapeIn.maxZ;

        //buffer.vertex(minX, minY, minZ).color(red, green, blue, 0.0F).nextVertex();
        //buffer.vertex(minX, minY, minZ).color(red, green, blue, alpha).nextVertex();
        //buffer.vertex(maxX, minY, minZ).color(red, green, blue, alpha).nextVertex();
        //buffer.vertex(maxX, minY, maxZ).color(red, green, blue, alpha).nextVertex();
        //buffer.vertex(minX, minY, maxZ).color(red, green, blue, alpha).nextVertex();
        //buffer.vertex(minX, minY, minZ).color(red, green, blue, alpha).nextVertex();
        //buffer.vertex(minX, maxY, minZ).color(red, green, blue, alpha).nextVertex();
        //buffer.vertex(maxX, maxY, minZ).color(red, green, blue, alpha).nextVertex();
        //buffer.vertex(maxX, maxY, maxZ).color(red, green, blue, alpha).nextVertex();
        //buffer.vertex(minX, maxY, maxZ).color(red, green, blue, alpha).nextVertex();
        //buffer.vertex(minX, maxY, minZ).color(red, green, blue, alpha).nextVertex();
        //buffer.vertex(minX, maxY, maxZ).color(red, green, blue, 0.0F).nextVertex();
        //buffer.vertex(minX, minY, maxZ).color(red, green, blue, alpha).nextVertex();
        //buffer.vertex(maxX, maxY, maxZ).color(red, green, blue, 0.0F).nextVertex();
        //buffer.vertex(maxX, minY, maxZ).color(red, green, blue, alpha).nextVertex();
        //buffer.vertex(maxX, maxY, minZ).color(red, green, blue, 0.0F).nextVertex();
        //buffer.vertex(maxX, minY, minZ).color(red, green, blue, alpha).nextVertex();
        //buffer.vertex(maxX, minY, minZ).color(red, green, blue, 0.0F).nextVertex();
        WorldRenderer.addVerticesForOutlineShape(buffer, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
        tesselator.end();
//        for (Box box : voxelShapeIn.getBoundingBoxes()) {
//            vertexConsumer.begin(1, VertexFormats.POSITION);
//            vertexConsumer.vertex((float) (box.x1 - 0.005 + xIn), (float) (box.y1 - 0.005 + yIn), (float) (box.z1 - 0.005 + zIn)).next();
//            vertexConsumer.vertex((float) (box.x1 - 0.005 + xIn), (float) (box.y2 + 0.005 + yIn), (float) (box.z1 - 0.005 + zIn)).next();
//            tessellator.draw();
//            vertexConsumer.begin(1, VertexFormats.POSITION);
//            vertexConsumer.vertex((float) (box.x2 + 0.005 + xIn), (float) (box.y1 - 0.005 + yIn), (float) (box.z1 - 0.005 + zIn)).next();
//            vertexConsumer.vertex((float) (box.x2 + 0.005 + xIn), (float) (box.y2 + 0.005 + yIn), (float) (box.z1 - 0.005 + zIn)).next();
//            tessellator.draw();
//            vertexConsumer.begin(1, VertexFormats.POSITION);
//            vertexConsumer.vertex((float) (box.x1 - 0.005 + xIn), (float) (box.y1 - 0.005 + yIn), (float) (box.z2 + 0.005 + zIn)).next();
//            vertexConsumer.vertex((float) (box.x1 - 0.005 + xIn), (float) (box.y2 + 0.005 + yIn), (float) (box.z2 + 0.005 + zIn)).next();
//            tessellator.draw();
//            vertexConsumer.begin(1, VertexFormats.POSITION);
//            vertexConsumer.vertex((float) (box.x2 + 0.005 + xIn), (float) (box.y1 - 0.005 + yIn), (float) (box.z2 + 0.005 + zIn)).next();
//            vertexConsumer.vertex((float) (box.x2 + 0.005 + xIn), (float) (box.y2 + 0.005 + yIn), (float) (box.z2 + 0.005 + zIn)).next();
//            tessellator.draw();
//            vertexConsumer.begin(1, VertexFormats.POSITION);
//            vertexConsumer.vertex((float) (box.x1 - 0.005 + xIn), (float) (box.y1 - 0.005 + yIn), (float) (box.z1 - 0.005 + zIn)).next();
//            vertexConsumer.vertex((float) (box.x1 - 0.005 + xIn), (float) (box.y1 - 0.005 + yIn), (float) (box.z2 + 0.005 + zIn)).next();
//            tessellator.draw();
//            vertexConsumer.begin(1, VertexFormats.POSITION);
//            vertexConsumer.vertex((float) (box.x2 + 0.005 + xIn), (float) (box.y1 - 0.005 + yIn), (float) (box.z1 - 0.005 + zIn)).next();
//            vertexConsumer.vertex((float) (box.x2 + 0.005 + xIn), (float) (box.y1 - 0.005 + yIn), (float) (box.z2 + 0.005 + zIn)).next();
//            tessellator.draw();
//            vertexConsumer.begin(1, VertexFormats.POSITION);
//            vertexConsumer.vertex((float) (box.x1 - 0.005 + xIn), (float) (box.y2 + 0.005 + yIn), (float) (box.z1 - 0.005 + zIn)).next();
//            vertexConsumer.vertex((float) (box.x1 - 0.005 + xIn), (float) (box.y2 + 0.005 + yIn), (float) (box.z2 + 0.005 + zIn)).next();
//            tessellator.draw();
//            vertexConsumer.begin(1, VertexFormats.POSITION);
//            vertexConsumer.vertex((float) (box.x2 + 0.005 + xIn), (float) (box.y2 + 0.005 + yIn), (float) (box.z1 - 0.005 + zIn)).next();
//            vertexConsumer.vertex((float) (box.x2 + 0.005 + xIn), (float) (box.y2 + 0.005 + yIn), (float) (box.z2 + 0.005 + zIn)).next();
//            tessellator.draw();
//            vertexConsumer.begin(1, VertexFormats.POSITION);
//            vertexConsumer.vertex((float) (box.x1 - 0.005 + xIn), (float) (box.y1 - 0.005 + yIn), (float) (box.z1 - 0.005 + zIn)).next();
//            vertexConsumer.vertex((float) (box.x2 + 0.005 + xIn), (float) (box.y1 - 0.005 + yIn), (float) (box.z1 - 0.005 + zIn)).next();
//            tessellator.draw();
//            vertexConsumer.begin(1, VertexFormats.POSITION);
//            vertexConsumer.vertex((float) (box.x1 - 0.005 + xIn), (float) (box.y2 + 0.005 + yIn), (float) (box.z1 - 0.005 + zIn)).next();
//            vertexConsumer.vertex((float) (box.x2 + 0.005 + xIn), (float) (box.y2 + 0.005 + yIn), (float) (box.z1 - 0.005 + zIn)).next();
//            tessellator.draw();
//            vertexConsumer.begin(1, VertexFormats.POSITION);
//            vertexConsumer.vertex((float) (box.x1 - 0.005 + xIn), (float) (box.y1 - 0.005 + yIn), (float) (box.z2 + 0.005 + zIn)).next();
//            vertexConsumer.vertex((float) (box.x2 + 0.005 + xIn), (float) (box.y1 - 0.005 + yIn), (float) (box.z2 + 0.005 + zIn)).next();
//            tessellator.draw();
//            vertexConsumer.begin(1, VertexFormats.POSITION);
//            vertexConsumer.vertex((float) (box.x1 - 0.005 + xIn), (float) (box.y2 + 0.005 + yIn), (float) (box.z2 + 0.005 + zIn)).next();
//            vertexConsumer.vertex((float) (box.x2 + 0.005 + xIn), (float) (box.y2 + 0.005 + yIn), (float) (box.z2 + 0.005 + zIn)).next();
//            tessellator.draw();
//        }
        //RenderSystem.enableCull();
        //RenderSystem.disableAlphaTest();
        //RenderSystem.enableAlphaTest();
        //RenderSystem.disableBlend();
        //RenderSystem.depthMask(true);
        //RenderSystem.popMatrix();
    }

    private void drawBlinkingBlock(Box voxelShapeIn, double xIn, double yIn, double zIn, float red, float green, float blue, float alpha) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuffer();
        //RenderSystem.pushMatrix();
        //RenderSystem.enableBlend();
        //RenderSystem.enableDepthTest();
        //RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        //RenderSystem.depthMask(false);
        //RenderSystem.color4f(red, green, blue, alpha);
        //RenderSystem.defaultAlphaFunc();
        //RenderSystem.enableAlphaTest();
        //RenderSystem.disableCull();
        //RenderSystem.disableTexture();
        //VoxelShape shape = voxelShapeIn.getBoundingBoxes().stream()
        //        .map(box -> box.expand(0.005, 0.005, 0.005))
        //        .map(VoxelShapes::cuboid)
        //        .reduce(VoxelShapes::union)
        //        .orElse(VoxelShapes.empty()).simplify();
        //for (Box box : shape.getBoundingBoxes()) {
        //    box(tesselator, buffer, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, xIn, yIn, zIn);
        //}
        GlStateManager.color4f(red, green, blue, alpha);
        box(tesselator, buffer, voxelShapeIn);
        //RenderSystem.enableCull();
        //RenderSystem.disableAlphaTest();
        //RenderSystem.enableAlphaTest();
        //RenderSystem.disableBlend();
        //RenderSystem.depthMask(true);
        //RenderSystem.popMatrix();
    }

    private void box(Tesselator tesselator, BufferBuilder buffer, Box box) {
        double minX = box.minX;
        double maxX = box.maxX;
        double minY = box.minY;
        double maxY = box.maxY;
        double minZ = box.minZ;
        double maxZ = box.maxZ;
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION);
        buffer.vertex(minX, minY, minZ).nextVertex();
        buffer.vertex(maxX, minY, minZ).nextVertex();
        buffer.vertex(maxX, minY, maxZ).nextVertex();
        buffer.vertex(minX, minY, maxZ).nextVertex();
        buffer.vertex(minX, minY, minZ).nextVertex();
        tesselator.end();

        //Down
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION);
        buffer.vertex(minX, maxY, minZ).nextVertex();
        buffer.vertex(minX, maxY, maxZ).nextVertex();
        buffer.vertex(maxX, maxY, maxZ).nextVertex();
        buffer.vertex(maxX, maxY, minZ).nextVertex();
        buffer.vertex(minX, maxY, minZ).nextVertex();
        tesselator.end();

        //North
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION);
        buffer.vertex(minX, minY, minZ).nextVertex();
        buffer.vertex(minX, maxY, minZ).nextVertex();
        buffer.vertex(maxX, maxY, minZ).nextVertex();
        buffer.vertex(maxX, minY, minZ).nextVertex();
        buffer.vertex(minX, minY, minZ).nextVertex();
        tesselator.end();

        //South
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION);
        buffer.vertex(minX, minY, maxZ).nextVertex();
        buffer.vertex(maxX, minY, maxZ).nextVertex();
        buffer.vertex(maxX, maxY, maxZ).nextVertex();
        buffer.vertex(minX, maxY, maxZ).nextVertex();
        buffer.vertex(minX, minY, maxZ).nextVertex();
        tesselator.end();

        //West
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION);
        buffer.vertex(minX, minY, minZ).nextVertex();
        buffer.vertex(minX, minY, maxZ).nextVertex();
        buffer.vertex(minX, maxY, maxZ).nextVertex();
        buffer.vertex(minX, maxY, minZ).nextVertex();
        buffer.vertex(minX, minY, minZ).nextVertex();
        tesselator.end();

        //East
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION);
        buffer.vertex(maxX, minY, minZ).nextVertex();
        buffer.vertex(maxX, maxY, minZ).nextVertex();
        buffer.vertex(maxX, maxY, maxZ).nextVertex();
        buffer.vertex(maxX, minY, maxZ).nextVertex();
        buffer.vertex(maxX, minY, minZ).nextVertex();
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

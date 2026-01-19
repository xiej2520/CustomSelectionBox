package me.shedaniel.csb;

import me.shedaniel.csb.api.CSBRenderer;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.client.render.vertex.BufferBuilder;
import net.minecraft.client.render.vertex.DefaultVertexFormat;
import net.minecraft.client.render.vertex.Tesselator;
import net.minecraft.client.render.world.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.HitResult;
import net.minecraft.world.InteractionResult;
import org.lwjgl.opengl.GL11;


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
        Vec3d cameraPos = Camera.getPos(camera, tickDelta);

        Box shape = blockState.getOutlineShape(world, blockPos);
        double dx = camera.prevX + (camera.x - camera.prevX) * tickDelta;
        double dy = camera.prevY + (camera.y - camera.prevY) * tickDelta;
        double dz = camera.prevZ + (camera.z - camera.prevZ) * tickDelta;

        Box offsetShape = shape.expand(0.002F).moved(-dx, -dy, -dz);
        if (CSBConfig.isAdjustBoundingBoxByLinkedBlocks()) {
            //shape = adjustShapeByLinkedBlocks(world, blockState, blockPos, shape);
        }
        drawOutlinedBoundingBox(offsetShape, getOutlineRed(), getOutlineGreen(), getOutlineBlue(), getOutlineAlpha());
        drawBlinkingBlock(offsetShape, dx, dy, dz, getInnerRed(), getInnerGreen(), getInnerBlue(), getInnerAlpha());


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

    //private Box adjustShapeByLinkedBlocks(ClientWorld world, BlockState blockState, BlockPos blockPos, Box shape) {
    //    try {
    //        if (blockState.getBlock() instanceof ChestBlock) {
    //            // Chests
    //            Block block = blockState.getBlock();
    //            Direction facing = ChestBlock.getFacing(blockState);
    //            BlockState anotherChestState = world.getBlockState(blockPos.offset(facing, 1));
    //            if (anotherChestState.getBlock() == block)
    //                if (blockPos.offset(facing, 1).offset(ChestBlock.getFacing(anotherChestState)).equals(blockPos))
    //                    return VoxelShapes.union(shape, anotherChestState.getOutlineShape(world, blockPos).offset(facing.getOffsetX(), facing.getOffsetY(), facing.getOffsetZ()));
    //        } else if (blockState.getBlock() instanceof DoorBlock) {
    //            // Doors
    //            Block block = blockState.getBlock();
    //            if (world.getBlockState(blockPos.up(1)).getBlock() == block) {
    //                BlockState otherState = world.getBlockState(blockPos.up(1));
    //                if (otherState.get(DoorBlock.POWERED).equals(blockState.get(DoorBlock.POWERED)) && otherState.get(DoorBlock.FACING).equals(blockState.get(DoorBlock.FACING)) && otherState.get(DoorBlock.HINGE).equals(blockState.get(DoorBlock.HINGE))) {
    //                    return VoxelShapes.union(shape, otherState.getOutlineShape(world, blockPos).offset(0, 1, 0));
    //                }
    //            }
    //            if (world.getBlockState(blockPos.down(1)).getBlock() == block) {
    //                BlockState otherState = world.getBlockState(blockPos.down(1));
    //                if (otherState.get(DoorBlock.POWERED).equals(blockState.get(DoorBlock.POWERED)) && otherState.get(DoorBlock.FACING).equals(blockState.get(DoorBlock.FACING)) && otherState.get(DoorBlock.HINGE).equals(blockState.get(DoorBlock.HINGE)))
    //                    return VoxelShapes.union(shape, otherState.getOutlineShape(world, blockPos).offset(0, -1, 0));
    //            }
    //        } else if (blockState.getBlock() instanceof BedBlock) {
    //            // Beds
    //            Block block = blockState.getBlock();
    //            Direction direction = blockState.get(HorizontalFacingBlock.FACING);
    //            BlockState otherState = world.getBlockState(blockPos.offset(direction));
    //            if (blockState.get(BedBlock.PART).equals(BedPart.FOOT) && otherState.getBlock() == block) {
    //                if (otherState.get(BedBlock.PART).equals(BedPart.HEAD))
    //                    return VoxelShapes.union(shape, otherState.getOutlineShape(world, blockPos).offset(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ()));
    //            }
    //            otherState = world.getBlockState(blockPos.offset(direction.getOpposite()));
    //            direction = direction.getOpposite();
    //            if (blockState.get(BedBlock.PART).equals(BedPart.HEAD) && otherState.getBlock() == block) {
    //                if (otherState.get(BedBlock.PART).equals(BedPart.FOOT))
    //                    return VoxelShapes.union(shape, otherState.getOutlineShape(world, blockPos).offset(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ()));
    //            }
    //        } else if (blockState.getBlock() instanceof PistonBlock && blockState.get(PistonBlock.EXTENDED)) {
    //            // Piston Base
    //            Block block = blockState.getBlock();
    //            Direction direction = blockState.get(FacingBlock.FACING);
    //            BlockState otherState = world.getBlockState(blockPos.offset(direction));
    //            if (otherState.get(PistonHeadBlock.TYPE).equals(block == Blocks.PISTON ? PistonType.DEFAULT : PistonType.STICKY) && direction.equals(otherState.get(FacingBlock.FACING)))
    //                return VoxelShapes.union(shape, otherState.getOutlineShape(world, blockPos).offset(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ()));
    //        } else if (blockState.getBlock() instanceof PistonHeadBlock) {
    //            // Piston Arm
    //            Block block = blockState.getBlock();
    //            Direction direction = blockState.get(FacingBlock.FACING);
    //            BlockState otherState = world.getBlockState(blockPos.offset(direction.getOpposite()));
    //            if (otherState.getBlock() instanceof PistonBlock && direction == otherState.get(FacingBlock.FACING) && otherState.get(PistonBlock.EXTENDED))
    //                return VoxelShapes.union(shape, otherState.getOutlineShape(world, blockPos.offset(direction.getOpposite())).offset(direction.getOpposite().getOffsetX(), direction.getOpposite().getOffsetY(), direction.getOpposite().getOffsetZ()));
    //        }
    //    } catch (Exception ignored) {
    //    }
    //    return shape;
    //}
}

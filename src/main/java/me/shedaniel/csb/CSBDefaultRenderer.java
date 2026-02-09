package me.shedaniel.csb;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.csb.api.CSBRenderer;
import net.minecraft.block.*;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.PistonType;
import net.minecraft.client.render.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.lwjgl.opengl.GL11;

public class CSBDefaultRenderer implements CSBRenderer {

    @Override
    public double getPriority() {
        return 1050d;
    }

    @Override
    public ActionResult render(ClientWorld world, Camera camera, BlockHitResult hitResult, float tickDelta, float breakProgress) {
        Vec3d cameraPos = camera.getPos();

        RenderSystem.pushMatrix();
        // using shape.moved(-dx, -dy, -dz) looks better at edges and less z-fighting
        //GlStateManager.translated(-dx, -dy, -dz);

        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        RenderSystem.disableTexture();

        RenderSystem.depthMask(false);
        if (!CSBConfig.isShowHidden()) {
            RenderSystem.enableDepthTest();
        }
        // avoid z-fighting with outline and blinking block
        RenderSystem.enablePolygonOffset();
        RenderSystem.polygonOffset(-1.0F, -1.0F);

        GL11.glLineWidth(getOutlineThickness());

        BlockPos blockPos = hitResult.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);

        VoxelShape shape = blockState.getOutlineShape(world, blockPos, EntityContext.of(camera.getFocusedEntity()));
        if (CSBConfig.isLinkBlocks()) {
            shape = adjustShapeByLinkedBlocks(world, blockState, blockPos, shape);
        }
        drawSelectionBox(shape.offset(blockPos.getX() - cameraPos.getX(), blockPos.getY() - cameraPos.getY(), blockPos.getZ() - cameraPos.getZ()), breakProgress);

        RenderSystem.disablePolygonOffset();
        if (!CSBConfig.isShowHidden()) {
            RenderSystem.disableDepthTest();
        }
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();

        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        RenderSystem.popMatrix();

        return ActionResult.SUCCESS;
    }

    private void drawSelectionBox(VoxelShape shape, float breakProgress) {
        float blinkAlpha = CSBConfig.getBreakAnimation() == CSBConfig.BreakAnimation.ALPHA ? breakProgress : getInnerAlpha();

        if (CSBConfig.getBreakAnimation() == CSBConfig.BreakAnimation.DOWN) {
            for (Box box : shape.getBoundingBoxes()) {
                double dy = (box.y2 - box.y1) * breakProgress;
                VoxelShape cuboid = VoxelShapes.cuboid(box.shrink(0, -dy, 0).offset(0, -dy, 0).expand(0.002));
                drawOutlinedBoundingBox(cuboid, getOutlineRed(), getOutlineGreen(), getOutlineBlue(), getOutlineAlpha());
                drawBlinkingBlock(cuboid, getInnerRed(), getInnerGreen(), getInnerBlue(), blinkAlpha);
            }
        } else if (CSBConfig.getBreakAnimation() == CSBConfig.BreakAnimation.SHRINK) {
            for (Box box : shape.getBoundingBoxes()) {
                double dx = (box.x2 - box.x1) * breakProgress;
                double dy = (box.y2 - box.y1) * breakProgress;
                double dz = (box.z2 - box.z1) * breakProgress;
                VoxelShape cuboid = VoxelShapes.cuboid(box.shrink(-dx, -dy, -dz).offset(-dx / 2, -dy / 2, -dz / 2).expand(0.002));
                drawOutlinedBoundingBox(cuboid, getOutlineRed(), getOutlineGreen(), getOutlineBlue(), getOutlineAlpha());
                drawBlinkingBlock(cuboid, getInnerRed(), getInnerGreen(), getInnerBlue(), blinkAlpha);
            }
        } else {
            for (Box box : shape.getBoundingBoxes()) {
                // expand to avoid z-fighting for outlines and blinking block
                VoxelShape cuboid = VoxelShapes.cuboid(box.expand(0.002));
                drawOutlinedBoundingBox(cuboid, getOutlineRed(), getOutlineGreen(), getOutlineBlue(), getOutlineAlpha());
                drawBlinkingBlock(cuboid, getInnerRed(), getInnerGreen(), getInnerBlue(), blinkAlpha);
            }
        }
    }

    public static void drawOutlinedBoundingBox(VoxelShape voxelShapeIn, float r, float g, float b, float a) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        RenderSystem.color4f(r, g, b, a);
        buffer.begin(GL11.GL_LINES, VertexFormats.POSITION);
        for (Box box : voxelShapeIn.getBoundingBoxes()) {
            buffer.vertex(box.x1, box.y1, box.z1).next(); buffer.vertex(box.x2, box.y1, box.z1).next();
            buffer.vertex(box.x2, box.y1, box.z1).next(); buffer.vertex(box.x2, box.y1, box.z2).next();
            buffer.vertex(box.x2, box.y1, box.z2).next(); buffer.vertex(box.x1, box.y1, box.z2).next();
            buffer.vertex(box.x1, box.y1, box.z2).next(); buffer.vertex(box.x1, box.y1, box.z1).next();

            buffer.vertex(box.x1, box.y2, box.z1).next(); buffer.vertex(box.x2, box.y2, box.z1).next();
            buffer.vertex(box.x2, box.y2, box.z1).next(); buffer.vertex(box.x2, box.y2, box.z2).next();
            buffer.vertex(box.x2, box.y2, box.z2).next(); buffer.vertex(box.x1, box.y2, box.z2).next();
            buffer.vertex(box.x1, box.y2, box.z2).next(); buffer.vertex(box.x1, box.y2, box.z1).next();

            buffer.vertex(box.x1, box.y1, box.z1).next(); buffer.vertex(box.x1, box.y2, box.z1).next();
            buffer.vertex(box.x2, box.y1, box.z1).next(); buffer.vertex(box.x2, box.y2, box.z1).next();
            buffer.vertex(box.x2, box.y1, box.z2).next(); buffer.vertex(box.x2, box.y2, box.z2).next();
            buffer.vertex(box.x1, box.y1, box.z2).next(); buffer.vertex(box.x1, box.y2, box.z2).next();
        }
        tessellator.draw();
    }

    public static void drawBlinkingBlock(VoxelShape voxelShapeIn, float r, float g, float b, float a) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        for (Box box : voxelShapeIn.getBoundingBoxes()) {
            WorldRenderer.drawBox(buffer, box.x1, box.y1, box.z1, box.x2, box.y2, box.z2, r, g, b, a);
        }
        tessellator.draw();
    }

    private VoxelShape adjustShapeByLinkedBlocks(ClientWorld world, BlockState state, BlockPos pos, VoxelShape shape) {
        Block block = state.getBlock();
        try {
             //if (block instanceof ChestBlock) {
             //    // chests aren't actually linked for breaking ¯\_('')_/¯
             //    // technically all chests continuously adjacent to each other get combined pre-1.13, but good enough
             //   Direction facing = state.get(ChestBlock.FACING);
             //   for (Direction d : Direction.Plane.HORIZONTAL) {
             //       BlockPos offsetPos = pos.offset(d);
             //       BlockState anotherChestState = world.getBlockState(pos.offset(d));
             //       if (anotherChestState.getBlock().equals(block)
             //               && anotherChestState.get(ChestBlock.FACING) == facing) {
             //           other = Optional.ofNullable(anotherChestState.getOutlineShape(world, offsetPos));
             //           break;
             //   //Direction facing = state.get(ChestBlock.FACING);
             //   Direction offset = ChestBlock.getFacing(state); // more like getAdjacent
             //   BlockPos offsetPos = pos.offset(offset);
             //   BlockState anotherChestState = world.getBlockState(offsetPos);
             //   if (anotherChestState.getBlock() == block) { // trapped vs non-trapped
             //       if (offsetPos.offset(ChestBlock.getFacing(anotherChestState)).equals(pos)) {
             //           return VoxelShapes.union(shape, anotherChestState.getOutlineShape(world, offsetPos).offset(offset.getOffsetX(), offset.getOffsetY(), offset.getOffsetZ()));
            if (block instanceof TallPlantBlock) {
                if (state.get(TallPlantBlock.HALF).equals(DoubleBlockHalf.LOWER)) {
                    BlockState otherState = world.getBlockState(pos.up(1));
                    if (otherState.getBlock().equals(block) && otherState.get(TallPlantBlock.HALF).equals(DoubleBlockHalf.UPPER)) {
                        return VoxelShapes.union(shape, otherState.getOutlineShape(world, pos.up(1)).offset(0, 1, 0));
                    }
                }
                if (state.get(TallPlantBlock.HALF).equals(DoubleBlockHalf.UPPER)) {
                    BlockState otherState = world.getBlockState(pos.down(1));
                    if (otherState.getBlock().equals(block) && otherState.get(TallPlantBlock.HALF).equals(DoubleBlockHalf.LOWER)) {
                        return VoxelShapes.union(shape, otherState.getOutlineShape(world, pos.down(1)).offset(0, -1, 0));
                    }
                }
            } else if (block instanceof DoorBlock) {
                if (state.get(DoorBlock.HALF).equals(DoubleBlockHalf.LOWER)) {
                    BlockState otherState = world.getBlockState(pos.up(1));
                    if (otherState.getBlock().equals(block)
                        && otherState.get(DoorBlock.FACING).equals(state.get(DoorBlock.FACING))
                        && otherState.get(DoorBlock.HINGE).equals(state.get(DoorBlock.HINGE))
                        && otherState.get(DoorBlock.HALF).equals(DoubleBlockHalf.UPPER)
                    ) {
                        return VoxelShapes.union(shape, otherState.getOutlineShape(world, pos.up(1)).offset(0, 1, 0));
                    }
                }
                if (state.get(DoorBlock.HALF).equals(DoubleBlockHalf.UPPER)) {
                    BlockState otherState = world.getBlockState(pos.down(1));
                    if (otherState.getBlock().equals(block)
                        && otherState.get(DoorBlock.FACING).equals(state.get(DoorBlock.FACING))
                        && otherState.get(DoorBlock.HINGE).equals(state.get(DoorBlock.HINGE))
                        && otherState.get(DoorBlock.HALF).equals(DoubleBlockHalf.LOWER)
                    ) {
                        return VoxelShapes.union(shape, otherState.getOutlineShape(world, pos.down(1)).offset(0, -1, 0));
                    }
                }
            } else if (block instanceof BedBlock) {
                Direction direction = state.get(HorizontalFacingBlock.FACING);
                if (state.get(BedBlock.PART).equals(BedPart.FOOT)) {
                    BlockState otherState = world.getBlockState(pos.offset(direction));
                    if (otherState.get(BedBlock.PART).equals(BedPart.HEAD)) {
                        return VoxelShapes.union(shape, otherState.getOutlineShape(world, pos)
                                .offset(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ()));
                    }
                }
                if (state.get(BedBlock.PART).equals(BedPart.HEAD)) {
                    direction = direction.getOpposite();
                    BlockState otherState = world.getBlockState(pos.offset(direction));
                    if (otherState.get(BedBlock.PART).equals(BedPart.FOOT)) {
                        return VoxelShapes.union(shape,
                                 otherState.getOutlineShape(world, pos.offset(direction))
                                        .offset(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ())
                         );
                    }
                }
            } else if (block instanceof PistonBlock && state.get(PistonBlock.EXTENDED)) {
                // Piston Base
                Direction direction = state.get(FacingBlock.FACING);
                BlockState otherState = world.getBlockState(pos.offset(direction));
                if (otherState.get(PistonHeadBlock.TYPE).equals(block == Blocks.PISTON ? PistonType.DEFAULT : PistonType.STICKY)
                        && direction.equals(otherState.get(FacingBlock.FACING))) {
                    return VoxelShapes.union(shape,
                            otherState.getOutlineShape(world, pos).offset(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ())
                    );
                }
            } else if (block instanceof PistonHeadBlock) {
                // Piston Arm
                Direction direction = state.get(FacingBlock.FACING);
                BlockState otherState = world.getBlockState(pos.offset(direction.getOpposite()));
                if (otherState.getBlock() instanceof PistonBlock && direction == otherState.get(FacingBlock.FACING) && otherState.get(PistonBlock.EXTENDED)) {
                    return VoxelShapes.union(
                            shape,
                             otherState.getOutlineShape(world, pos.offset(direction.getOpposite()))
                                    .offset(direction.getOpposite().getOffsetX(), direction.getOpposite().getOffsetY(), direction.getOpposite().getOffsetZ())
                     );
                }
            }
        } catch (Exception ignored) {

        }
        return shape;
    }
}

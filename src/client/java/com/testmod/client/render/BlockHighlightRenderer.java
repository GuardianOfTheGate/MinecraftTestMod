package com.testmod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class BlockHighlightRenderer {

    //renders grid around Blocks in positions Set
    public static void render(WorldRenderContext context){
        if(HighlightedBlocks.positions.isEmpty()) return;

        PoseStack matrices = context.matrices();
        Vec3 camPos = context.gameRenderer().getMainCamera().position();

        matrices.pushPose();
        matrices.translate(-camPos.x, -camPos.y, -camPos.z);

        MultiBufferSource.BufferSource consumers = (MultiBufferSource.BufferSource) context.consumers();
        VertexConsumer lines = consumers.getBuffer(RenderTypes.lines());

        for (BlockPos pos : HighlightedBlocks.positions) {
            AABB box = new AABB(pos).inflate(0.002);
            drawBoxOutline(matrices, lines, box, 1.0f, 1.0f, 0.0f, 0.8f);
        }

        consumers.endBatch(RenderTypes.lines());
        matrices.popPose();
    }

    //determines Outline of a given Block
    private static void drawBoxOutline(PoseStack matrices, VertexConsumer consumer,
                                       AABB box, float r, float g, float b, float a) {
        Matrix4f matrix = matrices.last().pose();
        PoseStack.Pose pose = matrices.last();

        float x0 = (float) box.minX, y0 = (float) box.minY, z0 = (float) box.minZ;
        float x1 = (float) box.maxX, y1 = (float) box.maxY, z1 = (float) box.maxZ;

        drawLine(consumer, matrix, pose, x0, y0, z0, x1, y0, z0, r, g, b, a);
        drawLine(consumer, matrix, pose, x0, y0, z0, x0, y1, z0, r, g, b, a);
        drawLine(consumer, matrix, pose, x0, y0, z0, x0, y0, z1, r, g, b, a);
        drawLine(consumer, matrix, pose, x1, y0, z0, x1, y1, z0, r, g, b, a);
        drawLine(consumer, matrix, pose, x1, y0, z0, x1, y0, z1, r, g, b, a);
        drawLine(consumer, matrix, pose, x0, y1, z0, x1, y1, z0, r, g, b, a);
        drawLine(consumer, matrix, pose, x0, y1, z0, x0, y1, z1, r, g, b, a);
        drawLine(consumer, matrix, pose, x0, y0, z1, x1, y0, z1, r, g, b, a);
        drawLine(consumer, matrix, pose, x0, y0, z1, x0, y1, z1, r, g, b, a);
        drawLine(consumer, matrix, pose, x1, y1, z0, x1, y1, z1, r, g, b, a);
        drawLine(consumer, matrix, pose, x1, y0, z1, x1, y1, z1, r, g, b, a);
        drawLine(consumer, matrix, pose, x0, y1, z1, x1, y1, z1, r, g, b, a);
    }

    // adds a line to be drawn to the render Buffer
    private static void drawLine(VertexConsumer consumer, Matrix4f matrix, PoseStack.Pose pose,
                                 float x1, float y1, float z1,
                                 float x2, float y2, float z2,
                                 float r, float g, float b, float a) {
        float nx = x2-x1, ny = y2-y1, nz = z2-z1;
        float len = (float) Math.sqrt(nx*nx + ny*ny + nz*nz);
        consumer.addVertex(matrix, x1, y1, z1)
                .setColor(r, g, b, a)
                .setNormal(pose, nx/len, ny/len, nz/len)
                .setLineWidth(2.0f);
        consumer.addVertex(matrix, x2, y2, z2)
                .setColor(r, g, b, a)
                .setNormal(pose, nx/len, ny/len, nz/len)
                .setLineWidth(2.0f);
    }


}

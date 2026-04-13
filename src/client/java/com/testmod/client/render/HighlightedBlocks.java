package com.testmod.client.render;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.HashSet;
import java.util.Set;

public class HighlightedBlocks {

    //determines a target Block depending on origin Block, facing Direction and a given Offset
    private static BlockPos getTargetPos(BlockPos origin, Direction facing, int u, int v, int depth) {
        return switch (facing.getAxis()) {
            case Z -> origin.offset(u, v, facing.getStepZ() * (depth + 1));
            case X -> origin.offset(facing.getStepX() * (depth + 1), v, u);
            default -> origin;
        };
    }


    public static final Set<BlockPos> positions = new HashSet<>();

    //adds Blocks to the positions set
    public static void update(BlockPos targetBlock, Direction direction) {
        positions.clear();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = 0; dz <= 4; dz++) {
                for (int dy = 0; dy <= 4; dy++) {
                    BlockPos pos = getTargetPos(targetBlock, direction, dx, dy, dz);
                    positions.add(pos);
                }
            }
        }
    }
    //clears the positions set
    public static void clear() {

        positions.clear();
    }

}

package com.testmod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;


//Mines a 3x3 cube on BlockHit. Hit block is centre of cubes front face
public class Stripmine extends Item{

    public Stripmine(Item.Properties properties){
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context){

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        Direction face = context.getClickedFace();
        Direction.Axis faceAxis = face.getAxis();

        if(level.isClientSide() || player == null){
            return InteractionResult.PASS;
        }

        for(int depth = 0; depth < 3; depth++) {
            for (int u = -1; u <= 1; u++) {
                for (int v = -1; v <= 1; v++) {
                    BlockPos targetPos = switch (faceAxis) {
                        case Y -> pos.offset(u, face.getStepY() * -depth, v); // Top/Bottom → X/Z-Ebene
                        case Z -> pos.offset(u, v, face.getStepZ() * -depth); // North/South → X/Y-Ebene
                        case X -> pos.offset(face.getStepX() * -depth, u, v); // East/West  → Z/Y-Ebene
                    };

                    BlockState state = level.getBlockState(targetPos);
                    if (state.isAir() || state.getDestroySpeed(level, targetPos) < 0) continue;

                    BlockEntity blockEntity = level.getBlockEntity(targetPos);
                    state.getBlock().playerDestroy(level, player, targetPos, state, blockEntity, context.getItemInHand());
                    level.removeBlock(targetPos, false);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }
}

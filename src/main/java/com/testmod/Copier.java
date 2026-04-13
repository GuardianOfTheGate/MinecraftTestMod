package com.testmod;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.function.Consumer;


public class Copier extends Item {
    public static final Logger LOGGER = LoggerFactory.getLogger("template-mod-3");

    // Bedrock as "empty/unset" sentinel value
    private final BlockState[][][] recordedBlocks = new BlockState[5][5][5];

    public Copier(Item.Properties properties) {
        super(properties);
        // Fill with bedrock as default
        for (BlockState[][] plane : recordedBlocks)
            for (BlockState[] row : plane)
                Arrays.fill(row, Blocks.BEDROCK.defaultBlockState());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        textConsumer.accept(Component.translatable("Right-Click to copy all Blocks in the Grid").withStyle(ChatFormatting.GRAY));
        textConsumer.accept(Component.translatable("Left-Click to place copied Blocks inside the Grid").withStyle(ChatFormatting.GRAY));
    }



    public BlockPos getTargetPos(BlockPos origin, Direction facing, int u, int v, int depth) {
        return switch (facing.getAxis()) {
            case Z -> origin.offset(u, v, facing.getStepZ() * (depth + 1));
            case X -> origin.offset(facing.getStepX() * (depth + 1), v, u);
            default -> origin;
        };
    }

    @Override
    public @NonNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (level.isClientSide() || player == null) return InteractionResult.PASS;

        Direction facing = player.getDirection();

        for (int depth = 0; depth < 5; depth++) {
            for (int u = -2; u <= 2; u++) {
                for (int v = 0; v < 5; v++) {
                    BlockPos targetPos = getTargetPos(pos, facing, u, v, depth);
                    recordedBlocks[u + 2][v][depth] = level.getBlockState(targetPos);
                }
            }
        }

        LOGGER.info("Recorded Blocks");
        return InteractionResult.SUCCESS;
    }

    public InteractionResult onLeftClick(Player player, Level level, BlockPos pos) {
        if (level.isClientSide() || player == null) return InteractionResult.PASS;

        Direction facing = player.getDirection();

        for (int depth = 0; depth < 5; depth++) {
            for (int u = -2; u <= 2; u++) {
                for (int v = 0; v < 5; v++) {
                    BlockState stored = recordedBlocks[u + 2][v][depth];

                    // Skip if nothing was recorded (still bedrock sentinel)
                    if (stored.is(Blocks.BEDROCK)) continue;

                    BlockPos targetPos = getTargetPos(pos, facing, u, v, depth);
                    level.setBlock(targetPos, stored, 3);
                }
            }
        }

        LOGGER.info("Placed Blocks");
        return InteractionResult.SUCCESS;
    }
}

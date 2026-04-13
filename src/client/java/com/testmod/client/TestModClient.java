package com.testmod.client;

import com.testmod.client.render.BlockHighlightRenderer;
import com.testmod.client.render.HighlightedBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import com.testmod.Copier;

public class TestModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

		ClientTickEvents.END_CLIENT_TICK.register(client ->{
			if (client.player == null || client.level == null){
				HighlightedBlocks.clear();
				return;
			}
			if(!(client.player.getMainHandItem().getItem() instanceof Copier)){
				HighlightedBlocks.clear();
				return;
			}
			HitResult hit = client.hitResult;
			if (hit instanceof BlockHitResult blockHit
					&& hit.getType() == HitResult.Type.BLOCK
					&& client.player.isWithinBlockInteractionRange(blockHit.getBlockPos(), 0.5)) {

				HighlightedBlocks.update(blockHit.getBlockPos(), client.player.getDirection());
			}
			else{
				HighlightedBlocks.clear();
			}
		});

		WorldRenderEvents.AFTER_ENTITIES.register(
				BlockHighlightRenderer::render
		);

	}
}
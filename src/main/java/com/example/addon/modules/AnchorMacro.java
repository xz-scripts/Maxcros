package com.example.addon.modules;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import com.example.addon.Addon;

public class AnchorMacro extends Module {

    public AnchorMacro() {
        super(Addon.CATEGORY, "anchor-macro", "Automatically charges and detonates placed respawn anchors.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;

        if (mc.crosshairTarget instanceof BlockHitResult hitResult) {
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                var blockState = mc.world.getBlockState(hitResult.getBlockPos());

                if (blockState.isOf(Blocks.RESPAWN_ANCHOR)) {
                    FindItemResult glowstone = InvUtils.findInHotbar(Items.GLOWSTONE);

                    if (glowstone.found()) {
                        InvUtils.swap(glowstone.slot(), false);
                        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);

                        int safeSlot = getSafeSlot();
                        if (safeSlot != -1) {
                            InvUtils.swap(safeSlot, false);
                        }

                        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);
                    }
                }
            }
        }
    }

    private int getSafeSlot() {
        for (int i = 0; i < 9; i++) {
            if (!mc.player.getInventory().getStack(i).isOf(Items.GLOWSTONE)) {
                return i;
            }
        }
        return -1;
    }
}

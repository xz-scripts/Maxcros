package com.example.addon.modules;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import com.example.addon.Addon;

public class CrystalMacro extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Keybind> crystalKeybind = sgGeneral.add(new KeybindSetting.Builder()
        .name("crystal-keybind")
        .description("Keybind to execute the crystal sequence.")
        .defaultValue(Keybind.none())
        .build()
    );

    private boolean wasPressed = false;

    public CrystalMacro() {
        super(Addon.CATEGORY, "crystal-macro", "Places obsidian or places and detonates end crystals on keybind.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;

        boolean isPressed = crystalKeybind.get().isPressed();

        if (isPressed && !wasPressed) {
            executeMacro();
        }

        wasPressed = isPressed;
    }

    private void executeMacro() {
        if (!(mc.crosshairTarget instanceof BlockHitResult hitResult)) return;
        if (hitResult.getType() != HitResult.Type.BLOCK) return;

        BlockPos targetPos = hitResult.getBlockPos();
        var blockState = mc.world.getBlockState(targetPos);

        if (blockState.isOf(Blocks.OBSIDIAN) || blockState.isOf(Blocks.BEDROCK)) {
            FindItemResult crystal = InvUtils.findInHotbar(Items.END_CRYSTAL);
            if (crystal.found()) {
                InvUtils.swap(crystal.slot(), false);
                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);
                mc.interactionManager.attackBlock(targetPos.up(), hitResult.getSide());
            }
        } else {
            FindItemResult obsidian = InvUtils.findInHotbar(Items.OBSIDIAN);
            if (obsidian.found()) {
                InvUtils.swap(obsidian.slot(), false);
                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);

                FindItemResult crystal = InvUtils.findInHotbar(Items.END_CRYSTAL);
                if (crystal.found()) {
                    InvUtils.swap(crystal.slot(), false);
                    BlockHitResult obsHitResult = new BlockHitResult(
                        hitResult.getPos(),
                        hitResult.getSide(),
                        targetPos.offset(hitResult.getSide()),
                        false
                    );
                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, obsHitResult);
                    mc.interactionManager.attackBlock(targetPos.offset(hitResult.getSide()).up(), hitResult.getSide());
                }
            }
        }
    }
}

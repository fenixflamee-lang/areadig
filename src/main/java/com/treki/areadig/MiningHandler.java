package com.treki.areadig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MiningHandler {
    private boolean busy = false;

    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent event) {
        if (busy) return;
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (player.isShiftKeyDown()) return; // Shift = single block

        ItemStack tool = player.getMainHandItem();
        int size = getSize(tool);
        if (size < 2) return;

        BlockPos origin = event.getPos();

        // Which face did the player hit?
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getViewVector(1.0F);
        double reach = player.getBlockReach() + 1.0D;
        BlockHitResult hit = level.clip(new ClipContext(eye, eye.add(look.scale(reach)),
                ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        if (hit.getType() != HitResult.Type.BLOCK || !hit.getBlockPos().equals(origin)) return;

        Direction.Axis axis = hit.getDirection().getAxis();
        Direction.Axis a1;
        Direction.Axis a2;
        switch (axis) {
            case X -> { a1 = Direction.Axis.Y; a2 = Direction.Axis.Z; }
            case Y -> { a1 = Direction.Axis.X; a2 = Direction.Axis.Z; }
            default -> { a1 = Direction.Axis.X; a2 = Direction.Axis.Y; }
        }
        Direction d1 = Direction.get(Direction.AxisDirection.POSITIVE, a1);
        Direction d2 = Direction.get(Direction.AxisDirection.POSITIVE, a2);

        Vec3 loc = hit.getLocation();
        double f1 = loc.get(a1) - origin.get(a1);
        double f2 = loc.get(a2) - origin.get(a2);
        int[] r1 = range(size, f1);
        int[] r2 = range(size, f2);

        busy = true;
        try {
            for (int i = r1[0]; i <= r1[1]; i++) {
                for (int j = r2[0]; j <= r2[1]; j++) {
                    if (i == 0 && j == 0) continue; // origin block breaks normally
                    if (tool.isEmpty()) return;

                    BlockPos pos = origin.relative(d1, i).relative(d2, j);
                    BlockState state = level.getBlockState(pos);
                    if (state.isAir()) continue;
                    if (state.getDestroySpeed(level, pos) < 0) continue; // bedrock etc.
                    if (tool.getDestroySpeed(state) <= 1.0F) continue;   // tool isn't effective here

                    player.gameMode.destroyBlock(pos);
                    tool = player.getMainHandItem();
                }
            }
        } finally {
            busy = false;
        }
    }

    private static int getSize(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.AREA.get(), stack);
        return level > 0 ? AreaEnchantment.sizeFor(level) : 0;
    }

    /** Offset range along one axis. Even sizes lean toward the side of the block that was hit. */
    private static int[] range(int n, double frac) {
        if (n % 2 == 1) {
            int h = (n - 1) / 2;
            return new int[]{-h, h};
        }
        int h = n / 2;
        return frac >= 0.5 ? new int[]{-(h - 1), h} : new int[]{-h, h - 1};
    }
}

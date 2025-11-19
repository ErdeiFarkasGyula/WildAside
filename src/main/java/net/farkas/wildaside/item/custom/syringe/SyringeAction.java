package net.farkas.wildaside.item.custom.syringe;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class SyringeAction {
    public enum Type { PULL_ENTITY, PULL_WATER, PUSH, TRANSFER_TO_HOLDER, PULL_AIR }

    public Type type;
    public int targetEntityId = -1;
    public net.minecraft.world.phys.HitResult hit;

    private SyringeAction(Type t) { this.type = t; }
    public static SyringeAction pullEntity(int id) { SyringeAction s = new SyringeAction(Type.PULL_ENTITY); s.targetEntityId = id; return s; }
    public static SyringeAction pullWater(net.minecraft.world.phys.HitResult hit) { SyringeAction s = new SyringeAction(Type.PULL_WATER); s.hit = hit; return s; }
    public static SyringeAction pullAir() { return new SyringeAction(Type.PULL_AIR); }
    public static SyringeAction push() { return new SyringeAction(Type.PUSH); }
    public static SyringeAction transferToHolder() { return new SyringeAction(Type.TRANSFER_TO_HOLDER); }

    public void execute(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        ItemStack stack = player.getInventory().getSelected();

        switch(type) {
            case PULL_ENTITY:
                Entity e = level.getEntity(targetEntityId);
                if (e instanceof LivingEntity living) {
                    SyringeLogic.extractFromEntity(stack, living);
                    player.playSound(net.minecraft.sounds.SoundEvents.BOTTLE_FILL, 1f, 1f);
                }
                break;
            case PULL_WATER:
                SyringeLogic.extractFromWater(stack);
                player.playSound(net.minecraft.sounds.SoundEvents.BOTTLE_FILL, 1f, 1f);
                break;
            case PUSH:
                SyringeLogic.push(stack);
                player.playSound(net.minecraft.sounds.SoundEvents.BOTTLE_EMPTY, 1f, 1f);
                break;
            case TRANSFER_TO_HOLDER:
                ItemStack off = player.getItemInHand(InteractionHand.OFF_HAND);
                SyringeLogic.transferToHolder(stack, off);
                player.playSound(net.minecraft.sounds.SoundEvents.BOTTLE_EMPTY, 1f, 1f);
                break;
            case PULL_AIR:
                player.playSound(SoundEvents.BOTTLE_EMPTY, 1f, 1f);
                break;
        }
    }
}
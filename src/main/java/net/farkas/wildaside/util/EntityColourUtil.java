package net.farkas.wildaside.util;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.common.ForgeSpawnEggItem;

public class EntityColourUtil {
    public static int[] getSpawnEggColors(EntityType<?> type) {
        for (SpawnEggItem egg : SpawnEggItem.eggs()) {
            if (egg.getType(null) == type) {
                return new int[] { egg.getColor(0), egg.getColor(1) };
            }
        }

        return new int[] { 0xAAAAAA, 0x555555 };
    }
}

package net.farkas.wildaside.item.custom.syringe;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WildAside.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class SyringeColourHandler {
    @SubscribeEvent
    public static void register(RegisterColorHandlersEvent.Item event) {
        event.getItemColors().register((stack, tintIndex) -> {
            CompoundTag tag = stack.getTag();
            if (tag == null) return 0xFFFFFF;
            if (tintIndex == 1) {
                if (tag.getBoolean(SyringeItem.TAG_WATER)) return 0x3F76E4;
                if (!tag.contains(SyringeItem.TAG_SOURCE)) return 0xFFFFFF;

                String src = tag.getString(SyringeItem.TAG_SOURCE);
                var entityType = EntityType.byString(src).orElse(null);
                if (entityType == null) return 0xFFFFFF;
                SpawnEggItem egg = SpawnEggItem.byId(entityType);
                if (egg == null) return 0xFFFFFF;

                int base = egg.getColor(0);
                int age = tag.getInt(SyringeItem.TAG_BLOOD_AGE);
                float clot = Math.min(1f, (float) age / (float) SyringeItem.CLOT_TICKS);

                int r = (int) (((base >> 16) & 0xFF) * (1f - clot * 0.6f));
                int g = (int) (((base >> 8) & 0xFF) * (1f - clot * 0.6f));
                int b = (int) (((base) & 0xFF) * (1f - clot * 0.6f));
                return (r << 16) | (g << 8) | b;
            } else if (tintIndex == 2) {

                int c = tag.getInt(SyringeItem.TAG_CONTAM);
                if (c <= 0) return 0xFFFFFF;

                int alpha = 120 + c * 30;
                int r = 80, g = 30, b = 20;
                return (r << 16) | (g << 8) | b;
            }
            return 0xFFFFFF;

        }, ModItems.SYRINGE.get());
    }
}

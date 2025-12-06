package net.farkas.wildaside.item.custom;

import net.farkas.wildaside.dna.DnaConstants;
import net.farkas.wildaside.item.ModItems;
import net.farkas.wildaside.util.ContaminationHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

import static net.farkas.wildaside.dna.DnaConstants.BLOOD_CLOTTING_TIME;
import static net.farkas.wildaside.dna.DnaConstants.BLOOD_CLOTTING_TIME_WITH_AGENT;

public class VibrionItem extends Item {
    public static final int GRASS_SPREAD_WIDTH = 8;
    public static final int GRASS_SPREAD_HEIGHT = 2;
    public static final int GRASS_COUNT_MULTIPLIER = 8;

    public VibrionItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (!pLevel.isClientSide() && pUsedHand == InteractionHand.MAIN_HAND) {
            ItemStack mainhandStack = pPlayer.getItemInHand(InteractionHand.MAIN_HAND);
            ItemStack offhandStack = pPlayer.getItemInHand(InteractionHand.OFF_HAND);

            if (offhandStack.is(ModItems.DNA_HOLDER.get())) {
                CompoundTag tag = offhandStack.getOrCreateTag();
                tag.putLong(BLOOD_CLOTTING_TIME, BLOOD_CLOTTING_TIME_WITH_AGENT);
                offhandStack.setTag(tag);

                int count = mainhandStack.getCount();
                if (count <= 1) {
                    pPlayer.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                }
                else {
                    pPlayer.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(mainhandStack.getItem(), mainhandStack.getCount() - 1));
                }

                pPlayer.setItemInHand(InteractionHand.OFF_HAND, offhandStack);

                pLevel.playSound(pPlayer, pPlayer.blockPosition(), SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1f, 1f);
            }
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }

    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        BlockPos blockpos1 = blockpos.relative(pContext.getClickedFace());

        if (applyBonemeal(pContext.getItemInHand(), level, blockpos, pContext.getPlayer())) {
            if (!level.isClientSide) {
                level.levelEvent(1505, blockpos, 0);
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        else {
            BlockState blockstate = level.getBlockState(blockpos);
            boolean flag = blockstate.isFaceSturdy(level, blockpos, pContext.getClickedFace());
            if (flag && growWaterPlant(pContext.getItemInHand(), level, blockpos1, pContext.getClickedFace())) {
                if (!level.isClientSide) {
                    level.levelEvent(1505, blockpos1, 0);
                }

                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            else {
                return InteractionResult.PASS;
            }
        }
    }

    @Deprecated //Forge: Use Player/Hand version
    public static boolean growCrop(ItemStack pStack, Level pLevel, BlockPos pPos) {
        if (pLevel instanceof net.minecraft.server.level.ServerLevel)
            return applyBonemeal(pStack, pLevel, pPos, net.minecraftforge.common.util.FakePlayerFactory.getMinecraft((net.minecraft.server.level.ServerLevel) pLevel));
        return false;
    }

    public static boolean applyBonemeal(ItemStack pStack, Level pLevel, BlockPos pPos, net.minecraft.world.entity.player.Player player) {
        BlockState blockstate = pLevel.getBlockState(pPos);
        int hook = net.minecraftforge.event.ForgeEventFactory.onApplyBonemeal(player, pLevel, pPos, blockstate, pStack);
        if (hook != 0) return hook > 0;
        if (blockstate.getBlock() instanceof BonemealableBlock) {
            BonemealableBlock bonemealableblock = (BonemealableBlock) blockstate.getBlock();
            if (bonemealableblock.isValidBonemealTarget(pLevel, pPos, blockstate, pLevel.isClientSide)) {
                if (pLevel instanceof ServerLevel) {
                    if (bonemealableblock.isBonemealSuccess(pLevel, pLevel.random, pPos, blockstate)) {
                        bonemealableblock.performBonemeal((ServerLevel) pLevel, pLevel.random, pPos, blockstate);
                    }

                    pStack.shrink(1);
                }

                return true;
            }
        }

        return false;
    }

    public static boolean growWaterPlant(ItemStack pStack, Level pLevel, BlockPos pPos, @Nullable Direction pClickedSide) {
        if (pLevel.getBlockState(pPos).is(Blocks.WATER) && pLevel.getFluidState(pPos).getAmount() == 8) {
            if (pLevel instanceof ServerLevel) {
                RandomSource randomsource = pLevel.getRandom();

                label78:
                for (int i = 0; i < 128; ++i) {
                    BlockPos blockpos = pPos;
                    BlockState blockstate = Blocks.SEAGRASS.defaultBlockState();

                    for (int j = 0; j < i / 16; ++j) {
                        blockpos = blockpos.offset(randomsource.nextInt(3) - 1, (randomsource.nextInt(3) - 1) * randomsource.nextInt(3) / 2, randomsource.nextInt(3) - 1);
                        if (pLevel.getBlockState(blockpos).isCollisionShapeFullBlock(pLevel, blockpos)) {
                            continue label78;
                        }
                    }

                    Holder<Biome> holder = pLevel.getBiome(blockpos);
                    if (holder.is(BiomeTags.PRODUCES_CORALS_FROM_BONEMEAL)) {
                        if (i == 0 && pClickedSide != null && pClickedSide.getAxis().isHorizontal()) {
                            blockstate = BuiltInRegistries.BLOCK.getTag(BlockTags.WALL_CORALS).flatMap((p_204098_) -> {
                                return p_204098_.getRandomElement(pLevel.random);
                            }).map((p_204100_) -> {
                                return p_204100_.value().defaultBlockState();
                            }).orElse(blockstate);
                            if (blockstate.hasProperty(BaseCoralWallFanBlock.FACING)) {
                                blockstate = blockstate.setValue(BaseCoralWallFanBlock.FACING, pClickedSide);
                            }
                        }
                        else if (randomsource.nextInt(4) == 0) {
                            blockstate = BuiltInRegistries.BLOCK.getTag(BlockTags.UNDERWATER_BONEMEALS).flatMap((p_204091_) -> {
                                return p_204091_.getRandomElement(pLevel.random);
                            }).map((p_204095_) -> {
                                return p_204095_.value().defaultBlockState();
                            }).orElse(blockstate);
                        }
                    }

                    if (blockstate.is(BlockTags.WALL_CORALS, (p_204093_) -> {
                        return p_204093_.hasProperty(BaseCoralWallFanBlock.FACING);
                    })) {
                        for (int k = 0; !blockstate.canSurvive(pLevel, blockpos) && k < 4; ++k) {
                            blockstate = blockstate.setValue(BaseCoralWallFanBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(randomsource));
                        }
                    }

                    if (blockstate.canSurvive(pLevel, blockpos)) {
                        BlockState blockstate1 = pLevel.getBlockState(blockpos);
                        if (blockstate1.is(Blocks.WATER) && pLevel.getFluidState(blockpos).getAmount() == 8) {
                            pLevel.setBlock(blockpos, blockstate, 3);
                        }
                        else if (blockstate1.is(Blocks.SEAGRASS) && randomsource.nextInt(10) == 0) {
                            ((BonemealableBlock) Blocks.SEAGRASS).performBonemeal((ServerLevel) pLevel, randomsource, blockpos, blockstate1);
                        }
                    }
                }

                pStack.shrink(1);
            }
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        if (!pLevel.isClientSide) {
            ContaminationHandler.addDose(pLivingEntity, pLevel.random.nextInt(250, 750));
        }

        return super.finishUsingItem(pStack, pLevel, pLivingEntity);
    }
}

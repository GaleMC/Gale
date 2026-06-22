package org.dreeam.leaf.util;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

/**
 * Masks for predicates on {@link BlockBehaviour.BlockStateBase},
 * allowing for {@link BlockBehaviour.BlockStateBase} to be quickly tested for these predicates
 * by checking its {@link BlockBehaviour.BlockStateBase#predicateFlags}.
 */
public final class BlockMasks {

    /**
     * {@link BlockTags#WALLS}.
     */
    public static final int WALLS_TAG = 0x01;

    /**
     * {@link BlockTags#FENCES}.
     */
    public static final int FENCES_TAG = 0x02;

    /**
     * {@link BlockTags#CLIMBABLE}.
     */
    public static final int CLIMBABLE_TAG = 0x04;

    /**
     * Instance of {@link PowderSnowBlock}.
     */
    public static final int POWDER_SNOW_CLASS = 0x08;

    /**
     * Instance of {@link FenceGateBlock}.
     */
    public static final int FENCE_GATE_CLASS = 0x10;

    /**
     * {@link FlowingFluid#canHoldAnyFluid}.
     */
    public static final int CAN_HOLD_ANY_FLUID = 0x100;

    /**
     * {@link BlockTags#CAULDRONS}.
     */
    public static final int CAULDRONS_TAG = 0x200;

    /**
     * Instance of {@link TrapDoorBlock}
     * and {@link TrapDoorBlock#OPEN} set to {@code true}.
     */
    public static final int TRAP_DOOR_CLASS_AND_OPEN_PROPERTY_IS_TRUE = 0x400;

    /**
     * {@link BlockTags#CAN_GLIDE_THROUGH}.
     */
    public static final int CAN_GLIDE_THROUGH_TAG = 0x800;

    /**
     * {@link #WALLS_TAG} or {@link #FENCE_GATE_CLASS}.
     */
    public static final int WALLS_TAG_OR_FENCE_GATE_CLASS = WALLS_TAG | FENCE_GATE_CLASS;

    /**
     * {@link #CLIMBABLE_TAG} or {@link #POWDER_SNOW_CLASS}.
     */
    public static final int CLIMBABLE_TAG_OR_POWDER_SNOW_CLASS = CLIMBABLE_TAG | POWDER_SNOW_CLASS;

    /**
     * {@link #CLIMBABLE_TAG} or {@link #TRAP_DOOR_CLASS_AND_OPEN_PROPERTY_IS_TRUE}.
     */
    public static final int CLIMBABLE_TAG_OR_TRAP_DOOR_CLASS_AND_OPEN_PROPERTY_IS_TRUE = CLIMBABLE_TAG | TRAP_DOOR_CLASS_AND_OPEN_PROPERTY_IS_TRUE;

    public static int init(final BlockState state) {
        int i = 0;
        i |= state.is(BlockTags.WALLS) ? WALLS_TAG : 0;
        i |= state.is(BlockTags.FENCES) ? FENCES_TAG : 0;
        i |= state.is(BlockTags.CLIMBABLE) ? CLIMBABLE_TAG : 0;
        i |= state.is(BlockTags.CAULDRONS) ? CAULDRONS_TAG : 0;
        i |= state.is(BlockTags.CAN_GLIDE_THROUGH) ? CAN_GLIDE_THROUGH_TAG : 0;
        i |= state.getBlock() instanceof PowderSnowBlock ? POWDER_SNOW_CLASS : 0;
        i |= state.getBlock() instanceof FenceGateBlock ? FENCE_GATE_CLASS : 0;
        i |= state.getBlock() instanceof TrapDoorBlock && state.getValue(TrapDoorBlock.OPEN) ? TRAP_DOOR_CLASS_AND_OPEN_PROPERTY_IS_TRUE : 0;
        i |= FlowingFluid.canHoldAnyFluid(state) ? CAN_HOLD_ANY_FLUID : 0;
        return i;
    }

}

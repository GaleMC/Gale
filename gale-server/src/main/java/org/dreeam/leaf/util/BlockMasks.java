package org.dreeam.leaf.util;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.pathfinder.NodeEvaluator;

/**
 * Masks for predicates on {@link BlockBehaviour.BlockStateBase},
 * allowing for {@link BlockBehaviour.BlockStateBase} to be quickly tested for these predicates
 * by checking its {@link BlockBehaviour.BlockStateBase#predicateFlags}.
 */
public final class BlockMasks {

    /**
     * {@link BlockTags#WALLS}.
     */
    public static final int WALLS_TAG = 1 << 0;

    /**
     * {@link BlockTags#FENCES}.
     */
    public static final int FENCES_TAG = 1 << 1;

    /**
     * {@link BlockTags#CLIMBABLE}.
     */
    public static final int CLIMBABLE_TAG = 1 << 2;

    /**
     * Instance of {@link PowderSnowBlock}.
     */
    public static final int POWDER_SNOW_CLASS = 1 << 3;

    /**
     * Instance of {@link FenceGateBlock}.
     */
    public static final int FENCE_GATE_CLASS = 1 << 4;

    /**
     * {@link FlowingFluid#canHoldAnyFluid}.
     */
    public static final int CAN_HOLD_ANY_FLUID = 1 << 5;

    /**
     * {@link BlockTags#CAULDRONS}.
     */
    public static final int CAULDRONS_TAG = 1 << 6;

    /**
     * Instance of {@link TrapDoorBlock} and {@link TrapDoorBlock#OPEN} set to {@code true}.
     */
    public static final int TRAP_DOOR_CLASS_AND_OPEN_PROPERTY_IS_TRUE = 1 << 7;

    /**
     * {@link BlockTags#CAN_GLIDE_THROUGH}.
     */
    public static final int CAN_GLIDE_THROUGH_TAG = 1 << 8;

    /**
     * {@link BlockTags#DOORS}.
     */
    public static final int DOORS_TAG = 1 << 9;

    /**
     * {@link BlockTags#BEDS}.
     */
    public static final int BEDS_TAG = 1 << 10;

    /**
     * {@link BlockTags#BEDS} and {@link BedBlock#OCCUPIED} set to true.
     */
    public static final int BEDS_TAG_AND_OCCUPIED_PROPERTY_IS_TRUE = 1 << 11;

    /**
     * {@link NodeEvaluator#isBurningBlock(BlockState)}.
     */
    public static final int IS_BURNING_BLOCK = 1 << 12;

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

    /**
     * {@link #DOORS_TAG} or {@link #FENCES_TAG}.
     */
    public static final int DOORS_TAG_OR_FENCES_TAG = DOORS_TAG | FENCES_TAG;

    /**
     * Used in {@link BlockBehaviour.BlockStateBase#isUnoccupiedBed()}.
     */
    public static final int UNOCCUPIED_BED_MASK = BEDS_TAG | BEDS_TAG_AND_OCCUPIED_PROPERTY_IS_TRUE;

    public static int init(final BlockState state) {
        int i = 0;
        i |= state.is(BlockTags.WALLS) ? WALLS_TAG : 0;
        i |= state.is(BlockTags.FENCES) ? FENCES_TAG : 0;
        i |= state.is(BlockTags.CLIMBABLE) ? CLIMBABLE_TAG : 0;
        i |= state.is(BlockTags.CAULDRONS) ? CAULDRONS_TAG : 0;
        i |= state.is(BlockTags.CAN_GLIDE_THROUGH) ? CAN_GLIDE_THROUGH_TAG : 0;
        i |= state.is(BlockTags.DOORS) ? DOORS_TAG : 0;
        i |= state.is(BlockTags.BEDS) ? BEDS_TAG : 0;
        i |= state.is(BlockTags.BEDS) && state.getOptionalValue(BedBlock.OCCUPIED).orElse(false) ? BEDS_TAG_AND_OCCUPIED_PROPERTY_IS_TRUE : 0;
        i |= NodeEvaluator.gale$computeIsBurningBlock(state) ? IS_BURNING_BLOCK : 0;
        i |= state.getBlock() instanceof PowderSnowBlock ? POWDER_SNOW_CLASS : 0;
        i |= state.getBlock() instanceof FenceGateBlock ? FENCE_GATE_CLASS : 0;
        i |= state.getBlock() instanceof TrapDoorBlock && state.getValue(TrapDoorBlock.OPEN) ? TRAP_DOOR_CLASS_AND_OPEN_PROPERTY_IS_TRUE : 0;
        i |= FlowingFluid.canHoldAnyFluid(state) ? CAN_HOLD_ANY_FLUID : 0;
        return i;
    }

}

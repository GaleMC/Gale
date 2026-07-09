package org.dreeam.leaf.util;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.sensing.PiglinSpecificSensor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
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
     * {@link NodeEvaluator#gale$precompute_isBurningBlock_compute}.
     */
    public static final int IS_BURNING_BLOCK = 1 << 12;

    /**
     * {@link BlockTags#MOB_INTERACTABLE_DOORS} and instance of {@link DoorBlock}.
     */
    public static final int MOB_INTERACTABLE_DOORS_TAG_AND_DOOR_CLASS = 1 << 13;

    /**
     * {@link Blocks#FURNACE} and {@link FurnaceBlock#LIT} set to true,
     * or {@link BlockTags#BEDS} and {@link BedBlock#PART} is not set to {@link BedPart#HEAD}.
     */
    public static final int CAT_SIT_VALID_WARM_TARGET = 1 << 14;

    /**
     * {@link BlockTags#SNAPS_GOAT_HORN}.
     */
    public static final int SNAPS_GOAT_HORN_TAG = 1 << 15;

    /**
     * {@link BlockTags#SUPPORTS_FROGSPAWN}.
     */
    public static final int SUPPORTS_FROGSPAWN_TAG = 1 << 16;

    /**
     * {@link BlockTags#EDIBLE_FOR_SHEEP}.
     */
    public static final int EDIBLE_FOR_SHEEP_TAG = 1 << 17;

    /**
     * {@link BlockTags#HOGLIN_REPELLENTS}.
     */
    public static final int HOGLIN_REPELLENTS_TAG = 1 << 18;

    /**
     * {@link PiglinSpecificSensor#gale$precompute_isValidRepellent_compute}.
     */
    public static final int IS_VALID_PIGLIN_REPELLENT = 1 << 19;

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
        i |= state.getBlock() instanceof PowderSnowBlock ? POWDER_SNOW_CLASS : 0;
        i |= state.getBlock() instanceof FenceGateBlock ? FENCE_GATE_CLASS : 0;
        i |= FlowingFluid.canHoldAnyFluid(state) ? CAN_HOLD_ANY_FLUID : 0;
        i |= state.is(BlockTags.CAULDRONS) ? CAULDRONS_TAG : 0;
        i |= state.getBlock() instanceof TrapDoorBlock && state.getValue(TrapDoorBlock.OPEN) ? TRAP_DOOR_CLASS_AND_OPEN_PROPERTY_IS_TRUE : 0;
        i |= state.is(BlockTags.CAN_GLIDE_THROUGH) ? CAN_GLIDE_THROUGH_TAG : 0;
        i |= state.is(BlockTags.DOORS) ? DOORS_TAG : 0;
        i |= state.is(BlockTags.BEDS) ? BEDS_TAG : 0;
        i |= state.is(BlockTags.BEDS) && state.getOptionalValue(BedBlock.OCCUPIED).orElse(false) ? BEDS_TAG_AND_OCCUPIED_PROPERTY_IS_TRUE : 0;
        i |= NodeEvaluator.gale$precompute_isBurningBlock_compute(state) ? IS_BURNING_BLOCK : 0;
        i |= state.is(BlockTags.MOB_INTERACTABLE_DOORS) && state.getBlock() instanceof DoorBlock ? MOB_INTERACTABLE_DOORS_TAG_AND_DOOR_CLASS : 0;
        i |= state.is(Blocks.FURNACE) && state.getValue(FurnaceBlock.LIT) || (state.is(BlockTags.BEDS) && state.getOptionalValue(BedBlock.PART).map(v -> v != BedPart.HEAD).orElse(true)) ? CAT_SIT_VALID_WARM_TARGET : 0;
        i |= state.is(BlockTags.SNAPS_GOAT_HORN) ? SNAPS_GOAT_HORN_TAG : 0;
        i |= state.is(BlockTags.SUPPORTS_FROGSPAWN) ? SUPPORTS_FROGSPAWN_TAG : 0;
        i |= state.is(BlockTags.EDIBLE_FOR_SHEEP) ? EDIBLE_FOR_SHEEP_TAG : 0;
        i |= state.is(BlockTags.HOGLIN_REPELLENTS) ? HOGLIN_REPELLENTS_TAG : 0;
        i |= PiglinSpecificSensor.gale$precompute_isValidRepellent_compute(state) ? IS_VALID_PIGLIN_REPELLENT : 0;
        return i;
    }

}

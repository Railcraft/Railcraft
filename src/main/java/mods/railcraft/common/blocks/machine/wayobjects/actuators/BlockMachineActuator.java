/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.wayobjects.actuators;

import mods.railcraft.api.tracks.ISwitchActuator;
import mods.railcraft.common.blocks.machine.BlockMachine;
import mods.railcraft.common.blocks.machine.RailcraftBlockMetadata;
import mods.railcraft.common.items.ItemCircuit;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Optional;

/**
 * Created by CovertJaguar on 9/8/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftBlockMetadata(variant = ActuatorVariant.class)
public class BlockMachineActuator extends BlockMachine<ActuatorVariant> {
    public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class, EnumFacing.HORIZONTALS);
    public static final PropertyEnum<ISwitchActuator.ArrowDirection> RED_FLAG = PropertyEnum.create("red_flag", ISwitchActuator.ArrowDirection.class);
    public static final PropertyEnum<ISwitchActuator.ArrowDirection> WHITE_FLAG = PropertyEnum.create("white_flag", ISwitchActuator.ArrowDirection.class);
    public static final PropertyBool THROWN = PropertyBool.create("thrown");

    public BlockMachineActuator() {
        super(false);
        setDefaultState(getDefaultState()
                .withProperty(FACING, EnumFacing.NORTH)
                .withProperty(RED_FLAG, ISwitchActuator.ArrowDirection.NORTH_SOUTH)
                .withProperty(WHITE_FLAG, ISwitchActuator.ArrowDirection.EAST_WEST)
                .withProperty(THROWN, false)
        );
        setCreativeTab(CreativeTabs.TRANSPORTATION);
        setSoundType(SoundType.METAL);
        setResistance(50);
    }

    @Override
    public float getBlockHardness(IBlockState state, World worldIn, BlockPos pos) {
        return 8;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getVariantProperty(), FACING, THROWN, RED_FLAG, WHITE_FLAG);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        state = super.getActualState(state, worldIn, pos);
        Optional<TileActuatorBase> tile = WorldPlugin.getTileEntity(worldIn, pos, TileActuatorBase.class);
        state = state.withProperty(FACING, tile.map(TileActuatorBase::getFacing).orElse(EnumFacing.NORTH));
        state = state.withProperty(RED_FLAG, tile.map(TileActuatorBase::getRedArrowRenderState).orElse(ISwitchActuator.ArrowDirection.NORTH_SOUTH));
        state = state.withProperty(WHITE_FLAG, tile.map(TileActuatorBase::getWhiteArrowRenderState).orElse(ISwitchActuator.ArrowDirection.EAST_WEST));
        state = state.withProperty(THROWN, tile.map(t -> t.shouldSwitch(null)).orElse(false));
        return state;
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World worldIn, BlockPos pos) {
        Optional<TileActuatorBase> tile = WorldPlugin.getTileEntity(worldIn, pos, TileActuatorBase.class);
        boolean thrown = tile.map(t -> t.shouldSwitch(null)).orElse(false);
        return thrown ? PowerPlugin.FULL_POWER : PowerPlugin.NO_POWER;
    }

    @Override
    public void defineRecipes() {
        // Define Switch Lever
        ActuatorVariant actuator = ActuatorVariant.LEVER;
        if (actuator.isAvailable()) {
            ItemStack stack = actuator.getItem();
            CraftingPlugin.addRecipe(stack,
                    "RBW",
                    "PLI",
                    'W', "dyeWhite",
                    'I', "ingotIron",
                    'L', Blocks.LEVER,
                    'P', Blocks.PISTON,
                    'B', "dyeBlack",
                    'R', "dyeRed");
            CraftingPlugin.addRecipe(stack,
                    "RBW",
                    "ILP",
                    'W', "dyeWhite",
                    'I', "ingotIron",
                    'L', Blocks.LEVER,
                    'P', Blocks.PISTON,
                    'B', "dyeBlack",
                    'R', "dyeRed");
        }

        // Define Switch Motor
        actuator = ActuatorVariant.MOTOR;
        if (actuator.isAvailable()) {
            ItemStack stack = actuator.getItem();
            CraftingPlugin.addRecipe(stack,
                    "RBW",
                    "PCI",
                    'W', "dyeWhite",
                    'I', "ingotIron",
                    'P', Blocks.PISTON,
                    'C', RailcraftItems.CIRCUIT.getRecipeObject(ItemCircuit.EnumCircuit.RECEIVER),
                    'B', "dyeBlack",
                    'R', "dyeRed");
            CraftingPlugin.addRecipe(stack,
                    "RBW",
                    "ICP",
                    'W', "dyeWhite",
                    'I', "ingotIron",
                    'P', Blocks.PISTON,
                    'C', RailcraftItems.CIRCUIT.getRecipeObject(ItemCircuit.EnumCircuit.RECEIVER),
                    'B', "dyeBlack",
                    'R', "dyeRed");
        }
    }
}

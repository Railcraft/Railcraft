/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.equipment;

import mods.railcraft.common.blocks.TileManager;
import mods.railcraft.common.blocks.charge.IChargeBlock;
import mods.railcraft.common.blocks.machine.BlockMachine;
import mods.railcraft.common.blocks.machine.RailcraftBlockMetadata;
import mods.railcraft.common.blocks.machine.charge.TileChargeFeeder;
import mods.railcraft.common.blocks.machine.interfaces.ITileCharge;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.modules.ModuleFactory;
import mods.railcraft.common.modules.RailcraftModuleManager;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.ai.TamingInteractHandler;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Created by CovertJaguar on 9/8/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftBlockMetadata(variant = EquipmentVariant.class)
public class BlockMachineEquipment extends BlockMachine<EquipmentVariant> implements IChargeBlock {
    public static final ChargeDef CHARGE_DEF = new ChargeDef(ConnectType.BLOCK, (world, pos) -> {
        TileEntity tileEntity = WorldPlugin.getBlockTile(world, pos);
        if (tileEntity instanceof TileRollingMachinePowered) {
            return ((TileChargeFeeder) tileEntity).getChargeBattery();
        }
        //noinspection ConstantConditions
        return null;
    });

    public BlockMachineEquipment() {
        super(true);
        setDefaultState(getDefaultState());
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getVariantProperty());
    }

    @Override
    public void finalizeDefinition() {
        MinecraftForge.EVENT_BUS.register(new TamingInteractHandler());
    }

    @Override
    public void defineRecipes() {
        EquipmentVariant rollingManual = EquipmentVariant.ROLLING_MACHINE_MANUAL;
        if (rollingManual.isAvailable()) {
            ItemStack stack = rollingManual.getStack();
            CraftingPlugin.addRecipe(stack,
                    "IPI",
                    "PCP",
                    "IPI",
                    'I', "gearBronze",
                    'P', Blocks.PISTON,
                    'C', "workbench");
        }

        EquipmentVariant rollingPowered = EquipmentVariant.ROLLING_MACHINE_POWERED;
        if (rollingPowered.isAvailable()) {
            ItemStack stack = rollingPowered.getStack();
            CraftingPlugin.addRecipe(stack,
                    "IPI",
                    "PCP",
                    "IPI",
                    'I', "gearSteel",
                    'P', Blocks.PISTON,
                    'C', "workbench");
        }

        EquipmentVariant feed = EquipmentVariant.FEED_STATION;
        if (feed.isAvailable()) {
            ItemStack stack = feed.getStack();
            CraftingPlugin.addRecipe(stack,
                    "PCP",
                    "CSC",
                    "PCP",
                    'P', "plankWood",
                    'S', RailcraftModuleManager.isModuleEnabled(ModuleFactory.class) && RailcraftItems.PLATE.isEnabled() ? RailcraftItems.PLATE.getRecipeObject(Metal.STEEL) : "blockIron",
                    'C', new ItemStack(Items.GOLDEN_CARROT));
        }

        EquipmentVariant alpha = EquipmentVariant.SMOKER;
        if (alpha.isAvailable()) {
            ItemStack stack = alpha.getStack();
            CraftingPlugin.addRecipe(stack,
                    " N ",
                    "RCR",
                    'N', new ItemStack(Blocks.NETHERRACK),
                    'C', new ItemStack(Items.CAULDRON),
                    'R', "dustRedstone");
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(worldIn, pos, state, rand);
        if (getVariant(state) == EquipmentVariant.ROLLING_MACHINE_POWERED)
            registerNode(state, worldIn, pos);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        if (getVariant(state) == EquipmentVariant.ROLLING_MACHINE_POWERED)
            registerNode(state, worldIn, pos);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        if (getVariant(state) == EquipmentVariant.ROLLING_MACHINE_POWERED)
            deregisterNode(worldIn, pos);
    }

    @Nullable
    @Override
    public ChargeDef getChargeDef(IBlockState state, IBlockAccess world, BlockPos pos) {
        return TileManager.forTile(this::getTileClass, state, world, pos)
                .retrieve(ITileCharge.class, ITileCharge::getChargeDef).orElse(null);
    }
}

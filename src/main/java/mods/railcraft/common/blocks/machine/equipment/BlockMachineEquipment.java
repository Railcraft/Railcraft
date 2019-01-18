/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.equipment;

import mods.railcraft.api.charge.Charge;
import mods.railcraft.api.charge.IChargeBlock;
import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.blocks.TileManager;
import mods.railcraft.common.blocks.interfaces.ITileCharge;
import mods.railcraft.common.blocks.machine.BlockMachine;
import mods.railcraft.common.items.ItemCharge;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.modules.ModuleFactory;
import mods.railcraft.common.modules.RailcraftModuleManager;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.entity.ai.TamingInteractHandler;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Random;

/**
 * Created by CovertJaguar on 9/8/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@BlockMeta.Variant(EquipmentVariant.class)
public class BlockMachineEquipment extends BlockMachine<EquipmentVariant> implements IChargeBlock {
    public BlockMachineEquipment() {
        super(Material.ROCK);
        setDefaultState(getDefaultState());
        setTickRandomly(true);
    }

    @Override
    public void finalizeDefinition() {
        super.finalizeDefinition();
        MinecraftForge.EVENT_BUS.register(new TamingInteractHandler());
    }

    @Override
    public void defineRecipes() {
        EquipmentVariant rollingManual = EquipmentVariant.ROLLING_MACHINE_MANUAL;
        if (rollingManual.isAvailable()) {
            ItemStack stack = rollingManual.getStack();
            CraftingPlugin.addShapedRecipe(stack,
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
            CraftingPlugin.addShapedRecipe(stack,
                    "IPI",
                    "PCP",
                    "IMI",
                    'I', "gearSteel",
                    'M', RailcraftItems.CHARGE, ItemCharge.EnumCharge.MOTOR,
                    'P', Blocks.PISTON,
                    'C', "workbench");
        }

        EquipmentVariant feed = EquipmentVariant.FEED_STATION;
        if (feed.isAvailable()) {
            ItemStack stack = feed.getStack();
            CraftingPlugin.addShapedRecipe(stack,
                    "PCP",
                    "CSC",
                    "PCP",
                    'P', "plankWood",
                    'S', RailcraftModuleManager.isModuleEnabled(ModuleFactory.class) && RailcraftItems.PLATE.isEnabled() ? RailcraftItems.PLATE.getIngredient(Metal.STEEL) : "blockIron",
                    'C', new ItemStack(Items.GOLDEN_CARROT));
        }

        EquipmentVariant alpha = EquipmentVariant.SMOKER;
        if (alpha.isAvailable()) {
            ItemStack stack = alpha.getStack();
            CraftingPlugin.addShapedRecipe(stack,
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

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        switch (getVariant(state)) {
            case ROLLING_MACHINE_MANUAL:
            case FEED_STATION:
                return SoundType.WOOD;
            case ROLLING_MACHINE_POWERED:
            case SMOKER:
                return SoundType.METAL;
        }
        return super.getSoundType(state, world, pos, entity);
    }

    @Override
    public Map<Charge, ChargeSpec> getChargeSpecs(IBlockState state, IBlockAccess world, BlockPos pos) {
        return TileManager.forTile(this::getTileClass, state, world, pos)
                .retrieve(ITileCharge.class, ITileCharge::getChargeSpec).orElse(Collections.emptyMap());
    }
}

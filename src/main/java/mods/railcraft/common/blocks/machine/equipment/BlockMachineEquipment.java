/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.equipment;

import mods.railcraft.common.blocks.machine.BlockMachine;
import mods.railcraft.common.blocks.machine.RailcraftBlockMetadata;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.modules.ModuleFactory;
import mods.railcraft.common.modules.RailcraftModuleManager;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.ai.TamingInteractHandler;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by CovertJaguar on 9/8/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftBlockMetadata(variant = EquipmentVariant.class)
public class BlockMachineEquipment extends BlockMachine<EquipmentVariant> {

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
                    'S', RailcraftModuleManager.isModuleEnabled(ModuleFactory.class) ? RailcraftItems.PLATE.getRecipeObject(Metal.STEEL) : "blockIron",
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
}

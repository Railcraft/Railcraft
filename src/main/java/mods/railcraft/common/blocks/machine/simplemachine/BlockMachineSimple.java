/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.simplemachine;

import mods.railcraft.common.blocks.machine.BlockMachine;
import mods.railcraft.common.blocks.machine.RailcraftBlockMetadata;
import mods.railcraft.common.blocks.machine.alpha.ai.TamingInteractHandler;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.modules.ModuleFactory;
import mods.railcraft.common.modules.RailcraftModuleManager;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
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
@RailcraftBlockMetadata(variant = SimpleMachineVariant.class)
public class BlockMachineSimple extends BlockMachine<SimpleMachineVariant> {

    public BlockMachineSimple() {
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
        SimpleMachineVariant rolling = SimpleMachineVariant.ROLLING_MACHINE;
        if (rolling.isAvailable()) {
            ItemStack stack = rolling.getItem();
            CraftingPlugin.addRecipe(stack,
                    "IPI",
                    "PCP",
                    "IPI",
                    'I', "ingotIron",
                    'P', Blocks.PISTON,
                    'C', "workbench");
        }

        SimpleMachineVariant feed = SimpleMachineVariant.FEED_STATION;
        if (feed.isAvailable()) {
            ItemStack stack = feed.getItem();
            CraftingPlugin.addRecipe(stack,
                    "PCP",
                    "CSC",
                    "PCP",
                    'P', "plankWood",
                    'S', RailcraftModuleManager.isModuleEnabled(ModuleFactory.class) ? RailcraftItems.PLATE.getRecipeObject(Metal.STEEL) : "blockIron",
                    'C', new ItemStack(Items.GOLDEN_CARROT));
        }
    }
}

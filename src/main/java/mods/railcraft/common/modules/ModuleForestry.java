/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.items.ItemTie;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.ModItems;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.misc.Mod;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftModule(value = "railcraft:forestry", description = "forestry integration")
public class ModuleForestry extends RailcraftModulePayload {
    @Override
    public void checkPrerequisites() throws MissingPrerequisiteException {
        if (!Mod.FORESTRY.isLoaded())
            throw new MissingPrerequisiteException("Forestry not detected");
    }

    public ModuleForestry() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            @Optional.Method(modid = ForestryPlugin.FORESTRY_ID)
            public void construction() {
                add(
                        RailcraftItems.FILTER_BEE,
                        RailcraftItems.FILTER_BEE_GENOME,
                        RailcraftItems.BACKPACK_APOTHECARY_T1,
                        RailcraftItems.BACKPACK_APOTHECARY_T2,
                        RailcraftItems.BACKPACK_ICEMAN_T1,
                        RailcraftItems.BACKPACK_ICEMAN_T2,
                        RailcraftItems.BACKPACK_TRACKMAN_T1,
                        RailcraftItems.BACKPACK_TRACKMAN_T2,
                        RailcraftItems.BACKPACK_SIGNALMAN_T1,
                        RailcraftItems.BACKPACK_SIGNALMAN_T2
                );
            }

            @Override
            @Optional.Method(modid = ForestryPlugin.FORESTRY_ID)
            public void init() {
                if (RailcraftItems.FILTER_BEE_GENOME.isEnabled()) {
                    ForestryPlugin.instance().registerBeeFilterRecipe();
                }
            }

            @Override
            @Optional.Method(modid = ForestryPlugin.FORESTRY_ID)
            public void postInit() {
                ForestryPlugin.instance().defineBackpackRecipes();
                ForestryPlugin.instance().setupBackpackContents();

                ItemStack stack = RailcraftItems.TIE.getStack(1, ItemTie.EnumTie.WOOD);
                ForestryPlugin.instance().addCarpenterRecipe("ties", 40, Fluids.CREOSOTE.get(750), null, stack,
                        "###",
                        '#', "slabWood");

                stack = RailcraftItems.TIE.getStack(1, ItemTie.EnumTie.STONE);
                ForestryPlugin.instance().addCarpenterRecipe("ties", 40, Fluids.WATER.get(750), null, stack,
                        "#r#",
                        '#', RailcraftItems.CONCRETE,
                        'r', RailcraftItems.REBAR);

                if (Fluids.CREOSOTE.get() != null && RailcraftConfig.creosoteTorchOutput() > 0) {
                    ForestryPlugin.instance().addCarpenterRecipe("torches", 10, Fluids.CREOSOTE.get(FluidTools.BUCKET_VOLUME), null, new ItemStack(Blocks.TORCH, RailcraftConfig.creosoteTorchOutput()),
                            "#",
                            "|",
                            '#', Blocks.WOOL,
                            '|', Items.STICK);
                }

                if (RailcraftConfig.getRecipeConfig("forestry.misc.brass.casing")) {
                    ItemStack casing = ModItems.STURDY_CASING.get();
                    // todo broke!
                    CraftingPlugin.addRecipe(casing,
                            "III",
                            "I I",
                            "III",
                            'I', RailcraftItems.INGOT, Metal.BRASS);
                }
            }
        });
    }
}

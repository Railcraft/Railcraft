/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.modules;

import mods.railcraft.common.worldgen.ComponentWorkshop;
import mods.railcraft.common.worldgen.VillagerTradeHandler;
import mods.railcraft.common.worldgen.WorkshopCreationHandeler;
import cpw.mods.fml.common.registry.VillagerRegistry;
import mods.railcraft.common.blocks.aesthetics.cube.BlockCube;
import mods.railcraft.common.blocks.aesthetics.cube.EnumCube;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import mods.railcraft.common.blocks.ore.BlockOre;
import mods.railcraft.common.blocks.ore.BlockWorldLogic;
import mods.railcraft.common.blocks.ore.EnumOre;
import mods.railcraft.common.worldgen.SaltpeterGenerator;
import mods.railcraft.common.worldgen.SulfurGenerator;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.worldgen.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.MapGenStructureIO;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ModuleWorld extends RailcraftModule {

    public static final ResourceLocation VILLAGER_TEXTURE = new ResourceLocation("railcraft:textures/entities/villager/trackman.png");

    @Override
    public void preInit() {
        if (RailcraftConfig.isWorldGenEnabled("workshop")) {
            int id = RailcraftConfig.villagerID();
            VillagerRegistry.instance().registerVillagerId(id);
            VillagerRegistry.instance().registerVillageTradeHandler(id, new VillagerTradeHandler());
            VillagerRegistry.instance().registerVillageCreationHandler(new WorkshopCreationHandeler());
            try {
                MapGenStructureIO.func_143031_a(ComponentWorkshop.class, "railcraft:workshop");
            } catch (Throwable e) {
            }
        }
    }

    @Override
    public void initFirst() {
        BlockOre.registerBlock();
        BlockWorldLogic.registerBlock();

        EnumCube cubeType = EnumCube.ABYSSAL_STONE;
        if (RailcraftConfig.isSubBlockEnabled(cubeType.getTag())) {
            BlockCube.registerBlock();
        }

        cubeType = EnumCube.QUARRIED_STONE;
        if (RailcraftConfig.isSubBlockEnabled(cubeType.getTag())) {
            BlockCube.registerBlock();
        }

        if (RailcraftConfig.isWorldGenEnabled("saltpeter") && EnumOre.SALTPETER.isEnabled())
            MinecraftForge.ORE_GEN_BUS.register(new SaltpeterGenerator());
        if (RailcraftConfig.isWorldGenEnabled("sulfur") && EnumOre.SULFUR.isEnabled())
            MinecraftForge.ORE_GEN_BUS.register(new SulfurGenerator());
        if (RailcraftConfig.isWorldGenEnabled("firestone") && EnumOre.FIRESTONE.isEnabled() && ModuleManager.isModuleLoaded(ModuleManager.Module.MAGIC))
            MinecraftForge.EVENT_BUS.register(new FirestoneGenerator());
        if (RailcraftConfig.isWorldGenEnabled("abyssal") && EnumCube.ABYSSAL_STONE.isEnabled())
            MinecraftForge.EVENT_BUS.register(GeodePopulator.instance());
        if (RailcraftConfig.isWorldGenEnabled("quarried") && EnumCube.QUARRIED_STONE.isEnabled())
            MinecraftForge.EVENT_BUS.register(QuarryPopulator.instance());

        if (RailcraftConfig.isWorldGenEnabled("iron") && EnumOre.POOR_IRON.isEnabled())
            MinecraftForge.ORE_GEN_BUS.register(new PoorIronGenerator());
        if (RailcraftConfig.isWorldGenEnabled("gold") && EnumOre.POOR_GOLD.isEnabled())
            MinecraftForge.ORE_GEN_BUS.register(new PoorGoldGenerator());
        if (RailcraftConfig.isWorldGenEnabled("copper") && EnumOre.POOR_COPPER.isEnabled())
            MinecraftForge.ORE_GEN_BUS.register(new PoorCopperGenerator());
        if (RailcraftConfig.isWorldGenEnabled("tin") && EnumOre.POOR_TIN.isEnabled())
            MinecraftForge.ORE_GEN_BUS.register(new PoorTinGenerator());
        if (RailcraftConfig.isWorldGenEnabled("lead") && EnumOre.POOR_LEAD.isEnabled())
            MinecraftForge.ORE_GEN_BUS.register(new PoorLeadGenerator());

        if (RailcraftConfig.getRecipeConfig("railcraft.misc.gunpowder")) {
            IRecipe recipe = new ShapelessOreRecipe(new ItemStack(Items.gunpowder, 2), "dustSaltpeter", "dustSaltpeter", "dustSulfur", "dustCharcoal");
            CraftingManager.getInstance().getRecipeList().add(recipe);
        }

        if (RailcraftConfig.getRecipeConfig("forestry.misc.fertilizer")) {
            ItemStack fert = ForestryPlugin.getItem("fertilizerCompound");

            if (fert != null) {
                fert = fert.copy();
                fert.stackSize = 2;
                CraftingPlugin.addShapelessRecipe(fert,
                        "dustSaltpeter",
                        "sand",
                        "sand",
                        new ItemStack(Blocks.dirt),
                        new ItemStack(Blocks.dirt));
            }
        }
    }

}

/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.aesthetics.generic.EnumGeneric;
import mods.railcraft.common.blocks.ore.EnumOre;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.worldgen.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftModule("railcraft:world")
public class ModuleWorld extends RailcraftModulePayload {

    public static final String VILLAGER_TEXTURE = RailcraftConstants.ENTITY_TEXTURE_FOLDER + "villager/trackman.png";
    public static final String ZOMBIE_TEXTURE = RailcraftConstants.ENTITY_TEXTURE_FOLDER + "villager/zombie_trackman.png";
    public static final String VILLAGER_ID = RailcraftConstants.RESOURCE_DOMAIN + ":trackman";

    public ModuleWorld() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        RailcraftBlocks.ore,
                        RailcraftBlocks.worldLogic
                );
                if (RailcraftConfig.isWorldGenEnabled("workshop")) {
                    WorkshopCreationHandler workshop = new WorkshopCreationHandler();
                    VillagerRegistry villagerRegistry = VillagerRegistry.instance();
                    villagerRegistry.registerVillageCreationHandler(workshop);

                    VillagerRegistry.VillagerProfession villagerTrackman = new VillagerRegistry.VillagerProfession(VILLAGER_ID, VILLAGER_TEXTURE, ZOMBIE_TEXTURE);
                    villagerRegistry.register(villagerTrackman);

                    VillagerRegistry.VillagerCareer trackmanCareer = new VillagerRegistry.VillagerCareer(villagerTrackman, "trackman");
                    VillagerTrades.define(trackmanCareer);

                    try {
                        MapGenStructureIO.registerStructureComponent(ComponentWorkshop.class, "railcraft:workshop");
                    } catch (Throwable ignored) {
                    }
                }
            }

            @Override
            public void preInit() {
                EnumGeneric cubeType = EnumGeneric.STONE_ABYSSAL;
                if (RailcraftConfig.isSubBlockEnabled(cubeType.getTag())) {
                    RailcraftBlocks.generic.register();
                }

                cubeType = EnumGeneric.STONE_QUARRIED;
                if (RailcraftConfig.isSubBlockEnabled(cubeType.getTag())) {
                    RailcraftBlocks.generic.register();
                }

                if (RailcraftConfig.isWorldGenEnabled("saltpeter") && EnumOre.SALTPETER.isEnabled())
                    MinecraftForge.ORE_GEN_BUS.register(new GeneratorSaltpeter());
                if (RailcraftConfig.isWorldGenEnabled("sulfur") && EnumOre.SULFUR.isEnabled())
                    MinecraftForge.ORE_GEN_BUS.register(new SulfurGenerator());
                if (RailcraftConfig.isWorldGenEnabled("firestone") && EnumOre.FIRESTONE.isEnabled() && RailcraftModuleManager.isModuleEnabled(ModuleMagic.class))
                    MinecraftForge.EVENT_BUS.register(new DecoratorFirestone());
                if (RailcraftConfig.isWorldGenEnabled("abyssal") && EnumGeneric.STONE_ABYSSAL.isEnabled())
                    MinecraftForge.EVENT_BUS.register(PopulatorGeode.instance());
                if (RailcraftConfig.isWorldGenEnabled("quarried") && EnumGeneric.STONE_QUARRIED.isEnabled())
                    MinecraftForge.EVENT_BUS.register(PopulatorQuarry.instance());

                if (RailcraftConfig.isWorldGenEnabled("iron") && EnumOre.POOR_IRON.isEnabled())
                    MinecraftForge.ORE_GEN_BUS.register(new GeneratorPoorOreIron());
                if (RailcraftConfig.isWorldGenEnabled("gold") && EnumOre.POOR_GOLD.isEnabled())
                    MinecraftForge.ORE_GEN_BUS.register(new GeneratorPoorOreGold());
                if (RailcraftConfig.isWorldGenEnabled("copper") && EnumOre.POOR_COPPER.isEnabled())
                    MinecraftForge.ORE_GEN_BUS.register(new GeneratorPoorOreCopper());
                if (RailcraftConfig.isWorldGenEnabled("tin") && EnumOre.POOR_TIN.isEnabled())
                    MinecraftForge.ORE_GEN_BUS.register(new GeneratorPoorOreTin());
                if (RailcraftConfig.isWorldGenEnabled("lead") && EnumOre.POOR_LEAD.isEnabled())
                    MinecraftForge.ORE_GEN_BUS.register(new GeneratorPoorOreLead());

                if (RailcraftConfig.getRecipeConfig("railcraft.misc.gunpowder")) {
                    IRecipe recipe = new ShapelessOreRecipe(new ItemStack(Items.GUNPOWDER, 2), "dustSaltpeter", "dustSaltpeter", "dustSulfur", "dustCharcoal");
                    CraftingManager.getInstance().getRecipeList().add(recipe);
                }

                if (RailcraftConfig.getRecipeConfig("forestry.misc.fertilizer")) {
                    ItemStack fertilizer = ForestryPlugin.getItem("fertilizerCompound");

                    if (fertilizer != null) {
                        fertilizer = fertilizer.copy();
                        fertilizer.stackSize = 2;
                        CraftingPlugin.addShapelessRecipe(fertilizer,
                                "dustSaltpeter",
                                "sand",
                                "sand",
                                new ItemStack(Blocks.DIRT),
                                new ItemStack(Blocks.DIRT));
                    }
                }
            }
        });
    }
}

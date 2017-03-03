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
import mods.railcraft.common.blocks.ore.EnumOreMagic;
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
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftModule(value = "railcraft:world", description = "world gen, ores, villages")
public class ModuleWorld extends RailcraftModulePayload {

    public static final String VILLAGER_TEXTURE = RailcraftConstants.ENTITY_TEXTURE_FOLDER + "villager/trackman.png";
    public static final String ZOMBIE_TEXTURE = RailcraftConstants.ENTITY_TEXTURE_FOLDER + "villager/zombie_trackman.png";
    public static final String VILLAGER_ID = RailcraftConstants.RESOURCE_DOMAIN + ":trackman";

    public static VillagerRegistry.VillagerProfession villagerTrackman;

    public ModuleWorld() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        RailcraftBlocks.ORE,
                        RailcraftBlocks.ORE_MAGIC,
                        RailcraftBlocks.WORLD_LOGIC
                );
                if (RailcraftConfig.isWorldGenEnabled("workshop")) {
                    WorkshopCreationHandler workshop = new WorkshopCreationHandler();
                    VillagerRegistry villagerRegistry = VillagerRegistry.instance();
                    villagerRegistry.registerVillageCreationHandler(workshop);

                    villagerTrackman = new VillagerRegistry.VillagerProfession(VILLAGER_ID, VILLAGER_TEXTURE, ZOMBIE_TEXTURE);
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
                if (RailcraftConfig.vanillaOreGenChance() < 100)
                    MinecraftForge.ORE_GEN_BUS.register(new VanillaOreDisabler());

                EnumGeneric cubeType = EnumGeneric.STONE_ABYSSAL;
                if (RailcraftConfig.isSubBlockEnabled(cubeType.getTag())) {
                    RailcraftBlocks.GENERIC.register();
                }

                cubeType = EnumGeneric.STONE_QUARRIED;
                if (RailcraftConfig.isSubBlockEnabled(cubeType.getTag())) {
                    RailcraftBlocks.GENERIC.register();
                }

                if (RailcraftConfig.isWorldGenEnabled("saltpeter") && EnumOre.SALTPETER.isEnabled())
                    GameRegistry.registerWorldGenerator(new GeneratorSaltpeter(), 100);
                if (RailcraftConfig.isWorldGenEnabled("sulfur") && EnumOre.SULFUR.isEnabled())
                    GameRegistry.registerWorldGenerator(new GeneratorSulfur(), 100);
                if (RailcraftConfig.isWorldGenEnabled("firestone") && EnumOreMagic.FIRESTONE.isEnabled() && RailcraftModuleManager.isModuleEnabled(ModuleMagic.class))
                    GameRegistry.registerWorldGenerator(new GeneratorFirestone(), 100);
                if (RailcraftConfig.isWorldGenEnabled("abyssal") && EnumGeneric.STONE_ABYSSAL.isEnabled())
                    GameRegistry.registerWorldGenerator(new GeneratorGeode(), 0);
                if (RailcraftConfig.isWorldGenEnabled("quarried") && EnumGeneric.STONE_QUARRIED.isEnabled())
                    MinecraftForge.EVENT_BUS.register(PopulatorQuarry.instance());

                if (RailcraftBlocks.ORE.isEnabled()) {
                    if (RailcraftConfig.isWorldGenEnabled("iron"))
                        GameRegistry.registerWorldGenerator(new GeneratorMineIron(), 100);
                    if (RailcraftConfig.isWorldGenEnabled("gold"))
                        GameRegistry.registerWorldGenerator(new GeneratorMineGold(), 100);
                    if (RailcraftConfig.isWorldGenEnabled("copper"))
                        GameRegistry.registerWorldGenerator(new GeneratorMineCopper(), 100);
                    if (RailcraftConfig.isWorldGenEnabled("tin"))
                        GameRegistry.registerWorldGenerator(new GeneratorMineTin(), 100);
                    if (RailcraftConfig.isWorldGenEnabled("lead"))
                        GameRegistry.registerWorldGenerator(new GeneratorMineLead(), 100);
                    if (RailcraftConfig.isWorldGenEnabled("silver"))
                        GameRegistry.registerWorldGenerator(new GeneratorMineSilver(), 100);
                }

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

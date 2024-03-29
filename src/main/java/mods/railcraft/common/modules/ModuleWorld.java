/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.ore.EnumOre;
import mods.railcraft.common.blocks.ore.EnumOreMagic;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.collections.BlockItemParser;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.worldgen.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import org.jetbrains.annotations.Nullable;

import java.io.File;

import static mods.railcraft.common.util.inventory.InvTools.setSize;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftModule(value = "railcraft:world", description = "world gen, ores, villages")
public class ModuleWorld extends RailcraftModulePayload {

    public static Config config;
    @ObjectHolder("minecraft:smith")
    static VillagerRegistry.VillagerProfession smith;
    public static final String VILLAGER_TEXTURE = RailcraftConstants.ENTITY_TEXTURE_FOLDER + "villager/trackman.png";
    public static final String ZOMBIE_TEXTURE = RailcraftConstants.ENTITY_TEXTURE_FOLDER + "villager/zombie_trackman.png";
    public static final String VILLAGER_ID = RailcraftConstants.RESOURCE_DOMAIN + ":trackman";

    public static @Nullable VillagerRegistry.VillagerProfession villagerTrackman;

    public ModuleWorld() {
        add(
                RailcraftBlocks.ABYSSAL_STONE,
                RailcraftBlocks.JADED_STONE,
                RailcraftBlocks.QUARRIED_STONE,
                RailcraftBlocks.GENERIC,
                RailcraftBlocks.ORE,
                RailcraftBlocks.ORE_METAL,
                RailcraftBlocks.ORE_METAL_POOR,
                RailcraftBlocks.ORE_MAGIC,
                RailcraftBlocks.WORLD_LOGIC
        );
        setEnabledEventHandler(new ModuleEventHandler() {
            private File oreConfigFolder;

            @Override
            public void preInit() {
                if (RailcraftConfig.vanillaOreGenChance() < 100)
                    MinecraftForge.ORE_GEN_BUS.register(new VanillaOreDisabler());

                if (RailcraftConfig.isWorldGenEnabled("saltpeter") && EnumOre.SALTPETER.isEnabled())
                    GameRegistry.registerWorldGenerator(new GeneratorSaltpeter(), 100);
                if (RailcraftConfig.isWorldGenEnabled("sulfur") && EnumOre.SULFUR.isEnabled())
                    GameRegistry.registerWorldGenerator(new GeneratorSulfur(), 100);
                if (RailcraftConfig.isWorldGenEnabled("firestone") && EnumOreMagic.FIRESTONE.isEnabled() && RailcraftModuleManager.isModuleEnabled(ModuleMagic.class))
                    GameRegistry.registerWorldGenerator(new GeneratorFirestone(), 100);
                if (RailcraftConfig.isWorldGenEnabled("abyssal") && RailcraftBlocks.ABYSSAL_STONE.isEnabled())
                    GameRegistry.registerWorldGenerator(new GeneratorGeode(), 0);
                if (RailcraftConfig.isWorldGenEnabled("quarried") && RailcraftBlocks.QUARRIED_STONE.isEnabled())
                    MinecraftForge.EVENT_BUS.register(PopulatorQuarry.instance());
                if (RailcraftConfig.isWorldGenEnabled("jaded") && RailcraftBlocks.JADED_STONE.isEnabled())
                    MinecraftForge.EVENT_BUS.register(PopulatorJaded.instance());

                oreConfigFolder = new File(Railcraft.instance.getConfigFolder(), "ores");
                if (!oreConfigFolder.exists())
                    //noinspection ResultOfMethodCallIgnored
                    oreConfigFolder.mkdirs();

                if (RailcraftConfig.generateDefaultOreConfigs()) {
                    generateDefaultConfigs(8, 60, 3, 8, 20, 8, 29, Metal.COPPER);
                    generateDefaultConfigs(8, 15, 1, 1, 0, 0, 79, Metal.GOLD);
                    generateDefaultConfigs(8, 40, 4, 16, 0, 0, 26, Metal.IRON);
                    generateDefaultConfigs(9, 40, 6, 4, 15, 6, 26, Metal.NICKEL); // Same depth/seed as Iron so they will generate together
                    generateDefaultConfigs(8, 30, 3, 6, 10, 7, 82, Metal.LEAD);
                    generateDefaultConfigs(9, 30, 4, 4, 10, 6, 82, Metal.ZINC); // Same depth/seed as Lead so they will generate together
                    generateDefaultConfigs(8, 20, 2, 2, 10, 4, 47, Metal.SILVER);
                    generateDefaultConfigs(8, 50, 2, 4, 15, 6, 50, Metal.TIN);
                }
            }

            private void generateDefaultConfigs(int weight, int depth, int mineRange, int mineBlockCount, int diffuseRange, int diffuseBlockCount, int seed, Metal metal) {
                String[] fileNames = {
                        "mine_" + metal.name() + ".cfg",
                        "diffuse_" + metal.name() + ".cfg"
                };
                Configuration[] oreConfigs = new Configuration[2];
                int numConfigs = 2;
                switch (metal) {
                    case GOLD:
                    case IRON:
                        numConfigs = 1;
                }
                for (int ii = 0; ii < numConfigs; ii++) {
                    File file = new File(oreConfigFolder, fileNames[ii]);
                    if (file.exists())
                        //noinspection ResultOfMethodCallIgnored
                        file.delete();
                    oreConfigs[ii] = new Configuration(file);
                    oreConfigs[ii].load();
                }
                IBlockState fringeState = Metal.Form.POOR_ORE.getState(metal);
                if (fringeState == null)
                    return;
                String fringeOre = BlockItemParser.toString(fringeState);
                IBlockState coreState = Metal.Form.ORE.getState(metal);
                if (coreState == null)
                    return;
                String coreOre = BlockItemParser.toString(coreState);
                OreGeneratorFactory.makeMine(oreConfigs[0], weight, mineBlockCount, depth, mineRange, seed, fringeOre, coreOre);
                switch (metal) {
                    case GOLD:
                    case IRON:
                        break;
                    default:
                        OreGeneratorFactory.makeDiffuse(oreConfigs[1], weight, diffuseBlockCount, depth, diffuseRange, coreOre);
                }
            }

            @Override
            public void init() {
                File[] oreConfigs = oreConfigFolder.listFiles((dir, name) -> name != null && name.endsWith(".cfg"));
                if (oreConfigs == null)
                    throw new RuntimeException("'ore' directory does not exist or is not accessible.");
                for (File oreConfigFile : oreConfigs) {
                    Configuration oreConfig = new Configuration(oreConfigFile);
                    oreConfig.load();

                    OreGeneratorFactory genFactory = new OreGeneratorFactory(oreConfig);
                    GameRegistry.registerWorldGenerator(genFactory.worldGen, genFactory.settings.weight);
                    if (oreConfig.hasChanged())
                        oreConfig.save();
                }

                if (RailcraftConfig.getRecipeConfig("railcraft.misc.gunpowder")) {
                    CraftingPlugin.addShapelessRecipe(new ItemStack(Items.GUNPOWDER, 2), "dustSaltpeter", "dustSaltpeter", "dustSulfur", "dustCharcoal");
                }

                if (RailcraftConfig.getRecipeConfig("forestry.misc.fertilizer")) {
                    ItemStack fertilizer = ForestryPlugin.getItem("fertilizerCompound");

                    if (!InvTools.isEmpty(fertilizer)) {
                        fertilizer = fertilizer.copy();
                        setSize(fertilizer, 2);
                        CraftingPlugin.addShapelessRecipe(fertilizer,
                                "dustSaltpeter",
                                "sand",
                                "sand",
                                new ItemStack(Blocks.DIRT),
                                new ItemStack(Blocks.DIRT));
                    }
                }
                if (RailcraftConfig.isWorldGenEnabled("villager")) {
                    villagerTrackman = new VillagerRegistry.VillagerProfession(VILLAGER_ID, VILLAGER_TEXTURE, ZOMBIE_TEXTURE);
                    ForgeRegistries.VILLAGER_PROFESSIONS.register(villagerTrackman); //TODO registry event

                    VillagerCareer trackmanCareer = new VillagerCareer(villagerTrackman, "trackman");
                    VillagerCareer cartmanCareer = new VillagerCareer(villagerTrackman, "cartman");

                    VillagerTrades.addTradeForTrackman(trackmanCareer);
                    VillagerTrades.addTradeForCartman(cartmanCareer);

                    if (smith != null) {
                        VillagerCareer alloyer = new VillagerCareer(smith, "alloyer");
                        VillagerCareer steelForger = new VillagerCareer(smith, "steel_forger");

                        VillagerTrades.addTradeForAlloyer(alloyer);
                        VillagerTrades.addTradeForSteelForger(steelForger);
                    }
                }
                if (RailcraftConfig.isWorldGenEnabled("workshop")) {
                    WorkshopCreationHandler workshop = new WorkshopCreationHandler();
                    VillagerRegistry villagerRegistry = VillagerRegistry.instance();
                    villagerRegistry.registerVillageCreationHandler(workshop);

                    try {
                        MapGenStructureIO.registerStructureComponent(ComponentWorkshop.class, "railcraft:workshop");
                    } catch (Throwable ignored) {
                    }
                }
            }
        });
    }

    @Override
    public void loadConfig(Configuration config) {
        ModuleWorld.config = new ModuleWorld.Config(config);
    }

    public static class Config {
        public final float geodeChance;
        public final float monolithChance;
        public final float quarryChance;

        public Config(Configuration config) {
            geodeChance = config.getFloat("geodeChance", CAT_CONFIG,
                    0.05F, 0.0F, 1.0F,
                    "how often a Geode succeeds a spawn check under the ocean, 0 = never, 1 = every time");
            monolithChance = config.getFloat("monolithChance", CAT_CONFIG,
                    0.005F, 0.0F, 1.0F,
                    "how often a Monolith succeeds a spawn check in the plains, 0 = never, 1 = every time");
            quarryChance = config.getFloat("quarryChance", CAT_CONFIG,
                    0.025F, 0.0F, 1.0F,
                    "how often a Quarry succeeds a spawn check in a forest, 0 = never, 1 = every time");
        }
    }
}

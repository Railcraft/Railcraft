/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.worldgen;

import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.util.collections.BlockItemParser;
import mods.railcraft.common.util.collections.CollectionTools;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by CovertJaguar on 6/7/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class OreGeneratorFactory {
    enum Type {
        MINE,
        DIFFUSE
    }

    public static final String CAT = "ore";
    public final Type type;
    public final GeneratorSettings settings;
    public final IWorldGenerator worldGen;

    public static OreGeneratorFactory makeMine(Configuration config, int defaultWeight, int defaultBlockCount, int defaultDepth, int defaultRange, int defaultSeed, String defaultFringeOre, String defaultCoreOre) {
        return new OreGeneratorFactory(config, "MINE", defaultWeight, defaultBlockCount, defaultDepth, defaultRange, defaultSeed, defaultFringeOre, defaultCoreOre);
    }

    public OreGeneratorFactory(Configuration config) {
        this(config, "MINE", 100, 4, 40, 3, 29, "railcraft:ore_metal_poor#2", "railcraft:ore_metal#0");
    }

    private OreGeneratorFactory(Configuration config, String defaultType, int defaultWeight, int defaultBlockCount, int defaultDepth, int defaultRange, int defaultSeed, String defaultFringeOre, String defaultCoreOre) {
        config.setCategoryComment(CAT + ".retrogen", "Retrogen settings. You must have the Railcraft-Retrogen mod installed for these to do anything.");
        boolean retrogen = config.getBoolean("retrogen", CAT + ".retrogen", false, "Whether retrogen should be enabled on this generator. =");
        String retrogenMarker = config.getString("retrogenMarker", CAT + ".retrogen", "RCRGMARK", "The marker used to indicate whether a chunk has generated this ore. Generally this should be unique each time you run retrogen.");

        String name = config.getConfigFile().getName().replace(".cfg", "").replace(" ", "_");

        type = Type.valueOf(config.getString("type", CAT, defaultType, "The generation type, can be either 'DIFFUSE' or 'MINE'."));

        BiomeRules biomeRules = new BiomeRules(config);

        switch (type) {
            case MINE:
                GeneratorSettingsMine settings = new GeneratorSettingsMine(config, defaultWeight, defaultBlockCount, defaultDepth, defaultRange, defaultSeed, defaultFringeOre, defaultCoreOre);
                this.settings = settings;
                IWorldGenerator genImpl = new GeneratorMine(config, biomeRules, settings);
                worldGen = new GeneratorRailcraftOre(genImpl, retrogen, retrogenMarker).setRegistryName(new ResourceLocation(RailcraftConstants.RESOURCE_DOMAIN, name));
                break;
            default:
                throw new OreConfigurationException(config, "Something went wrong. This should be impossible.");
        }

        if (config.hasChanged())
            config.save();
    }

    public static class GeneratorSettings {
        public final int weight;
        public final int depth;
        public final int range;
        public final int blockCount;

        public GeneratorSettings(Configuration config, int defaultWeight, int defaultBlockCount, int defaultDepth, int defaultRange) {
            weight = config.getInt("weight", CAT, defaultWeight, 0, Integer.MAX_VALUE, "The generator weight, larger weights generate later. You can use this to sort what order stuff is generated.");
            depth = config.getInt("depth", OreGeneratorFactory.CAT, defaultDepth, 10, Integer.MAX_VALUE, "The y level that the mine will generate at. Generally you should keep this below 220 for vanilla height worlds. If your sea level is the normal 63, its usually best to stay below 50 as well due to the topsoil.");
            range = config.getInt("range", OreGeneratorFactory.CAT, defaultRange, 1, 20, "The scale of the gaussian distribution used to spread the mine vertically, how tall it is. Note that it spreads above and blow the y level equally, so a value of 3 is roughly 6 blocks tall.");
            blockCount = config.getInt("blockCount", OreGeneratorFactory.CAT, defaultBlockCount, 1, 16, "The number of ore blocks generated during each successful event. Each chunk generally gets 216 generation events, but not all events result in ore spawn due to chance settings and noise fields.");
        }
    }

    public static class GeneratorSettingsMine extends GeneratorSettings {
        public final boolean skyGen;
        public final int noiseSeed;
        public final double cloudScale, veinScale, fringeLimit, richLimit, coreLimit, veinLimit, fringeGenChance, coreGenChance, coreOreChance;
        public final IBlockState fringeOre, coreOre;

        public GeneratorSettingsMine(Configuration config, int defaultWeight, int defaultBlockCount, int defaultDepth, int defaultRange, int defaultSeed, String defaultFringeOre, String defaultCoreOre) {
            super(config, defaultWeight, defaultBlockCount, defaultDepth, defaultRange);

            this.skyGen = RailcraftConfig.isWorldGenEnabled("sky");

            config.setCategoryComment(CAT + ".ore", "The ore blocks to be generated. Format: <modid>:<blockname>#<meta>");
            fringeOre = BlockItemParser.parseBlock(config.getString("fringe", OreGeneratorFactory.CAT + ".ore", defaultFringeOre, "The ore block generated on the fringe of the mine."));
            coreOre = BlockItemParser.parseBlock(config.getString("core", OreGeneratorFactory.CAT + ".ore", defaultCoreOre, "The ore block generated in the core of the mine."));

            noiseSeed = config.getInt("seed", OreGeneratorFactory.CAT, defaultSeed, 0, Integer.MAX_VALUE, "The seed used to create the noise map. Generally it is set to the atomic number of the element being generated, but it can be anything you want. Should be unique for each generator or your mines will generate in the same places, which can be desirable if you want to mix ores like Iron and Nickel.");
            cloudScale = config.getFloat("cloud", OreGeneratorFactory.CAT + ".scale", 0.0018F, 0.000001F, 1F, "The scale of the noise map used to determine the boundaries of the mine. Very small changes can have drastic effects. Smaller numbers result in larger mines. Recommended to not change this.");
            veinScale = config.getFloat("vein", OreGeneratorFactory.CAT + ".scale", 0.015F, 0.000001F, 1F, "The scale of the noise map used to create the veins. Very small changes can have drastic effects. Smaller numbers result in larger veins. Recommended to not change this.");
            fringeLimit = config.getFloat("fringe", OreGeneratorFactory.CAT + ".limits", 0.7F, 0F, 1F, "The limit of noise of the cloud layer above which fringe ore is generated. Lower numbers result in larger, more common, fringe areas.");
            richLimit = config.getFloat("rich", OreGeneratorFactory.CAT + ".limits", 0.8F, 0F, 1F, "The limit of noise of the cloud layer above which core ore is generated in rich biomes. Lower numbers result in larger rich areas.");
            coreLimit = config.getFloat("core", OreGeneratorFactory.CAT + ".limits", 0.9F, 0F, 1F, "The limit of noise of the cloud layer above which core ore is generated. Lower numbers result in larger core areas.");
            veinLimit = config.getFloat("vein", OreGeneratorFactory.CAT + ".limits", 0.25F, 0F, 1F, "The limit of noise of the vein layer below which ore is generated. Larger numbers result in larger veins.");
            fringeGenChance = config.getFloat("fringeGen", OreGeneratorFactory.CAT + ".chances", 0.3F, 0F, 1F, "The percent chance that a generate event in a fringe area will result in ore spawning.");
            coreGenChance = config.getFloat("coreGen", OreGeneratorFactory.CAT + ".chances", 1F, 0F, 1F, "The percent chance that a generate event in a core area will result in ore spawning.");
            coreOreChance = config.getFloat("coreOre", OreGeneratorFactory.CAT + ".chances", 0.2F, 0F, 1F, "The percent chance that a generate event in a core area will result in core ore spawning instead of fringe ore. Applied after coreGen.");
        }
    }

    public static class BiomeRules {
        final Set<Biome> biomeBlacklist;
        final Set<Biome> biomeWhitelist;

        final Set<BiomeDictionary.Type> biomeTypeBlacklist;
        final Set<BiomeDictionary.Type> biomeTypeWhitelist;

        final Set<Biome> richBiomes;
        final Set<BiomeDictionary.Type> richBiomeTypes;

        public BiomeRules(Configuration config) {
            config.setCategoryComment(CAT + ".biomes", "Expects fully qualified Biome registry names.\n" +
                    "See Biome.java in Minecraft/Forge for the names.\n" +
                    "Format: <modid>:<biome_registry_name>.\n" +
                    "'<modid>:all' can be used to specify all Biomes from a specific mod.");
            String[] biomeBlacklistNames = config.getStringList("blacklist", CAT + ".biomes", new String[]{}, "Biome registry names where the ore will will not generate. Takes priority over the whitelist and types.");
            biomeBlacklist = Collections.unmodifiableSet(Arrays.stream(biomeBlacklistNames).flatMap(this::getBiomes).collect(Collectors.toSet()));
            String[] biomeWhitelistNames = config.getStringList("whitelist", CAT + ".biomes", new String[]{}, "Biome registry names where the ore will generate. Takes priority over types.");
            biomeWhitelist = Collections.unmodifiableSet(Arrays.stream(biomeWhitelistNames).flatMap(this::getBiomes).collect(Collectors.toSet()));

            config.setCategoryComment(CAT + ".biomesTypes", "Biome Dictionary types can be found in BiomeDictionary.java in Forge.\n" +
                    "You can use 'ALL' to specify all types.");
            String[] biomeTypeBlacklistNames = config.getStringList("blacklist", CAT + ".biomesTypes", new String[]{}, "Biome Dictionary types where the ore will will not generate. Takes priority over the whitelist.");
            biomeTypeBlacklist = Collections.unmodifiableSet(Arrays.stream(biomeTypeBlacklistNames).flatMap(this::getTypes).collect(Collectors.toSet()));
            String[] biomeTypeWhitelistNames = config.getStringList("whitelist", CAT + ".biomesTypes", new String[]{"ALL"}, "Biome Dictionary types where the ore will generate.");
            biomeTypeWhitelist = Collections.unmodifiableSet(Arrays.stream(biomeTypeWhitelistNames).flatMap(this::getTypes).collect(Collectors.toSet()));

            config.setCategoryComment(CAT + ".rich", "Biomes where the ore will generator more richly.");
            String[] richBiomeNames = config.getStringList("biomes", CAT + ".rich", new String[]{"minecraft:mesa"}, "Biomes where the ore will generator more richly. Expects fully qualified Biome registry names. '<modid>:all' can be used to specify all Biomes from a specific mod.");
            richBiomes = Collections.unmodifiableSet(Arrays.stream(richBiomeNames).flatMap(this::getBiomes).collect(Collectors.toSet()));

            String[] richBiomeTypeNames = config.getStringList("biomeTypes", CAT + ".rich", new String[]{"MOUNTAIN", "MESA", "HILLS"}, "Biome Dictionary types where the ore will generator more richly. You can use 'ALL' to specify all types.");
            richBiomeTypes = Collections.unmodifiableSet(Arrays.stream(richBiomeTypeNames).flatMap(this::getTypes).collect(Collectors.toSet()));

        }

        public boolean isValidBiome(Biome biome) {
            if (!biomeBlacklist.contains(biome) && biomeWhitelist.contains(biome))
                return true;
            BiomeDictionary.Type[] type = BiomeDictionary.getTypesForBiome(biome);
            return !CollectionTools.intersects(biomeTypeBlacklist, type)
                    && CollectionTools.intersects(biomeTypeWhitelist, type);
        }

        public boolean isRichBiome(Biome biome) {
            if (richBiomes.contains(biome))
                return true;
            BiomeDictionary.Type[] type = BiomeDictionary.getTypesForBiome(biome);
            return CollectionTools.intersects(richBiomeTypes, type);
        }

        private Stream<Biome> getBiomes(String name) {
            ResourceLocation resource = new ResourceLocation(name);
            if ("all".equalsIgnoreCase(resource.getResourcePath()))
                return StreamSupport.stream(Biome.REGISTRY.spliterator(), false).filter(b -> resource.getResourceDomain().equalsIgnoreCase(b.getRegistryName().getResourceDomain()));
            Biome biome = Biome.REGISTRY.getObject(resource);
            if (biome == null)
                return Stream.empty();
            return Stream.of(biome);
        }

        private Stream<BiomeDictionary.Type> getTypes(String name) {
            name = name.toUpperCase(Locale.ROOT);
            if ("ALL".equalsIgnoreCase(name))
                return Arrays.stream(BiomeDictionary.Type.values());
            return Stream.of(BiomeDictionary.Type.valueOf(name));
        }
    }

    /**
     * Created by CovertJaguar on 6/9/2017 for Railcraft.
     *
     * @author CovertJaguar <http://www.railcraft.info>
     */
    public static class OreConfigurationException extends RuntimeException {
        public OreConfigurationException(Configuration config, String msg) {
            super("Error detected in Ore Config: " + config.getConfigFile().getName() + " - " + msg);
        }
    }
}

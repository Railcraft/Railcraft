/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.collections;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
//TODO: test this!
public class BlockItemParser {

    public static String toString(RailcraftBlocks block, IVariantEnum variant) {
        return block.getRegistryName() + "#" + variant.ordinal();
    }

    public static String toString(IBlockState state) {
        return state.getBlock().getRegistryName() + "#" + state.getBlock().getMetaFromState(state);
    }

    public static IBlockState parseBlock(String line) {
        String[] tokens = line.split("#");
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tokens[0]));
        if (block == null)
            throw new IllegalArgumentException("Invalid Block Name while parsing config = " + line);
        int meta = tokens.length > 1 ? Integer.valueOf(tokens[1]) : 0;
        return block.getStateFromMeta(meta);
    }

    public static ItemKey parseItem(String line) {
        String[] tokens = line.split("#");
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(tokens[0]));
        if (item == null)
            throw new IllegalArgumentException("Invalid Item Name while parsing config = " + line);
        int meta = tokens.length > 1 ? Integer.valueOf(tokens[1]) : -1;
        return new ItemKey(item, meta);
    }

    public static <T> Set<T> parseList(String list, String logMessage, Function<String, T> keyParser) {
        try {
            Set<T> set = new HashSet<T>();
            for (String line : list.replaceAll("[{} ]", "").split("[,;]+")) {
                if (line.equals(""))
                    continue;
                set.add(keyParser.apply(line));
                Game.log(Level.DEBUG, logMessage, line);
            }
            return set;
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Invalid list while parsing config = " + list);
        }
    }

    public static <T, V> Map<T, V> parseDictionary(String list, String logMessage, Function<String, T> keyParser, Function<String, V> valueParser) {
        try {
            Map<T, V> map = new HashMap<T, V>();
            for (String line : list.replaceAll("[{} ]", "").split("[,;]+")) {
                if (StringUtils.isEmpty(line))
                    continue;
                String[] entry = line.split("=");
                map.put(keyParser.apply(entry[0]), valueParser.apply(entry[1]));
                Game.log(Level.DEBUG, logMessage, line);
            }
            return map;
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Invalid map while parsing config = " + list);
        }
    }

    public static <T, V> Map<T, V> parseDictionary(String[] list, String logMessage, Function<String, T> keyParser, Function<String, V> valueParser) {
        try {
            Map<T, V> map = new HashMap<T, V>();
            for (String line : list) {
                line = line.replaceAll("[{} ]", "");
                if (StringUtils.isEmpty(line))
                    continue;
                String[] entry = line.split("=");
                map.put(keyParser.apply(entry[0]), valueParser.apply(entry[1]));
                Game.log(Level.DEBUG, logMessage, line);
            }
            return map;
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Invalid map while parsing config = " + list);
        }
    }

}

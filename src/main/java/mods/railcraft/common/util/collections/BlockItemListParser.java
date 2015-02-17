/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.collections;

import cpw.mods.fml.common.registry.GameData;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.Level;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.item.Item;


/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class BlockItemListParser {

    public enum ParseType {

        ITEM {
                    @Override
                    public ItemKey makeKey(String entry) throws IllegalArgumentException {
                        String[] tokens = entry.split("#");
                        Item item = GameData.getItemRegistry().getObject(tokens[0]);
                        if (item == null)
                            throw new IllegalArgumentException("Invalid Item Name while parsing config = " + entry);
                        int meta = tokens.length > 1 ? Integer.valueOf(tokens[1]) : -1;
                        return new ItemKey(item, meta);
                    }

                },
        BLOCK {
                    @Override
                    public BlockKey makeKey(String entry) throws IllegalArgumentException {
                        String[] tokens = entry.split("#");
                        Block block = GameData.getBlockRegistry().getObject(tokens[0]);
                        if (block == null)
                            throw new IllegalArgumentException("Invalid Block Name while parsing config = " + entry);
                        int meta = tokens.length > 1 ? Integer.valueOf(tokens[1]) : -1;
                        return new BlockKey(block, meta);
                    }

                };

        public abstract Object makeKey(String entry);

    };

    public enum ValueType {

        INT {

                    @Override
                    public Integer parseValue(String value) {
                        return Integer.valueOf(value);
                    }

                },
        FLOAT {

                    @Override
                    public Float parseValue(String value) {
                        return Float.valueOf(value);
                    }

                };

        public abstract Object parseValue(String value);

    }

    public static <T> Set<T> parseList(String list, String logMessage, ParseType type) {
        try {
            Set<T> set = new HashSet<T>();
            for (String segment : list.replaceAll("[{} ]", "").split("[,;]+")) {
                if (segment.equals(""))
                    continue;
                set.add((T) type.makeKey(segment));
                Game.log(Level.DEBUG, logMessage, segment);
            }
            return set;
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Invalid list while parsing config = " + list);
        }
    }

    public static <T, V> Map<T, V> parseDictionary(String list, String logMessage, ParseType type, ValueType valueType) {
        try {
            Map<T, V> map = new HashMap<T, V>();
            for (String segment : list.replaceAll("[{} ]", "").split("[,;]+")) {
                if (segment.equals(""))
                    continue;
                String[] entry = segment.split("=");
                map.put((T) type.makeKey(entry[0]), (V) valueType.parseValue(entry[1]));
                Game.log(Level.DEBUG, logMessage, segment);
            }
            return map;
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Invalid map while parsing config = " + list);
        }
    }

}

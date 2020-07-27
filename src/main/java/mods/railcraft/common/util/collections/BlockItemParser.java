/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.collections;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import mods.railcraft.common.util.crafting.Ingredients;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.util.Strings;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class BlockItemParser {

    public static String toString(Object obj) {
        if (obj instanceof IBlockState) {
            IBlockState state = (IBlockState) obj;
            Block block = state.getBlock();
            return block.getRegistryName() + "#" + block.getMetaFromState(state);
        }
        if (obj instanceof Ingredient) {
            return Stream.of(((Ingredient) obj).getMatchingStacks())
                    .map(stack -> {
                        String name = Optional.ofNullable(stack.getItem().getRegistryName()).map(Objects::toString).orElse("Null");
                        if (stack.getHasSubtypes() && !InvTools.isWildcard(stack))
                            name += "#" + stack.getMetadata();
                        return name;
                    })
                    .collect(Collectors.toList()).toString();
        }
        return obj.toString();
    }

    public static Set<IBlockState> parseBlock(String line) {
        String[] tokens = line.split("#");
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tokens[0]));
        if (block == null)
            throw new IllegalArgumentException("No matching block found for " + line);
        int meta = tokens.length > 1 ? Integer.valueOf(tokens[1]) : 0;
        //noinspection deprecation
        return Collections.singleton(block.getStateFromMeta(meta));
    }

    public static Set<Ingredient> parseItem(String line) {
        Ingredient ingredient;
        if (line.contains(":")) {
            String[] tokens = line.split("#");
            String[] id = tokens[0].split(":");
            Set<Item> items;
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id[0], id[1]));
            if (item != null) {
                items = Collections.singleton(item);
            } else {
                items = ForgeRegistries.ITEMS.getEntries().stream()
                        .filter(entry -> entry.getKey().getNamespace().equals(id[0]))
                        .filter(entry -> entry.getKey().getPath().matches(id[1]))
                        .map(Map.Entry::getValue)
                        .collect(Collectors.toSet());
            }
            if (items.isEmpty())
                throw new IllegalArgumentException("No matching item found for " + line);
            Range<Integer> metadata;
            if (tokens.length > 1) {
                if (tokens[1].contains("-")) {
                    String[] metaTokens = tokens[1].split("-");
                    metadata = Range.closed(Integer.valueOf(metaTokens[0]), Integer.valueOf(metaTokens[1]));
                } else {
                    metadata = Range.singleton(Integer.valueOf(tokens[1]));
                }
            } else {
                metadata = Range.singleton(OreDictionary.WILDCARD_VALUE);
            }
            ContiguousSet<Integer> metaSet = ContiguousSet.create(metadata, DiscreteDomain.integers());
            ingredient = Ingredient.fromStacks(items.stream()
                    .flatMap(i -> metaSet.stream().map(meta -> new ItemStack(i, 1, meta)))
                    .toArray(ItemStack[]::new));
        } else ingredient = Ingredients.from(line);
        return Collections.singleton(ingredient);
    }

    public static <T> List<T> parseList(String list, String logMessage, Function<String, Collection<T>> keyParser) {
        return parseList(list.replaceAll("[{} ]", "").split("[,;]+"), logMessage, keyParser);
    }

    public static <T> List<T> parseList(String[] list, String logMessage, Function<String, Collection<T>> keyParser) {
        return streamLines(list).flatMap(line -> {
            try {
                Collection<T> entries = keyParser.apply(line);
                entries.forEach(e -> Game.log().msg(Level.INFO, logMessage + ": {0}", toString(e)));
                return entries.stream();
            } catch (Exception ex) {
                Game.log().throwable(Level.WARN, 0, ex, "Invalid list entry while {0}: {1}", logMessage, line);
            }
            return Stream.empty();
        }).collect(Collectors.toList());
    }

    public static <T, V> Map<T, V> parseDictionary(String[] list, String logMessage, Function<String, Collection<T>> keyParser, Function<String, V> valueParser) {
        Map<T, V> map = new HashMap<>();
        streamLines(list).forEach(line -> {
            try {
                String[] entry = line.split("=");
                V value = valueParser.apply(entry[1]);
                keyParser.apply(entry[0]).forEach(key -> {
                    Game.log().msg(Level.INFO, logMessage + ": {0}", toString(key) + "=" + value);
                    map.put(key, value);
                });
            } catch (Exception ex) {
                Game.log().throwable(Level.WARN, 0, ex, "Invalid map entry while {0}: {1}", logMessage, line);
            }
        });
        return map;
    }

    private static Stream<String> streamLines(String[] list) {
        return Arrays.stream(list).map(s -> s.replaceAll("[{} ]", "")).filter(Strings::isNotBlank);
    }

}

/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.forge;

import cpw.mods.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Level;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CraftingPlugin {

    public static void addFurnaceRecipe(ItemStack input, ItemStack output, float xp) {
        if (input == null && output == null) {
            Game.logTrace(Level.WARN, "Tried to define invalid furnace recipe, the input and output were both null. Skipping");
            return;
        }
        if (input == null) {
            Game.logTrace(Level.WARN, "Tried to define invalid furnace recipe for {0}, the input was null. Skipping", output.getUnlocalizedName());
            return;
        }
        if (output == null) {
            Game.logTrace(Level.WARN, "Tried to define invalid furnace recipe for {0}, the output was null. Skipping", input.getUnlocalizedName());
            return;
        }
        FurnaceRecipes.smelting().func_151394_a(input, output, xp);
    }

    public static void addShapedRecipe(ItemStack result, Object... input) {
        if (result == null || result.stackSize <= 0) {
            Game.logTrace(Level.WARN, "Tried to define invalid shaped recipe, the result was null or zero. Skipping");
            return;
        }
        boolean oreRecipe = false;
        for (Object obj : input) {
            if (obj instanceof String) {
                if (((String) obj).length() > 3)
                    oreRecipe = true;
            } else if (obj instanceof Boolean)
                oreRecipe = true;
            else if (obj == null) {
                Game.logTrace(Level.WARN, "Tried to define invalid shaped recipe for {0}, a necessary item was probably disabled. Skipping", result.getUnlocalizedName());
                return;
            }
        }
        if (oreRecipe) {
            IRecipe recipe = new ShapedOreRecipe(result, input);
            addRecipe(recipe);
        } else
            GameRegistry.addRecipe(result, input);
    }

    /**
     *
     * @param result
     * @param input
     */
    public static void addShapelessRecipe(ItemStack result, Object... input) {
        if (result == null || result.stackSize <= 0) {
            Game.logTrace(Level.WARN, "Tried to define invalid shapeless recipe, the result was null or zero. Skipping");
            return;
        }
        boolean oreRecipe = false;
        for (Object obj : input) {
            if (obj instanceof String)
                oreRecipe = true;
            else if (obj == null) {
                Game.logTrace(Level.WARN, "Tried to define invalid shapeless recipe for {0}, a necessary item was probably disabled. Skipping", result.getUnlocalizedName());
                return;
            }
        }
        if (oreRecipe) {
            IRecipe recipe = new ShapelessOreRecipe(result, input);
            addRecipe(recipe);
        } else
            GameRegistry.addShapelessRecipe(result, input);
    }

    public static void addRecipe(IRecipe recipe) {
        GameRegistry.addRecipe(recipe);
    }

}

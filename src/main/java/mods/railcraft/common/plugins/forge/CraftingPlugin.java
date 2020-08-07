/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.forge;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterators;
import com.google.common.collect.Multiset;
import com.google.common.collect.PeekingIterator;
import mods.railcraft.api.core.IIngredientSource;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.api.crafting.IRecipeBuilder;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.modules.RailcraftModuleManager;
import mods.railcraft.common.util.crafting.Ingredients;
import mods.railcraft.common.util.crafting.InvalidRecipeException;
import mods.railcraft.common.util.crafting.ShapedRailcraftRecipe;
import mods.railcraft.common.util.crafting.ShapelessRailcraftRecipe;
import mods.railcraft.common.util.misc.Code;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static mods.railcraft.common.util.inventory.InvTools.isEmpty;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CraftingPlugin {
    private static ResourceLocation DEFAULT_GROUP = new ResourceLocation("railcraft", "crafting");
    private static List<IRecipeBuilder<?>> recipeBuilders = new LinkedList<>();
    private static Multiset<String> recipeNames = HashMultiset.create();

    // TODO add descriptor
    public static void addFurnaceRecipe(@Nullable ItemStack input, @Nullable ItemStack output, float xp) {
        if (isEmpty(input)) {
            if (isEmpty(output)) {
                Game.log().trace(Level.WARN, "Tried to define invalid furnace recipe, the input and output were both empty. Skipping");
                return;
            }
            Game.log().trace(Level.WARN, "Tried to define invalid furnace recipe for {0}, the input was null. Skipping", output.getTranslationKey());
            return;
        }
        if (isEmpty(output)) {
            Game.log().trace(Level.WARN, "Tried to define invalid furnace recipe for {0}, the output was null. Skipping", input.getTranslationKey());
            return;
        }
        canRegisterRecipes();
        FurnaceRecipes.instance().addSmeltingRecipe(input, output, xp);
    }

    public static Object[] cleanRecipeArray(ResourceLocation name, Object[] recipeArray) throws InvalidRecipeException {
        List<Object> newParameters = new ArrayList<>(recipeArray.length);
        PeekingIterator<Object> it = Iterators.peekingIterator(Iterators.forArray(recipeArray));
        while (it.hasNext()) {
            Object obj = it.peek();
            if (obj instanceof String || obj instanceof Boolean)
                newParameters.add(it.next());
            else
                break;
        }
        while (it.hasNext()) {
            Object obj = it.next();
            if (obj instanceof Character) {
                newParameters.add(obj);
                continue;
            }
            if (obj instanceof IIngredientSource && it.hasNext() && it.peek() instanceof IVariantEnum) {
                obj = ((IIngredientSource) obj).getIngredient((IVariantEnum) it.next());
            }
            Ingredient ingredient = Ingredients.from(obj);
            if (ingredient == Ingredient.EMPTY)
                throw new MissingIngredientException(name);
            newParameters.add(ingredient);
        }
        return newParameters.toArray();
    }

    private static void validateOutput(@Nullable ResourceLocation name, ItemStack output, Object[] recipeArray) throws InvalidRecipeException {
        if (isEmpty(output)) {
            if (name == null)
                name = Game.getActiveModResource("unknown");
            throw new InvalidRecipeException("Tried to define invalid recipe named {0}, the output was null or zero. Skipping.", name);
        }
    }

    private static ResourceLocation checkName(@Nullable ResourceLocation name, ItemStack output, Object[] recipeArray) throws InvalidRecipeException {
        if (name != null)
            return name;
        validateOutput(new ResourceLocation("unknown:unknown"), output, recipeArray);
        return getNameFromOutput(output);
    }

    private static Object[] processRecipe(@Nullable ResourceLocation name, ItemStack output, Object[] recipeArray) throws InvalidRecipeException {
        validateOutput(name, output, recipeArray);
        return cleanRecipeArray(name, recipeArray);
    }

    // TODO replace all this junk with a builder
    public static void addShapedRecipe(ItemStack result, Object... recipeArray) {
        addShapedRecipe(null, DEFAULT_GROUP, result, recipeArray);
    }

    public static void addShapedRecipe(String name, ItemStack result, Object... recipeArray) {
        addShapedRecipe(new ResourceLocation(name), DEFAULT_GROUP, result, recipeArray);
    }

    public static void addShapedRecipe(ResourceLocation name, ItemStack result, Object... recipeArray) {
        addShapedRecipe(name, DEFAULT_GROUP, result, recipeArray);
    }

    public static void addShapedRecipe(@Nullable ResourceLocation name, ResourceLocation group, ItemStack output, Object... recipeArray) {
        IRecipe recipe;
        try {
            name = checkName(name, output, recipeArray);
            recipe = makeShapedRecipe(name, group, output, recipeArray);
        } catch (InvalidRecipeException ex) {
            Game.log().trace(Level.WARN, ex.getRawMessage());
            return;
        }
        addRecipe(name, recipe);
    }

    @Deprecated
    public static void addShapelessRecipe(ItemStack result, Object... recipeArray) {
        addShapelessRecipe(null, DEFAULT_GROUP, result, recipeArray);
    }

    public static void addShapelessRecipe(String name, ItemStack result, Object... recipeArray) {
        addShapelessRecipe(new ResourceLocation(name), DEFAULT_GROUP, result, recipeArray);
    }

    public static void addShapelessRecipe(@Nullable ResourceLocation name, ResourceLocation group, ItemStack output, Object... recipeArray) {
        IRecipe recipe;
        try {
            name = checkName(name, output, recipeArray);
            recipe = makeShapelessRecipe(name, group, output, recipeArray);
        } catch (InvalidRecipeException ex) {
            Game.log().msg(Level.WARN, ex.getRawMessage());
            return;
        }
        addRecipe(name, recipe);
    }

    public static void addRecipe(String name, IRecipe recipe) {
        addRecipe(new ResourceLocation(name), recipe);
    }

    public static void addRecipe(IRecipe recipe) {
        canRegisterRecipes();
        ForgeRegistries.RECIPES.register(recipe);
    }

    public static void addRecipe(ResourceLocation name, IRecipe recipe) {
        addRecipe(recipe.setRegistryName(name));
    }

    public static IRecipe makeShapedRecipe(@Nullable ResourceLocation name, ResourceLocation group, ItemStack output, Object... components) throws InvalidRecipeException {
        Object[] cleanArray = processRecipe(name, output, components);
        CraftingHelper.ShapedPrimer primer = CraftingHelper.parseShaped(cleanArray);
        return new ShapedRailcraftRecipe(group.toString(), primer.width, primer.height, primer.input, output);
    }

    public static IRecipe makeShapelessRecipe(@Nullable ResourceLocation name, ResourceLocation group, ItemStack output, Object... components) throws InvalidRecipeException {
        Object[] cleanArray = processRecipe(name, output, components);
        return new ShapelessRailcraftRecipe(group.toString(), output,
                Arrays.stream(cleanArray).map(Ingredients::from)
                        .collect(Collectors.toCollection(NonNullList::create)));
    }

    public static void canRegisterRecipes() {
        if (RailcraftModuleManager.getStage().compareTo(RailcraftModuleManager.Stage.INIT) < 0)
            throw new IllegalStateException("Recipe registered too soon.");
    }

    public static ResourceLocation getNameFromOutput(ItemStack output) {
        ResourceLocation itemId = Objects.requireNonNull(output.getItem().getRegistryName());
        if (output.getHasSubtypes()) {
            return getNameFromOutput(new ResourceLocation(itemId.getNamespace(), itemId.getPath() + "#" + output.getMetadata()));
        }
        return getNameFromOutput(itemId);
    }

    public static ResourceLocation getNameFromOutput(ResourceLocation location) {
        String name = location.getNamespace().equals(Railcraft.MOD_ID) ? location.getPath() : location.getNamespace() + "_" + location.getPath();
        recipeNames.add(name);
        return new ResourceLocation(RailcraftConstantsAPI.MOD_ID, name + "$" + recipeNames.count(name));
    }

    public static @Nullable ResourceLocation guessName(@Nullable Object input) {
        if (input instanceof IForgeRegistryEntry) {
            return getNameFromOutput(Objects.requireNonNull(((IForgeRegistryEntry) input).getRegistryName()));
        } else if (input instanceof ItemStack) {
            return getNameFromOutput((ItemStack) input);
        } else if (input instanceof String) {
            return getNameFromOutput(new ResourceLocation("ore", (String) input));
        } else if (input instanceof IRailcraftObjectContainer) {
            IRailcraftObjectContainer<?> container = Code.cast(input);
            return getNameFromOutput(container.getRegistryName());
        }
        return null;
    }

    public static void addBuilder(IRecipeBuilder<?> builder) {
        recipeBuilders.add(builder);
    }

    public static void areAllBuildersRegistered() {
        Optional<IRecipeBuilder<?>> recipeBuilder =
                recipeBuilders.stream().filter(IRecipeBuilder::notRegistered).findFirst();
        if (recipeBuilder.isPresent())
            throw new IllegalStateException(String.format("Incomplete recipe definition detected for %s.",
                    recipeBuilder.get().getName()));
        recipeBuilders.clear();
    }

    private static class MissingIngredientException extends InvalidRecipeException {
        public MissingIngredientException(ResourceLocation name) {
            super("Tried to define {0} as a recipe, but it was missing an ingredient. A necessary item was probably disabled. Skipping", name);
        }
    }
}

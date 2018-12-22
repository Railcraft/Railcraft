/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.forge;

import com.google.common.collect.Lists;
import com.google.gson.*;
import mods.railcraft.api.core.IIngredientSource;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.common.modules.RailcraftModuleManager;
import mods.railcraft.common.util.crafting.IngredientFluid;
import mods.railcraft.common.util.crafting.InvalidRecipeException;
import mods.railcraft.common.util.crafting.RemainingItemShapedRecipe;
import mods.railcraft.common.util.crafting.RemainingItemShapelessRecipe;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static mods.railcraft.common.util.inventory.InvTools.isEmpty;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class CraftingPlugin {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final ResourceLocationGenerator gen = new ResourceLocationGenerator();
    private static final ResourceLocation GROUP = RailcraftConstantsAPI.locationOf("crafting_plugin");

    public static void addFurnaceRecipe(ItemStack input, ItemStack output, float xp) {
        if (isEmpty(input)) {
            if (isEmpty(output)) {
                Game.logTrace(Level.WARN, "Tried to define invalid furnace recipe, the input and output were both null. Skipping");
                return;
            }
            Game.logTrace(Level.WARN, "Tried to define invalid furnace recipe for {0}, the input was null. Skipping", output.getTranslationKey());
            return;
        }
        if (isEmpty(output)) {
            Game.logTrace(Level.WARN, "Tried to define invalid furnace recipe for {0}, the output was null. Skipping", input.getTranslationKey());
            return;
        }

        if (isBeforeInit()) {
            INSTANCE.addFurnaceRecipeWaiter(input, output, xp);
        } else
            FurnaceRecipes.instance().addSmeltingRecipe(input, output, xp);
    }

    private static RecipePrototype process(ItemStack result, Object... recipeArray) throws InvalidRecipeException {
        if (isEmpty(result)) {
            throw new InvalidRecipeException("Tried to define invalid recipe, the result was null or zero. Skipping");
        }
        NonNullList<Ingredient> ingredients = fetchIngredients(result, recipeArray);
        return new RecipePrototype(result, ingredients);
    }

    private static Object[] updateShapedArray(ItemStack result, Object... recipeArray) throws InvalidRecipeException {
        List<Object> ret = new ArrayList<>();
        int i = 0;
        while (recipeArray[i] instanceof String || recipeArray[i] instanceof Boolean) {
            ret.add(recipeArray[i]);
            i++;
        }
        for (; i < recipeArray.length; i++) {
            Object obj = recipeArray[i];
            if (obj instanceof Character || obj instanceof Boolean) {
                ret.add(recipeArray[i]);
            } else if (obj instanceof IIngredientSource) {
                Object obj2 = i + 1 < recipeArray.length ? recipeArray[i + 1] : null;
                if (obj2 instanceof IVariantEnum) {
                    ret.add(((IIngredientSource) obj).getIngredient((IVariantEnum) obj2));
                    i++;
                } else {
                    ret.add(((IIngredientSource) obj).getIngredient());
                }
            } else if (obj == null) {
                throw new MissingIngredientException(result);
            } else {
                Ingredient ingredient = getIngredient(obj);
                if (ingredient == null) {
                    throw new MissingIngredientException(result);
                }
                ret.add(ingredient);
            }
        }
        return ret.toArray();
    }

    private static NonNullList<Ingredient> fetchIngredients(ItemStack result, Object... recipeArray) throws InvalidRecipeException {
        List<Object> recipeList = Lists.newArrayList(recipeArray);
        NonNullList<Ingredient> ingredients = NonNullList.create();
        for (int i = 0; i < recipeList.size(); i++) {
            Object obj = recipeList.get(i);
            if (obj instanceof IIngredientSource) {
                Object obj2 = i + 1 < recipeList.size() ? recipeList.get(i + 1) : null;
                if (obj2 instanceof IVariantEnum) {
                    ingredients.add(((IIngredientSource) obj).getIngredient((IVariantEnum) obj2));
                    recipeList.remove(i + 1);
                } else {
                    ingredients.add(((IIngredientSource) obj).getIngredient());
                }
                if (recipeList.get(i) == null)
                    throw new MissingIngredientException(result);
            } else if (obj == null) {
                throw new MissingIngredientException(result);
            } else {
                Ingredient ingredient = getIngredient(obj);
                if (ingredient == null) {
                    throw new MissingIngredientException(result);
                }
                ingredients.add(ingredient);
            }
        }
        return ingredients;
    }

    public static @Nullable Ingredient getIngredient(Object source) {
        if (source instanceof FluidStack) {
            return new IngredientFluid((FluidStack) source);
        }
        return CraftingHelper.getIngredient(source);
    }

    public static Ingredient getIngredient(Block source) {
        return Ingredient.fromStacks(new ItemStack(source, 1, OreDictionary.WILDCARD_VALUE));
    }

    public static void addRecipe(ItemStack result, Object... recipeArray) {
        Object[] array;
        try {
            array = updateShapedArray(result, recipeArray);
        } catch (InvalidRecipeException ex) {
            Game.logTrace(Level.WARN, ex.getRawMessage());
            return;
        }

        ShapedPrimer primer = CraftingHelper.parseShaped(array);
        IRecipe recipe = new RemainingItemShapedRecipe(GROUP.toString(), primer.width, primer.height, primer.input, result).setRegistryName(gen.next());
        addRecipe(recipe);

        /*
        ProcessedRecipe processedRecipe;
        try {
            processedRecipe = processRecipe(RecipeType.SHAPED, result, recipeArray);
        } catch (InvalidRecipeException ex) {
            Game.logTrace(Level.WARN, ex.getRawMessage());
            return;
        }
        if (processedRecipe.hasFluid) {
            addRecipe(new ShapedFluidRecipe(processedRecipe.result, processedRecipe.recipeArray));
            return;
        }
        if (processedRecipe.isOreRecipe) {
            addRecipe(new ShapedOreRecipe(gen.next(), processedRecipe.result, processedRecipe.recipeArray));
        } else {
            if (isBeforeInit())
                INSTANCE.addShapedRecipeWaiter(processedRecipe.result, processedRecipe.recipeArray);
            else GameRegistry.addShapedRecipe(gen.next(), GROUP, processedRecipe.result, processedRecipe.recipeArray);
        }
        */
    }

    public static void addShapelessRecipe(ItemStack result, Object... recipeArray) {
        RecipePrototype prototype;
        try {
            prototype = process(result, recipeArray);
        } catch (InvalidRecipeException ex) {
            Game.logTrace(Level.WARN, ex.getRawMessage());
            return;
        }

        IRecipe recipe = new RemainingItemShapelessRecipe(GROUP.toString(), prototype.result, prototype.ingredients).setRegistryName(gen.next());
        addRecipe(recipe);
        /*
        ProcessedRecipe processedRecipe;
        try {
            processedRecipe = processRecipe(RecipeType.SHAPELESS, result, recipeArray);
        } catch (InvalidRecipeException ex) {
            Game.logTrace(Level.WARN, ex.getRawMessage());
            return;
        }
        if (processedRecipe.hasFluid) {
            addRecipe(new ShapelessFluidRecipe(processedRecipe.result, processedRecipe.recipeArray));
            return;
        }
        if (processedRecipe.isOreRecipe) {
            addRecipe(new ShapelessOreRecipe(gen.next(), processedRecipe.result, processedRecipe.recipeArray));
        } else {
            if (isBeforeInit())
                INSTANCE.addShapelessRecipeWaiter(processedRecipe.result, processedRecipe.recipeArray);
            else {
                Ingredient[] ingredients = new Ingredient[processedRecipe.recipeArray.length];
                for (int i = 0; i < processedRecipe.recipeArray.length; i++) {
                    ingredients[i] = getIngredient(processedRecipe.recipeArray[i]);
                }
                GameRegistry.addShapelessRecipe(gen.next(), GROUP, processedRecipe.result, ingredients);
            }
//                GameRegistry.addShapelessRecipe(gen.next(), GROUP, processedRecipe.result, processedRecipe.recipeArray);
        }
        */
    }

//    @SuppressWarnings("deprecation")
//    public static ItemStack getIngredientStack(IRailcraftRecipeIngredient ingredient, int qty) {
//        Object object = ingredient.getRecipeObject();
//        if (object instanceof ItemStack) {
//            ItemStack stack = ((ItemStack) object).copy();
//            setSize(stack, qty);
//            return stack;
//        }
//        if (object instanceof Item)
//            return new ItemStack((Item) object, qty);
//        if (object instanceof Block)
//            return new ItemStack((Block) object, qty);
//        if (object instanceof String)
//            return OreDictPlugin.getOre((String) object, qty);
//        throw new RuntimeException("Unknown ingredient object");
//    }

    private static final class MissingIngredientException extends InvalidRecipeException {
        MissingIngredientException(RecipeType recipeType, ItemStack result) {
            super("Tried to define invalid {0} recipe for {1}, a necessary item was probably disabled. Skipping", recipeType, result.getTranslationKey());
        }

        MissingIngredientException(ItemStack result) {
            super("Tried to define invalid recipe for {0}, a necessary item was probably disabled. Skipping", result.getTranslationKey());
        }
    }

    private static final class RecipePrototype {
        public final ItemStack result;
        public final NonNullList<Ingredient> ingredients;

        RecipePrototype(ItemStack result, NonNullList<Ingredient> ingredients) {
            this.result = result;
            this.ingredients = ingredients;
        }
    }

    public static FluidStack getFluidStackFromRecipeFile(JsonObject json) {
        String name = JsonUtils.getString(json, "fluid");

        Fluid fluid = FluidRegistry.getFluid(name);

        if (fluid == null)
            throw new JsonSyntaxException("Unknown fluid '" + name + "'");

        int amount = JsonUtils.getInt(json, "amount");

        if (json.has("nbt")) {
            // Lets hope this works? Needs test
            try {
                JsonElement element = json.get("nbt");
                NBTTagCompound nbt;
                if (element.isJsonObject())
                    nbt = JsonToNBT.getTagFromJson(GSON.toJson(element));
                else
                    nbt = JsonToNBT.getTagFromJson(element.getAsString());

                return new FluidStack(fluid, amount, nbt);
            } catch (NBTException e) {
                throw new JsonSyntaxException("Invalid NBT Entry: " + e);
            }
        }

        return new FluidStack(fluid, amount);
    }

    public static IRecipe createDummyRecipe(ResourceLocation namespace) {
        return new DummyRecipe(namespace);
    }

    private static final class DummyRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

        DummyRecipe(ResourceLocation name) {
            setRegistryName(name);
        }

        @Override
        public boolean matches(InventoryCrafting inv, World worldIn) {
            return false;
        }

        @Override
        public ItemStack getCraftingResult(InventoryCrafting inv) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean canFit(int width, int height) {
            return false;
        }

        @Override
        public ItemStack getRecipeOutput() {
            return ItemStack.EMPTY;
        }
    }

    public static Iterator<ResourceLocation> getGenerator() {
        return gen;
    }

    private static final class ResourceLocationGenerator implements Iterator<ResourceLocation> {
        int now;

        ResourceLocationGenerator() {
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public ResourceLocation next() {
            return RailcraftConstantsAPI.locationOf("recipe" + (now++));
        }
    }

    // Dumpster zone below
    // =================================================================================//

    @Deprecated
    public static Object[] cleanRecipeArray(RecipeType recipeType, ItemStack result, Object... recipeArray) throws InvalidRecipeException {
        List<Object> recipeList = Lists.newArrayList(recipeArray);
        for (int i = 0; i < recipeList.size(); i++) {
            Object obj = recipeList.get(i);
            if (obj instanceof IIngredientSource) {
                Object obj2 = i + 1 < recipeList.size() ? recipeList.get(i + 1) : null;
                if (obj2 instanceof IVariantEnum) {
                    recipeList.set(i, ((IIngredientSource) obj).getIngredient((IVariantEnum) obj2));
                    recipeList.remove(i + 1);
                } else {
                    recipeList.set(i, ((IIngredientSource) obj).getIngredient());
                }
                if (recipeList.get(i) == null)
                    throw new MissingIngredientException(recipeType, result);
            } else if (obj == null) {
                throw new MissingIngredientException(recipeType, result);
            }
        }
        return recipeList.toArray();
    }

    private static void getExtraInfo(RecipeType recipeType, ItemStack result, boolean[] extraInfo, Object... recipeArray) throws InvalidRecipeException {
        Arrays.fill(extraInfo, false);
        for (Object obj : recipeArray) {
            if (!extraInfo[0]) {
                if (obj instanceof String) {
                    if (recipeType == RecipeType.SHAPELESS || ((String) obj).length() > 3)
                        extraInfo[0] = true;
                } else if (recipeType == RecipeType.SHAPED && obj instanceof Boolean)
                    extraInfo[0] = true;
                else if (obj == null) {
                    throw new MissingIngredientException(recipeType, result);
                }
            }
            if (!extraInfo[1] && obj instanceof FluidStack)
                extraInfo[1] = true;
        }
    }

    @Deprecated
    @Contract("_, null, _ -> fail")
    public static ProcessedRecipe processRecipe(RecipeType recipeType, @Nullable ItemStack result, Object... recipeArray) throws InvalidRecipeException {
        if (isEmpty(result)) {
            throw new InvalidRecipeException("Tried to define invalid {0} recipe, the result was null or zero. Skipping", recipeType);
        }
        recipeArray = cleanRecipeArray(recipeType, result, recipeArray);
        boolean[] info = new boolean[2];
        getExtraInfo(recipeType, result, info, recipeArray);
        return new ProcessedRecipe(info[0], info[1], result, recipeArray);
    }

    public enum RecipeType {
        SHAPED, SHAPELESS
    }

    public static class ProcessedRecipe {
        public final ItemStack result;
        public final Object[] recipeArray;
        public final boolean isOreRecipe;
        public final boolean hasFluid;

        ProcessedRecipe(boolean isOreRecipe, boolean fluid, ItemStack result, Object... recipeArray) {
            this.isOreRecipe = isOreRecipe;
            this.result = result;
            this.recipeArray = recipeArray;
            this.hasFluid = fluid;
        }
    }

    public static void addRecipe(IRecipe recipe) {
        if (recipe.getRegistryName() == null)
            recipe.setRegistryName(gen.next());
        if (isBeforeInit())
            INSTANCE.addRecipeWaiter(recipe);
        else
            ForgeRegistries.RECIPES.register(recipe);
    }

    public static boolean isBeforeInit() {
        return RailcraftModuleManager.getStage().compareTo(RailcraftModuleManager.Stage.INIT) < 0;
    }

    public static void init() {
        INSTANCE.waitingRecipes.forEach(Runnable::run);
    }

    public static final CraftingPlugin INSTANCE = new CraftingPlugin();
    @Deprecated
    private List<Runnable> waitingRecipes = new ArrayList<>();

    @Deprecated
    private void add(Runnable e) {
        Game.logTrace(Level.WARN, 7, "Recipes registered before INIT! At:");
        waitingRecipes.add(e);
    }

    @Deprecated
    private void addRecipeWaiter(IRecipe recipe) {
        add(() -> {
        });
    }

    @Deprecated
    private void addShapedRecipeWaiter(ItemStack stack, Object... args) {
        add(() -> {
        });
    }

    //TODO fix all these messes
    private void addShapelessRecipeWaiter(ItemStack stack, Object... args) {
        add(() -> {
        });
    }

    private void addFurnaceRecipeWaiter(ItemStack input, ItemStack output, float xp) {
        add(() -> FurnaceRecipes.instance().addSmeltingRecipe(input, output, xp));
    }

}

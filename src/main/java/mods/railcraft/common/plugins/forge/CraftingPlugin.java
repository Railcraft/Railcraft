/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.forge;

import com.google.common.collect.Lists;
import mods.railcraft.api.core.IRailcraftRecipeIngredient;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.common.modules.RailcraftModuleManager;
import mods.railcraft.common.util.crafting.InvalidRecipeException;
import mods.railcraft.common.util.crafting.ShapedFluidRecipe;
import mods.railcraft.common.util.crafting.ShapelessFluidRecipe;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static mods.railcraft.common.util.inventory.InvTools.isEmpty;
import static mods.railcraft.common.util.inventory.InvTools.setSize;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
//TODO CraftingHelper
public final class CraftingPlugin {

    private static final ResourceLocationGenerator gen = new ResourceLocationGenerator();
    private static final ResourceLocation GROUP = RailcraftConstantsAPI.locationOf("crafting_plugin");

    public static void addFurnaceRecipe(@Nullable ItemStack input, @Nullable ItemStack output, float xp) {
        if (isEmpty(input)) {
            if (isEmpty(output)) {
                Game.logTrace(Level.WARN, "Tried to define invalid furnace recipe, the input and output were both null. Skipping");
                return;
            }
            Game.logTrace(Level.WARN, "Tried to define invalid furnace recipe for {0}, the input was null. Skipping", output.getUnlocalizedName());
            return;
        }
        if (isEmpty(output)) {
            Game.logTrace(Level.WARN, "Tried to define invalid furnace recipe for {0}, the output was null. Skipping", input.getUnlocalizedName());
            return;
        }

        if (isBeforeInit()) {
            INSTANCE.addFurnaceRecipeWaiter(input, output, xp);
        } else
            FurnaceRecipes.instance().addSmeltingRecipe(input, output, xp);
    }

    @Deprecated
    public static Object[] cleanRecipeArray(RecipeType recipeType, ItemStack result, Object... recipeArray) throws InvalidRecipeException {
        List<Object> recipeList = Lists.newArrayList(recipeArray);
        for (int i = 0; i < recipeList.size(); i++) {
            Object obj = recipeList.get(i);
            if (obj instanceof IRailcraftRecipeIngredient) {
                Object obj2 = i + 1 < recipeList.size() ? recipeList.get(i + 1) : null;
                if (obj2 instanceof IVariantEnum) {
                    recipeList.set(i, ((IRailcraftRecipeIngredient) obj).getRecipeObject((IVariantEnum) obj2));
                    recipeList.remove(i + 1);
                } else {
                    recipeList.set(i, ((IRailcraftRecipeIngredient) obj).getRecipeObject());
                }
                if (recipeList.get(i) == null)
                    throw new MissingIngredientException(recipeType, result);
            } else if (obj == null) {
                throw new MissingIngredientException(recipeType, result);
            }
        }
        return recipeList.toArray();
    }

    @Deprecated
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

    @Deprecated
    public static void addRecipe(@Nullable ItemStack result, Object... recipeArray) {
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
    }

    @Deprecated
    public static void addShapelessRecipe(@Nullable ItemStack result, Object... recipeArray) {
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
            else Game.log(Level.ERROR, "Shapeless recipes");
//                GameRegistry.addShapelessRecipe(gen.next(), GROUP, processedRecipe.result, processedRecipe.recipeArray);
        }
    }

    @Deprecated
    public static void addRecipe(IRecipe recipe) {
        if (recipe.getRegistryName() == null)
            recipe.setRegistryName(gen.next());
        if (isBeforeInit())
            INSTANCE.addRecipeWaiter(recipe);
        else
            ForgeRegistries.RECIPES.register(recipe);
    }

    @Deprecated
    public static IRecipe makeVanillaShapedRecipe(ItemStack output, Object... components) {
        String s = "";
        int index = 0;
        int width = 0;
        int height = 0;
        if (components[index] instanceof String[]) {
            String as[] = (String[]) components[index++];
            for (String s2 : as) {
                height++;
                width = s2.length();
                s = (new StringBuilder()).append(s).append(s2).toString();
            }
        } else {
            while (components[index] instanceof String) {
                String s1 = (String) components[index++];
                height++;
                width = s1.length();
                s = (new StringBuilder()).append(s).append(s1).toString();
            }
        }
        HashMap<Character, ItemStack> hashMap = new HashMap<>();
        for (; index < components.length; index += 2) {
            Character character = (Character) components[index];
            ItemStack itemStack = InvTools.emptyStack();
            if (components[index + 1] instanceof Item) {
                itemStack = new ItemStack((Item) components[index + 1]);
            } else if (components[index + 1] instanceof Block) {
                itemStack = new ItemStack((Block) components[index + 1], 1, -1);
            } else if (components[index + 1] instanceof ItemStack) {
                itemStack = (ItemStack) components[index + 1];
            }
            hashMap.put(character, itemStack);
        }

        ItemStack[] recipeArray = new ItemStack[width * height];
        for (int i1 = 0; i1 < width * height; i1++) {
            char c = s.charAt(i1);
            if (hashMap.containsKey(c)) {
                recipeArray[i1] = hashMap.get(c).copy();
            } else {
                recipeArray[i1] = InvTools.emptyStack();
            }
        }

        return DUMMY;
//        return new ShapedRecipes("railcraft:crafting_plugin", width, height, recipeArray, output);
    }

    @Deprecated
    public static IRecipe makeVanillaShapelessRecipe(ItemStack output, Object... components) {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        for (Object obj : components) {
            if (obj instanceof ItemStack) {
                ingredients.add(Ingredient.fromStacks(((ItemStack) obj).copy()));
                continue;
            }
            if (obj instanceof Item) {
                ingredients.add(Ingredient.fromItem((Item) obj));
                continue;
            }
            if (obj instanceof Block) {
                ingredients.add(Ingredient.fromItem(Item.getItemFromBlock((Block) obj)));
            }
        }

        IRecipe result = new ShapelessRecipes(GROUP.toString(), output, ingredients);
        result.setRegistryName(gen.next());
        return result;
    }

    public static NonNullList<ItemStack> emptyContainers(InventoryCrafting inv) {
        ItemStack[] grid = new ItemStack[inv.getSizeInventory()];

        for (int i = 0; i < grid.length; ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            grid[i] = ForgeHooks.getContainerItem(itemstack);
        }

        return NonNullList.from(ItemStack.EMPTY, grid);
    }

    @Deprecated
    public static ItemStack getIngredientStack(IRailcraftRecipeIngredient ingredient, int qty) {
        Object object = ingredient.getRecipeObject();
        if (object instanceof ItemStack) {
            ItemStack stack = ((ItemStack) object).copy();
            setSize(stack, qty);
            return stack;
        }
        if (object instanceof Item)
            return new ItemStack((Item) object, qty);
        if (object instanceof Block)
            return new ItemStack((Block) object, qty);
        if (object instanceof String)
            return OreDictPlugin.getOre((String) object, qty);
        throw new RuntimeException("Unknown ingredient object");
    }

    @Deprecated
    public enum RecipeType {
        SHAPED, SHAPELESS
    }

    private static class MissingIngredientException extends InvalidRecipeException {
        public MissingIngredientException(RecipeType recipeType, ItemStack result) {
            super("Tried to define invalid {0} recipe for {1}, a necessary item was probably disabled. Skipping", recipeType, result.getUnlocalizedName());
        }
    }

    @Deprecated
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

    public static boolean isBeforeInit() {
        return RailcraftModuleManager.getStage().compareTo(RailcraftModuleManager.Stage.INIT) < 0;
    }

    @Deprecated
    public static void onInit() {
        INSTANCE.waitingRecipes.forEach(Runnable::run);
    }

    public static final CraftingPlugin INSTANCE = new CraftingPlugin();
    @Deprecated
    private List<Runnable> waitingRecipes;

    CraftingPlugin() {
        waitingRecipes = new ArrayList<>();
    }

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

    //TODO fix all these messs
    private void addShapelessRecipeWaiter(ItemStack stack, Object... args) {
        add(() -> {
        });
    }

    private void addFurnaceRecipeWaiter(ItemStack input, ItemStack output, float xp) {
        add(() -> FurnaceRecipes.instance().addSmeltingRecipe(input, output, xp));
    }

    public static IRecipe disabledRecipe(ResourceLocation namespace) {
        return new DummyRecipe(namespace);
    }

    static final IRecipe DUMMY = new DummyRecipe(RailcraftConstantsAPI.locationOf("crafting_plugin"));

    private static final class DummyRecipe implements IRecipe {

        private final ResourceLocation name;

        DummyRecipe(ResourceLocation name) {
            this.name = name;
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

        @Override
        public IRecipe setRegistryName(ResourceLocation name) {
            return this;
        }

        @Override
        public ResourceLocation getRegistryName() {
            return name;
        }

        @Override
        public Class<IRecipe> getRegistryType() {
            return IRecipe.class;
        }
    }

    private static final class ResourceLocationGenerator {
        int now = 0;

        ResourceLocationGenerator() {
        }

        ResourceLocation next() {
            return RailcraftConstantsAPI.locationOf("recipe" + (now++));
        }
    }

}

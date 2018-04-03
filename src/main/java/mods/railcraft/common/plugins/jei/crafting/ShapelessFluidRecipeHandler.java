//package mods.railcraft.common.plugins.jei.crafting;
//
//import mezz.jei.api.IJeiHelpers;
//import mezz.jei.api.recipe.IRecipeHandler;
//import mezz.jei.api.recipe.IRecipeWrapper;
//import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
//import mezz.jei.util.ErrorUtil;
//import mezz.jei.util.Log;
//import mods.railcraft.common.util.crafting.ShapelessFluidRecipe;
//
//import java.util.List;
//
//public class ShapelessFluidRecipeHandler implements IRecipeHandler<ShapelessFluidRecipe> {
//    private final IJeiHelpers jeiHelpers;
//
//    public ShapelessFluidRecipeHandler(IJeiHelpers jeiHelpers) {
//        this.jeiHelpers = jeiHelpers;
//    }
//
//    @Override
//    public Class<ShapelessFluidRecipe> getRecipeClass() {
//        return ShapelessFluidRecipe.class;
//    }
//
//    @Override
//    public String getRecipeCategoryUid() {
//        return VanillaRecipeCategoryUid.CRAFTING;
//    }
//
//    @Override
//    public String getRecipeCategoryUid(ShapelessFluidRecipe recipe) {
//        return VanillaRecipeCategoryUid.CRAFTING;
//    }
//
//    @Override
//    public IRecipeWrapper getRecipeWrapper(ShapelessFluidRecipe recipe) {
//        return new ShapelessFluidRecipeWrapper(jeiHelpers, recipe);
//    }
//
//    @Override
//    public boolean isRecipeValid(ShapelessFluidRecipe recipe) {
//        if (recipe.getRecipeOutput() == null) {
//            String recipeInfo = ErrorUtil.getInfoFromRecipe(recipe, this);
//            Log.error("Recipe has no outputs. {}", recipeInfo);
//            return false;
//        }
//        int inputCount = 0;
//        for (Object input : recipe.getInput()) {
//            if (input instanceof List) {
//                if (((List) input).isEmpty()) {
//                    // missing items for an oreDict name. This is normal behavior, but the recipe is invalid.
//                    return false;
//                }
//            }
//            if (input != null) {
//                inputCount++;
//            }
//        }
//        if (inputCount > 9) {
//            String recipeInfo = ErrorUtil.getInfoFromRecipe(recipe, this);
//            Log.error("Recipe has too many inputs. {}", recipeInfo);
//            return false;
//        }
//        if (inputCount == 0) {
//            String recipeInfo = ErrorUtil.getInfoFromRecipe(recipe, this);
//            Log.error("Recipe has no inputs. {}", recipeInfo);
//            return false;
//        }
//        return true;
//    }
//}

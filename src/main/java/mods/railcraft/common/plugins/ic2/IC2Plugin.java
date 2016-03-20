/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.ic2;

import cpw.mods.fml.common.Loader;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.item.ElectricItem;
import ic2.api.item.IC2Items;
import ic2.api.item.IElectricItem;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class IC2Plugin {

    public static final int[] POWER_TIERS = {1, 6, 32, 512, 2048, 8192};
    private static final Map<String, ItemStack> itemCache = new HashMap<String, ItemStack>();
    private static final Map<String, Boolean> itemCacheFlag = new HashMap<String, Boolean>();
    private static Boolean modLoaded = null;
    private static Boolean classic = null;

    public static ItemStack getItem(String tag) {
        if (!isModInstalled())
            return null;
        ItemStack stack = itemCache.get(tag);
        if (stack != null)
            return stack;
        Boolean wasCached = itemCacheFlag.get(tag);
        if (wasCached == Boolean.TRUE)
            return null;
        try {
            itemCacheFlag.put(tag, Boolean.TRUE);
            stack = IC2Items.getItem(tag);
            if (stack != null)
                itemCache.put(tag, stack.copy());
            return stack;
        } catch (Throwable error) {
            Game.logErrorAPI("IC2", error, Items.class);
        }
        return null;
    }

    public static void addTileToNet(TileEntity tile) {
        try {
            if (tile instanceof IEnergyTile)
                MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent((IEnergyTile) tile));
        } catch (Throwable error) {
            Game.logErrorAPI("IC2", error, EnergyTileLoadEvent.class);
        }
    }

    public static void removeTileFromNet(TileEntity tile) {
        try {
            if (tile instanceof IEnergyTile)
                MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent((IEnergyTile) tile));
        } catch (Throwable error) {
            Game.logErrorAPI("IC2", error, EnergyTileUnloadEvent.class);
        }
    }

    public static boolean isEnergyItem(ItemStack stack) {
        try {
            return stack != null && stack.getItem() instanceof IElectricItem;
        } catch (Throwable error) {
            Game.logErrorAPI("IC2", error, IElectricItem.class);
        }
        return false;
    }

    /**
     * @param stack
     * @return energy used
     */
    public static double chargeItem(ItemStack stack, double energy, int tier) {
        try {
            if (stack != null && stack.getItem() instanceof IElectricItem && energy > 0)
                return ElectricItem.manager.charge(stack, energy, tier, false, false);
        } catch (Throwable error) {
            Game.logErrorAPI("IC2", error, ElectricItem.class);
        }
        return 0;
    }

    /**
     * @param stack
     * @return energy received
     */
    public static double dischargeItem(ItemStack stack, double energyNeeded, int tier) {
        try {
            if (stack != null && stack.getItem() instanceof IElectricItem && ((IElectricItem) stack.getItem()).canProvideEnergy(stack))
                return ElectricItem.manager.discharge(stack, energyNeeded, tier, false, true, false);
        } catch (Throwable error) {
            Game.logErrorAPI("IC2", error, ElectricItem.class);
        }
        return 0;
    }

    public static boolean canCharge(ItemStack stack, int tier) {
        try {
            if (stack != null && stack.getItem() instanceof IElectricItem) {
                IElectricItem battery = (IElectricItem) stack.getItem();
                return tier >= battery.getTier(stack);
            }
        } catch (Throwable error) {
            Game.logErrorAPI("IC2", error, IElectricItem.class);
        }
        return false;
    }

    public static boolean canDischarge(ItemStack stack, int tier) {
        try {
            if (stack != null && stack.getItem() instanceof IElectricItem) {
                IElectricItem battery = (IElectricItem) stack.getItem();
                return battery.canProvideEnergy(stack) && tier >= battery.getTier(stack);
            }
        } catch (Throwable error) {
            Game.logErrorAPI("IC2", error, IElectricItem.class);
        }
        return false;
    }

    public static void addMaceratorRecipe(ItemStack input, ItemStack output) {
        try {
            Recipes.macerator.addRecipe(new RecipeInputItemStack(input), null, output);
        } catch (Throwable error) {
            Game.logErrorAPI("IC2", error, Recipes.class);
        }
    }

    public static void removeMaceratorRecipes(ItemStack... items) {
        try {
            Map<IRecipeInput, RecipeOutput> recipes = Recipes.macerator.getRecipes();

            Iterator<Entry<IRecipeInput, RecipeOutput>> it = recipes.entrySet().iterator();
            while (it.hasNext()) {
                Entry<IRecipeInput, RecipeOutput> entry = it.next();
                if (doesRecipeRequire(entry.getKey(), items) || doesRecipeProduce(entry.getValue(), items))
                    it.remove();
            }
        } catch (Throwable error) {
            Game.logErrorAPI("IC2", error, Recipes.class);
        }
    }

    private static boolean doesRecipeRequire(IRecipeInput input, ItemStack... items) {
        for (ItemStack stack : input.getInputs()) {
            if (InvTools.isItemEqual(stack, items))
                return true;
        }
        return false;
    }

    private static boolean doesRecipeProduce(RecipeOutput recipe, ItemStack... items) {
        for (ItemStack output : recipe.items) {
            if (InvTools.isItemEqual(output, items))
                return true;
        }
        return false;
    }

    public static void removeMaceratorDustRecipes(ItemStack... items) {
        try {
            Map<IRecipeInput, RecipeOutput> recipes = Recipes.macerator.getRecipes();

            Iterator<Entry<IRecipeInput, RecipeOutput>> it = recipes.entrySet().iterator();
            while (it.hasNext()) {
                Entry<IRecipeInput, RecipeOutput> entry = it.next();
                if (isInputBlock(entry.getKey(), items) && doesRecipeProduce(entry.getValue(), items))
                    it.remove();
            }
        } catch (Throwable error) {
            Game.logErrorAPI("IC2", error, Recipes.class);
        }
    }

    private static boolean isInputBlock(IRecipeInput input, ItemStack... items) {
        for (ItemStack stack : input.getInputs()) {
            if (stack != null && stack.getItem() instanceof ItemBlock)
                return true;
        }
        return false;
    }

    public static void nerfSyntheticCoal() {
        for (IRecipe recipe : (List<IRecipe>) CraftingManager.getInstance().getRecipeList()) {
            try {
                ItemStack output = recipe.getRecipeOutput();
                if (output != null)
                    if (output.getItem() == Items.coal && output.stackSize == 20)
                        output.stackSize = 5;
            } catch (Throwable error) {
            }
        }
    }

    public static boolean isModInstalled() {
        if (modLoaded == null)
            modLoaded = Loader.isModLoaded("IC2") || Loader.isModLoaded("IC2-Classic-Spmod");
        return modLoaded;
    }

    public static boolean isClassic() {
        if (classic == null)
            classic = Loader.isModLoaded("IC2-Classic-Spmod");
        return classic;
    }

}

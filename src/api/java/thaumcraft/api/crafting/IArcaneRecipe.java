package thaumcraft.api.crafting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import thaumcraft.api.aspects.AspectList;

public interface IArcaneRecipe extends IRecipe, ITCRecipe
{
	

	boolean matches(InventoryCrafting inv, World worldIn);
	boolean matches(InventoryCrafting inv, World world, EntityPlayer player);

    ItemStack getCraftingResult(InventoryCrafting inv);

    int getRecipeSize();

    ItemStack getRecipeOutput();

    ItemStack[] getRemainingItems(InventoryCrafting inv);
    
    int getVis();
    int getVis(InventoryCrafting inv);
    String getResearch();
    
    ItemStack[] getCrystals();
}

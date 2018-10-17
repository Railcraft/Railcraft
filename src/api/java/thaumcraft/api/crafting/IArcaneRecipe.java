package thaumcraft.api.crafting;

import net.minecraft.item.crafting.IRecipe;
import thaumcraft.api.aspects.AspectList;

public interface IArcaneRecipe extends IRecipe
{	
    int getVis();
    String getResearch();
    AspectList getCrystals();
}

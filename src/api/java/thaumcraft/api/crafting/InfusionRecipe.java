package thaumcraft.api.crafting;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;

public class InfusionRecipe implements ITCRecipe
{
	public AspectList aspects;
	public String research;
	private String name;
	public Object[] components;
	public Object recipeInput;
	public Object recipeOutput;
	public int instability;
	
	/**
	 * @param research the research key required for this recipe to work. Leave blank if it will work without research<br>
	 * 		  Can specify stage like IPlayerKnowledge.isResearchKnown
	 * @param result the recipe output. It can either be an itemstack or an nbt compound tag that will be added to the central item
	 * 		If nbt it needs to be in the format Object[] {"nbttagname", NBT Tag Object}  eg. new Object[] { "mask", new NBTTagInt(1) }
	 * @param instability a number that represents the N in 1000 chance for the infusion altar to spawn an
	 * 		  instability effect each second while the crafting is in progress
	 * @param aspects the essentia cost per aspect. 
	 * @param aspects input the central item to be infused. If string is passed it will look up oredictionary entries
	 * @param recipe An array of items required to craft this. Input itemstacks are NBT sensitive. 
	 * 				If string is passed it will look up oredictionary entries.
	 */
	
	public InfusionRecipe(String research, Object output, int inst, AspectList aspects2, Object input, Object[] recipe) {
		this.name="";
		this.research = research;
		this.recipeOutput = output;
		this.recipeInput = input;
		this.aspects = aspects2;
		this.components = recipe;
		this.instability = inst;
	}

	/**
     * Used to check if a recipe matches current crafting inventory
     * @param player 
     */
	public boolean matches(ArrayList<ItemStack> input, ItemStack central, World world, EntityPlayer player) {
		if (getRecipeInput()==null) return false;
			
		if (!ThaumcraftCapabilities.getKnowledge(player).isResearchKnown(research)) {
    		return false;
    	}
		
		ItemStack i2 = central.copy();
		if (getRecipeInput() instanceof ItemStack &&
				((ItemStack)getRecipeInput()).getItemDamage()==OreDictionary.WILDCARD_VALUE) {
			i2.setItemDamage(OreDictionary.WILDCARD_VALUE);
		}
		
		if (!ThaumcraftApiHelper.areItemStacksEqualForCrafting(i2, getRecipeInput())) return false;
		
		ArrayList<ItemStack> ii = new ArrayList<ItemStack>();
		for (ItemStack is:input) {
			ii.add(is.copy());
		}
		
		for (Object comp:getComponents()) {
			boolean b=false;
			for (int a=0;a<ii.size();a++) {
				 i2 = ii.get(a).copy();
				if (ThaumcraftApiHelper.areItemStacksEqualForCrafting(i2, comp)) {
					ii.remove(a);
					b=true;
					break;
				}
			}
			if (!b) return false;
		}
		return ii.size()==0?true:false;
    }
    
    public String getResearch() {
		return research;
    }
    
	public Object getRecipeInput() {
		return recipeInput;
	}

	public Object[] getComponents() {
		return components;
	}
	
	public Object getRecipeOutput() {
		return recipeOutput;
	}
	
	public AspectList getAspects() {
		return aspects;
	}
			
	
	public Object getRecipeOutput(EntityPlayer player, ItemStack input, ArrayList<ItemStack> comps ) {
		return recipeOutput;
    }
    
    public AspectList getAspects(EntityPlayer player, ItemStack input, ArrayList<ItemStack> comps) {
		return aspects;
    }
    
    public int getInstability(EntityPlayer player, ItemStack input, ArrayList<ItemStack> comps) {
		return instability;
    }

	@Override
	public String getRecipeName() {
		return name;
	}
	
	@Override
	public void setRecipeName(String name) {
		this.name=name;
	}
}

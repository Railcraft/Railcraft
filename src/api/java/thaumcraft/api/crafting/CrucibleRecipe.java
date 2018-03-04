package thaumcraft.api.crafting;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class CrucibleRecipe implements ITCRecipe {

	private ItemStack recipeOutput;
	
	public Object catalyst;
	public AspectList aspects;
	public String[] research;
	private String name;
	public int hash;
	
	/**
	 * @param researchKey the research key required for this recipe to work.<br>
	 * 		  Can specify stage like IPlayerKnowledge.isResearchKnown
	 * @param result the output result
     * @param cat an itemstack of the catalyst or a string if it is an ore dictionary item
     * @param tags the aspects required to craft this
     */
	public CrucibleRecipe(String researchKey, ItemStack result, Object cat, AspectList tags) {
		this(new String[] {researchKey}, result, cat, tags);
	}
	
	/**
	 * @param researchKey the research key required for this recipe to work.<br>
	 * 		  Can specify stage like IPlayerKnowledge.isResearchKnown
	 * @param result the output result
     * @param cat an itemstack of the catalyst or a string if it is an ore dictionary item
     * @param tags the aspects required to craft this
     */
	public CrucibleRecipe(String[] researchKey, ItemStack result, Object cat, AspectList tags) {
		recipeOutput = result;
		this.name="";
		this.aspects = tags;
		this.research = researchKey;
		this.catalyst = cat;
		if (cat instanceof String) {
			this.catalyst = OreDictionary.getOres((String) cat,false);
		}
		String hc = "";
		for (String ss:research) hc+=ss;
		hc += result.toString();
		for (Aspect tag:tags.getAspects()) {
			hc += tag.getTag()+tags.getAmount(tag);
		}
		if (cat instanceof ItemStack) {
			hc += ((ItemStack)cat).toString();
		} else
		if (cat instanceof List && ((List<ItemStack>)catalyst).size()>0) {
			for (ItemStack is :(List<ItemStack>)catalyst) {
				hc += is.toString();
			}
		}
		
		hash = hc.hashCode();
	}
	
		

	public boolean matches(AspectList itags, ItemStack cat) {
		if (catalyst instanceof ItemStack && !OreDictionary.itemMatches((ItemStack) catalyst,cat,false)) {
			return false;
		} else 
		if (catalyst instanceof List && ((List<ItemStack>)catalyst).size()>0) {
			if (!ThaumcraftApiHelper.containsMatch(false, new ItemStack[]{cat},
					(List<ItemStack>)catalyst)) return false;
		}
		if (itags==null) return false;
		for (Aspect tag:aspects.getAspects()) {
			if (itags.getAmount(tag)<aspects.getAmount(tag)) return false;
		}
		return true;
	}
	
	public boolean catalystMatches(ItemStack cat) {
		if (catalyst instanceof ItemStack && OreDictionary.itemMatches((ItemStack) catalyst,cat,false)) {
			return true;
		} else 
		if (catalyst instanceof List && ((List<ItemStack>)catalyst).size()>0) {
			if (ThaumcraftApiHelper.containsMatch(false, new ItemStack[]{cat}, (List<ItemStack>)catalyst)) return true;
		}
		return false;
	}
	
	public AspectList removeMatching(AspectList itags) {
		AspectList temptags = new AspectList();
		temptags.aspects.putAll(itags.aspects);
		
		for (Aspect tag:aspects.getAspects()) {
			temptags.remove(tag, aspects.getAmount(tag));
		}
		
		itags = temptags;
		return itags;
	}
	
	public ItemStack getRecipeOutput() {
		return recipeOutput;
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

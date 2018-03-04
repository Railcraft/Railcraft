package thaumcraft.api.research.theorycraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;

public class CardPonder extends TheorycraftCard {

	@Override
	public int getInspirationCost() {
		return 2;
	}
		
	@Override
	public String getLocalizedName() {
		return new TextComponentTranslation("card.ponder.name").getUnformattedText();
	}
	
	@Override
	public String getLocalizedText() {
		return new TextComponentTranslation("card.ponder.text").getUnformattedText();
	}
	
	@Override
	public boolean initialize(EntityPlayer player, ResearchTableData data) {
		return data.categoriesBlocked.size()<data.categoryTotals.size();
	}

	@Override
	public boolean activate(EntityPlayer player, ResearchTableData data) {
		int a = 20;
		while (a>0)
			for (String category:data.categoryTotals.keySet()) {
				if (data.categoriesBlocked.contains(category)) {
					if (data.categoryTotals.size()<=1) return false;
					continue;
				}
				data.addTotal(category, 1);
				a--;
				if (a<=0) break;
			}
		data.addTotal("BASICS", 5);
		return a!=20;
	}
	
	
}

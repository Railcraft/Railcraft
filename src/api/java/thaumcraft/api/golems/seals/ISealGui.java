package thaumcraft.api.golems.seals;

public interface ISealGui {
	int CAT_PRIORITY = 0;
	int CAT_FILTER = 1;
	int CAT_AREA = 2;
	int CAT_TOGGLES = 3;
	int CAT_TAGS = 4;
	
	int[] getGuiCategories();
	
}

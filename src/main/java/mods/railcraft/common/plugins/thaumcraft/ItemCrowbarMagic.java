/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.thaumcraft;

import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.ItemCrowbar;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thaumcraft.api.IRepairable;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemCrowbarMagic extends ItemCrowbar implements IRepairable {
    public static final String RESEARCH_TAG = "RC_Crowbar";
    private static final String ITEM_TAG = "railcraft.tool.crowbar.magic";
    private static Item item;

    public ItemCrowbarMagic() {
        super(ThaumcraftPlugin.getThaumiumToolMaterial());
        setUnlocalizedName(ITEM_TAG);
    }

    public static void registerItem() {
        if (item == null && RailcraftConfig.isItemEnabled(ITEM_TAG)) {
            item = new ItemCrowbarMagic();
            RailcraftRegistry.register(item);
        }
    }

    public static void registerResearch() {
        if (item == null)
            return;
        try {
            IArcaneRecipe recipe = ThaumcraftApi.addArcaneCraftingRecipe(RESEARCH_TAG, new ItemStack(item),
                    new AspectList().add(Aspect.ORDER, 8),
                    " RI",
                    "RIR",
                    "IR ",
                    'I', ThaumcraftPlugin.getItem("itemResource", 2),
                    'R', "dyeRed");

            AspectList aspects = new AspectList();
            aspects.add(Aspect.TOOL, 1).add(Aspect.MECHANISM, 2).add(Aspect.TRAVEL, 1);

            ResearchItem thaumiumCrowbar = new ResearchItemRC(RESEARCH_TAG, ThaumcraftPlugin.RESEARCH_CATEGORY, aspects, 0, 0, 3, new ItemStack(item));
            thaumiumCrowbar.setPages(ThaumcraftPlugin.getResearchPage(RESEARCH_TAG), new ResearchPage(recipe))
                    .setParentsHidden("THAUMIUM")
                    .registerResearchItem();
        } catch (Throwable error) {
            Game.logErrorAPI("Thaumcraft", error, ResearchItem.class);
        }
    }

    public static ItemStack getItem() {
        if (item == null)
            return null;
        return new ItemStack(item);
    }
}

/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.forestry;

import mods.railcraft.common.plugins.thaumcraft.ResearchItemRC;
import mods.railcraft.common.plugins.thaumcraft.ThaumcraftPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@Optional.Interface(iface = "forestry.api.storage.IBackpackDefinition", modid = ForestryPlugin.FORESTRY_ID)
public class ApothecariesBackpack extends BaseBackpack {

    private static ApothecariesBackpack instance;

    public static ApothecariesBackpack getInstance() {
        if (instance == null)
            instance = new ApothecariesBackpack();
        return instance;
    }

    protected ApothecariesBackpack() {
        super("railcraft.apothecary");
    }

    public void setup() {
        add(Items.POTIONITEM);
        add(Items.GLASS_BOTTLE);
    }

    @Override
    public int getPrimaryColour() {
        return 16262179;
    }

    @Override
    public int getSecondaryColour() {
        return 0xFFFFFF;
    }

    public static void registerThaumcraftResearch() {
        try {
            IArcaneRecipe recipe = ThaumcraftApi.addArcaneCraftingRecipe("RC_ApothecariesBackpack", new ItemStack(ForestryPlugin.apothecariesBackpackT1),
                    new AspectList().add(Aspect.AIR, 16).add(Aspect.ORDER, 16),
                    "X#X",
                    "VYV",
                    "X#X",
                    '#', Blocks.WOOL,
                    'V', new ItemStack(Items.POTIONITEM, 1, 8197),
                    'X', Items.STRING,
                    'Y', new ItemStack(Blocks.CHEST));

            AspectList aspects = new AspectList();
            aspects.add(Aspect.VOID, 3).add(Aspect.CRAFT, 3).add(Aspect.MOTION, 2);

            ResearchItem backpack = new ResearchItemRC("RC_ApothecariesBackpack", ThaumcraftPlugin.RESEARCH_CATEGORY, aspects, 2, 0, 6, new ItemStack(ForestryPlugin.apothecariesBackpackT1));
            backpack.setPages(ThaumcraftPlugin.getResearchPage("RC_ApothecariesBackpack"), new ResearchPage(recipe)).setParentsHidden("ENCHFABRIC").registerResearchItem();

        } catch (Throwable error) {
            Game.logErrorAPI("Thaumcraft", error, ResearchItem.class);
        }
    }

}

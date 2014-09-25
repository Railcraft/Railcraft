/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.thaumcraft;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchItem;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ResearchItemRC extends ResearchItem {

    public ResearchItemRC(String key, String catagory) {
        super(key, catagory);
    }

    public ResearchItemRC(String key, String category, AspectList aspects, int displayColumn, int displayRow, int complexity, ResourceLocation icon) {
        super(key, category, aspects, displayColumn, displayRow, complexity, icon);
    }

    public ResearchItemRC(String key, String category, AspectList aspects, int displayColumn, int displayRow, int complexity, ItemStack icon) {
        super(key, category, aspects, displayColumn, displayRow, complexity, icon);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getName() {
        return LocalizationPlugin.translate(String.format("thaumcraft.research.%s.name", key));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getText() {
        return LocalizationPlugin.translate(String.format("thaumcraft.research.%s.text", key));
    }

}

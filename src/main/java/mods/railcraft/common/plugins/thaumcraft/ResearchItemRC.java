/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.thaumcraft;

import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchEntry;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ResearchItemRC extends ResearchEntry {

    public ResearchItemRC(String key, String catagory) {
        setKey(key);
        setCategory(catagory);
    }

    public ResearchItemRC(String key, String category, AspectList aspects, int displayColumn, int displayRow, int complexity, ResourceLocation icon) {
        this(key, category);
        setDisplayColumn(displayColumn);
        setDisplayRow(displayRow);
        setIcons(new Object[]{icon});
    }

    public ResearchItemRC(String key, String category, AspectList aspects, int displayColumn, int displayRow, int complexity, ItemStack icon) {
        this(key, category);
        setDisplayColumn(displayColumn);
        setDisplayRow(displayRow);
        setIcons(new Object[]{icon});
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getName() {
        return LocalizationPlugin.translate(String.format("thaumcraft.research.%s.name", getKey()));
    }

}

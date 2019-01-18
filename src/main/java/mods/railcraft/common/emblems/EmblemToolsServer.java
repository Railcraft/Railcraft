/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.emblems;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class EmblemToolsServer {

    public static IEmblemManager manager;

    public static String getEmblemIdentifier(ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt != null) {
                NBTTagString emblemIdent = (NBTTagString) nbt.getTag("emblem");
                return emblemIdent.getString();
            }
        }
        return "";
    }

}

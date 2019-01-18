/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.items;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.client.render.models.resource.ModelManager;
import mods.railcraft.common.core.RailcraftConstants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by CovertJaguar on 7/18/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IRailcraftItemSimple extends IRailcraftItem {

    @Override
    @SideOnly(Side.CLIENT)
    default void initializeClient() {
        IVariantEnum[] variants = getVariants();
        if (variants != null) {
            for (int i = 0, variantsLength = variants.length; i < variantsLength; i++) {
                IVariantEnum variant = variants[i];
                ModelManager.registerItemModel(getObject(), i, getPath() + RailcraftConstants.SEPERATOR + variant.getResourcePathSuffix());
            }
        } else {
            ModelManager.registerItemModel(getObject(), 0);
        }
    }

}

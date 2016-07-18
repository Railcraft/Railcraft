/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/

package mods.railcraft.common.items;

import mods.railcraft.client.render.tools.ModelManager;
import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.core.IVariantEnum;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by CovertJaguar on 7/18/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IRailcraftItem extends IRailcraftObject {

    @SideOnly(Side.CLIENT)
    default void defineModels() {
        Class<? extends IVariantEnum> variantEnum = getVariantEnum();
        if (variantEnum != null) {
            for (IVariantEnum variant : variantEnum.getEnumConstants()) {
                ModelManager.registerItemModel((Item) this, variant.ordinal(), getResourcePath() + "." + variant.getResourcePathSuffix());

            }
        } else {
            ModelManager.registerItemModel((Item) this, 0);
        }
    }
}

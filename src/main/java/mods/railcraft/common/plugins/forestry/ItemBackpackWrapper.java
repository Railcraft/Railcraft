/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.forestry;

import forestry.api.core.ForestryAPI;
import forestry.api.core.IItemModelRegister;
import mods.railcraft.common.items.ItemWrapper;
import mods.railcraft.common.plugins.misc.Mod;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * Created by CovertJaguar on 4/26/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemBackpackWrapper extends ItemWrapper {
    public ItemBackpackWrapper(Item item) {
        super(item);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void initializeClient() {
        ((IItemModelRegister) getObject()).registerModel(getObject(), ForestryAPI.modelManager);
        try {
            Field itemColorList = ForestryAPI.modelManager.getClass().getDeclaredField("itemColorList");
            itemColorList.setAccessible(true);
            //TODO this is not a list
            Set<Object> list = (Set<Object>) itemColorList.get(ForestryAPI.modelManager);
            list.add(getObject());
        } catch (NoSuchFieldException | IllegalAccessException | ClassCastException ex) {
            Game.log().api(Mod.FORESTRY.modId, ex, ForestryAPI.modelManager.getClass());
        }
    }
}

/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.forestry;

import net.minecraft.init.Items;
import net.minecraftforge.fml.common.Optional;

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



}

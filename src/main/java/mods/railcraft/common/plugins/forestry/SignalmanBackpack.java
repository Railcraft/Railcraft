/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.forestry;

import mods.railcraft.api.core.items.IMinecartItem;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.items.RailcraftItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMinecart;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Optional;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@Optional.Interface(iface = "forestry.api.storage.IBackpackDefinition", modid = ForestryPlugin.FORESTRY_ID)
public class SignalmanBackpack extends BaseBackpack {

    private static SignalmanBackpack instance;

    public static SignalmanBackpack getInstance() {
        if (instance == null)
            instance = new SignalmanBackpack();
        return instance;
    }

    protected SignalmanBackpack() {
        super("railcraft.signalman");
    }

    public void setup() {

        add(RailcraftItems.SIGNAL_BLOCK_SURVEYOR);
        add(RailcraftItems.SIGNAL_TUNER);
        add(RailcraftItems.SIGNAL_LAMP);
        add(RailcraftItems.CIRCUIT);
        add(RailcraftItems.SIGNAL_LABEL);
        add(RailcraftItems.MAG_GLASS);
        add(RailcraftItems.GOGGLES);

        add(RailcraftBlocks.WAY_OBJECT);
        add(RailcraftBlocks.DETECTOR);
        add(RailcraftBlocks.SIGNAL_BOX);
    }

    @Override
    public int getPrimaryColour() {
        return 0x004B82;
    }

    @Override
    public int getSecondaryColour() {
        return 0xFFFFFF;
    }

}

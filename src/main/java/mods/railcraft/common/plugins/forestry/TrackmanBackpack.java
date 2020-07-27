/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.forestry;

import mods.railcraft.api.items.IMinecartItem;
import mods.railcraft.api.items.IToolCrowbar;
import mods.railcraft.api.items.ITrackItem;
import mods.railcraft.api.tracks.IItemTrack;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.items.RailcraftItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMinecart;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@Optional.Interface(iface = "forestry.api.storage.IBackpackDefinition", modid = ForestryPlugin.FORESTRY_ID)
public class TrackmanBackpack extends BaseBackpack {

    private static TrackmanBackpack instance;

    public static TrackmanBackpack getInstance() {
        if (instance == null)
            instance = new TrackmanBackpack();
        return instance;
    }

    protected TrackmanBackpack() {
        super("railcraft.trackman");
    }

    public void setup() {
        for (Block block : ForgeRegistries.BLOCKS.getValuesCollection()) {
            if (TrackTools.isRail(block))
                add(block);
        }

        for (Item item : ForgeRegistries.ITEMS.getValuesCollection()) {
            if (item instanceof ItemMinecart || item instanceof IMinecartItem || item instanceof ITrackItem || item instanceof IItemTrack)
                add(item);
        }

        add(IToolCrowbar.ORE_TAG);

        add(RailcraftBlocks.WORLDSPIKE);
        add(RailcraftBlocks.WORLDSPIKE_POINT);

        add(RailcraftItems.BOTTLE_CREOSOTE);

        add(RailcraftItems.SPIKE_MAUL_IRON);
        add(RailcraftItems.SPIKE_MAUL_STEEL);
        add(RailcraftItems.CHARGE_METER);
        add(RailcraftItems.RAIL);
        add(RailcraftItems.RAILBED);
        add(RailcraftItems.TICKET);
        add(RailcraftItems.TICKET_GOLD);
        add(RailcraftItems.TIE);
        add(RailcraftItems.WHISTLE_TUNER);
        add(RailcraftItems.OVERALLS);
        add(RailcraftItems.TRACK_KIT);
        add(RailcraftItems.TRACK_PARTS);

        add(RailcraftBlocks.CHARGE_FEEDER);
        add(RailcraftBlocks.MANIPULATOR);
        add(RailcraftBlocks.TRACK_ELEVATOR);
        add(RailcraftBlocks.WIRE);
    }

    @Override
    public int getPrimaryColour() {
        return 0x0094FF;
    }

    @Override
    public int getSecondaryColour() {
        return 0xFFFFFF;
    }

}

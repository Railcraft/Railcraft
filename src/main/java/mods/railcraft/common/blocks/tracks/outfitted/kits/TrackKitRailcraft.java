/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.outfitted.kits;

import mods.railcraft.api.tracks.ITrackKitInstance;
import mods.railcraft.api.tracks.TrackKit;
import mods.railcraft.api.tracks.TrackKitInstance;
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.jetbrains.annotations.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TrackKitRailcraft extends TrackKitInstance {

    public abstract TrackKits getTrackKitContainer();

    @Override
    public TrackKit getTrackKit() {
        return getTrackKitContainer().getTrackKit();
    }

    public int getPowerPropagation() {
        return 0;
    }

    public boolean canPropagatePowerTo(ITrackKitInstance track) {
        return true;
    }

    @Override
    public boolean onCrowbarWhack(EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem) {
        EnumGui gui = getGUI();
        if (gui != null) {
            GuiHandler.openGui(gui, player, theWorldAsserted(), getPos());
            return true;
        }
        return super.onCrowbarWhack(player, hand, heldItem);
    }

    protected EnumGui getGUI() {
        return null;
    }

}

/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks.kits;

import mods.railcraft.api.tracks.TrackKit;
import mods.railcraft.api.tracks.TrackRegistry;
import mods.railcraft.client.render.tools.ModelManager;
import mods.railcraft.common.items.ItemRailcraft;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by CovertJaguar on 8/11/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemTrackKit extends ItemRailcraft {
    @Override
    public void initializeDefinintion() {
        TrackKit.itemKit = this;
    }

    public TrackKit getTrackKit(ItemStack stack) {
        return TrackRegistry.getTrackKit(InvTools.getItemDataRailcraft(stack));
    }

    @Override
    public int getMetadata(ItemStack stack) {
        return TrackRegistry.getTrackKitId(getTrackKit(stack));
    }

    @Override
    public int getDamage(ItemStack stack) {
        return getMetadata(stack);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + getTrackKit(stack).getResourcePathSuffix();
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        Map<String, TrackKit> trackKits = TrackRegistry.getTrackKits();
        list.addAll(trackKits.values().stream().filter(TrackKit::isVisible).map(this::getStack).collect(Collectors.toList()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initializeClient() {
        Map<String, TrackKit> trackKits = TrackRegistry.getTrackKits();
        for (TrackKit trackKit : trackKits.values()) {
            ModelManager.registerItemModel(this, trackKit.ordinal(), getResourcePath() + "." + trackKit.getResourcePathSuffix());
        }
    }
}

/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks.kits;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.api.tracks.TrackKit;
import mods.railcraft.api.tracks.TrackRegistry;
import mods.railcraft.client.render.models.resource.ModelManager;
import mods.railcraft.common.items.ItemRailcraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
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
        setCreativeTab(CreativeTabs.TRANSPORTATION);
    }

    @Override
    @Nullable
    public Class<? extends IVariantEnum> getVariantEnum() {
        return TrackKit.class;
    }

    @Override
    public int getMetadata(ItemStack stack) {
        return TrackRegistry.TRACK_KIT.getId(TrackRegistry.TRACK_KIT.get(stack));
    }

    @Override
    public int getDamage(ItemStack stack) {
        return getMetadata(stack);
    }

    @Override
    @Nullable
    public ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
        if (variant != null) {
            checkVariant(variant);
            return ((TrackKit) variant).getTrackKitItem(qty);
        }
        return TrackRegistry.getMissingTrackKit().getTrackKitItem();
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + TrackRegistry.TRACK_KIT.get(stack).getResourcePathSuffix();
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        Map<String, TrackKit> trackKits = TrackRegistry.TRACK_KIT.getVariants();
        list.addAll(trackKits.values().stream().filter(TrackKit::isVisible).map(this::getStack).collect(Collectors.toList()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initializeClient() {
        Map<String, TrackKit> trackKits = TrackRegistry.TRACK_KIT.getVariants();
        trackKits.values().stream().filter(TrackKit::isVisible).forEach(trackKit -> {
            ModelManager.registerItemModel(this, trackKit.ordinal(), trackKit.getRegistryName().getResourceDomain(), "track_kits/" + trackKit.getRegistryName().getResourcePath());
        });
    }
}

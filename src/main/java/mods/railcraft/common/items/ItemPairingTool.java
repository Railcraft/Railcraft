/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.items;

import mods.railcraft.api.core.WorldCoordinate;
import mods.railcraft.api.items.ActivationBlockingItem;
import mods.railcraft.api.items.InvToolsAPI;
import mods.railcraft.api.signals.IPair;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Created by CovertJaguar on 6/7/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@ActivationBlockingItem
public class ItemPairingTool extends ItemRailcraft {
    public static final String PAIR_DATA_TAG = "pairData";
    public static final String COORD_TAG = "coord";
    private final String guiTagPrefix;

    public ItemPairingTool(String guiTagPrefix) {
        this.guiTagPrefix = guiTagPrefix;
        setMaxDamage(0);
        setHasSubtypes(true);
        setMaxStackSize(1);
    }

    public @Nullable WorldCoordinate getPairData(ItemStack stack) {
        return InvToolsAPI.getRailcraftDataSubtag(stack, PAIR_DATA_TAG)
                .map(nbt -> WorldCoordinate.readFromNBT(nbt, COORD_TAG))
                .orElse(null);
    }

    public void setPairData(ItemStack stack, TileEntity tile) {
        NBTTagCompound pairData = new NBTTagCompound();
        WorldCoordinate pos = WorldCoordinate.from(tile);
        pos.writeToNBT(pairData, COORD_TAG);
        InvToolsAPI.setRailcraftDataSubtag(stack, PAIR_DATA_TAG, pairData);
    }

    public void clearPairData(ItemStack stack) {
        InvToolsAPI.clearRailcraftDataSubtag(stack, PAIR_DATA_TAG);
    }

    public <T> boolean actionCleanPairing(ItemStack stack, EntityPlayer player, World worldIn, Class<? extends T> clazz, Function<T, IPair> transform) {
        if (Game.isHost(worldIn)) {
            WorldCoordinate signalPos = getPairData(stack);
            if (signalPos != null) {
                if (signalPos.getDim() != worldIn.provider.getDimension()) {
                    abandonPairing(stack, player);
                    return true;
                } else if (player.isSneaking()) {
                    TileEntity tile = DimensionManager.getWorld(signalPos.getDim()).getTileEntity(signalPos.getPos());
                    if (clazz.isInstance(tile)) {
                        transform.apply(clazz.cast(tile)).endPairing();
                    }
                    abandonPairing(stack, player);
                    return true;
                }
            }
        }
        return false;
    }

    private void abandonPairing(ItemStack stack, EntityPlayer playerIn) {
        ChatPlugin.sendLocalizedChatFromServer(playerIn, guiTagPrefix + "abandon");
        clearPairData(stack);
    }
}

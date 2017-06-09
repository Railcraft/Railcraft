/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.items;

import mods.railcraft.api.core.WorldCoordinate;
import mods.railcraft.api.core.items.IActivationBlockingItem;
import mods.railcraft.api.core.items.InvToolsAPI;
import mods.railcraft.api.signals.AbstractPair;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Created by CovertJaguar on 6/7/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemPairingTool extends ItemRailcraft implements IActivationBlockingItem {
    public static final String PAIR_DATA_TAG = "pairData";
    public static final String COORD_TAG = "coord";
    private final String guiTagPrefix;

    public ItemPairingTool(String guiTagPrefix) {
        this.guiTagPrefix = guiTagPrefix;
        setMaxDamage(0);
        setHasSubtypes(true);
        setMaxStackSize(1);
    }

    @Nullable
    public WorldCoordinate getPairData(ItemStack stack) {
        WorldCoordinate pos = null;
        NBTTagCompound pairData = InvToolsAPI.getItemDataRailcraft(stack, PAIR_DATA_TAG);
        if (pairData != null)
            pos = WorldCoordinate.readFromNBT(pairData, COORD_TAG);
        return pos;
    }

    public void setPairData(ItemStack stack, TileEntity tile) {
        NBTTagCompound pairData = new NBTTagCompound();
        WorldCoordinate pos = new WorldCoordinate(tile);
        pos.writeToNBT(pairData, COORD_TAG);
        InvToolsAPI.setItemDataRailcraft(stack, PAIR_DATA_TAG, pairData);
    }

    public void clearPairData(ItemStack stack) {
        InvToolsAPI.clearItemDataRailcraft(stack, PAIR_DATA_TAG);
    }

    public <T> boolean actionCleanPairing(ItemStack stack, EntityPlayer player, World worldIn, Class<? extends T> clazz, Function<T, AbstractPair> transform) {
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

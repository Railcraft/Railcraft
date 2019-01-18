/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.core;

import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CommonProxy {

    public World getClientWorld() {
        return null;
    }

    public String getItemDisplayName(ItemStack stack) {
        if (InvTools.isEmpty(stack))
            return "";
        return stack.getDisplayName();
    }

    public int getItemRarityColor(ItemStack stack) {
        return 15;
    }

    public void initializeClient() {
    }

    public void finalizeClient() {
    }

    public UUID getPlayerIdentifier(EntityPlayer player) {
        return player.getGameProfile().getId();
    }

    public String getPlayerUsername(String playerId) {
        return playerId;
    }

    public String getPlayerUsername(EntityPlayer player) {
        return player.getGameProfile().getName();
    }

    public String getCurrentLanguage() {
        return "en_US";
    }

    public void openRoutingTableGui(EntityPlayer player, @Nullable TileEntity tile, ItemStack stack) {
    }

}

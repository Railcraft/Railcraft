/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.UUID;

public class CommonProxy {

    public World getClientWorld() {
        return null;
    }

    public String getItemDisplayName(ItemStack stack) {
        if (stack == null)
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

}

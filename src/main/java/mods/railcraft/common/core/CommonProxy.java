/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.core;

import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

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

    public int getRenderId() {
        return 0;
    }

    public void preInitClient() {
    }

    public void initClient() {
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

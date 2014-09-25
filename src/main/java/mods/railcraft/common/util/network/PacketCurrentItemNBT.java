/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.logging.log4j.Level;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class PacketCurrentItemNBT extends RailcraftPacket {

    private final EntityPlayer player;
    private final ItemStack currentItem;

    public PacketCurrentItemNBT(EntityPlayer player, ItemStack stack) {
        this.player = player;
        this.currentItem = stack;
    }

    @Override
    public void writeData(DataOutputStream data) throws IOException {
        DataTools.writeItemStack(currentItem, data);
    }

    @Override
    public void readData(DataInputStream data) throws IOException {
        try {
            ItemStack stack = DataTools.readItemStack(data);

            if (stack == null || currentItem == null)
                return;

            if (stack.getItem() != currentItem.getItem())
                return;

            if (!(currentItem.getItem() instanceof IEditableItem))
                return;

            IEditableItem eItem = (IEditableItem) stack.getItem();

            if (!eItem.canPlayerEdit(player, currentItem)) {
                Game.log(Level.WARN, "{0} attempted to edit an item he is not allowed to edit {0}.", Railcraft.proxy.getPlayerUsername(player), currentItem.getItem().getUnlocalizedName());
                return;
            }

            if (!eItem.validateNBT(stack.getTagCompound())) {
                Game.log(Level.WARN, "Item NBT not valid!");
                return;
            }

            currentItem.setTagCompound(stack.getTagCompound());
        } catch (Exception exception) {
            Game.logThrowable("Error reading Item NBT packet", exception);
        }
    }

    public void sendPacket() {
        PacketDispatcher.sendToServer(this);
    }

    @Override
    public int getID() {
        return PacketType.ITEM_NBT.ordinal();
    }

}

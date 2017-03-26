/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.network;

import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.stream.Stream;

public class PacketCurrentItemNBT extends RailcraftPacket {

    private final EntityPlayer player;
    @Nullable
    private final ItemStack currentItem;

    public PacketCurrentItemNBT(EntityPlayer player, @Nullable ItemStack stack) {
        this.player = player;
        this.currentItem = stack;
    }

    @Override
    public void writeData(RailcraftOutputStream data) throws IOException {
        data.writeItemStack(currentItem);
    }

    @Override
    public void readData(RailcraftInputStream data) throws IOException {
        try {
            ItemStack stack = data.readItemStack();

            if (stack == null)
                return;

            // Since dual wielding was introduced, the server may have "lost"
            // the active item by the time it gets this packet. In this case,
            // check the player's hands as well.
            ItemStack targetItem = Stream
                    .of(currentItem, player.getHeldItemMainhand(), player.getHeldItemOffhand())
                    .filter(ci -> ci != null
                            && ci.getItem() == stack.getItem()
                            && ci.getItem() instanceof IEditableItem)
                    .findFirst().orElse(null);

            if (targetItem == null)
                return;

            IEditableItem eItem = (IEditableItem) stack.getItem();

            if (!eItem.canPlayerEdit(player, targetItem)) {
                Game.log(Level.WARN, "{0} attempted to edit an item he is not allowed to edit {0}.", Railcraft.proxy.getPlayerUsername(player), targetItem.getItem().getUnlocalizedName());
                return;
            }

            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt == null || !eItem.validateNBT(nbt)) {
                Game.log(Level.WARN, "Item NBT not valid!");
                return;
            }

            targetItem.setTagCompound(stack.getTagCompound());
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

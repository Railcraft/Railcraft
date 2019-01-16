/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.network;

import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.routing.ITileRouting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.stream.Stream;

public abstract class PacketItemNBT extends RailcraftPacket {
    public static class CurrentItem extends PacketItemNBT {
        public CurrentItem(EntityPlayer player, @Nullable ItemStack stack) {
            super(player, stack);
        }

        public CurrentItem(EntityPlayer player) {
            super(player);
        }

        @Override
        protected ItemStack findTargetStack(ItemStack readStack) {
            // Since dual wielding was introduced, the server may have "lost"
            // the active item by the time it gets this packet. In this case,
            // check the player's hands as well.
            return Stream
                    .of(sourceStack, player.getHeldItemMainhand(), player.getHeldItemOffhand())
                    .filter(currentStack -> !InvTools.isEmpty(currentStack)
                            && currentStack.getItem() == readStack.getItem()
                            && currentStack.getItem() instanceof IEditableItem)
                    .findFirst().orElse(null);
        }

        @Override
        public int getID() {
            return PacketType.ITEM_NBT_HAND.ordinal();
        }
    }

    public static class RoutableTile extends PacketItemNBT {
        private TileEntity tile;

        public RoutableTile(EntityPlayer player, TileEntity tile, @Nullable ItemStack stack) {
            super(player, stack);
            this.tile = tile;
        }

        public RoutableTile(EntityPlayer player) {
            this(player, null, null);
        }

        @Override
        protected void writeLocationData(RailcraftOutputStream data) throws IOException {
            data.writeBlockPos(tile.getPos());
        }

        @Override
        protected void readLocationData(RailcraftInputStream data) throws IOException {
            BlockPos pos = data.readBlockPos();
            tile = WorldPlugin.getBlockTile(player.world, pos);
        }

        @Override
        protected ItemStack findTargetStack(ItemStack readStack) {
            if (tile instanceof ITileRouting)
                return ((ITileRouting) tile).getRoutingTable();
            return null;
        }

        @Override
        protected void updateTargetStack(ItemStack targetStack) {
            if (tile instanceof ITileRouting)
                ((ITileRouting) tile).setRoutingTable(targetStack);
        }

        @Override
        public int getID() {
            return PacketType.ITEM_NBT_TILE.ordinal();
        }
    }

    protected final EntityPlayer player;
    protected final @Nullable ItemStack sourceStack;

    protected PacketItemNBT(EntityPlayer player, @Nullable ItemStack stack) {
        this.player = player;
        this.sourceStack = stack;
    }

    protected PacketItemNBT(EntityPlayer player) {
        this(player, null);
    }

    protected void writeLocationData(RailcraftOutputStream data) throws IOException {
    }

    protected void readLocationData(RailcraftInputStream data) throws IOException {
    }

    protected abstract @Nullable ItemStack findTargetStack(ItemStack readStack) throws IOException;

    protected void updateTargetStack(ItemStack targetStack) {
    }

    @Override
    public void writeData(RailcraftOutputStream data) throws IOException {
        writeLocationData(data);
        data.writeItemStack(sourceStack);
    }

    @Override
    public void readData(RailcraftInputStream data) {
        try {
            readLocationData(data);
            ItemStack readStack = data.readItemStack();

            if (InvTools.isEmpty(readStack))
                return;

            ItemStack targetStack = findTargetStack(readStack);

            if (InvTools.isEmpty(targetStack))
                return;

            IEditableItem eItem = (IEditableItem) readStack.getItem();

            if (!eItem.canPlayerEdit(player, targetStack)) {
                Game.log().msg(Level.WARN, "{0} attempted to edit an item he is not allowed to edit {0}.", Railcraft.proxy.getPlayerUsername(player), targetStack.getItem().getTranslationKey());
                return;
            }

            NBTTagCompound nbt = readStack.getTagCompound();
            if (nbt == null || !eItem.validateNBT(nbt)) {
                Game.log().msg(Level.WARN, "Item NBT not valid!");
                return;
            }

            targetStack.setTagCompound(readStack.getTagCompound());
            updateTargetStack(targetStack);
        } catch (Exception exception) {
            Game.log().throwable("Error reading Item NBT packet", exception);
        }
    }

    public void sendPacket() {
        PacketDispatcher.sendToServer(this);
    }

}

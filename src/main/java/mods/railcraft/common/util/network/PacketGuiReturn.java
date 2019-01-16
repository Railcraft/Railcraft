/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.io.IOException;

public class PacketGuiReturn extends RailcraftPacket {

    private EntityPlayer sender;
    private IGuiReturnHandler obj;
    private byte[] extraData;

    public PacketGuiReturn(EntityPlayer sender) {
        this.sender = sender;
    }

    public PacketGuiReturn(IGuiReturnHandler obj) {
        this.obj = obj;
    }

    public PacketGuiReturn(IGuiReturnHandler obj, byte[] extraData) {
        this.obj = obj;
        this.extraData = extraData;
    }

    @Override
    public void writeData(RailcraftOutputStream data) throws IOException {
        data.writeInt(obj.theWorldAsserted().provider.getDimension());
        if (obj instanceof TileEntity) {
            TileEntity tile = (TileEntity) obj;
            data.writeBoolean(true);
            BlockPos pos = tile.getPos();
            data.writeInt(pos.getX());
            data.writeInt(pos.getY());
            data.writeInt(pos.getZ());
        } else if (obj instanceof Entity) {
            Entity entity = (Entity) obj;
            data.writeBoolean(false);
            data.writeInt(entity.getEntityId());
        } else
            return;
        obj.writeGuiData(data);
        if (extraData != null)
            data.write(extraData);
    }

    @Override
    public void readData(RailcraftInputStream data) throws IOException {
        int dim = data.readInt();
        World world = DimensionManager.getWorld(dim);
        boolean tileReturn = data.readBoolean();
        if (tileReturn) {
            int x = data.readInt();
            int y = data.readInt();
            int z = data.readInt();

            TileEntity t = world.getTileEntity(new BlockPos(x, y, z));

            if (t instanceof IGuiReturnHandler)
                ((IGuiReturnHandler) t).readGuiData(data, sender);

        } else {
            int entityId = data.readInt();
            Entity entity = world.getEntityByID(entityId);

            if (entity instanceof IGuiReturnHandler)
                ((IGuiReturnHandler) entity).readGuiData(data, sender);
        }
    }

    public void sendPacket() {
        PacketDispatcher.sendToServer(this);
    }

    @Override
    public int getID() {
        return PacketType.GUI_RETURN.ordinal();
    }

}

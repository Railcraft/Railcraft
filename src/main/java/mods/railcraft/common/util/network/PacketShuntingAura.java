/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.network;

import mods.railcraft.client.render.world.GoggleAuraWorldRenderer;
import mods.railcraft.common.carts.LinkageManager;
import mods.railcraft.common.carts.Train;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.Collection;

public class PacketShuntingAura extends RailcraftPacket {
    private Collection<EntityMinecart> carts;

    public PacketShuntingAura() {
    }

    public PacketShuntingAura(Collection<EntityMinecart> carts) {
        this.carts = carts;
    }

    @Override
    public void writeData(RailcraftOutputStream data) throws IOException {
        data.writeInt(carts.size());
        for (EntityMinecart cart : carts) {
            data.writeUUID(cart.getPersistentID());
            data.writeUUID(Train.getTrainUUID(cart));
            LinkageManager lm = LinkageManager.INSTANCE;
            data.writeUUID(lm.getLinkA(cart));
            data.writeUUID(lm.getLinkB(cart));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void readData(RailcraftInputStream data) throws IOException {
        World world = Game.getWorld();
        if (world == null)
            return;
        GoggleAuraWorldRenderer.INSTANCE.cartInfos.clear();
        int count = data.readInt();
        for (int i = 0; i < count; i++) {
            GoggleAuraWorldRenderer.INSTANCE.cartInfos.add(new GoggleAuraWorldRenderer.CartInfo(
                    data.readUUID(),
                    data.readUUID(),
                    data.readUUID(),
                    data.readUUID()
            ));
        }
    }

    @Override
    public int getID() {
        return PacketType.SHUNTING_AURA.ordinal();
    }
}

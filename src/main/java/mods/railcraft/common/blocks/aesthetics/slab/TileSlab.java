/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.aesthetics.slab;

import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.blocks.aesthetics.BlockMaterial;
import mods.railcraft.common.blocks.aesthetics.MaterialRegistry;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileSlab extends RailcraftTileEntity {

    private BlockMaterial top;
    private BlockMaterial bottom;

    public BlockMaterial getTopSlab() {
        return top;
    }

    public BlockMaterial getBottomSlab() {
        return bottom;
    }

    public boolean isDoubleSlab() {
        return top != null && bottom != null;
    }

    public boolean isTopSlab() {
        return top != null && bottom == null;
    }

    public void setTopSlab(BlockMaterial slab) {
        if (top != slab) {
            this.top = slab;
            sendUpdateToClient();
        }
    }

    public boolean isBottomSlab() {
        return top == null && bottom != null;
    }

    public void setBottomSlab(BlockMaterial slab) {
        if (bottom != slab) {
            this.bottom = slab;
            sendUpdateToClient();
        }
    }

    public BlockMaterial getUpmostSlab() {
        if (top != null) {
            return top;
        }
        return bottom;
    }

    public boolean addSlab(BlockMaterial slab) {
        if (bottom == null) {
            setBottomSlab(slab);
            return true;
        }
        if (top == null) {
            setTopSlab(slab);
            return true;
        }
        return false;
    }

    @Override
    public String getLocalizationTag() {
        return BlockRailcraftSlab.getTag(getUpmostSlab());
    }

    @Nonnull
    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        if (top != null) {
            data.setString("top", top.getRegistryName());
        }
        if (bottom != null) {
            data.setString("bottom", bottom.getRegistryName());
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.hasKey("top")) {
            top = MaterialRegistry.get(data.getString("top"));
        }
        if (data.hasKey("bottom")) {
            bottom = MaterialRegistry.get(data.getString("bottom"));
        }
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeUTF(top != null ? top.getRegistryName() : "");
        data.writeUTF(bottom != null ? bottom.getRegistryName() : "");
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        String t = data.readUTF();
        if (!t.isEmpty()) {
            top = MaterialRegistry.get(t);
        } else {
            top = null;
        }
        String b = data.readUTF();
        if (!b.isEmpty()) {
            bottom = MaterialRegistry.get(b);
        } else {
            bottom = null;
        }
        markBlockForUpdate();
    }

    @Override
    public short getId() {
        return 43;
    }
}

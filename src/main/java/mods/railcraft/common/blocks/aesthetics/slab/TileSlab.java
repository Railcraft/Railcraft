/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.slab;

import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.blocks.aesthetics.EnumBlockMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileSlab extends RailcraftTileEntity {
    private EnumBlockMaterial top = null;
    private EnumBlockMaterial bottom = null;

    @Override
    public boolean canUpdate() {
        return false;
    }

    public IIcon getTexture(int side) {
        if (bottom != null) {
            return bottom.getIcon(side);
        }
        if (top != null) {
            return top.getIcon(side);
        }
        return EnumBlockMaterial.SANDY_BRICK.getIcon(side);
    }

    public EnumBlockMaterial getTopSlab() {
        return top;
    }

    public EnumBlockMaterial getBottomSlab() {
        return bottom;
    }

    public boolean isDoubleSlab() {
        return top != null && bottom != null;
    }

    public boolean isTopSlab() {
        return top != null && bottom == null;
    }

    public void setTopSlab(EnumBlockMaterial slab) {
        if (top != slab) {
            this.top = slab;
            sendUpdateToClient();
        }
    }

    public boolean isBottomSlab() {
        return top == null && bottom != null;
    }

    public void setBottomSlab(EnumBlockMaterial slab) {
        if (bottom != slab) {
            this.bottom = slab;
            sendUpdateToClient();
        }
    }

    public EnumBlockMaterial getUpmostSlab() {
        if (top != null) {
            return top;
        }
        return bottom;
    }

    public boolean addSlab(EnumBlockMaterial slab) {
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
        return "tile." + BlockRailcraftSlab.getTag(getUpmostSlab());
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        if (top != null) {
            data.setString("top", top.name());
        }
        if (bottom != null) {
            data.setString("bottom", bottom.name());
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.hasKey("top")) {
            top = EnumBlockMaterial.fromName(data.getString("top"));
        }
        if (data.hasKey("bottom")) {
            bottom = EnumBlockMaterial.fromName(data.getString("bottom"));
        }
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte((byte) (top != null ? top.ordinal() : -1));
        data.writeByte((byte) (bottom != null ? bottom.ordinal() : -1));
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        int t = data.readByte();
        if (t != -1) {
            top = EnumBlockMaterial.fromOrdinal(t);
        } else {
            top = null;
        }
        int b = data.readByte();
        if (b != -1) {
            bottom = EnumBlockMaterial.fromOrdinal(b);
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

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
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileSlab extends RailcraftTileEntity {

    @Nonnull
    private BlockMaterial top = BlockMaterial.NO_MAT;
    @Nonnull
    private BlockMaterial bottom = BlockMaterial.NO_MAT;

    public BlockMaterial getTopSlab() {
        return top;
    }

    public BlockMaterial getBottomSlab() {
        return bottom;
    }

    public boolean isDoubleSlab() {
        return top != BlockMaterial.NO_MAT && bottom != BlockMaterial.NO_MAT;
    }

    public boolean isTopSlab() {
        return top != BlockMaterial.NO_MAT && bottom == BlockMaterial.NO_MAT;
    }

    public void setTopSlab(BlockMaterial slab) {
        if (top != slab) {
            this.top = slab;
            sendUpdateToClient();
        }
    }

    public boolean isBottomSlab() {
        return top == BlockMaterial.NO_MAT && bottom != BlockMaterial.NO_MAT;
    }

    public void setBottomSlab(BlockMaterial slab) {
        if (bottom != slab) {
            this.bottom = slab;
            sendUpdateToClient();
        }
    }

    public BlockMaterial getUpmostSlab() {
        if (top != BlockMaterial.NO_MAT)
            return top;
        if (bottom != BlockMaterial.NO_MAT)
            return bottom;
        return BlockMaterial.getPlaceholder();
    }

    public boolean addSlab(BlockMaterial slab) {
        if (bottom == BlockMaterial.NO_MAT) {
            setBottomSlab(slab);
            return true;
        }
        if (top == BlockMaterial.NO_MAT) {
            setTopSlab(slab);
            return true;
        }
        return false;
    }

    @Override
    public String getLocalizationTag() {
        return BlockRailcraftSlab.getTag(getUpmostSlab());
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        if (top != BlockMaterial.NO_MAT) {
            data.setString("top", top.getName());
        }
        if (bottom != BlockMaterial.NO_MAT) {
            data.setString("bottom", bottom.getName());
        }
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.hasKey("top")) {
            top = BlockMaterial.fromName(data.getString("top"));
        }
        if (data.hasKey("bottom")) {
            bottom = BlockMaterial.fromName(data.getString("bottom"));
        }
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeUTF(top != BlockMaterial.NO_MAT ? top.getName() : "");
        data.writeUTF(bottom != BlockMaterial.NO_MAT ? bottom.getName() : "");
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        String t = data.readUTF();
        if (!t.isEmpty()) {
            top = BlockMaterial.fromName(t);
        } else {
            top = BlockMaterial.NO_MAT;
        }
        String b = data.readUTF();
        if (!b.isEmpty()) {
            bottom = BlockMaterial.fromName(b);
        } else {
            bottom = BlockMaterial.NO_MAT;
        }
        markBlockForUpdate();
    }

    @Override
    public short getId() {
        return 43;
    }
}

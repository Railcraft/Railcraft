/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.aesthetics.materials.slab;

import mods.railcraft.common.blocks.TileRailcraft;
import mods.railcraft.common.blocks.aesthetics.materials.IMaterialBlock;
import mods.railcraft.common.blocks.aesthetics.materials.Materials;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileSlab extends TileRailcraft {

    private Materials top = Materials.NO_MAT;

    private Materials bottom = Materials.NO_MAT;

    public Materials getTopSlab() {
        return top;
    }

    public Materials getBottomSlab() {
        return bottom;
    }

    public boolean isDoubleSlab() {
        return top != Materials.NO_MAT && bottom != Materials.NO_MAT;
    }

    public boolean isTopSlab() {
        return top != Materials.NO_MAT && bottom == Materials.NO_MAT;
    }

    public void setTopSlab(Materials slab) {
        if (top != slab) {
            this.top = slab;
            sendUpdateToClient();
        }
    }

    public boolean isBottomSlab() {
        return top == Materials.NO_MAT && bottom != Materials.NO_MAT;
    }

    public void setBottomSlab(Materials slab) {
        if (bottom != slab) {
            this.bottom = slab;
            sendUpdateToClient();
        }
    }

    public Materials getUpmostSlab() {
        if (top != Materials.NO_MAT)
            return top;
        if (bottom != Materials.NO_MAT)
            return bottom;
        return Materials.getPlaceholder();
    }

    public boolean addSlab(Materials slab) {
        if (bottom == Materials.NO_MAT) {
            setBottomSlab(slab);
            return true;
        }
        if (top == Materials.NO_MAT) {
            setTopSlab(slab);
            return true;
        }
        return false;
    }

    @Override
    public String getLocalizationTag() {
        return ((IMaterialBlock) getBlockType()).getTranslationKey(getUpmostSlab()) + ".name";
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        if (top != Materials.NO_MAT) {
            data.setString("top", top.getName());
        }
        if (bottom != Materials.NO_MAT) {
            data.setString("bottom", bottom.getName());
        }
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.hasKey("top")) {
            top = Materials.fromName(data.getString("top"));
        }
        if (data.hasKey("bottom")) {
            bottom = Materials.fromName(data.getString("bottom"));
        }
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeUTF(top != Materials.NO_MAT ? top.getName() : "");
        data.writeUTF(bottom != Materials.NO_MAT ? bottom.getName() : "");
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        String t = data.readUTF();
        if (!t.isEmpty()) {
            top = Materials.fromName(t);
        } else {
            top = Materials.NO_MAT;
        }
        String b = data.readUTF();
        if (!b.isEmpty()) {
            bottom = Materials.fromName(b);
        } else {
            bottom = Materials.NO_MAT;
        }
        markBlockForUpdate();
    }

}

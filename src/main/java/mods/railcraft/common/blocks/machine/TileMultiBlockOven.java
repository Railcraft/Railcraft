/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine;

import buildcraft.api.statements.IActionExternal;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import mods.railcraft.common.plugins.buildcraft.actions.Actions;
import mods.railcraft.common.plugins.buildcraft.triggers.IHasWork;
import mods.railcraft.common.plugins.buildcraft.triggers.INeedsFuel;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumSkyBlock;

public abstract class TileMultiBlockOven extends TileMultiBlockInventory implements INeedsFuel, IHasWork {

    protected int cookTime;
    protected boolean cooking;
    private boolean wasBurning;
    protected boolean paused = false;
    private final Set<IActionExternal> actions = new HashSet<IActionExternal>();

    public TileMultiBlockOven(String name, int invNum, List<? extends MultiBlockPattern> patterns) {
        super(name, invNum, patterns);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (getPatternMarker() == 'W') {
            if (clock % 4 == 0) {
                updateLighting();
            }
        }

        if (Game.isHost(worldObj)) {
            if (isMaster) {
                if (clock % 16 == 0) {
                    processActions();
                }
            }
        }
    }

    protected void updateLighting() {
        boolean b = isBurning();
        if (wasBurning != b) {
            wasBurning = b;
            worldObj.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord, zCoord);
            markBlockForUpdate();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(Random random) {
        updateLighting();
        if (getPatternMarker() == 'W' && isStructureValid() && random.nextInt(100) < 20 && isBurning()) {
            float f = (float) xCoord + 0.5F;
            float f1 = (float) yCoord + 0.4375F + (random.nextFloat() * 3F / 16F);
            float f2 = (float) zCoord + 0.5F;
            float f3 = 0.52F;
            float f4 = random.nextFloat() * 0.6F - 0.3F;
            worldObj.spawnParticle("flame", f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
            worldObj.spawnParticle("flame", f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
            worldObj.spawnParticle("flame", f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);
            worldObj.spawnParticle("flame", f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setInteger("cookTime", cookTime);
        data.setBoolean("cooking", cooking);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        cookTime = data.getInteger("cookTime");
        cooking = data.getBoolean("cooking");
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeInt(cookTime);
        data.writeBoolean(cooking);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        cookTime = data.readInt();
        cooking = data.readBoolean();
    }

    public int getCookTime() {
        TileMultiBlockOven masterOven = (TileMultiBlockOven) getMasterBlock();
        if (masterOven != null) {
            return masterOven.cookTime;
        }
        return -1;
    }

    public boolean isCooking() {
        TileMultiBlockOven masterOven = (TileMultiBlockOven) getMasterBlock();
        if (masterOven != null) {
            return masterOven.cooking;
        }
        return false;
    }

    public boolean isBurning() {
        return isCooking();
    }

    public void setCooking(boolean c) {
        if (cooking != c) {
            cooking = c;
            sendUpdateToClient();
        }
    }

    public void setCookTime(int i) {
        cookTime = i;
    }

    public abstract int getTotalCookTime();

    public int getCookProgressScaled(int i) {
        if (cookTime == 0 || getTotalCookTime() == 0) {
            return 0;
        }
        int scale = (cookTime * i) / getTotalCookTime();
        scale = Math.min(scale, i);
        scale = Math.max(scale, 0);
        return scale;
    }

    public abstract int getBurnProgressScaled(int i);

    @Override
    public int getLightValue() {
        if (getPatternMarker() == 'W' && isStructureValid() && isBurning()) {
            return 13;
        }
        return 0;
    }

    private void processActions() {
        paused = false;
        for (IActionExternal action : actions) {
            if (action == Actions.PAUSE) {
                paused = true;
            }
        }
        actions.clear();
    }

    @Override
    public boolean hasWork() {
        return isCooking();
    }

    @Override
    public void actionActivated(IActionExternal action) {
        TileMultiBlockOven mBlock = (TileMultiBlockOven) getMasterBlock();
        if (mBlock != null) {
            mBlock.actions.add(action);
        }
    }

}

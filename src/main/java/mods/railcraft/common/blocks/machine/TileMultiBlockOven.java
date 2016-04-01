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
import mods.railcraft.common.plugins.buildcraft.actions.Actions;
import mods.railcraft.common.plugins.buildcraft.triggers.IHasWork;
import mods.railcraft.common.plugins.buildcraft.triggers.INeedsFuel;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static net.minecraft.util.EnumParticleTypes.FLAME;

public abstract class TileMultiBlockOven extends TileMultiBlockInventory implements INeedsFuel, IHasWork {

    private final Set<IActionExternal> actions = new HashSet<IActionExternal>();
    protected int cookTime;
    private boolean cooking;
    protected boolean paused;
    private boolean wasBurning;

    public TileMultiBlockOven(String name, int invNum, List<? extends MultiBlockPattern> patterns) {
        super(name, invNum, patterns);
    }

    @Override
    public void update() {
        super.update();

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

    private void updateLighting() {
        boolean b = isBurning();
        if (wasBurning != b) {
            wasBurning = b;
            worldObj.checkLightFor(EnumSkyBlock.BLOCK, getPos());
            markBlockForUpdate();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(Random random) {
        updateLighting();
        if (getPatternMarker() == 'W' && isStructureValid() && random.nextInt(100) < 20 && isBurning()) {
            float x = getPos().getX() + 0.5F;
            float y = getPos().getY() + 0.4375F + (random.nextFloat() * 3F / 16F);
            float z = getPos().getZ() + 0.5F;
            float offset = 0.52F;
            float randVal = random.nextFloat() * 0.6F - 0.3F;
            worldObj.spawnParticle(FLAME, x - offset, y, z + randVal, 0.0D, 0.0D, 0.0D);
            worldObj.spawnParticle(FLAME, x + offset, y, z + randVal, 0.0D, 0.0D, 0.0D);
            worldObj.spawnParticle(FLAME, x + randVal, y, z - offset, 0.0D, 0.0D, 0.0D);
            worldObj.spawnParticle(FLAME, x + randVal, y, z + offset, 0.0D, 0.0D, 0.0D);
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

    public void setCookTime(int i) {
        cookTime = i;
    }

    @SuppressWarnings("WeakerAccess")
    public boolean isCooking() {
        TileMultiBlockOven masterOven = (TileMultiBlockOven) getMasterBlock();
        return masterOven != null && masterOven.cooking;
    }

    protected void setCooking(boolean c) {
        if (cooking != c) {
            cooking = c;
            sendUpdateToClient();
        }
    }

    public boolean isBurning() {
        return isCooking();
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

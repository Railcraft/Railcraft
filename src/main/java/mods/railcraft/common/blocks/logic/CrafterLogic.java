/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import buildcraft.api.statements.IActionExternal;
import mods.railcraft.common.plugins.buildcraft.actions.Actions;
import mods.railcraft.common.plugins.buildcraft.triggers.IHasWork;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by CovertJaguar on 12/27/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class CrafterLogic extends InventoryLogic implements IHasWork {
    protected int progress;
    protected int duration;
    private boolean processing;
    protected boolean paused;
    private final Set<Object> actions = new HashSet<>();

    protected CrafterLogic(Adapter adapter, int sizeInv) {
        super(adapter, sizeInv);
    }

    @Override
    void updateServer() {
        if (clock(16)) {
            processActions();
        }
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int i) {
        progress = i;
    }

    public boolean isProcessing() {
        return processing;
    }

    protected void setProcessing(boolean c) {
        if (processing != c) {
            processing = c;
            sendUpdateToClient();
        }
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public double getProgressPercent() {
        if (getProgress() == 0 || getDuration() == 0) {
            return 0;
        }
        return (double) getProgress() / getDuration();
    }

    protected void processActions() {
        paused = actions.stream().anyMatch(a -> a == Actions.PAUSE);
        actions.clear();
    }

    @Override
    public void actionActivated(IActionExternal action) {
        actions.add(action);
    }

    @Override
    public boolean hasWork() {
        return isProcessing();
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setInteger("cookTime", progress);
        data.setBoolean("cooking", processing);
        return super.writeToNBT(data);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        progress = data.getInteger("cookTime");
        processing = data.getBoolean("cooking");
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeInt(progress);
        data.writeBoolean(processing);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);

        progress = data.readInt();
        processing = data.readBoolean();
    }
}

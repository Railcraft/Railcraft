/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
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
    public static int PROGRESS_STEP = 16;
    protected int progress;
    protected int duration;
    private boolean processing;
    protected boolean paused;
    private int finishedAt;
    private final Set<Object> actions = new HashSet<>();

    protected CrafterLogic(Adapter adapter, int sizeInv) {
        super(adapter, sizeInv);
    }

    @Override
    protected void updateServer() {
        if (clock(PROGRESS_STEP)) {
            processActions();
            progressCrafting();
        }
    }

    public final int getProgress() {
        return progress;
    }

    public final void setProgress(int i) {
        progress = i;
    }

    protected void reset() {
        setProgress(0);
        setProcessing(false);
        sendUpdateToClient();
    }

    public final boolean isProcessing() {
        return processing;
    }

    protected final void setProcessing(boolean c) {
        if (processing != c) {
            processing = c;
            sendUpdateToClient();
        }
    }

    public final void setDuration(int duration) {
        this.duration = duration;
    }

    public final int getDuration() {
        return duration;
    }

    protected abstract int calculateDuration();

    protected final void setFinished() {
        finishedAt = clock();
    }

    protected final boolean isFinished() {
        return processing && clock() > finishedAt + PROGRESS_STEP + 5;
    }

    public final double getProgressPercent() {
        if (getProgress() == 0 || getDuration() == 0) {
            return 0;
        }
        return (double) getProgress() / getDuration();
    }

    protected final void processActions() {
        paused = actions.stream().anyMatch(a -> a == Actions.PAUSE);
        actions.clear();
    }

    @Override
    public final void actionActivated(IActionExternal action) {
        actions.add(action);
    }

    @Override
    public boolean hasWork() {
        return isProcessing();
    }

    protected void setupCrafting() {}

    protected boolean lacksRequirements() {
        return false;
    }

    protected boolean doProcessStep() {return true;}

    protected final void progressCrafting() {
        if (isFinished()) setProcessing(false);
        if (paused) return;

        setupCrafting();

        if (lacksRequirements()) {
            reset();
            return;
        }

        setProcessing(true);
        if (doProcessStep()) {
            progress += PROGRESS_STEP;
            duration = calculateDuration();
            if (progress < duration) return;

            progress = duration;
            setFinished();
            if (craftAndPush())
                reset();
        }
    }

    protected abstract boolean craftAndPush();

    @Override
    @OverridingMethodsMustInvokeSuper
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("cookTime", progress);
        data.setBoolean("cooking", processing);
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
        data.writeBoolean(processing);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        processing = data.readBoolean();
    }
}

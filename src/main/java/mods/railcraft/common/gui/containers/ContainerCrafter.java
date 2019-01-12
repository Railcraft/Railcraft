/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.logic.CrafterLogic;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by CovertJaguar on 1/11/2019 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ContainerCrafter extends RailcraftContainer {
    protected final CrafterLogic logic;
    private int lastProgress, lastDuration;

    public ContainerCrafter(CrafterLogic logic) {
        super(logic);
        this.logic = logic;
    }

    protected final void addPlayerSlots(InventoryPlayer invPlayer) {
        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new Slot(invPlayer, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
            }
        }
        for (int j = 0; j < 9; j++) {
            addSlot(new Slot(invPlayer, j, 8 + j * 18, 142));
        }
    }

    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();

        for (IContainerListener listener : listeners) {
            int progress = logic.getProgress();
            if (lastProgress != progress)
                listener.sendWindowProperty(this, 10, progress);

            int duration = logic.getDuration();
            if (lastDuration != duration)
                listener.sendWindowProperty(this, 11, duration);
        }

        lastProgress = logic.getProgress();
        lastDuration = logic.getDuration();
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);

        listener.sendWindowProperty(this, 10, logic.getProgress());
        listener.sendWindowProperty(this, 11, logic.getDuration());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        switch (id) {
            case 10:
                logic.setProgress(data);
                break;
            case 11:
                logic.setDuration(data);
        }
    }
}

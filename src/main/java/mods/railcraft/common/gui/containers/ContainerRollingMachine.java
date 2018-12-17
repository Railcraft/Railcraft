/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.api.crafting.IRollingMachineRecipe;
import mods.railcraft.common.blocks.TileRailcraft;
import mods.railcraft.common.blocks.machine.equipment.TileRollingMachine;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.slots.SlotUnshiftable;
import mods.railcraft.common.gui.slots.SlotUntouchable;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.crafting.RollingMachineCraftingManager;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;

public class ContainerRollingMachine extends RailcraftContainer {

    final TileRollingMachine tile;
    private final InventoryCrafting craftMatrix;
    private final IInventory craftResult;
    private int lastProgress;
    private ItemStack prevOutput;

    public ContainerRollingMachine(final InventoryPlayer inventoryplayer, final TileRollingMachine tile) {
        this(inventoryplayer, tile, 93, 27);
    }

    public ContainerRollingMachine(final InventoryPlayer inventoryplayer, final TileRollingMachine tile, int cx, int cy) {
        this.tile = tile;
        craftMatrix = tile.getCraftMatrix(this);
        craftResult = new InventoryCraftResult() {
            @Override
            public void setInventorySlotContents(int slot, ItemStack stack) {
                super.setInventorySlotContents(slot, stack);
                if (!stack.isEmpty() && Game.isClient(tile.getWorld()))
                    InvTools.addItemToolTip(stack, LocalizationPlugin.translate("gui.railcraft.rolling.machine.tips.craft"));
            }
        };

        addSlot(new SlotRollingMachine(craftResult, 0, cx, cy));
        addSlot(new SlotOutput(tile.getInvResult(), 0, 124, 35));

        for (int l = 0; l < 3; l++) {
            for (int k1 = 0; k1 < 3; k1++) {
                addSlot(new SlotUnshiftable(craftMatrix, k1 + l * 3, 30 + k1 * 18, 17 + l * 18));
            }
        }

        for (int i1 = 0; i1 < 3; i1++) {
            for (int l1 = 0; l1 < 9; l1++) {
                addSlot(new Slot(inventoryplayer, l1 + i1 * 9 + 9, 8 + l1 * 18, 84 + i1 * 18));
            }
        }

        for (int j1 = 0; j1 < 9; j1++) {
            addSlot(new Slot(inventoryplayer, j1, 8 + j1 * 18, 142));
        }

        onCraftMatrixChanged(craftMatrix);
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendWindowProperty(this, 0, tile.getProgress());
        listener.sendWindowProperty(this, 1, tile.getProcessTime());
    }

    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();
        for (IContainerListener listener : listeners) {
            if (lastProgress != tile.getProgress()) {
                listener.sendWindowProperty(this, 0, tile.getProgress());
                listener.sendWindowProperty(this, 1, tile.getProcessTime());
            }
        }

        ItemStack output = tile.getInvResult().getStackInSlot(0);
        if (!InvTools.isItemEqualStrict(output, prevOutput)) {
            onCraftMatrixChanged(craftMatrix);
            prevOutput = InvTools.copy(output);
        }

        lastProgress = tile.getProgress();
    }

    @Override
    public void updateProgressBar(int id, int data) {
        switch (id) {
            case 0:
                tile.setProgress(data);
                break;
            case 1:
                tile.setProcessTime(data);
                break;
            default:
                //Noop
        }
    }

    @Override
    public final void onCraftMatrixChanged(IInventory inv) {
        IRollingMachineRecipe output = RollingMachineCraftingManager.getInstance().findMatching(craftMatrix);
        craftResult.setInventorySlotContents(0, output == null ? ItemStack.EMPTY : output.getOutput(craftMatrix));
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return TileRailcraft.isUsableByPlayerHelper(tile, player);
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        tile.onGuiClosed(playerIn);
    }

    private class SlotRollingMachine extends SlotUntouchable {

        SlotRollingMachine(IInventory contents, int id, int x, int y) {
            super(contents, id, x, y);
        }

        @Override
        public ItemStack onTake(EntityPlayer player, ItemStack stack) {
            super.onTake(player, stack);
            tile.useLast = true;
            return stack;
        }
    }
}

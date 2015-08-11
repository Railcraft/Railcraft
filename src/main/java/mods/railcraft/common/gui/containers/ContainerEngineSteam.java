/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.containers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import mods.railcraft.common.blocks.machine.beta.TileEngineSteam;
import mods.railcraft.common.gui.widgets.IndicatorWidget;
import mods.railcraft.common.gui.widgets.FluidGaugeWidget;
import mods.railcraft.common.gui.widgets.RFEnergyIndicator;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.entity.player.EntityPlayerMP;

public class ContainerEngineSteam extends RailcraftContainer {

    private final TileEngineSteam tile;
    private int lastEnergy;
    private float lastOutput;
    private final RFEnergyIndicator energyIndicator;

    public ContainerEngineSteam(InventoryPlayer inventoryplayer, TileEngineSteam tile) {
        this.tile = tile;

        addWidget(new FluidGaugeWidget(tile.getTankManager().get(0), 71, 23, 176, 0, 16, 47));

        energyIndicator = new RFEnergyIndicator(tile.maxEnergy());
        addWidget(new IndicatorWidget(energyIndicator, 94, 25, 176, 47, 6, 43));

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new Slot(inventoryplayer, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
            }
        }

        for (int j = 0; j < 9; j++) {
            addSlot(new Slot(inventoryplayer, j, 8 + j * 18, 142));
        }
    }

    @Override
    public void addCraftingToCrafters(ICrafting crafter) {
        super.addCraftingToCrafters(crafter);
        tile.getTankManager().initGuiData(this, crafter, 0);

        PacketBuilder.instance().sendGuiIntegerPacket((EntityPlayerMP) crafter, windowId, 12, tile.energy);
        crafter.sendProgressBarUpdate(this, 14, Math.round(tile.currentOutput * 100));
    }

    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();
        tile.getTankManager().updateGuiData(this, crafters, 0);

        for (int var1 = 0; var1 < this.crafters.size(); ++var1) {
            ICrafting crafter = (ICrafting) this.crafters.get(var1);

            if (this.lastEnergy != tile.energy)
                PacketBuilder.instance().sendGuiIntegerPacket((EntityPlayerMP) crafter, windowId, 13, tile.energy);

            if (this.lastOutput != tile.currentOutput)
                crafter.sendProgressBarUpdate(this, 14, Math.round(tile.currentOutput * 100));
        }

        this.lastEnergy = tile.energy;
        this.lastOutput = tile.currentOutput;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {
        tile.getTankManager().processGuiUpdate(id, value);

        switch (id) {
            case 12:
                energyIndicator.setEnergy(value);
                break;
            case 13:
                energyIndicator.updateEnergy(value);
                break;
            case 14:
                tile.currentOutput = value / 100f;
                break;
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return RailcraftTileEntity.isUseableByPlayerHelper(tile, entityplayer);
    }

}

/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.single;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.TileSmartItemTicking;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.plugins.forge.AIPlugin;
import mods.railcraft.common.util.entity.ai.EntityAISearchForBlock;
import mods.railcraft.common.util.entity.ai.EntityAIWatchBlock;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.logic.Logic;
import mods.railcraft.common.util.logic.ILogicContainer;
import mods.railcraft.common.util.logic.TradeStationLogic;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class TileTradeStation extends TileSmartItemTicking implements ILogicContainer<TradeStationLogic>, IGuiReturnHandler, ISidedInventory {

    private static final int[] SLOTS = InvTools.buildSlotArray(0, 16);
    private final TradeStationLogic logic;

    public TileTradeStation() {
        super(16);
        logic = new TradeStationLogic(Logic.Adapter.of(this), this) {

            @Override
            protected void modifyNearbyAI() {
                for (EntityVillager villager : findNearbyVillagers(20)) {
                    AIPlugin.addAITask(villager, 9, new EntityAIWatchBlock(villager, RailcraftBlocks.TRADE_STATION.getDefaultState(), 4, 0.08F));
                    AIPlugin.addAITask(villager, 9, new EntityAISearchForBlock(villager, RailcraftBlocks.TRADE_STATION.getDefaultState(), 16, 0.002F));
                }
            }
        };
    }

    @Override
    public TradeStationLogic getLogic() {
        return logic;
    }

    @Override
    public void update() {
        super.update();
        logic.update();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        return logic.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        logic.readFromNBT(data);
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        logic.writePacketData(data);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        logic.readPacketData(data);
    }

    @Override
    public void readGuiData(RailcraftInputStream data, @Nullable EntityPlayer sender) throws IOException {
        logic.readGuiData(data, sender);
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return slot < 10;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing face) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, EnumFacing face) {
        return isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing face) {
        return slot >= 10;
    }

    @Override
    public @Nullable EnumGui getGui() {
        return EnumGui.TRADE_STATION;
    }
}

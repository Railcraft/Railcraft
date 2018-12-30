/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks;

import buildcraft.api.statements.IActionExternal;
import mods.railcraft.common.plugins.buildcraft.actions.IActionReceptor;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.blocks.logic.ILogicContainer;
import mods.railcraft.common.blocks.logic.Logic;
import mods.railcraft.common.blocks.logic.StructureLogic;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.io.IOException;
import java.util.Optional;

/**
 * Created by CovertJaguar on 12/27/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileLogic extends TileRailcraftTicking implements ISmartTile, IActionReceptor, ILogicContainer {
    private Logic logic;

    protected void setLogic(Logic logic) {
        this.logic = logic;
    }

    @Override
    public <L> Optional<L> getLogic(Class<L> logicClass) {
        return logic.getLogic(logicClass);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void update() {
        super.update();
        logic.update();
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onBlockAdded() {
        if (Game.isClient(world)) return;
        getLogic(StructureLogic.class).ifPresent(StructureLogic::onBlockChange);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onBlockRemoval() {
        if (Game.isClient(world)) return;
        getLogic(StructureLogic.class).ifPresent(StructureLogic::onBlockChange);
        getLogic(IInventory.class).ifPresent(i -> InvTools.spewInventory(i, world, getPos()));
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onLoad() {
        super.onLoad();
        getLogic(StructureLogic.class).ifPresent(StructureLogic::onBlockChange);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onChunkUnload() {
        super.onChunkUnload();
        if (Game.isClient(world)) return;
        getLogic(StructureLogic.class).ifPresent(StructureLogic::scheduleMasterRetest);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void invalidate() {
        if (world == null || Game.isHost(world)) {
            getLogic(StructureLogic.class).ifPresent(StructureLogic::scheduleMasterRetest);
        }
        super.invalidate();
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data = super.writeToNBT(data);
        data = logic.writeToNBT(data);
        return data;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        logic.readFromNBT(data);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        logic.writePacketData(data);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void readPacketData(RailcraftInputStream data) throws IOException {
        logic.readPacketData(data);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void writeGuiData(RailcraftOutputStream data) throws IOException {
        logic.writeGuiData(data);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void readGuiData(RailcraftInputStream data, EntityPlayer sender) throws IOException {
        logic.readGuiData(data, sender);
    }

    @Override
    public void actionActivated(IActionExternal action) {
        getLogic(IActionReceptor.class).ifPresent(l -> l.actionActivated(action));
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                && getLogic(IItemHandler.class).isPresent())
            return true;
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
                && getLogic(IFluidHandler.class).isPresent())
            return true;
        return super.hasCapability(capability, facing);
    }

    @Override
    public @Nullable <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            Optional<IItemHandler> inv = getLogic(IItemHandler.class);
            if (inv.isPresent())
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inv.get());
        }
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            Optional<IFluidHandler> tank = getLogic(IFluidHandler.class);
            if (tank.isPresent())
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tank.get());
        }
        return super.getCapability(capability, facing);
    }
}

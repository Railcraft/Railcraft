/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks;

import buildcraft.api.statements.IActionExternal;
import mods.railcraft.common.blocks.logic.ILogicContainer;
import mods.railcraft.common.blocks.logic.InventoryLogic;
import mods.railcraft.common.blocks.logic.Logic;
import mods.railcraft.common.blocks.logic.StructureLogic;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.plugins.buildcraft.actions.IActionReceptor;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
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
    public void onNeighborBlockChange(IBlockState state, Block neighborBlock, BlockPos neighborPos) {
        super.onNeighborBlockChange(state, neighborBlock, neighborPos);
        if (Game.isClient(world)) return;
        getLogic(StructureLogic.class).ifPresent(logic -> {
            if (logic.isPart(neighborBlock) || neighborBlock == Blocks.AIR)
                logic.onBlockChange();
        });
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
        getLogic(IInventory.class).ifPresent(i -> InvTools.spewInventory(i, world, getPos()));
        getLogic(StructureLogic.class).ifPresent(StructureLogic::onBlockChange);
    }

    @Override
    public boolean blockActivated(EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (PlayerPlugin.doesItemBlockActivation(player, hand))
            return false;
        return logic.interact(player, hand) || openGui(player);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onLoad() {
        super.onLoad();
        if (Game.isClient(world)) return;
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
        logic.writeToNBT(data);
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
    public @Nullable EnumGui getGui() {
        return logic.getGUI();
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        if (getLogic(StructureLogic.class).map(StructureLogic::isStructureValid).orElse(true))
            return ISmartTile.super.openGui(player);
        return false;
    }

    @Override
    public void actionActivated(IActionExternal action) {
        getLogic(IActionReceptor.class).ifPresent(l -> l.actionActivated(action));
    }

    @Override
    public boolean canCreatureSpawn(EntityLiving.SpawnPlacementType type) {
        return getLogic(StructureLogic.class).map(l -> !(l.isStructureValid() && l.getPatternPosition() != null && l.getPatternPosition().getY() < 2)).orElse(true);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                && getLogic(InventoryLogic.class).isPresent())
            return true;
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
                && getLogic(IFluidHandler.class).isPresent())
            return true;
        return super.hasCapability(capability, facing);
    }

    @Override
    public @Nullable <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            Optional<InventoryLogic> inv = getLogic(InventoryLogic.class);
            if (inv.isPresent())
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inv.get().getItemHandler(facing));
        }
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            Optional<IFluidHandler> tank = getLogic(IFluidHandler.class);
            if (tank.isPresent())
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tank.get());
        }
        return super.getCapability(capability, facing);
    }
}

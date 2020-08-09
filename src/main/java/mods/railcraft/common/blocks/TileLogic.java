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
import mods.railcraft.common.blocks.interfaces.IDropsInv;
import mods.railcraft.common.blocks.interfaces.ITileCompare;
import mods.railcraft.common.blocks.logic.*;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.plugins.buildcraft.actions.IActionReceptor;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
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
public abstract class TileLogic extends TileRailcraftTicking implements ISmartTile, IActionReceptor, ILogicContainer, ITileCompare {
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
    public void onBlockPlacedBy(IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(state, placer, stack);
        logic.placed(state, placer, stack);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onNeighborBlockChange(IBlockState ourState, Block neighborBlock, BlockPos fromPos) {
        super.onNeighborBlockChange(ourState, neighborBlock, fromPos);
        if (Game.isClient(world)) return;
        boolean isStructureUpdating = WorldPlugin.getTileEntity(world, fromPos, TileLogic.class)
                .flatMap(tileLogic -> tileLogic.getLogic(StructureLogic.class))
                .map(StructureLogic::isUpdatingNeighbors)
                .orElse(false);

        if (!isStructureUpdating)
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
        getLogic(IDropsInv.class).ifPresent(i -> i.spewInventory(world, getPos()));
        getLogic(StructureLogic.class).ifPresent(StructureLogic::onBlockChange);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
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
        return getLogic(StructureLogic.class).map(l -> !(l.isStructureValid() && l.getMasterPos().getY() == getY())).orElse(true);
    }

    @Override
    public int getComparatorInputOverride() {
        return getLogic(ITileCompare.class).map(ITileCompare::getComparatorInputOverride).orElse(0);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                && getLogic(InventoryLogic.class).isPresent())
            return true;
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
                && getLogic(IFluidHandler.class).isPresent())
            return true;
        if (capability == CapabilityEnergy.ENERGY
                && getLogic(IEnergyStorage.class).isPresent())
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
            FluidLogic handler = getLogic(FluidLogic.class)
                    .filter(logic -> !logic.isHidden())
                    .orElse(null);
            if (handler != null)
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(handler);
        }
        if (capability == CapabilityEnergy.ENERGY) {
            Optional<IEnergyStorage> energy = getLogic(IEnergyStorage.class);
            if (energy.isPresent())
                return CapabilityEnergy.ENERGY.cast(energy.get());
        }
        return super.getCapability(capability, facing);
    }
}

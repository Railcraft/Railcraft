/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.carts;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import mods.railcraft.common.blocks.logic.ILogicContainer;
import mods.railcraft.common.blocks.logic.InventoryLogic;
import mods.railcraft.common.blocks.logic.Logic;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.util.inventory.IExtInvSlot;
import mods.railcraft.common.util.inventory.IInventoryImplementor;
import mods.railcraft.common.util.inventory.InventoryIterator;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.Optionals;
import mods.railcraft.common.util.network.PacketBuilder;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.io.IOException;
import java.util.Optional;

/**
 * Created by CovertJaguar on 12/28/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class CartBaseLogic extends CartBase implements ILogicContainer, IEntityAdditionalSpawnData {
    private Logic logic;

    protected CartBaseLogic(World world) {
        super(world);
    }

    protected CartBaseLogic(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public void setLogic(Logic logic) {
        this.logic = logic;
    }

    @Override
    public <L> Optional<L> getLogic(Class<L> logicClass) {
        return Optional.of(logic).map(Optionals.toType(logicClass));
    }

    @Override
    public void setDead() {
        if (Game.isClient(world))
            getLogic(IInventoryImplementor.class).ifPresent(inv ->
                    InventoryIterator.get(inv).stream().forEach(IExtInvSlot::clear));
        super.setDead();
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public boolean doInteract(EntityPlayer player, EnumHand hand) {
        if (Game.isHost(world)) {
            if (!logic.interact(player, hand))
                openRailcraftGui(player);
        }
        return true;
    }

    protected void openRailcraftGui(EntityPlayer player) {
        EnumGui gui = getGUI();
        if (gui != null)
            GuiHandler.openGui(gui, player, world, this);
    }

    protected @Nullable EnumGui getGUI() {
        return logic.getGUI();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        logic.update();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        logic.writeToNBT(data);
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        logic.readFromNBT(data);
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        logic.writePacketData(data);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        logic.readPacketData(data);
    }

    @Override
    public void writeGuiData(RailcraftOutputStream data) throws IOException {
        logic.writeGuiData(data);
    }

    @Override
    public void readGuiData(RailcraftInputStream data, EntityPlayer sender) throws IOException {
        logic.readGuiData(data, sender);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        try {
            writePacketData(new RailcraftOutputStream(new ByteBufOutputStream(buffer)));
        } catch (IOException ignored) {
        }
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        try {
            readPacketData(new RailcraftInputStream(new ByteBufInputStream(buffer)));
        } catch (IOException ignored) {
        }
    }

    @Override
    public void sendUpdateToClient() {
        if (isAddedToWorld() && isEntityAlive())
            PacketBuilder.instance().sendEntitySync(this);
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

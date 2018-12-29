/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.carts;

import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.util.inventory.IExtInvSlot;
import mods.railcraft.common.util.inventory.InventoryIterator;
import mods.railcraft.common.util.logic.ILogicContainer;
import mods.railcraft.common.util.logic.Logic;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.io.IOException;
import java.util.Optional;

/**
 * Created by CovertJaguar on 12/28/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class CartBaseLogic extends CartBase implements ILogicContainer {
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
        return Optional.of(logicClass.cast(logic));
    }

    @Override
    public void setDead() {
        if (Game.isClient(world))
            getLogic(IItemHandlerModifiable.class).ifPresent(inv ->
                    InventoryIterator.get(inv).stream().forEach(IExtInvSlot::clear));
        super.setDead();
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public boolean doInteract(EntityPlayer player, EnumHand hand) {
        if (Game.isHost(world)) {
            openRailcraftGui(player);
        }
        return true;
    }

    protected void openRailcraftGui(EntityPlayer player) {
        GuiHandler.openGui(getGuiType(), player, world, this);
    }

    protected abstract EnumGui getGuiType();

    @Override
    public void onUpdate() {
        super.onUpdate();
        logic.update();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data = logic.writeToNBT(data);
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
    public void sendUpdateToClient() {

    }
}

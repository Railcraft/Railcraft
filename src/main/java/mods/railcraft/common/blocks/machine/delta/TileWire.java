/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.delta;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.api.electricity.IElectricGrid;
import mods.railcraft.common.blocks.frame.BlockFrame;
import mods.railcraft.common.blocks.machine.BoundingBoxManager;
import mods.railcraft.common.blocks.machine.BoundingBoxManager.ReducedBoundingBox;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileWire extends TileMachineBase implements IElectricGrid {

    public enum AddonType {

        NONE, FRAME, PYLON;
        public static final AddonType[] VALUES = values();

        public static AddonType fromOrdinal(int ordinal) {
            if (ordinal < 0 || ordinal >= VALUES.length)
                return NONE;
            return VALUES[ordinal];
        }

    }
    private final ChargeHandler chargeHandler = new ChargeHandler(this, ChargeHandler.ConnectType.WIRE, 0.25);
    private AddonType addon = AddonType.NONE;

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineDelta.WIRE;
    }

    @Override
    public IIcon getIcon(int side) {
        return getMachineType().getTexture(0);
    }

    @Override
    public boolean blockActivated(EntityPlayer player, int side) {
        ItemStack current = player.getCurrentEquippedItem();
        if (current != null && InvTools.isStackEqualToBlock(current, BlockFrame.getBlock()))
            if (setAddon(AddonType.FRAME)) {
                if (!player.capabilities.isCreativeMode)
                    player.setCurrentItemOrArmor(0, InvTools.depleteItem(current));
                return true;
            }
        return super.blockActivated(player, side);
    }

    @Override
    public ArrayList<ItemStack> getDrops(int fortune) {
        ArrayList<ItemStack> drops = super.getDrops(fortune);
        if (addon == AddonType.FRAME && BlockFrame.getBlock() != null)
            drops.add(BlockFrame.getItem());
        return drops;
    }

    @Override
    public boolean isSideSolid(ForgeDirection side) {
        if (addon == AddonType.FRAME)
            return side == ForgeDirection.UP;
        return false;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (Game.isNotHost(getWorld()))
            return;

        chargeHandler.tick();
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        chargeHandler.writeToNBT(data);
        data.setString("addonType", addon.name());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        chargeHandler.readFromNBT(data);
        addon = AddonType.valueOf(data.getString("addonType"));
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte(addon.ordinal());
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        setAddon(AddonType.fromOrdinal(data.readByte()));
        markBlockForUpdate();
    }

    public AddonType getAddon() {
        return addon;
    }

    public boolean setAddon(AddonType addon) {
        if (this.addon == addon)
            return false;
        if (this.addon != AddonType.NONE) {
            //dropstuff
        }
        this.addon = addon;
        sendUpdateToClient();
        return true;
    }

    @Override
    public ChargeHandler getChargeHandler() {
        return chargeHandler;
    }

    @Override
    public TileEntity getTile() {
        return this;
    }

    @Override
    public IPostConnection.ConnectStyle connectsToPost(ForgeDirection side) {
        if (getAddon() == AddonType.FRAME)
            return IPostConnection.ConnectStyle.TWO_THIN;
        return IPostConnection.ConnectStyle.NONE;
    }

    public static class WireBoundingBox extends ReducedBoundingBox {

        public WireBoundingBox() {
            super(4);
        }

        @Override
        public AxisAlignedBB getBox(World world, int x, int y, int z) {
            TileEntity tile = WorldPlugin.getBlockTile(world, x, y, z);
            if (tile instanceof TileWire) {
                TileWire wire = (TileWire) tile;
                AddonType type = wire.getAddon();
                if (type == AddonType.NONE)
                    return super.getBox(world, x, y, z);
            }
            return BoundingBoxManager.DEFAULT.getBox(world, x, y, z);
        }

    }

}

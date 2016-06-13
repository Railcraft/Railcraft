/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.delta;

import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.api.electricity.IElectricGrid;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.BoundingBoxManager;
import mods.railcraft.common.blocks.machine.BoundingBoxManager.ReducedBoundingBox;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

/**
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
    public EnumMachineDelta getMachineType() {
        return EnumMachineDelta.WIRE;
    }

    @Override
    public boolean blockActivated(EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side) {
        if (heldItem != null && InvTools.isStackEqualToBlock(heldItem, RailcraftBlocks.frame.block()))
            if (setAddon(AddonType.FRAME)) {
                if (!player.capabilities.isCreativeMode)
                    player.setHeldItem(hand, InvTools.depleteItem(heldItem));
                return true;
            }
        return super.blockActivated(player, hand, heldItem, side);
    }

    @Override
    public List<ItemStack> getDrops(int fortune) {
        List<ItemStack> drops = super.getDrops(fortune);
        if (addon == AddonType.FRAME) {
            ItemStack stackFrame = RailcraftBlocks.frame.getStack();
            if (stackFrame != null)
                drops.add(stackFrame);
        }
        return drops;
    }

    @Override
    public boolean isSideSolid(EnumFacing side) {
        return addon == AddonType.FRAME && side == EnumFacing.UP;
    }

    @Override
    public void update() {
        super.update();

        if (Game.isClient(getWorld()))
            return;

        chargeHandler.tick();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        chargeHandler.writeToNBT(data);
        data.setString("addonType", addon.name());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        chargeHandler.readFromNBT(data);
        addon = AddonType.valueOf(data.getString("addonType"));
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte(addon.ordinal());
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
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
        //TODO: drop stuff
//        if (this.addon != AddonType.NONE) {
//        }
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
    public IPostConnection.ConnectStyle connectsToPost(EnumFacing side) {
        if (getAddon() == AddonType.FRAME)
            return IPostConnection.ConnectStyle.TWO_THIN;
        return IPostConnection.ConnectStyle.NONE;
    }

    public static class WireBoundingBox extends ReducedBoundingBox {

        public WireBoundingBox() {
            super(4);
        }

        @Override
        public AxisAlignedBB getBox(World world, BlockPos pos) {
            TileEntity tile = WorldPlugin.getBlockTile(world, pos);
            if (tile instanceof TileWire) {
                TileWire wire = (TileWire) tile;
                AddonType type = wire.getAddon();
                if (type == AddonType.NONE)
                    return super.getBox(world, pos);
            }
            return BoundingBoxManager.DEFAULT.getBox(world, pos);
        }
    }
}

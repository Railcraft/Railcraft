/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.wayobjects.signals;

import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.common.blocks.interfaces.ITileLit;
import mods.railcraft.common.blocks.interfaces.ITileRotate;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.plugins.buildcraft.triggers.IAspectProvider;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public abstract class TileSignalBase extends TileMachineBase implements IAspectProvider, ITileRotate, ITileLit {

    private EnumFacing facing = EnumFacing.NORTH;
    private int prevLightValue;

    @Override
    public EnumFacing[] getValidRotations() {
        return EnumFacing.HORIZONTALS;
    }

    @Override
    public void update() {
        super.update();
        if (Game.isClient(world)) {
            boolean needsUpdate = false;
            int lightValue = getLightValue();
            if (prevLightValue != lightValue) {
                prevLightValue = lightValue;
                world.checkLightFor(EnumSkyBlock.BLOCK, getPos());
                needsUpdate = true;
            }
            if (needsUpdate) {
                markBlockForUpdate();
            }
        }
    }

    @Override
    public int getLightValue() {
        return getSignalAspect().getLightValue();
    }

    @Override
    public EnumFacing getFacing() {
        return facing;
    }

    @Override
    public void setFacing(EnumFacing facing) {
        this.facing = facing;
    }

    @Override
    public void onBlockPlacedBy(IBlockState state, @Nullable EntityLivingBase entityLiving, ItemStack stack) {
        super.onBlockPlacedBy(state, entityLiving, stack);
        facing = MiscTools.getHorizontalSideFacingPlayer(entityLiving);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setByte("Facing", (byte) facing.ordinal());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        facing = EnumFacing.byIndex(data.getByte("Facing"));
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeByte((byte) facing.ordinal());
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);

        facing = EnumFacing.byIndex(data.readByte());

        markBlockForUpdate();
    }

    public abstract SignalAspect getSignalAspect();

    @Override
    public SignalAspect getTriggerAspect() {
        return getSignalAspect();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public double getMaxRenderDistanceSquared() {
        return 512.0 * 512.0;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return TileEntity.INFINITE_EXTENT_AABB;
    }
}

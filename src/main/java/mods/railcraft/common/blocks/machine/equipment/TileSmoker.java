/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.equipment;

import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.blocks.machine.interfaces.ITileCompare;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.effects.EffectManager;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.util.Constants;

import java.io.IOException;
import java.util.Random;

import javax.annotation.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileSmoker extends TileMachineBase implements ITileCompare {

    private static final int SNOW_MELT_INTERVAL = 32;
    private static final Random rand = MiscTools.RANDOM;
    private boolean powered;
    private EnumColor color = EnumColor.BLACK;

    @Override
    public EquipmentVariant getMachineType() {
        return EquipmentVariant.SMOKER;
    }

    @Override
    public void update() {
        super.update();
        if (!powered) {
            if (Game.isHost(worldObj)) {
                if (clock % SNOW_MELT_INTERVAL == 0) {
                    Block blockAbove = WorldPlugin.getBlock(worldObj, getPos().up());
                    if (blockAbove == Blocks.SNOW_LAYER)
                        WorldPlugin.setBlockToAir(worldObj, getPos().up());
                }
            } else {
                if (!WorldPlugin.isBlockAir(worldObj, getPos().up())) return;
                double px = getX() + rand.nextFloat();
                double py = getY() + rand.nextFloat() * 0.5F + 1;
                double pz = getZ() + rand.nextFloat();
                EffectManager.instance.chimneyEffect(worldObj, px, py, pz, color);
            }
        }
    }

    @Override
    public void onNeighborBlockChange(IBlockState state, Block block) {
        super.onNeighborBlockChange(state, block);
        powered = PowerPlugin.isBlockBeingPowered(worldObj, getPos());
        sendUpdateToClient();
    }

    @Override
    public boolean isSideSolid(EnumFacing side) {
        return side != EnumFacing.UP;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        powered = data.getBoolean("powered");
        if (data.hasKey("color", Constants.NBT.TAG_BYTE)) {
            color = EnumColor.fromOrdinal(data.getByte("color"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("powered", powered);
        data.setByte("color", (byte) color.ordinal());
        return data;
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeBoolean(powered);
        data.writeByte(color.ordinal());
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        powered = data.readBoolean();
        color = EnumColor.fromOrdinal(data.readByte());
    }

    @Override
    public boolean blockActivated(EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY,
        float hitZ) {
        if (super.blockActivated(player, hand, heldItem, side, hitX, hitY, hitZ))
            return true;
        if (player.isSneaking())
            return false;
        if (InvTools.isEmpty(heldItem) || hand == EnumHand.OFF_HAND)
            return false;
        EnumColor color = EnumColor.dyeColorOf(heldItem);
        if (color == null || color == this.color) {
            return false;
        }
        if (!player.capabilities.isCreativeMode)
            player.setHeldItem(hand, InvTools.depleteItem(heldItem));
        this.color = color;
        markDirty();
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        return color.ordinal();
    }
}

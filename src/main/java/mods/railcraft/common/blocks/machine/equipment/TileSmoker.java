/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.equipment;

import mods.railcraft.client.util.effects.ClientEffects;
import mods.railcraft.common.blocks.interfaces.ITileCompare;
import mods.railcraft.common.blocks.interfaces.ITileNonSolid;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.misc.Optionals;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import java.io.IOException;
import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileSmoker extends TileMachineBase implements ITileCompare, ITileNonSolid {

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
            if (Game.isHost(world)) {
                if (clock % SNOW_MELT_INTERVAL == 0) {
                    Block blockAbove = WorldPlugin.getBlock(world, getPos().up());
                    if (blockAbove == Blocks.SNOW_LAYER)
                        WorldPlugin.setBlockToAir(world, getPos().up());
                }
            } else {
                if (!WorldPlugin.isBlockAir(world, getPos().up())) return;
                double px = getX() + rand.nextFloat();
                double py = getY() + rand.nextFloat() * 0.5F + 1;
                double pz = getZ() + rand.nextFloat();
                ClientEffects.INSTANCE.chimneyEffect(world, px, py, pz, color);
            }
        }
    }

    @Override
    public void onNeighborBlockChange(IBlockState state, Block block, BlockPos neighborPos) {
        super.onNeighborBlockChange(state, block, neighborPos);
        powered = PowerPlugin.isBlockBeingPowered(world, getPos());
        sendUpdateToClient();
    }

    @Override
    public BlockFaceShape getShape(EnumFacing side) {
        return side == EnumFacing.UP ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
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
    public boolean blockActivated(EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY,
                                  float hitZ) {
        if (super.blockActivated(player, hand, side, hitX, hitY, hitZ))
            return true;
        if (player.isSneaking())
            return false;
        ItemStack heldItem = player.getHeldItem(hand);
        if (InvTools.isEmpty(heldItem) || hand == EnumHand.OFF_HAND)
            return false;
        if (Optionals.test(EnumColor.dyeColorOf(heldItem), this::setColor)) {
            if (!player.capabilities.isCreativeMode)
                player.setHeldItem(hand, InvTools.depleteItem(heldItem));
            return true;
        }
        return false;
    }

    public boolean setColor(EnumColor color) {
        if (color != this.color) {
            this.color = color;
            markBlockForUpdate();
            markDirty();
            return true;
        }
        return false;
    }

    @Override
    public int getComparatorInputOverride() {
        return color.ordinal();
    }
}

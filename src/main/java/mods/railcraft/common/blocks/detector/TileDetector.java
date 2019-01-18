/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.detector;

import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.common.blocks.TileRailcraftTicking;
import mods.railcraft.common.carts.CartConstants;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import mods.railcraft.common.util.routing.ITileRouting;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.IOException;
import java.util.List;

public class TileDetector extends TileRailcraftTicking implements IGuiReturnHandler, ITileRouting {

    public static final float SENSITIVITY = 0.2f;
    private int powerState;

    public Detector detector = Detector.DUMMY;
    //    private boolean tested;
    private int powerDelay;

    public Detector getDetector() {
        return detector;
    }

    public void setDetector(EnumDetector type) {
        this.detector = type.buildHandler();
        detector.setTile(this);
        if (world != null) {
            markBlockForUpdate();
            notifyBlocksOfNeighborChange();
        }
    }

    @Override
    public ItemStack getRoutingTable() {
        if (detector instanceof ITileRouting)
            return ((ITileRouting) detector).getRoutingTable();
        return ItemStack.EMPTY;
    }

    @Override
    public void setRoutingTable(ItemStack stack) {
        if (detector instanceof ITileRouting)
            ((ITileRouting) detector).setRoutingTable(stack);
    }

    @Override
    public boolean isPowered() {
        return detector instanceof ITileRouting && ((ITileRouting) detector).isPowered();
    }

    @Override
    public String getLocalizationTag() {
        return getDetector().getType().getTag().replace('_', '.') + ".name";
    }

    public List<EntityMinecart> getCarts() {
        return CartToolsAPI.getMinecartsOnAllSides(world, getPos(), SENSITIVITY);
    }

    public boolean blockActivated(EntityPlayer player) {
        return detector.blockActivated(player);
    }

    @Override
    public void onNeighborBlockChange(IBlockState state, Block neighborBlock, BlockPos neighborPos) {
        super.onNeighborBlockChange(state, neighborBlock, neighborPos);
        detector.onNeighborBlockChange(state.getBlock());
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setString("type", detector.getType().getName());
        detector.writeToNBT(data);
        data.setByte("powerState", (byte) powerState);
        data.setByte("powerDelay", (byte) powerDelay);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        powerState = data.getByte("powerState");
        powerDelay = data.getByte("powerDelay");

        if (data.hasKey("type"))
            setDetector(EnumDetector.fromName(data.getString("type")));
        detector.readFromNBT(data);
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte(detector.getType().ordinal());
        data.writeByte(powerState);
        detector.writePacketData(data);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        int type = data.readByte();
        if (detector == Detector.DUMMY || detector.getType().ordinal() != type)
            setDetector(EnumDetector.fromOrdinal(type));
        powerState = data.readByte();
        detector.readPacketData(data);
        markBlockForUpdate();
    }

    @Override
    public void update() {
        super.update();
        if (Game.isClient(getWorld()))
            return;
        // Legacy stuff?
//        if (!tested) {
//            tested = true;
//            int meta = world.getBlockMetadata(getPos());
//            IBlockState state = WorldPlugin.getBlockState(world, getPos());
//            if (meta != 0) {
//                world.removeTileEntity(getPos());
//                Block block = RailcraftBlocks.detector.block();
//                if (block != null)
//                    world.setBlockState(getPos(), newState, 3);
//            }
//        }
        if (powerDelay > 0)
            powerDelay--;
        else if (detector.updateInterval() == 0 || clock % detector.updateInterval() == 0) {
            int newPowerState = detector.shouldTest() ? detector.testCarts(getCarts()) : PowerPlugin.NO_POWER;
            if (newPowerState != powerState) {
                powerState = newPowerState;
                if (powerState > PowerPlugin.NO_POWER)
                    powerDelay = CartConstants.DETECTED_POWER_OUTPUT_FADE;
                sendUpdateToClient();
                world.notifyNeighborsOfStateChange(getPos(), getBlockType(), true);
                WorldPlugin.notifyBlocksOfNeighborChangeOnSide(world, getPos(), getBlockType(), getBlockState().getValue(BlockDetector.FRONT));
            }
        }
    }

    @Override
    public void writeGuiData(RailcraftOutputStream data) throws IOException {
        detector.writeGuiData(data);
    }

    @Override
    public void readGuiData(RailcraftInputStream data, EntityPlayer sender) throws IOException {
        detector.readGuiData(data, sender);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    public int getPowerState() {
        return powerState;
    }
}

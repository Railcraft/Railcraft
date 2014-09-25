/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.detector;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import static mods.railcraft.common.plugins.forge.PowerPlugin.FULL_POWER;
import static mods.railcraft.common.plugins.forge.PowerPlugin.NO_POWER;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class Detector implements IGuiReturnHandler {

    public static final Detector DUMMY = new Detector();
    public static final float SENSITIVITY = 0.2f;
    protected TileDetector tile;

    public void setTile(TileDetector tile) {
        this.tile = tile;
    }

    public TileDetector getTile() {
        return tile;
    }

    public EnumDetector getType() {
        return EnumDetector.ANY;
    }

    public int testCarts(List<EntityMinecart> carts) {
        return carts.isEmpty() ? NO_POWER : FULL_POWER;
    }

    public boolean blockActivated(EntityPlayer player) {
        return false;
    }

    public void onBlockRemoved() {
    }

    public void onNeighborBlockChange(Block block) {
    }

    public void writeToNBT(NBTTagCompound data) {
    }

    public void readFromNBT(NBTTagCompound data) {
    }

    public void writePacketData(DataOutputStream data) throws IOException {
    }

    public void readPacketData(DataInputStream data) throws IOException {
    }

    protected boolean shouldTest() {
        return true;
    }

    protected short updateInterval() {
        return 0;
    }

    public float getHardness() {
        return 2;
    }

    protected final void openGui(EnumGui gui, EntityPlayer player) {
        GuiHandler.openGui(gui, player, tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);
    }

    @Override
    public final World getWorld() {
        return tile.getWorldObj();
    }

    @Override
    public void writeGuiData(DataOutputStream data) throws IOException {
    }

    @Override
    public void readGuiData(DataInputStream data, EntityPlayer sender) throws IOException {
    }

}

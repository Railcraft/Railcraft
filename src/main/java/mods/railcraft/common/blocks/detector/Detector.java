/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.detector;

import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.io.IOException;
import java.util.List;

import static mods.railcraft.common.plugins.forge.PowerPlugin.FULL_POWER;
import static mods.railcraft.common.plugins.forge.PowerPlugin.NO_POWER;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class Detector implements IGuiReturnHandler {

    public static final Detector DUMMY = new Detector() {
        @Override
        public String toString() {
            return "DUMMY";
        }
    };
    public static final float SENSITIVITY = 0.2f;
    protected TileDetector tile;

    public TileDetector getTile() {
        return tile;
    }

    public void setTile(TileDetector tile) {
        this.tile = tile;
    }

    public EnumDetector getType() {
        return EnumDetector.ANY;
    }

    public int testCarts(List<EntityMinecart> carts) {
        return carts.isEmpty() ? NO_POWER : FULL_POWER;
    }

    public boolean blockActivated(EntityPlayer player) {
        return !player.isSneaking() && openGui(player);
    }

    public boolean openGui(EntityPlayer player) {
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

    public void writePacketData(RailcraftOutputStream data) throws IOException {
    }

    public void readPacketData(RailcraftInputStream data) throws IOException {
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
        GuiHandler.openGui(gui, player, tile.getWorld(), tile.getPos());
    }

    @Override
    public final World theWorld() {
        return tile.getWorld();
    }

    @Override
    public final World theWorldAsserted() {
        World world = tile.getWorld();
        assert world != null;
        return world;
    }

    @Override
    public void writeGuiData(RailcraftOutputStream data) throws IOException {
    }

    @Override
    public void readGuiData(RailcraftInputStream data, EntityPlayer sender) throws IOException {
    }

    @Override
    public String toString() {
        return getType().name();
    }
}

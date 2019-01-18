/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.single;

import mods.railcraft.common.blocks.TileLogic;
import mods.railcraft.common.blocks.interfaces.ITileInventory;
import mods.railcraft.common.blocks.interfaces.ITileRotate;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.minecraft.util.EnumFacing.DOWN;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
//TODO: investigate chest locking
public abstract class TileChestRailcraft extends TileLogic implements ITileRotate, ITileInventory {

    private static final int TICK_PER_SYNC = 64;
    public float lidAngle;
    public float prevLidAngle;
    public int numUsingPlayers;

    @Override
    public IInventory getInventory() {
        //noinspection OptionalGetWithoutIsPresent
        return getLogic(IInventory.class).get();
    }

    @Override
    public void setFacing(EnumFacing facing) {
        if (hasWorld())
            world.setBlockState(pos, getBlockState().withProperty(BlockChestRailcraft.FACING, facing));
    }

    @Override
    public final EnumFacing getFacing() {
        return hasWorld() ? getBlockState().getValue(BlockChestRailcraft.FACING) : EnumFacing.NORTH;
    }

    @Override
    public final EnumFacing[] getValidRotations() {
        return EnumFacing.HORIZONTALS;
    }

    @Override
    public final boolean openGui(EntityPlayer player) {
        if (world.isSideSolid(getPos().up(), DOWN))
            return false;
        else if (isCatOnChest())
            return false;
        return super.openGui(player);
    }

    @Override
    public @Nullable EnumGui getGui() {
        return EnumGui.CHEST;
    }

    private boolean isCatOnChest() {
        AxisAlignedBB searchBox = AABBFactory.start().createBoxForTileAt(getPos()).offset(0.0, 1.0, 0.0).build();
        List<EntityOcelot> cats = world.getEntitiesWithinAABB(EntityOcelot.class, searchBox);
        return cats.stream().anyMatch(EntityTameable::isSitting);
    }

    @Override
    public void update() {
        super.update();

        if (clock % TICK_PER_SYNC == 0)
            WorldPlugin.addBlockEvent(world, getPos(), getBlockType(), 1, numUsingPlayers);

        this.prevLidAngle = lidAngle;
        float angleChange = 0.1F;

        if (numUsingPlayers > 0 && lidAngle == 0.0F)
            SoundHelper.playSound(world, null, getPos(), SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);

        if (numUsingPlayers == 0 && lidAngle > 0.0F || numUsingPlayers > 0 && lidAngle < 1.0F) {
            float angle = lidAngle;

            if (numUsingPlayers > 0)
                this.lidAngle += angleChange;
            else
                this.lidAngle -= angleChange;

            if (lidAngle > 1.0F)
                this.lidAngle = 1.0F;

            float openAngle = 0.5F;

            if (lidAngle < openAngle && angle >= openAngle)
                SoundHelper.playSound(world, null, getPos(), SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);

            if (lidAngle < 0.0F)
                this.lidAngle = 0.0F;
        }
    }

    @Override
    public boolean receiveClientEvent(int id, int value) {
        if (id == 1) {
            this.numUsingPlayers = value;
            return true;
        }
        return super.receiveClientEvent(id, value);
    }

    @Override
    public void openInventory(EntityPlayer player) {
        ++this.numUsingPlayers;
        WorldPlugin.addBlockEvent(world, getPos(), getBlockType(), 1, numUsingPlayers);
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        --this.numUsingPlayers;
        WorldPlugin.addBlockEvent(world, getPos(), getBlockType(), 1, numUsingPlayers);
    }
}

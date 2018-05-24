package mods.railcraft.common.blocks;

import com.google.common.collect.Lists;
import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.api.core.items.IActivationBlockingItem;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.List;
import java.util.Random;

/**
 *
 */
public interface ISmartTile {

    default TileEntity tile() {
        return (TileEntity) this;
    }

    default boolean canCreatureSpawn(EntityLiving.SpawnPlacementType type) {
        return true;
    }

    default void addDrops(List<ItemStack> drops, int fortune) {
        drops.add(new ItemStack(tile().getBlockType()));
    }

    default List<ItemStack> getBlockDroppedSilkTouch(int fortune) {
        return Lists.newArrayList(new ItemStack(tile().getBlockType()));
    }

    default boolean canSilkHarvest(EntityPlayer player) {
        return true;
    }

    @OverridingMethodsMustInvokeSuper
    default void initFromItem(ItemStack stack) {
    }

    default void onBlockAdded() {
    }

    /**
     * Called before the block is removed.
     */
    default void onBlockRemoval() {
        if (this instanceof IInventory)
            InvTools.dropInventory(new InventoryMapper((IInventory) this), tile().getWorld(), tile().getPos());
    }

    default boolean blockActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (player.isSneaking())
            return false;
        if (!InvTools.isEmpty(heldItem)) {
            if (heldItem.getItem() instanceof IActivationBlockingItem)
                return false;
            if (TrackTools.isRailItem(heldItem.getItem()))
                return false;
        }
        return openGui(player);
    }

    default boolean openGui(EntityPlayer player) {
        EnumGui gui = getGui();
        BlockPos pos = tile().getPos();

        if (gui != null) {
            GuiHandler.openGui(gui, player, tile().getWorld(), pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
        return false;
    }

    @Nullable
    EnumGui getGui();

    default boolean isSideSolid(EnumFacing side) {
        return true;
    }

    default float getResistance(@Nullable Entity exploder) {
        return 4.5f;
    }

    default float getHardness() {
        return 2.0f;
    }

    default boolean canConnectRedstone(@Nullable EnumFacing dir) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    default void randomDisplayTick(Random rand) {
    }

    default int colorMultiplier() {
        return 16777215;
    }

    default boolean recolourBlock(EnumDyeColor color) {
        return false;
    }

    default IPostConnection.ConnectStyle connectsToPost(EnumFacing side) {
        if (isSideSolid(side.getOpposite()))
            return IPostConnection.ConnectStyle.TWO_THIN;
        return IPostConnection.ConnectStyle.NONE;
    }

    void onBlockPlacedBy(IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack);

    default void onNeighborBlockChange(IBlockState state, Block neighborBlock, BlockPos neighborPos) {

    }

    default void notifyBlocksOfNeighborChange() {
        WorldPlugin.notifyBlocksOfNeighborChange(tile().getWorld(), tile().getPos(), tile().getBlockType());
    }

    default IBlockState getActualState(IBlockState base) {
        return base;
    }

}

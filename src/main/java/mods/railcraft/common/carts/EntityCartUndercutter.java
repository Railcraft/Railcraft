/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.CartTools;
import mods.railcraft.common.blocks.aesthetics.post.ItemPost;
import mods.railcraft.common.blocks.tracks.TrackShapeHelper;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EntityCartUndercutter extends CartMaintenancePatternBase {

    public static final Set<Block> EXCLUDED_BLOCKS = new HashSet<Block>();
    private static final int SLOT_EXIST_UNDER_A = 0;
    private static final int SLOT_EXIST_UNDER_B = 1;
    private static final int SLOT_EXIST_SIDE_A = 2;
    private static final int SLOT_EXIST_SIDE_B = 3;
    private static final int SLOT_REPLACE_UNDER = 4;
    private static final int SLOT_REPLACE_SIDE = 5;
    public static final int SLOT_STOCK_UNDER = 0;
    public static final int SLOT_STOCK_SIDE = 1;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 2);

    static {
        EXCLUDED_BLOCKS.add(Blocks.SAND);
    }

    @SuppressWarnings("SimplifiableIfStatement")
    public static boolean isValidBallast(ItemStack stack) {
        if (stack == null)
            return false;
        Block block = InvTools.getBlockFromStack(stack);
        if (block == null)
            return false;
        if (EntityCartUndercutter.EXCLUDED_BLOCKS.contains(block))
            return false;
        if (block.isOpaqueCube())
            return true;
        return stack.getItem() instanceof ItemPost;
    }

    public EntityCartUndercutter(World world) {
        super(world);
    }

    public EntityCartUndercutter(World world, double d, double d1, double d2) {
        this(world);
        setPosition(d, d1 + getYOffset(), d2);
        motionX = 0.0D;
        motionY = 0.0D;
        motionZ = 0.0D;
        prevPosX = d;
        prevPosY = d1;
        prevPosZ = d2;
    }

    @Override
    public ICartType getCartType() {
        return EnumCart.UNDERCUTTER;
    }

    @Override
    public int getSizeInventory() {
        return 2;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (Game.isNotHost(worldObj))
            return;

        stockItems(SLOT_REPLACE_UNDER, SLOT_STOCK_UNDER);
        stockItems(SLOT_REPLACE_SIDE, SLOT_STOCK_SIDE);

        BlockPos pos = getPosition();
        if (TrackTools.isRailBlockAt(worldObj, pos.down()))
            pos = pos.down();

        IBlockState state = WorldPlugin.getBlockState(worldObj, pos);

        if (TrackTools.isRailBlock(state)) {
            BlockRailBase.EnumRailDirection trackShape = TrackTools.getTrackDirection(worldObj, pos, state, this);
            pos = pos.down();

            boolean slotANull = true;
            boolean slotBNull = true;
            if (patternInv.getStackInSlot(SLOT_EXIST_UNDER_A) != null) {
                replaceUnder(pos, SLOT_EXIST_UNDER_A);
                slotANull = false;
            }
            if (patternInv.getStackInSlot(SLOT_EXIST_UNDER_B) != null) {
                replaceUnder(pos, SLOT_EXIST_UNDER_B);
                slotBNull = false;
            }

            if (slotANull && slotBNull)
                replaceUnder(pos, SLOT_EXIST_UNDER_A);

            slotANull = true;
            slotBNull = true;
            if (patternInv.getStackInSlot(SLOT_EXIST_SIDE_A) != null) {
                replaceSide(pos, SLOT_EXIST_SIDE_A, trackShape);
                slotANull = false;
            }
            if (patternInv.getStackInSlot(SLOT_EXIST_SIDE_B) != null) {
                replaceSide(pos, SLOT_EXIST_SIDE_B, trackShape);
                slotBNull = false;
            }

            if (slotANull && slotBNull)
                replaceSide(pos, SLOT_EXIST_SIDE_A, trackShape);
        }
    }

    private void replaceUnder(BlockPos pos, int slotExist) {
        replaceWith(pos, slotExist, SLOT_STOCK_UNDER);
    }

    private void replaceSide(BlockPos pos, int slotExist, BlockRailBase.EnumRailDirection trackShape) {
        if (TrackShapeHelper.isEastWest(trackShape)) {
            replaceWith(pos.north(), slotExist, SLOT_STOCK_SIDE);
            replaceWith(pos.south(), slotExist, SLOT_STOCK_SIDE);
        } else if (TrackShapeHelper.isNorthSouth(trackShape)) {
            replaceWith(pos.east(), slotExist, SLOT_STOCK_SIDE);
            replaceWith(pos.west(), slotExist, SLOT_STOCK_SIDE);
        }
    }

    private void replaceWith(BlockPos pos, int slotExist, int slotStock) {
        ItemStack exist = patternInv.getStackInSlot(slotExist);
        ItemStack stock = getStackInSlot(slotStock);

        if (!isValidBallast(stock))
            return;

        IBlockState oldState = WorldPlugin.getBlockState(worldObj, pos);

        if (oldState == null || !blockMatches(oldState, exist))
            return;

        if (safeToReplace(pos)) {
            Block stockBlock = InvTools.getBlockFromStack(stock);
            List<ItemStack> drops = oldState.getBlock().getDrops(worldObj, pos, oldState, 0);
            ItemBlock item = (ItemBlock) stock.getItem();
            int newMeta = 0;
            if (item.getHasSubtypes())
                newMeta = item.getMetadata(stock.getItemDamage());
            if (stockBlock != null && WorldPlugin.setBlockState(worldObj, pos, stockBlock.getStateFromMeta(newMeta))) {
                SoundHelper.playBlockSound(worldObj, pos, stockBlock.getSoundType().getPlaceSound(), (1f + 1.0F) / 2.0F, 1f * 0.8F, stockBlock, newMeta);
                decrStackSize(slotStock, 1);
                for (ItemStack stack : drops) {
                    CartTools.transferHelper.offerOrDropItem(this, stack);
                }
                blink();
            }
        }
    }

    private boolean blockMatches(IBlockState state, ItemStack stack) {
        if (stack == null)
            return true;

        if (stack.getItem() instanceof ItemBlock) {
            ItemBlock existItem = (ItemBlock) stack.getItem();
            int existMeta = OreDictionary.WILDCARD_VALUE;
            if (existItem.getHasSubtypes())
                existMeta = existItem.getMetadata(stack.getItemDamage());
            Block stackBlock = InvTools.getBlockFromStack(stack);
            return (stackBlock == state.getBlock() && (existMeta == OreDictionary.WILDCARD_VALUE || state.getBlock().getMetaFromState(state) == existMeta)) || (stackBlock == Blocks.DIRT && stackBlock == Blocks.GRASS);
        }
        return false;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    private boolean safeToReplace(BlockPos pos) {
        if (WorldPlugin.isBlockAir(worldObj, pos))
            return false;

        Block block = WorldPlugin.getBlock(worldObj, pos);

        if (block.getMaterial().isLiquid())
            return false;

        if (block.getBlockHardness(worldObj, pos) < 0)
            return false;

        return !block.isReplaceable(worldObj, pos);
    }

    @Override
    public boolean doInteract(EntityPlayer player) {
        if (Game.isHost(worldObj))
            GuiHandler.openGui(EnumGui.CART_UNDERCUTTER, player, worldObj, this);
        return true;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return SLOTS;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (slot == SLOT_REPLACE_UNDER) {
            ItemStack trackReplace = patternInv.getStackInSlot(SLOT_REPLACE_UNDER);
            return InvTools.isItemEqual(stack, trackReplace);
        }
        if (slot == SLOT_REPLACE_SIDE) {
            ItemStack trackReplace = patternInv.getStackInSlot(SLOT_REPLACE_SIDE);
            return InvTools.isItemEqual(stack, trackReplace);
        }
        return false;
    }

}

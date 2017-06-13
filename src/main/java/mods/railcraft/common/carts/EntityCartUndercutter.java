/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.CartToolsAPI;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EntityCartUndercutter extends CartBaseMaintenancePattern {

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
        if (InvTools.isEmpty(stack))
            return false;
        Block block = InvTools.getBlockFromStack(stack);
        if (block == null)
            return false;
        if (EntityCartUndercutter.EXCLUDED_BLOCKS.contains(block))
            return false;
        if (block.isVisuallyOpaque())
            return true;
        return stack.getItem() instanceof ItemPost;
    }

    public EntityCartUndercutter(World world) {
        super(world);
    }

    public EntityCartUndercutter(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.MOW_UNDERCUTTER;
    }

    @Override
    public int getSizeInventory() {
        return 2;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (Game.isClient(worldObj))
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
            if (!InvTools.isEmpty(patternInv.getStackInSlot(SLOT_EXIST_UNDER_A))) {
                replaceUnder(pos, SLOT_EXIST_UNDER_A);
                slotANull = false;
            }
            if (!InvTools.isEmpty(patternInv.getStackInSlot(SLOT_EXIST_UNDER_B))) {
                replaceUnder(pos, SLOT_EXIST_UNDER_B);
                slotBNull = false;
            }

            if (slotANull && slotBNull)
                replaceUnder(pos, SLOT_EXIST_UNDER_A);

            slotANull = true;
            slotBNull = true;
            if (!InvTools.isEmpty(patternInv.getStackInSlot(SLOT_EXIST_SIDE_A))) {
                replaceSide(pos, SLOT_EXIST_SIDE_A, trackShape);
                slotANull = false;
            }
            if (!InvTools.isEmpty(patternInv.getStackInSlot(SLOT_EXIST_SIDE_B))) {
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
            IBlockState newState;
            if (stockBlock != null && WorldPlugin.setBlockState(worldObj, pos, (newState = stockBlock.getStateFromMeta(newMeta)))) {
                SoundHelper.playBlockSound(worldObj, pos, stockBlock.getSoundType().getPlaceSound(), SoundCategory.NEUTRAL, (1f + 1.0F) / 2.0F, 1f * 0.8F, newState);
                decrStackSize(slotStock, 1);
                for (ItemStack stack : drops) {
                    CartToolsAPI.transferHelper.offerOrDropItem(this, stack);
                }
                blink();
            }
        }
    }

    private boolean blockMatches(IBlockState state, ItemStack stack) {
        if (InvTools.isEmpty(stack))
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

        IBlockState state = WorldPlugin.getBlockState(worldObj, pos);

        if (state.getMaterial().isLiquid())
            return false;

        if (state.getBlockHardness(worldObj, pos) < 0)
            return false;

        return !state.getBlock().isReplaceable(worldObj, pos);
    }

    @Override
    public boolean doInteract(EntityPlayer player, ItemStack stack, EnumHand hand) {
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

    @Nonnull
    @Override
    protected EnumGui getGuiType() {
        return EnumGui.CART_UNDERCUTTER;
    }
}

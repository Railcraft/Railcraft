package mods.railcraft.common.carts;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class EntityCartChestVoid extends CartBaseContainer {
    private static final int TICK_PER_VOID = 8;
    private int clock = MiscTools.RANDOM.nextInt();

    public EntityCartChestVoid(World world) {
        super(world);
    }

    public EntityCartChestVoid(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.CHEST_VOID;
    }

    @Override
    public IBlockState getDefaultDisplayTile() {
        return RailcraftBlocks.CHEST_VOID.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.NORTH);
    }

    @Override
    public boolean doInteract(EntityPlayer player, EnumHand hand) {
        if (Game.isHost(world))
            player.displayGUIChest(this);
        return true;
    }

    @Override
    public int getDefaultDisplayTileOffset() {
        return 8;
    }

    @Override
    public boolean canBeRidden() {
        return false;
    }

    @Override
    public int getSizeInventory() {
        return 27;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return RailcraftConfig.chestAllowLiquids() || getStackInSlot(slot) == null || !FluidItemHelper.isContainer(stack);
    }

    @Override
    public boolean canPassItemRequests() {
        return true;
    }

    @Override
    public boolean canAcceptPushedItem(EntityMinecart requester, ItemStack stack) {
        return true;
    }

    @Override
    public boolean canProvidePulledItem(EntityMinecart requester, ItemStack stack) {
        return false;
    }

    @Override
    public String getGuiID() {
        return "minecraft:chest";
    }

    @Override
    public void onUpdate() {
        clock++;
        if (clock % TICK_PER_VOID == 0 && Game.isHost(world))
            for (int slot = 0; slot < getSizeInventory(); slot++) {
                ItemStack stack = getStackInSlot(slot);
                if (!InvTools.isEmpty(stack)) {
                    decrStackSize(slot, 1);
                    break;
                }
            }
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new ContainerChest(playerInventory, this, playerIn);
    }

    @NotNull
    @Override
    protected EnumGui getGuiType() {
        throw new Error("Should not be called");
    }

}

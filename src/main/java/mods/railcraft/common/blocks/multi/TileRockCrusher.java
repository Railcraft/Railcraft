/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.multi;

import buildcraft.api.statements.IActionExternal;
import mods.railcraft.api.crafting.ICrusherRecipe;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.charge.ChargeManager;
import mods.railcraft.common.blocks.charge.ChargeNetwork;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.buildcraft.actions.Actions;
import mods.railcraft.common.plugins.buildcraft.triggers.IHasWork;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.crafting.RockCrusherCraftingManager;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.iterators.IInvSlot;
import mods.railcraft.common.util.inventory.iterators.InventoryIterator;
import mods.railcraft.common.util.inventory.manipulators.InventoryManipulator;
import mods.railcraft.common.util.inventory.wrappers.InventoryCopy;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.misc.RailcraftDamageSource;
import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

import static mods.railcraft.common.blocks.multi.BlockRockCrusher.ICON;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@net.minecraftforge.fml.common.Optional.Interface(iface = "mods.railcraft.common.plugins.buildcraft.triggers.IHasWork", modid = "BuildCraftAPI|statements")
public final class TileRockCrusher extends TileMultiBlockInventory<TileRockCrusher> implements IHasWork, ISidedInventory {

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 9;
    private static final int PROCESS_TIME = 100;
    private static final double CRUSHING_POWER_COST_PER_TICK = 160;
    private static final double SUCKING_POWER_COST = 5000;
    private static final double KILLING_POWER_COST = 10000;
    private static final double MAX_RECEIVE = 5000;
    private static final double MAX_ENERGY = CRUSHING_POWER_COST_PER_TICK * PROCESS_TIME;
    private static final int[] SLOTS_INPUT = InvTools.buildSlotArray(SLOT_INPUT, 9);
    private static final int[] SLOTS_OUTPUT = InvTools.buildSlotArray(SLOT_OUTPUT, 9);
    private static final List<MultiBlockPattern> patterns = new ArrayList<>();

    static {
        char[][][] map1 = {
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'B', 'D', 'B', 'O'},
                        {'O', 'B', 'D', 'B', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'a', 'd', 'f', 'O'},
                        {'O', 'c', 'e', 'h', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                }
        };
        patterns.add(new MultiBlockPattern(map1));

        char[][][] map2 = {
                {
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O'},
                        {'O', 'B', 'B', 'O'},
                        {'O', 'D', 'D', 'O'},
                        {'O', 'B', 'B', 'O'},
                        {'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O'},
                        {'O', 'a', 'f', 'O'},
                        {'O', 'b', 'g', 'O'},
                        {'O', 'c', 'h', 'O'},
                        {'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'}
                }
        };
        patterns.add(new MultiBlockPattern(map2));
    }

    private final InventoryMapper invInput = new InventoryMapper(this, 0, 9, false);
    private final InventoryMapper invOutput = new InventoryMapper(this, 9, 9, false);
    private final Set<Object> actions = new HashSet<>();
    private int processTime;
    private final Random random = new Random();
    //    @Nullable
//    private final EnergyStorage energyStorage;
//    @Nullable
//    public final RFEnergyIndicator rfIndicator;
    private boolean isWorking;
    private boolean paused;
    @Nullable
    private ChargeNetwork.ChargeNode node;

    @SuppressWarnings("unused")
    public TileRockCrusher() {
        super(18, patterns);

//        energyStorage = new EnergyStorage(MAX_ENERGY, MAX_RECEIVE, KILLING_POWER_COST);
//        rfIndicator = new RFEnergyIndicator(energyStorage);
    }

    public static void placeRockCrusher(World world, BlockPos pos, int patternIndex, @Nullable List<ItemStack> input, @Nullable List<ItemStack> output) {
        MultiBlockPattern pattern = TileRockCrusher.patterns.get(patternIndex);
        Map<Character, IBlockState> blockMapping = new HashMap<>();
        IBlockState state = RailcraftBlocks.ROCK_CRUSHER.getState(null);
        blockMapping.put('B', state);
        blockMapping.put('D', state);
        blockMapping.put('a', state);
        blockMapping.put('b', state);
        blockMapping.put('c', state);
        blockMapping.put('d', state);
        blockMapping.put('e', state);
        blockMapping.put('f', state);
        blockMapping.put('h', state);
        TileEntity tile = pattern.placeStructure(world, pos, blockMapping);
        if (tile instanceof TileRockCrusher) {
            TileRockCrusher master = (TileRockCrusher) tile;
            for (int slot = 0; slot < 9; slot++) {
                if (input != null && slot < input.size())
                    master.inv.setInventorySlotContents(TileRockCrusher.SLOT_INPUT + slot, input.get(slot));
                if (output != null && slot < output.size())
                    master.inv.setInventorySlotContents(TileRockCrusher.SLOT_OUTPUT + slot, output.get(slot));
            }
        }
    }

    @Override
    protected boolean isMapPositionValid(BlockPos pos, char mapPos) {
        IBlockState self = getBlockState();
        IBlockState other = WorldPlugin.getBlockState(world, pos);
        switch (mapPos) {
            case 'O': // Other
                if (self != other)
                    return true;
                break;
            case 'D': // Window
            case 'B': // Block
            case 'a': // Block
            case 'b': // Block
            case 'c': // Block
            case 'd': // Block
            case 'e': // Block
            case 'f': // Block
            case 'g': // Block
            case 'h': // Block
                if (self == other)
                    return true;
                break;
            case 'A': // Air
                if (other.getBlock().isAir(other, world, pos))
                    return true;
                break;
        }
        return false;
    }

//    private boolean useMasterEnergy(double amount, boolean doRemove) {
//        TileRockCrusher master = (TileRockCrusher) getMasterBlock();
//
//        if (master == null)
//            return false;
//        return mBlock != null && (mBlock.energyStorage == null || mBlock.energyStorage.extractEnergy(amount, !doRemove) == amount);
//        return false;
//    }

    private boolean useMasterEnergy(double amount) {
        TileRockCrusher master = getMasterBlock();
        if (master == null)
            return false;
        return master.node().useCharge(amount);
    }

    private boolean canUseMasterEnergy(double amount) {
        TileRockCrusher master = getMasterBlock();
        if (master == null)
            return false;
        return master.node().canUseCharge(amount);
    }

    @Override
    public void update() {
        super.update();

        if (Game.isHost(getWorld())) {
            BlockPos pos = getPos();
            double x = pos.getX();
            double y = pos.getZ();
            double z = pos.getZ();

            if (isStructureValid()) {
                // TileEntityHopper.getItemsAroundAPointOrSomethingLikeThat
                for (EntityItem item : TileEntityHopper.getCaptureItems(getWorld(), x, y + 1, z)) {
                    if (item != null && useMasterEnergy(SUCKING_POWER_COST)) {
                        ItemStack stack = item.getItem().copy();
                        InventoryManipulator.get((IInventory) invInput).addStack(stack);
                        item.setDead();
                    }
                }

                EntityLivingBase entity = MiscTools.getEntityAt(world, EntityLivingBase.class, getPos().up());
                if (entity != null && canUseMasterEnergy(KILLING_POWER_COST))
                    if (entity.attackEntityFrom(RailcraftDamageSource.CRUSHER, 10))
                        useMasterEnergy(KILLING_POWER_COST);
            }

            if (isMaster()) {
                if (clock % 16 == 0)
                    processActions();

                if (paused)
                    return;

                ItemStack input = InvTools.emptyStack();
                ICrusherRecipe recipe = null;
                for (IInvSlot slot : InventoryIterator.getVanilla(invInput)) {
                    input = slot.getStack();
                    if (!InvTools.isEmpty(input)) {
                        recipe = RockCrusherCraftingManager.getInstance().getRecipe(input);
                        if (recipe == null)
                            recipe = RockCrusherCraftingManager.NULL_RECIPE;
                        break;
                    }
                }

                if (recipe != null)
                    if (processTime >= PROCESS_TIME) {
                        isWorking = false;
                        InventoryCopy tempInv = new InventoryCopy(invOutput);
                        boolean hasRoom = true;
                        List<ItemStack> outputs = recipe.pollOutputs(random);
                        for (ItemStack output : outputs) {
                            output = InvTools.moveItemStack(output, tempInv);
                            if (!InvTools.isEmpty(output)) {
                                hasRoom = false;
                                break;
                            }
                        }

                        if (hasRoom) {
                            for (ItemStack output : outputs) {
                                InvTools.moveItemStack(output, invOutput);
                            }

                            InvTools.removeOneItem(invInput, input);

                            SoundHelper.playSound(world, null, getPos(), SoundEvents.ENTITY_IRONGOLEM_DEATH, SoundCategory.BLOCKS, 1.0f, world.rand.nextFloat() * 0.25F + 0.7F);

                            processTime = 0;
                        }
                    } else {
                        isWorking = true;
                        boolean success = node().useCharge(CRUSHING_POWER_COST_PER_TICK);
                        if (success) {
                            processTime++;
                        }
//                        if (!node().isNull()) { //TODO: no charge


//                            int energy = energyStorage.extractEnergy(CRUSHING_POWER_COST_PER_TICK, true);
//                            if (energy >= CRUSHING_POWER_COST_PER_TICK) {
//                                processTime++;
//                                energyStorage.extractEnergy(CRUSHING_POWER_COST_PER_TICK, false);
//                            }
//                        } else
//                            processTime++;
                    }
                else {
                    processTime = 0;
                    isWorking = false;
                }
            }
        }
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        TileRockCrusher mBlock = getMasterBlock();
        if (mBlock != null) {
            GuiHandler.openGui(EnumGui.ROCK_CRUSHER, player, world, mBlock.getPos());
            return true;
        }
        return false;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("processTime", processTime);

//        if (energyStorage != null)
//            energyStorage.writeToNBT(data);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        processTime = data.getInteger("processTime");

//        if (energyStorage != null)
//            energyStorage.readFromNBT(data);
    }

    public int getProcessTime() {
        TileRockCrusher mBlock = getMasterBlock();
        if (mBlock != null)
            return mBlock.processTime;
        return -1;
    }

    public void setProcessTime(int processTime) {
        TileRockCrusher mBlock = getMasterBlock();
        if (mBlock != null)
            mBlock.processTime = processTime;
    }

    public int getProgressScaled(int i) {
        return (getProcessTime() * i) / PROCESS_TIME;
    }

    @Override
    public boolean hasWork() {
        TileRockCrusher mBlock = getMasterBlock();
        return mBlock != null && mBlock.isWorking;
    }

    //    public void setPaused(boolean p) {
//        TileRockCrusher mBlock = (TileRockCrusher) getMasterBlock();
//        if (mBlock != null) {
//            mBlock.paused = p;
//        }
//    }
    private void processActions() {
        paused = actions.stream().anyMatch(a -> a == Actions.PAUSE);
        actions.clear();
    }

    @Override
    public void actionActivated(IActionExternal action) {
        TileRockCrusher mBlock = getMasterBlock();
        if (mBlock != null)
            mBlock.actions.add(action);
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        if (side == EnumFacing.UP)
            return SLOTS_INPUT;
        return SLOTS_OUTPUT;
    }

    @Override
    public boolean isEmpty() {
        return inv.isEmpty();
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index >= 9;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (!super.isItemValidForSlot(slot, stack))
            return false;
        if (slot < 9)
            return RockCrusherCraftingManager.getInstance().getRecipe(stack) != null;
        return false;
    }

    private ChargeNetwork.ChargeNode node() {
        if (node == null) {
            node = ChargeManager.getNetwork(world).getNode(pos);
        }
        return node;
    }

//    @Nullable
//    public EnergyStorage getEnergyStorage() {
//        TileRockCrusher mBlock = (TileRockCrusher) getMasterBlock();
//        if (mBlock != null && mBlock.energyStorage != null)
//            return mBlock.energyStorage;
//        return energyStorage;
//    }

//    @Override
//    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
//        if (getEnergyStorage() == null)
//            return 0;
//        return getEnergyStorage().receiveEnergy(maxReceive, simulate);
//    }

//    @Override
//    public int getEnergyStored(EnumFacing from) {
//        if (getEnergyStorage() == null)
//            return 0;
//        return getEnergyStorage().getEnergyStored();
//    }

//    @Override
//    public int getMaxEnergyStored(EnumFacing from) {
//        if (getEnergyStorage() == null)
//            return 0;
//        return getEnergyStorage().getMaxEnergyStored();
//    }

//    @Override
//    public boolean canConnectEnergy(EnumFacing from) {
//        return true;
//    }

    @NotNull
    @Override
    public EnumGui getGui() {
        return EnumGui.ROCK_CRUSHER;
    }

    @Override
    public IBlockState getActualState(IBlockState base) {
        return base.withProperty(ICON, getPatternMarker());
    }
}

/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.FilteredTank;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.gui.slots.SlotWaterOrEmpty;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.Predicates;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class TileTankWater extends TileTank<TileTankWater> {

    private static final int OUTPUT_RATE = 40;
    private static final int TANK_CAPACITY = FluidTools.BUCKET_VOLUME * 400;
    private static final int REFILL_INTERVAL = 8;
    private static final float REFILL_RATE = 10f;
    private static final float REFILL_PENALTY_INSIDE = 0.5f;
    private static final float REFILL_PENALTY_SNOW = 0.5f;
    private static final float REFILL_BOOST_RAIN = 3.0f;
    private static final byte REFILL_RATE_MIN = 1;
    private static final int SLOT_INPUT = 0;
    private static final int SLOT_OUTPUT = 1;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 2);
    private static final EnumFacing[] LIQUID_OUTPUTS = {EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH};
    private static final List<MultiBlockPattern> patterns = new ArrayList<>();
    private final FilteredTank tank;

    static {
        char[][][] map = {
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'A', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },};
        patterns.add(new MultiBlockPattern(map, 2, 1, 2));
    }

//    private InventoryMapper invInput = new InventoryMapper(this, SLOT_INPUT, 1);
//    private InventoryMapper invOutput = new InventoryMapper(this, SLOT_OUTPUT, 1);

    public TileTankWater() {
        super(2, patterns);
        tank = new FilteredTank(TANK_CAPACITY, this);
        tank.setFilter(Fluids.WATER::get);
        tankManager.add(tank);
    }

    public static void placeWaterTank(World world, BlockPos pos, int water) {
        MultiBlockPattern pattern = TileTankWater.patterns.get(0);
        Map<Character, IBlockState> blockMapping = new HashMap<>();
        blockMapping.put('B', RailcraftBlocks.TANK_WATER.getDefaultState());
        TileEntity tile = pattern.placeStructure(world, pos, blockMapping);
        if (tile instanceof TileTankWater) {
            TileTankWater master = (TileTankWater) tile;
            master.tank.setFluid(Fluids.WATER.get(water));
        }
    }

    @Override
    public String getTitle() {
        return LocalizationPlugin.translate("gui.railcraft.tank.water");
    }

    @Override
    public Slot getInputSlot(IInventory inv, int id, int x, int y) {
        return new SlotWaterOrEmpty(inv, id, x, y);
    }

    @Override
    public void update() {
        super.update();

        if (Game.isHost(getWorld())) {
            if (isMaster()) {
                if (world.provider.getDimension() != -1 && clock % REFILL_INTERVAL == 0) {
                    float rate = REFILL_RATE;
                    Biome biome = world.getBiome(getPos());
                    float humidity = biome.getRainfall();
                    rate *= humidity;
//                    String debug = "Biome=" + biome.biomeName + ", Humidity=" + humidity;

                    boolean outside = false;
                    for (int x = getX() - 1; x <= getX() + 1; x++) {
                        for (int z = getZ() - 1; z <= getZ() + 1; z++) {
                            outside = world.canBlockSeeSky(new BlockPos(x, getY() + 3, z));
//                            System.out.println(x + ", " + (y + 3) + ", " + z);
                            if (outside)
                                break;
                        }
                    }

//                    debug += ", Outside=" + outside;
                    if (!outside)
                        rate *= REFILL_PENALTY_INSIDE;
                    else if (world.isRaining())
                        if (biome.getEnableSnow())
                            rate *= REFILL_PENALTY_SNOW; //                            debug += ", Snow=true";
                        else
                            rate *= REFILL_BOOST_RAIN; //                            debug += ", Rain=true";
                    int rateFinal = MathHelper.floor(rate);
                    if (rateFinal < REFILL_RATE_MIN)
                        rateFinal = REFILL_RATE_MIN;
//                    debug += ", Refill=" + rateFinal;
//                    System.out.println(debug);

                    FluidStack fillStack = Fluids.WATER.get(rateFinal);
                    tank.fillInternal(fillStack, true);
                }

                //FIXME
                if (clock % FluidTools.BUCKET_FILL_TIME == 0)
                    FluidTools.processContainers(tankManager.get(0), this, SLOT_INPUT, SLOT_OUTPUT);
            }

            TankManager tMan = getTankManager();
            if (!tMan.isEmpty())
                tMan.push(tileCache, Predicates.notInstanceOf(getClass()), LIQUID_OUTPUTS, 0, OUTPUT_RATE);
        }
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        TileTankWater mBlock = getMasterBlock();
        if (mBlock != null) {
            GuiHandler.openGui(EnumGui.TANK, player, world, mBlock.getPos());
            return true;
        }
        return false;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index == SLOT_OUTPUT;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (!super.isItemValidForSlot(slot, stack))
            return false;
        switch (slot) {
            case SLOT_INPUT:
                return FluidItemHelper.isRoomInContainer(stack, Fluids.WATER.get()) || FluidItemHelper.containsFluid(stack, Fluids.WATER.get());
        }
        return false;
    }

    @NotNull
    @Override
    public EnumGui getGui() {
        return EnumGui.TANK;
    }
}

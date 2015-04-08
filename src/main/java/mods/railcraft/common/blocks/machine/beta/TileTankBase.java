/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.beta;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.ITankTile;
import mods.railcraft.common.blocks.machine.MultiBlockPattern;
import mods.railcraft.common.blocks.machine.TileMultiBlock;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.gui.slots.SlotLiquidContainer;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.StandaloneInventory;
import mods.railcraft.common.util.misc.EnumColor;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.Timer;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileTankBase extends TileMultiBlock implements ITankTile {
    public final static int CAPACITY_PER_BLOCK_IRON = 16 * FluidHelper.BUCKET_VOLUME;
    public final static int CAPACITY_PER_BLOCK_STEEL = 32 * FluidHelper.BUCKET_VOLUME;
    protected final static int SLOT_INPUT = 0;
    protected final static int SLOT_OUTPUT = 1;
    protected final static int NETWORK_UPDATE_INTERVAL = 64;
    private final static MetalTank IRON_TANK = new IronTank();
    private final static List<MultiBlockPattern> patterns = buildPatterns();
    protected final StandardTank tank = new StandardTank(64 * FluidHelper.BUCKET_VOLUME, this);
    protected final TankManager tankManager = new TankManager();
    private final StandaloneInventory inv;
    private final Timer networkTimer = new Timer();
    private EnumColor color = EnumColor.WHITE;
    private FluidStack previousFluidStack;
    private int previousFluidColor;

    protected TileTankBase() {
        super(patterns);
        inv = new StandaloneInventory(2, "gui.tank.iron", this);
        tankManager.add(tank);
    }

    public static void placeIronTank(World world, int x, int y, int z, int patternIndex, FluidStack fluid) {
        MultiBlockPattern pattern = TileTankBase.patterns.get(patternIndex);
        Map<Character, Integer> blockMapping = new HashMap<Character, Integer>();
        blockMapping.put('B', EnumMachineBeta.TANK_IRON_WALL.ordinal());
        blockMapping.put('W', EnumMachineBeta.TANK_IRON_GAUGE.ordinal());
        TileEntity tile = pattern.placeStructure(world, x, y, z, RailcraftBlocks.getBlockMachineBeta(), blockMapping);
        if (tile instanceof TileTankBase) {
            TileTankBase master = (TileTankBase) tile;
            master.tank.setFluid(fluid);
        }
    }

    public static void placeSteelTank(World world, int x, int y, int z, int patternIndex, FluidStack fluid) {
        MultiBlockPattern pattern = TileTankBase.patterns.get(patternIndex);
        Map<Character, Integer> blockMapping = new HashMap<Character, Integer>();
        blockMapping.put('B', EnumMachineBeta.TANK_STEEL_WALL.ordinal());
        blockMapping.put('W', EnumMachineBeta.TANK_STEEL_GAUGE.ordinal());
        TileEntity tile = pattern.placeStructure(world, x, y, z, RailcraftBlocks.getBlockMachineBeta(), blockMapping);
        if (tile instanceof TileTankBase) {
            TileTankBase master = (TileTankBase) tile;
            master.tank.setFluid(fluid);
        }
    }

    private static List<MultiBlockPattern> buildPatterns() {
        List<MultiBlockPattern> pats = new ArrayList<MultiBlockPattern>();
        boolean client = FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT;

        // 3x3
        int xOffset = 2;
        int yOffset = 0;
        int zOffset = 2;

        char[][] bottom = new char[][]{
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'B', 'B', 'B', 'O'},
                {'O', 'B', 'M', 'B', 'O'},
                {'O', 'B', 'B', 'B', 'O'},
                {'O', 'O', 'O', 'O', 'O'}
        };

        char[][] middle = new char[][]{
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'B', 'W', 'B', 'O'},
                {'O', 'W', 'A', 'W', 'O'},
                {'O', 'B', 'W', 'B', 'O'},
                {'O', 'O', 'O', 'O', 'O'}
        };

        char[][] top = new char[][]{
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'B', 'B', 'B', 'O'},
                {'O', 'B', 'T', 'B', 'O'},
                {'O', 'B', 'B', 'B', 'O'},
                {'O', 'O', 'O', 'O', 'O'}
        };

        char[][] border = new char[][]{
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'}
        };

        for (int i = 4; i <= 8; i++) {
            char[][][] map = buildMap(i, bottom, middle, top, border);
            AxisAlignedBB entityCheck = AxisAlignedBB.getBoundingBox(0, 1, 0, 1, i - 1, 1);
            pats.add(buildPattern(map, xOffset, yOffset, zOffset, entityCheck));
        }

        // 5x5
        if (client || RailcraftConfig.getMaxTankSize() >= 5) {
            xOffset = zOffset = 3;

            bottom = new char[][]{
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'B', 'B', 'B', 'B', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'M', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'B', 'B', 'B', 'B', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            };

            middle = new char[][]{
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'W', 'A', 'A', 'A', 'W', 'O'},
                    {'O', 'W', 'A', 'A', 'A', 'W', 'O'},
                    {'O', 'W', 'A', 'A', 'A', 'W', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            };

            top = new char[][]{
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'B', 'B', 'B', 'B', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'T', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'B', 'B', 'B', 'B', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            };

            border = new char[][]{
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            };

            for (int i = 4; i <= 8; i++) {
                char[][][] map = buildMap(i, bottom, middle, top, border);
                AxisAlignedBB entityCheck = AxisAlignedBB.getBoundingBox(-1, 1, -1, 2, i - 1, 2);
                pats.add(buildPattern(map, xOffset, yOffset, zOffset, entityCheck));
            }
        }

        // 7x7
        if (client || RailcraftConfig.getMaxTankSize() >= 7) {
            xOffset = zOffset = 4;

            bottom = new char[][]{
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'M', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            };

            middle = new char[][]{
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'W', 'A', 'A', 'A', 'A', 'A', 'W', 'O'},
                    {'O', 'W', 'A', 'A', 'A', 'A', 'A', 'W', 'O'},
                    {'O', 'W', 'A', 'A', 'A', 'A', 'A', 'W', 'O'},
                    {'O', 'W', 'A', 'A', 'A', 'A', 'A', 'W', 'O'},
                    {'O', 'W', 'A', 'A', 'A', 'A', 'A', 'W', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            };

            top = new char[][]{
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'T', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            };

            border = new char[][]{
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            };

            for (int i = 4; i <= 8; i++) {
                char[][][] map = buildMap(i, bottom, middle, top, border);
                AxisAlignedBB entityCheck = AxisAlignedBB.getBoundingBox(-2, 1, -2, 3, i - 1, 3);
                pats.add(buildPattern(map, xOffset, yOffset, zOffset, entityCheck));
            }
        }

        // 9x9
        if (client || RailcraftConfig.getMaxTankSize() >= 9) {
            xOffset = zOffset = 5;

            bottom = new char[][]{
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'M', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            };

            middle = new char[][]{
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'W', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'W', 'O'},
                    {'O', 'W', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'W', 'O'},
                    {'O', 'W', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'W', 'O'},
                    {'O', 'W', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'W', 'O'},
                    {'O', 'W', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'W', 'O'},
                    {'O', 'W', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'W', 'O'},
                    {'O', 'W', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'W', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            };

            top = new char[][]{
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'T', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            };

            border = new char[][]{
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            };

            for (int i = 4; i <= 8; i++) {
                char[][][] map = buildMap(i, bottom, middle, top, border);
                AxisAlignedBB entityCheck = AxisAlignedBB.getBoundingBox(-3, 1, -3, 4, i - 1, 4);
                pats.add(buildPattern(map, xOffset, yOffset, zOffset, entityCheck));
            }
        }

        return pats;
    }

    private static MultiBlockPattern buildPattern(char[][][] map, int xOffset, int yOffset, int zOffset, AxisAlignedBB entityCheck) {
        if (!RailcraftConfig.allowTankStacking()) {
            entityCheck.offset(0, 1, 0);
            yOffset = 1;
        }
        return new MultiBlockPattern(map, xOffset, yOffset, zOffset, entityCheck);
    }

    private static char[][][] buildMap(int height, char[][] bottom, char[][] mid, char[][] top, char[][] border) {
        char[][][] map;
        if (RailcraftConfig.allowTankStacking()) {
            map = new char[height][][];

            map[0] = bottom;
            map[height - 1] = top;

            for (int i = 1; i < height - 1; i++) {
                map[i] = mid;
            }
        } else {
            map = new char[height + 2][][];

            map[0] = border;
            map[1] = bottom;
            map[height] = top;
            map[height + 1] = border;

            for (int i = 2; i < height; i++) {
                map[i] = mid;
            }
        }

        return map;
    }

    public MetalTank getTankType() {
        return IRON_TANK;
    }

    @Override
    public IInventory getInventory() {
        return inv;
    }

    @Override
    public Slot getInputSlot(IInventory inv, int slotNum, int x, int y) {
        return new SlotLiquidContainer(inv, slotNum, x, y);
    }

    @Override
    public float getResistance(Entity exploder) {
        return getTankType().getResistance(exploder);
    }

    @Override
    protected int getMaxRecursionDepth() {
        return 500;
    }

    @Override
    public String getTitle() {
        return getTankType().getTitle();
    }

    @Override
    public void initFromItem(ItemStack stack) {
        super.initFromItem(stack);
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.hasKey("color"))
            recolourBlock(15 - nbt.getByte("color"));
    }

    @Override
    public boolean canSilkHarvest(EntityPlayer player) {
        return true;
    }

    @Override
    public ArrayList<ItemStack> getDrops(int fortune) {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        ItemStack drop = getMachineType().getItem();
        NBTTagCompound nbt = InvTools.getItemData(drop);
        nbt.setByte("color", (byte) EnumColor.WHITE.ordinal());
        items.add(drop);
        return items;
    }

    @Override
    public ArrayList<ItemStack> getBlockDroppedSilkTouch(int fortune) {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        ItemStack drop = getMachineType().getItem();
        NBTTagCompound nbt = InvTools.getItemData(drop);
        nbt.setByte("color", (byte) color.ordinal());
        items.add(drop);
        return items;
    }

    @Override
    public boolean recolourBlock(int cID) {
        EnumColor c = EnumColor.fromId(15 - cID);
        if (color != c) {
            color = c;
            markBlockForUpdate();
            return true;
        }
        return false;
    }

    @Override
    public int colorMultiplier() {
        return color.getHexColor();
    }

    @Override
    protected boolean isStructureTile(TileEntity tile) {
        return tile instanceof TileTankBase;
    }

    @Override
    public boolean blockActivated(EntityPlayer player, int side) {
        ItemStack current = player.getCurrentEquippedItem();
        if (Game.isHost(worldObj)) {
            if (isStructureValid() && FluidHelper.handleRightClick(getTankManager(), ForgeDirection.getOrientation(side), player, true, true)) {
                TileTankBase master = (TileTankBase) getMasterBlock();
                if (master != null)
                    master.syncClient();
                return true;
            }
        } else if (FluidItemHelper.isContainer(current))
            return true;

        // Prevents players from getting inside tanks using boats
        if (current != null && current.getItem() == Items.boat)
            return true;
        return super.blockActivated(player, side);
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        TileMultiBlock mBlock = getMasterBlock();
        if (mBlock != null) {
            GuiHandler.openGui(EnumGui.TANK, player, worldObj, mBlock.xCoord, mBlock.yCoord, mBlock.zCoord);
            return true;
        }
        return false;
    }

    @Override
    public TankManager getTankManager() {
        TileTankBase mBlock = (TileTankBase) getMasterBlock();
        if (mBlock != null)
            return mBlock.tankManager;
        return null;
    }

    @Override
    public StandardTank getTank() {
        TileTankBase mBlock = (TileTankBase) getMasterBlock();
        if (mBlock != null)
            return mBlock.tankManager.get(0);
        return null;
    }

    public int getCapacityPerBlock() {
        return CAPACITY_PER_BLOCK_IRON;
    }

    @Override
    protected void onPatternLock(MultiBlockPattern pattern) {
        if (isMaster) {
            int capacity = (pattern.getPatternWidthX() - 2) * (pattern.getPatternHeight() - (pattern.getMasterOffsetY() * 2)) * (pattern.getPatternWidthZ() - 2) * getCapacityPerBlock();
            tankManager.setCapacity(0, capacity);
        }
    }

    @Override
    protected void onMasterChanged() {
        TankManager tMan = getTankManager();
        if (tMan != null)
            tMan.get(0).setFluid(null);
    }

    @Override
    protected boolean isMapPositionValid(int x, int y, int z, char mapPos) {
        switch (mapPos) {
            case 'O': // Other
            {
                Block block = WorldPlugin.getBlock(worldObj, x, y, z);
                if (block == getBlockType()) {
                    int meta = worldObj.getBlockMetadata(x, y, z);
                    if (getTankType().isTankBlock(meta))
                        return false;
                }
                return true;
            }
            case 'W': // Gauge or Valve
            {
                Block block = WorldPlugin.getBlock(worldObj, x, y, z);
                if (block != getBlockType())
                    return false;
                int meta = worldObj.getBlockMetadata(x, y, z);
                return getTankType().isTankBlock(meta);
            }
            case 'B': // Block
            {
                Block block = WorldPlugin.getBlock(worldObj, x, y, z);
                if (block != getBlockType())
                    return false;
                int meta = worldObj.getBlockMetadata(x, y, z);
                return getTankType().isWallBlock(meta);
            }
            case 'M': // Master
            case 'T': // Top Block
            {
                Block block = WorldPlugin.getBlock(worldObj, x, y, z);
                if (block != getBlockType())
                    return false;
                int meta = worldObj.getBlockMetadata(x, y, z);
                if (!getTankType().isTankBlock(meta))
                    return false;
                TileEntity tile = worldObj.getTileEntity(x, y, z);
                if (!(tile instanceof TileMultiBlock)) {
                    worldObj.removeTileEntity(x, y, z);
                    return true;
                }
                return !((TileMultiBlock) tile).isStructureValid();
            }
            case 'A': // Air
            {
                return worldObj.isAirBlock(x, y, z);
            }
        }
        return true;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (Game.isHost(worldObj))
            if (isMaster) {

                if (clock % FluidHelper.BUCKET_FILL_TIME == 0)
                    FluidHelper.processContainers(tankManager.get(0), inv, SLOT_INPUT, SLOT_OUTPUT);

                if (networkTimer.hasTriggered(worldObj, NETWORK_UPDATE_INTERVAL))
                    syncClient();
            }
    }

    private void syncClient() {
        FluidStack fluidStack = tankManager.get(0).getFluid();
        int fluidColor = tankManager.get(0).getColor();
        if (fluidColor != previousFluidColor || !isFluidEqual(fluidStack, previousFluidStack)) {
            previousFluidStack = fluidStack == null ? null : fluidStack.copy();
            previousFluidColor = fluidColor;
            sendUpdateToClient();
        }
    }

    private boolean isFluidEqual(FluidStack L1, FluidStack L2) {
        if (L1 == L2)
            return true;
        if (L1 == null || L2 == null)
            return false;
        return L1.isFluidStackIdentical(L2);
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        tankManager.writeTanksToNBT(data);
        inv.writeToNBT("inv", data);
        data.setByte("color", (byte) color.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        tankManager.readTanksFromNBT(data);
        inv.readFromNBT("inv", data);
        if (data.hasKey("color"))
            color = EnumColor.fromId(data.getByte("color"));
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte(color.ordinal());
        tankManager.writePacketData(data);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        EnumColor c = EnumColor.fromId(data.readByte());
        tankManager.readPacketData(data);
        if (color != c) {
            color = c;
            markBlockForUpdate();
        }
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return Short.MAX_VALUE;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return isMaster ? pass == 0 : pass == 1;
    }

    public int getComparatorValue() {
        double fullness = (double) tank.getFluidAmount() / (double) tank.getCapacity();
        int power = (int) Math.ceil(fullness * 15.0);
        return power;
    }
}

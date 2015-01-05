/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.alpha;

import java.util.ArrayList;
import java.util.List;
import mods.railcraft.api.electricity.IElectricGrid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.MultiBlockPattern;
import mods.railcraft.common.blocks.machine.TileMultiBlock;
import mods.railcraft.common.util.steam.ISteamUser;
import mods.railcraft.common.blocks.machine.beta.TileBoilerFirebox;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.items.RailcraftPartItems;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.plugins.buildcraft.triggers.INeedsMaintenance;
import mods.railcraft.common.plugins.ic2.IC2Plugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.StandaloneInventory;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.FakeTank;
import mods.railcraft.common.fluids.tanks.FilteredTank;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.plugins.ic2.IMultiEmitterDelegate;
import mods.railcraft.common.plugins.ic2.TileIC2MultiEmitterDelegate;
import mods.railcraft.common.util.misc.Game;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileSteamTurbine extends TileMultiBlock implements IMultiEmitterDelegate, IFluidHandler, INeedsMaintenance, ISteamUser, IElectricGrid {

    enum Texture {

        END_TL(6), END_TR(7), END_BL(8), END_BR(9), SIDE_A(0), SIDE_B(10), GUAGE(11);
        private final int index;

        private Texture(int index) {
            this.index = index;
        }

        public IIcon getIcon() {
            return EnumMachineAlpha.TURBINE.getTexture(index);
        }

    }
    private static final int DAMAGE_CHANCE = 200;
    private static final int IC2_OUTPUT = 200;
    private static final int BC_OUTPUT = 64;
    private static final int STEAM_USAGE = 320;
    private static final int WATER_OUTPUT = 4;
    private static final List<MultiBlockPattern> patterns = new ArrayList<MultiBlockPattern>();
    private static ItemStack sampleRotor = null;

    public static ItemStack getSampleRotor() {
        if (sampleRotor == null)
            sampleRotor = RailcraftPartItems.getTurbineRotor();
        return sampleRotor;
    }

    private final StandaloneInventory inv = new StandaloneInventory(1, this);
    public float output;
    private final FluidStack waterFilter = Fluids.WATER.get(2);
    protected final FilteredTank tankSteam = new FilteredTank(FluidHelper.BUCKET_VOLUME * 4, Fluids.STEAM.get(), this);
    protected final FilteredTank tankWater = new FilteredTank(FluidHelper.BUCKET_VOLUME * 4, Fluids.WATER.get(), this);
    protected final TankManager tankManager = new TankManager();
    public static final int TANK_STEAM = 0;
    public static final int TANK_WATER = 1;
    private byte gaugeState = 0;
    // mainGauge is a renderer field 
    public double mainGauge;
    private double energy;
    private TileEntity emitterDelegate;
    private final ChargeHandler chargeHandler = new ChargeHandler(this, ChargeHandler.ConnectType.BLOCK);

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
                {'O', 'B', 'B', 'B', 'O'},
                {'O', 'B', 'B', 'B', 'O'},
                {'O', 'O', 'O', 'O', 'O'}
            },
            {
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'B', 'W', 'B', 'O'},
                {'O', 'B', 'W', 'B', 'O'},
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
                {'O', 'B', 'B', 'O'},
                {'O', 'B', 'B', 'O'},
                {'O', 'O', 'O', 'O'}
            },
            {
                {'O', 'O', 'O', 'O'},
                {'O', 'B', 'B', 'O'},
                {'O', 'W', 'W', 'O'},
                {'O', 'B', 'B', 'O'},
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

    public TileSteamTurbine() {
        super(patterns);
        tankManager.add(tankSteam); // Steam
        tankManager.add(tankWater); // Water

    }

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineAlpha.TURBINE;
    }

    @Override
    public ChargeHandler getChargeHandler() {
        return chargeHandler;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (Game.isHost(worldObj)) {
            if (isStructureValid()) {
                if (isMaster())
                    addToNet();
                chargeHandler.tick();
            } else
                dropFromNet();

            double chargeNeeded = chargeHandler.getCapacity() - chargeHandler.getCharge();
            if (chargeNeeded > 0) {
                double draw = (chargeNeeded / IC2_OUTPUT) * BC_OUTPUT;
                double e = getEnergy();
                if (e < draw)
                    draw = e;
                removeEnergy(draw);
                chargeHandler.addCharge((draw / BC_OUTPUT) * IC2_OUTPUT);
            }

            if (isMaster()) {
                boolean addedEnergy = false;
                if (energy < BC_OUTPUT * 2) {
                    FluidStack steam = tankManager.drain(TANK_STEAM, STEAM_USAGE, false);
//                if(steam != null) System.out.println("steam=" + steam.amount);
                    if (steam != null && steam.amount >= STEAM_USAGE) {
                        ItemStack rotor = inv.getStackInSlot(0);
                        if (InvTools.isItemEqual(rotor, getSampleRotor()) /*&& rotor.getItemDamage() < rotor.getMaxDamage() - 5*/) {
                            addedEnergy = true;
                            energy += BC_OUTPUT;
                            tankManager.drain(TANK_STEAM, STEAM_USAGE, true);

                            if (worldObj.rand.nextInt(4) != 0)
                                tankWater.fill(waterFilter, true);

                            if (worldObj.rand.nextInt(DAMAGE_CHANCE) == 0)
                                inv.setInventorySlotContents(0, InvTools.damageItem(rotor, 1));
                        }
                    }
                }

                output = (float) ((output * 49D + (addedEnergy ? 100D : 0D)) / 50D);

//                System.out.println("output=" + output);
//                System.out.println("addedEnergy=" + addedEnergy);
                if (clock % 4 == 0) {
                    gaugeState = (byte) getOutput();
                    WorldPlugin.addBlockEvent(worldObj, xCoord, yCoord, zCoord, getBlockType(), 1, gaugeState);
                }
            }
        }

        TankManager tMan = getTankManager();
        if (tMan != null)
            tMan.outputLiquid(getOutputs(), 0, WATER_OUTPUT);
    }

    private IFluidHandler getOutputOnSide(ForgeDirection side) {
        TileEntity tile = tileCache.getTileOnSide(side);
        if (tile instanceof TileBoilerFirebox)
            return (IFluidHandler) tile;
        return null;
    }

    private IFluidHandler[] getOutputs() {
        IFluidHandler[] outputs = new IFluidHandler[6];
        for (int side = 2; side < 6; side++) {
            ForgeDirection dir = ForgeDirection.getOrientation(side);
            outputs[side] = getOutputOnSide(dir);
        }
        return outputs;
    }

    private void addToNet() {
        if (emitterDelegate == null)
            try {
                emitterDelegate = new TileIC2MultiEmitterDelegate(this);
                IC2Plugin.addTileToNet(emitterDelegate);
            } catch (Throwable error) {
                Game.logErrorAPI("IndustrialCraft", error);
            }
    }

    private void dropFromNet() {
        if (emitterDelegate != null) {
            IC2Plugin.removeTileFromNet(emitterDelegate);
            emitterDelegate = null;
        }
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        dropFromNet();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        dropFromNet();
    }

    @Override
    protected void onMasterReset() {
        super.onMasterReset();
        dropFromNet();
    }

    @Override
    public void onBlockRemoval() {
        super.onBlockRemoval();
        InvTools.dropInventory(inv, worldObj, xCoord, yCoord, zCoord);
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        TileMultiBlock mBlock = getMasterBlock();
        if (mBlock != null) {
            GuiHandler.openGui(EnumGui.TURBINE, player, worldObj, mBlock.xCoord, mBlock.yCoord, mBlock.zCoord);
            return true;
        }
        return false;
    }

    public boolean hasEnergy() {
        return getEnergy() >= BC_OUTPUT;
    }

    public void removeEnergy(double amount) {
        TileSteamTurbine mBlock = (TileSteamTurbine) getMasterBlock();
        if (mBlock != null) {
            mBlock.energy -= amount;
            if (mBlock.energy < 0) mBlock.energy = 0;
        }
    }

    public double getEnergy() {
        TileSteamTurbine mBlock = (TileSteamTurbine) getMasterBlock();
        if (mBlock == null)
            return 0;
        return mBlock.energy;
    }

    public float getOutput() {
        TileSteamTurbine mBlock = (TileSteamTurbine) getMasterBlock();
        if (mBlock == null)
            return 0;
        return mBlock.output;
    }

    public float getMainGauge() {
        TileSteamTurbine mBlock = (TileSteamTurbine) getMasterBlock();
        if (mBlock == null)
            return 0;
        return mBlock.gaugeState * 0.01F;
    }

    @Override
    public IIcon getIcon(int side) {
        if (isStructureValid()) {
            int patIndex = patterns.indexOf(getPattern());
            if (side == 0 || side == 1) {
                if (patIndex == 0)
                    return Texture.SIDE_B.getIcon();
                return Texture.SIDE_A.getIcon();
            }

            if (getPatternMarker() == 'W')
                return Texture.GUAGE.getIcon();
            if (patIndex == 0)
                switch (side) {
                    case 2:
                        if (getPatternPositionY() == 2) {
                            if (getPatternPositionX() == 2)
                                return Texture.END_TL.getIcon();
                            return Texture.END_TR.getIcon();
                        }
                        if (getPatternPositionX() == 2)
                            return Texture.END_BL.getIcon();
                        return Texture.END_BR.getIcon();
                    case 3:
                        if (getPatternPositionY() == 2) {
                            if (getPatternPositionX() == 1)
                                return Texture.END_TL.getIcon();
                            return Texture.END_TR.getIcon();
                        }
                        if (getPatternPositionX() == 1)
                            return Texture.END_BL.getIcon();
                        return Texture.END_BR.getIcon();
                }
            else
                switch (side) {
                    case 4:
                        if (getPatternPositionY() == 2) {
                            if (getPatternPositionZ() == 1)
                                return Texture.END_TL.getIcon();
                            return Texture.END_TR.getIcon();
                        }
                        if (getPatternPositionZ() == 1)
                            return Texture.END_BL.getIcon();
                        return Texture.END_BR.getIcon();
                    case 5:
                        if (getPatternPositionY() == 2) {
                            if (getPatternPositionZ() == 2)
                                return Texture.END_TL.getIcon();
                            return Texture.END_TR.getIcon();
                        }
                        if (getPatternPositionZ() == 2)
                            return Texture.END_BL.getIcon();
                        return Texture.END_BR.getIcon();
                }

        }
        return Texture.SIDE_A.getIcon();
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        inv.writeToNBT("rotor", data);
        tankManager.writeTanksToNBT(data);
        chargeHandler.writeToNBT(data);
        data.setFloat("energy", (float) energy);
        data.setFloat("output", output);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        inv.readFromNBT("rotor", data);
        tankManager.readTanksFromNBT(data);
        chargeHandler.readFromNBT(data);
        energy = data.getFloat("energy");
        output = data.getFloat("output");
    }

    @Override
    public boolean receiveClientEvent(int id, int value) {
        if (id == 1) {
            gaugeState = (byte) value;
            return true;
        }
        return super.receiveClientEvent(id, value);
    }

    @Override
    public double getOfferedEnergy() {
        if (hasEnergy())
            return IC2_OUTPUT;
        return 0;
    }

    @Override
    public void drawEnergy(double amount) {
        removeEnergy((amount / IC2_OUTPUT) * BC_OUTPUT);
    }

    @Override
    public int getSourceTier() {
        return 3;
    }

    @Override
    public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
        return true;
    }

    @Override
    public TileEntity getTile() {
        return this;
    }

    @Override
    public List<TileEntity> getSubTiles() {
        return getComponents();
    }

    public StandaloneInventory getInventory() {
        TileSteamTurbine mBlock = (TileSteamTurbine) getMasterBlock();
        if (mBlock != null)
            return mBlock.inv;
        return inv;
    }

    public TankManager getTankManager() {
        TileSteamTurbine mBlock = (TileSteamTurbine) getMasterBlock();
        if (mBlock != null)
            return mBlock.tankManager;
        return null;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        TankManager tMan = getTankManager();
        if (tMan != null) return tMan.fill(TANK_STEAM, resource, doFill);
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        TankManager tMan = getTankManager();
        if (tMan != null) return tMan.drain(TANK_WATER, maxDrain, doDrain);
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (Fluids.WATER.is(resource))
            return drain(from, resource.amount, doDrain);
        return null;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection side) {
        TankManager tMan = getTankManager();
        if (tMan != null) return tMan.getTankInfo();
        return FakeTank.INFO;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return fluid == null || Fluids.STEAM.is(fluid);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return fluid == null || Fluids.WATER.is(fluid);
    }

//    @Override
//    public void onDisable(int duration) {
//        TileSteamTurbine mBlock = (TileSteamTurbine) getMasterBlock();
//        if (mBlock != null) {
//            mBlock.disabled = duration;
//        }
//    }
//
//    @Override
//    public boolean isDisabled() {
//        TileSteamTurbine mBlock = (TileSteamTurbine) getMasterBlock();
//        if (mBlock != null) {
//            return mBlock.disabled <= 0;
//        }
//        return true;
//    }
    @Override
    public boolean needsMaintenance() {
        TileSteamTurbine mBlock = (TileSteamTurbine) getMasterBlock();
        if (mBlock != null) {
            ItemStack rotor = mBlock.inv.getStackInSlot(0);
            if (rotor == null)
                return true;
            if (!InvTools.isItemEqual(rotor, getSampleRotor()))
                return true;
            if (rotor.getItemDamage() / (double) rotor.getMaxDamage() > 0.75f)
                return true;
        }
        return false;
    }

}

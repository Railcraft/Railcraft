/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.detector.types;

import mods.railcraft.common.blocks.detector.DetectorFilter;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.fluids.AdvancedFluidHandler;
import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.buttons.IButtonTextureSet;
import mods.railcraft.common.gui.buttons.IMultiButtonState;
import mods.railcraft.common.gui.buttons.MultiButtonController;
import mods.railcraft.common.gui.buttons.StandardButtonTextureSets;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static mods.railcraft.common.plugins.forge.PowerPlugin.FULL_POWER;
import static mods.railcraft.common.plugins.forge.PowerPlugin.NO_POWER;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class DetectorTank extends DetectorFilter {
    private final MultiButtonController<ButtonState> buttonController = MultiButtonController.create(ButtonState.ANALOG.ordinal(), ButtonState.values());

    public DetectorTank() {
        super(1);
    }

    public MultiButtonController<ButtonState> getButtonController() {
        return buttonController;
    }

    public @Nullable FluidStack getFilterFluid() {
        ItemStack filter = getFilters().getStackInSlot(0);
        if (!InvTools.isEmpty(filter))
            return FluidItemHelper.getFluidStackInContainer(filter);
        return null;
    }

    @Override
    public int testCarts(List<EntityMinecart> carts) {
        for (EntityMinecart cart : carts) {
            IFluidHandler fluidHandler = FluidTools.getFluidHandler(null, cart);
            if (fluidHandler != null) {
                AdvancedFluidHandler tank = new AdvancedFluidHandler(fluidHandler);
                boolean liquidMatches = false;
                FluidStack filterFluid = getFilterFluid();
                FluidStack tankLiquid = tank.drain(1, false);
                if (filterFluid == null)
                    liquidMatches = true;
                else if (FluidTools.matches(filterFluid, tankLiquid))
                    liquidMatches = true;
                else if (tank.canPutFluid(new FluidStack(filterFluid, 1)))
                    liquidMatches = true;
                boolean quantityMatches = false;
                ButtonState state = buttonController.getButtonState();
                switch (state) {
                    case VOID:
                        quantityMatches = true;
                        break;
                    case EMPTY:
                        if (filterFluid != null && tank.isTankEmpty(filterFluid))
                            quantityMatches = true;
                        else if (filterFluid == null && tank.areTanksEmpty())
                            quantityMatches = true;
                        break;
                    case NOT_EMPTY:
                        if (filterFluid != null && tank.getFluidQty(filterFluid) > 0)
                            quantityMatches = true;
                        else if (filterFluid == null && tank.isFluidInTank())
                            quantityMatches = true;
                        break;
                    case FULL:
                        if (filterFluid != null && tank.isTankFull(filterFluid))
                            quantityMatches = true;
                        else if (filterFluid == null && tank.areTanksFull())
                            quantityMatches = true;
                        break;
                    default:
                        float level = filterFluid != null ? tank.getFluidLevel(filterFluid) : tank.getFluidLevel();
                        switch (state) {
                            case ANALOG:
                                return (int) (FULL_POWER * level);
                            case QUARTER:
                                quantityMatches = level >= 0.25f;
                                break;
                            case HALF:
                                quantityMatches = level >= 0.5f;
                                break;
                            case MOST:
                                quantityMatches = level >= 0.75f;
                                break;
                            case LESS_THAN_QUARTER:
                                quantityMatches = level < 0.25f;
                                break;
                            case LESS_THAN_HALF:
                                quantityMatches = level < 0.5f;
                                break;
                            case LESS_THAN_MOST:
                                quantityMatches = level < 0.75f;
                                break;
                            case LESS_THAN_FULL:
                                quantityMatches = level < 1f;
                                break;
                        }
                }
                return liquidMatches && quantityMatches ? FULL_POWER : NO_POWER;
            }
        }
        return NO_POWER;
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        openGui(EnumGui.DETECTOR_TANK, player);
        return true;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setByte("state", (byte) buttonController.getCurrentState());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        buttonController.setCurrentState(data.getByte("state"));
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte(buttonController.getCurrentState());
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        buttonController.setCurrentState(data.readByte());
    }

    @Override
    public void writeGuiData(RailcraftOutputStream data) throws IOException {
        data.writeByte(buttonController.getCurrentState());
    }

    @Override
    public void readGuiData(RailcraftInputStream data, @Nullable EntityPlayer sender) throws IOException {
        buttonController.setCurrentState(data.readByte());
    }

    @Override
    public EnumDetector getType() {
        return EnumDetector.TANK;
    }

    private enum ButtonState implements IMultiButtonState {

        VOID("L = *"),
        EMPTY("L = 0%"),
        NOT_EMPTY("L > 0%"),
        FULL("L = 100%"),
        QUARTER("L >= 25%"),
        HALF("L >= 50%"),
        MOST("L >= 75%"),
        LESS_THAN_QUARTER("L < 25%"),
        LESS_THAN_HALF("L < 50%"),
        LESS_THAN_MOST("L < 75%"),
        LESS_THAN_FULL("L < 100%"),
        ANALOG("L = ~");
        private final String label;
        private final ToolTip tip;

        ButtonState(String label) {
            this.label = label;
            this.tip = ToolTip.buildToolTip("gui.detector.tank.tips." + name().toLowerCase(Locale.ENGLISH).replace("_", "."));
        }

        @Override
        public String getLabel() {
            return label;
        }

        @Override
        public IButtonTextureSet getTextureSet() {
            return StandardButtonTextureSets.LARGE_BUTTON;
        }

        @Override
        public ToolTip getToolTip() {
            return tip;
        }

    }
}

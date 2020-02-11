/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.api.tracks.TrackToolsAPI;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.gui.buttons.IButtonTextureSet;
import mods.railcraft.common.gui.buttons.IMultiButtonState;
import mods.railcraft.common.gui.buttons.MultiButtonController;
import mods.railcraft.common.gui.buttons.StandardButtonTextureSets;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.DataManagerPlugin;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class CartBaseMaintenance extends CartBaseContainer implements IGuiReturnHandler {

    private static final DataParameter<Byte> BLINK = DataManagerPlugin.create(DataSerializers.BYTE);
    protected static final double DRAG_FACTOR = 0.9;
    private static final int BLINK_DURATION = 3;
    private static final int DATA_ID_BLINK = 25;
    public static final DataParameter<Byte> CART_MODE = DataManagerPlugin.create(DataSerializers.BYTE);
    private final MultiButtonController<CartModeButtonState> modeController = MultiButtonController.create(0, CartModeButtonState.VALUES);

    @Override
    public float getMaxCartSpeedOnRail() {
        return getMode().speed;
    }

    public enum CartMode implements IStringSerializable {

        SERVICE(0.1f),
        TRANSPORT(0.4f);
        public static final EntityCartTrackLayer.CartMode[] VALUES = values();
        public final float speed;

        CartMode(float speed) {
            this.speed = speed;
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public MultiButtonController<CartModeButtonState> getModeController() {
        return modeController;
    }

    public enum CartModeButtonState implements IMultiButtonState {
        SERVICE("Service mode"),
        TRANSPORT("Transport mode");

        public static final CartModeButtonState[] VALUES = values();
        private final String label;

        CartModeButtonState(String label) {
            this.label = label;
        }

        @Override
        public String getLabel() {
            return LocalizationPlugin.translate("gui.railcraft.cart.maintenance.mode."+name().toLowerCase());
        }

        @Override
        public IButtonTextureSet getTextureSet() {
            return StandardButtonTextureSets.SMALL_BUTTON;
        }

        @Override
        public @Nullable ToolTip getToolTip() {
            return null;
        }
    }

    public CartMode getMode() {
        return DataManagerPlugin.readEnum(dataManager, CART_MODE, CartMode.VALUES);
    }

    public void setMode(CartMode mode) {
        if (getMode() != mode) DataManagerPlugin.writeEnum(dataManager, CART_MODE, mode);
    }

    public CartMode getOtherMode() {
        if(getMode() == CartMode.SERVICE) return CartMode.TRANSPORT;
        else if(getMode() == CartMode.TRANSPORT) return CartMode.SERVICE;
        return CartMode.SERVICE;
    }

    protected CartBaseMaintenance(World world) {
        super(world);
    }

    protected CartBaseMaintenance(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(BLINK, (byte) 0);
        dataManager.register(CART_MODE, (byte) CartMode.SERVICE.ordinal());
    }

    @Override
    public int getSizeInventory() {
        return 0;
    }

    protected void blink() {
        dataManager.set(BLINK, (byte) BLINK_DURATION);
    }

    protected void setBlink(byte blink) {
        dataManager.set(BLINK, blink);
    }

    protected byte getBlink() {
        return dataManager.get(BLINK);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (Game.isClient(world))
            return;

        if (isBlinking())
            setBlink((byte) (getBlink() - 1));
    }

    @Override
    public boolean canBeRidden() {
        return false;
    }

    @Override
    protected void applyDrag() {
        super.applyDrag();
        this.motionX *= DRAG_FACTOR;
        this.motionZ *= DRAG_FACTOR;
    }

    public boolean isBlinking() {
        return dataManager.get(BLINK) > 0;
    }

    protected boolean placeNewTrack(BlockPos pos, int slotStock, BlockRailBase.EnumRailDirection trackShape) {
        ItemStack trackStock = getStackInSlot(slotStock);
        if (!InvTools.isEmpty(trackStock))
            if (TrackToolsAPI.placeRailAt(trackStock, (WorldServer) getEntityWorld(), pos)) {
                decrStackSize(slotStock, 1);
                blink();
                return true;
            }
        return false;
    }

    protected BlockRailBase.EnumRailDirection removeOldTrack(BlockPos pos, Block block) {
        IBlockState state = WorldPlugin.getBlockState(getEntityWorld(), pos);
        //noinspection deprecation
        List<ItemStack> drops = block.getDrops(world, pos, state, 0);

        for (ItemStack stack : drops) {
            CartToolsAPI.transferHelper().offerOrDropItem(this, stack);
        }
        BlockRailBase.EnumRailDirection trackShape = TrackTools.getTrackDirectionRaw(state);
        getEntityWorld().setBlockToAir(pos);
        return trackShape;
    }

    @Override
    public void writeGuiData(RailcraftOutputStream data) throws IOException {
        data.writeEnum(getMode());
    }

    @Override
    public void readGuiData(RailcraftInputStream data, EntityPlayer sender) throws IOException {
        setMode(data.readEnum(CartMode.VALUES));
    }

    @Override
    public @Nullable World theWorld() {
        return world;
    }
}

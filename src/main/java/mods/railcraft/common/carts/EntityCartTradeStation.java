/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.carts;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.single.BlockTradeStation;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.plugins.forge.AIPlugin;
import mods.railcraft.common.plugins.forge.DataManagerPlugin;
import mods.railcraft.common.util.entity.ai.EntityAISearchForEntity;
import mods.railcraft.common.util.entity.ai.EntityAIWatchEntity;
import mods.railcraft.common.util.logic.Logic;
import mods.railcraft.common.util.logic.ILogicContainer;
import mods.railcraft.common.util.logic.TradeStationLogic;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 *
 */
public class EntityCartTradeStation extends CartBaseContainer implements ILogicContainer<TradeStationLogic>, IGuiReturnHandler {

    static final byte[] INITIAL_BUFFER;
    static final DataParameter<byte[]> BYTE_BUFFER = DataManagerPlugin.create(DataManagerPlugin.BYTE_ARRAY);
    private final TradeStationLogic logic;

    static {
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
             RailcraftOutputStream stream = new RailcraftOutputStream(bytes)) {
            stream.writeUTF("minecraft:farmer");
            stream.writeByte(EnumFacing.WEST.ordinal());
            INITIAL_BUFFER = bytes.toByteArray();
        } catch (IOException ex) {
            Game.log(Level.FATAL, "Cannot initialize trade station cart");
            throw new UncheckedIOException(ex);
        }
    }

    protected EntityCartTradeStation(World world) {
        super(world);
        logic = new TradeStationLogic(Logic.Adapter.of(this), this) {
            @Override
            public void sendUpdateToClient() {
                try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                     RailcraftOutputStream railcraftOutputStream = new RailcraftOutputStream(byteArrayOutputStream)) {
                    writePacketData(railcraftOutputStream);
                    getDataManager().set(BYTE_BUFFER, byteArrayOutputStream.toByteArray());
                } catch (IOException ex) {
                    Game.logThrowable("Error encoding output stream packet: {0}", ex);
                    if (Game.DEVELOPMENT_ENVIRONMENT)
                        throw new UncheckedIOException(ex);
                }
            }

            @Override
            protected void modifyNearbyAI() {
                for (EntityVillager villager : findNearbyVillagers(20)) {
                    AIPlugin.addAITask(villager, 9, new EntityAIWatchEntity(villager, entity -> entity instanceof EntityCartTradeStation, 4, 0.08F));
                    AIPlugin.addAITask(villager, 9, new EntityAISearchForEntity(villager, entity -> entity instanceof EntityCartTradeStation, 16, 0.002F));
                }
            }
        };
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(BYTE_BUFFER, INITIAL_BUFFER);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        logic.update();
    }

    @Override
    public IBlockState getDefaultDisplayTile() {
        return RailcraftBlocks.TRADE_STATION.getDefaultState().withProperty(BlockTradeStation.FACING, EnumFacing.WEST);
    }

    @Override
    protected EnumGui getGuiType() {
        return EnumGui.TRADE_STATION;
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.TRADE_STATION;
    }

    @Override
    public int getSizeInventory() {
        return 16;
    }

    @Override
    public TradeStationLogic getLogic() {
        return logic;
    }

    @Override
    public @Nullable World theWorld() {
        return world;
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (key == BYTE_BUFFER) {
            try (RailcraftInputStream input = new RailcraftInputStream(new ByteArrayInputStream(dataManager.get(BYTE_BUFFER)))) {
                logic.readPacketData(input);
            } catch (IOException ex) {
                Game.logThrowable("Error decoding input stream packet: {0}", ex);
                if (Game.DEVELOPMENT_ENVIRONMENT)
                    throw new UncheckedIOException(ex);
            }
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        logic.writeToNBT(compound);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        logic.readFromNBT(compound);
    }

    @Override
    public void writeGuiData(RailcraftOutputStream data) throws IOException {
        logic.writeGuiData(data);
    }

    @Override
    public void readGuiData(RailcraftInputStream data, EntityPlayer sender) throws IOException {
        logic.readGuiData(data, sender);
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return slot < 10;
    }
}

/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import mods.railcraft.api.signals.*;
import mods.railcraft.client.render.tools.RenderTools;
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

public class TileSignalDualHeadBlockSignal extends TileSignalBlockSignal implements IReceiverTile, IDualHeadSignal {

    private static final AxisAlignedBB BOUNDING_BOX = AABBFactory.start().box().expandHorizontally(-BOUNDS).build();
    private final SimpleSignalReceiver receiver = new SimpleSignalReceiver(getLocalizationTag(), this);

    @Override
    public EnumSignal getSignalType() {
        return EnumSignal.DUAL_HEAD_BLOCK_SIGNAL;
    }

    @Override
    public int getLightValue() {
        return Math.max(getSignalAspect().getLightValue(), getSignalAspect(DualLamp.BOTTOM).getLightValue());
    }

    @Override
    public void update() {
        super.update();
        if (Game.isClient(worldObj)) {
            receiver.tickClient();
            return;
        }
        receiver.tickServer();
        SignalAspect prevAspect = receiver.getAspect();
        if (receiver.isBeingPaired()) {
            receiver.setAspect(SignalAspect.BLINK_YELLOW);
        } else if (!receiver.isPaired()) {
            receiver.setAspect(SignalAspect.BLINK_RED);
        }
        if (prevAspect != receiver.getAspect()) {
            sendUpdateToClient();
        }
    }

    @Override
    public void onControllerAspectChange(SignalController con, SignalAspect aspect) {
        sendUpdateToClient();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockAccess world, BlockPos pos) {
        return BOUNDING_BOX;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        receiver.writeToNBT(data);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        receiver.readFromNBT(data);
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);

        receiver.writePacketData(data);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);

        receiver.readPacketData(data);
    }

    @Override
    public SimpleSignalReceiver getReceiver() {
        return receiver;
    }

    @Override
    public SignalAspect getSignalAspect(DualLamp lamp) {
        if (lamp == DualLamp.BOTTOM)
            return receiver.getAspect();
        return getSignalAspect();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public TextureAtlasSprite getLampTexture(DualLamp lamp, SignalAspect aspect) {
        return RenderTools.getMissingTexture();
    }
}

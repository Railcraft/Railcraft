/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.io.IOException;

import static net.minecraft.util.EnumFacing.NORTH;

/**
 * Created by CovertJaguar on 8/4/2020 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RotationLogic extends Logic {
    private EnumFacing facing = NORTH;

    public static RotationLogic getRotationLogic(ILogicContainer container) {
        return container.getLogic(RotationLogic.class).orElseGet(() -> {
            // This is kind of ugly, but it works. Normally we can't access the functional logic tree directly, only through the master.
            return container.getLogic(StructureLogic.class)
                    .flatMap(s -> s.functionalLogic.getLogic(RotationLogic.class))
                    .orElseThrow(() -> new RuntimeException(container.getClass().getSimpleName()));
        });
    }

    public RotationLogic(Adapter adapter) {
        super(adapter);
    }

    @Override
    public void placed(IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack) {
        super.placed(state, placer, stack);
        if (placer != null)
            setFacing(MiscTools.getHorizontalSideFacingPlayer(placer));
        adapter.updateModels();
    }

    public EnumFacing getFacing() {
        return facing;
    }

    public void setFacing(EnumFacing facing) {
        this.facing = facing;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setByte("facing", (byte) facing.getHorizontalIndex());
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        facing = EnumFacing.byHorizontalIndex(data.getByte("facing"));
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeEnum(facing);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        EnumFacing f = data.readEnum(EnumFacing.VALUES);
        if (facing != f) {
            facing = f;
            adapter.updateModels();
        }
    }
}

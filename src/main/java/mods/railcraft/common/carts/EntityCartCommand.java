/*
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import io.netty.buffer.ByteBuf;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Credits to CovertJaguar.
 *
 * @author liach
 */
public class EntityCartCommand extends EntityMinecartCommandBlock {


    public EntityCartCommand(World world) {
        super(world);
    }

    public EntityCartCommand(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public boolean doInteract(EntityPlayer player) {
        if (Game.isClient(worldObj)) {
            player.func_146095_a(this.getCommandLogic());
        }
        return true;
    }

    public CommandBlockBaseLogic getCommandLogic() {
        return commandLogic;
    }


    @Override
    public void func_145781_i(int dataValueId) {
        super.func_145781_i(dataValueId);

        if (dataValueId == 24) {
            try {
                this.commandLogic.func_145750_b(ITextComponent.Serializer.func_150699_a(this.getDataWatcher().getWatchableObjectString(24)));
            } catch (Throwable ignored) {

            }
        } else if (dataValueId == 23) {
            this.commandLogic.func_145752_a(this.getDataWatcher().getWatchableObjectString(23));
        }
    }
}

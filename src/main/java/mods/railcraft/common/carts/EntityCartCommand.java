/*
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Credits to CovertJaguar.
 * @author liach
 */
public class EntityCartCommand extends CartBase {

    private int timeExisted = 0;

    private final CommandBlockLogic commandLogic = new CommandBlockLogic() {

        @Override
        public void func_145756_e() {
            EntityCartCommand.this.getDataWatcher().updateObject(23, this.func_145753_i());
            EntityCartCommand.this.getDataWatcher().updateObject(24, IChatComponent.Serializer.func_150696_a(this.func_145749_h()));
        }

        @SideOnly(Side.CLIENT)
        @Override
        public int func_145751_f() {
            return 1;
        }

        @SideOnly(Side.CLIENT)
        @Override
        public void func_145757_a(ByteBuf buffer) {
            buffer.writeInt(EntityCartCommand.this.getEntityId());
        }

        @Override
        public ChunkCoordinates getPlayerCoordinates() {
            return new ChunkCoordinates(
                    MathHelper.floor_double(EntityCartCommand.this.posX),
                    MathHelper.floor_double(EntityCartCommand.this.posY + 0.5D),
                    MathHelper.floor_double(EntityCartCommand.this.posZ)
            );
        }

        @Override
        public World getEntityWorld() {
            return EntityCartCommand.this.worldObj;
        }
    };

    public EntityCartCommand(World world) {
        super(world);
    }

    public EntityCartCommand(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        this.getDataWatcher().addObject(23, "");
        this.getDataWatcher().addObject(24, "");
    }
    
    @Override
    protected void readEntityFromNBT(NBTTagCompound p_70037_1_)
    {
        super.readEntityFromNBT(p_70037_1_);
        this.commandLogic.func_145759_b(p_70037_1_);
        this.getDataWatcher().updateObject(23, this.commandLogic.func_145753_i());
        this.getDataWatcher().updateObject(24, IChatComponent.Serializer.func_150696_a(this.commandLogic.func_145749_h()));
    }
    
    @Override
    protected void writeEntityToNBT(NBTTagCompound p_70014_1_)
    {
        super.writeEntityToNBT(p_70014_1_);
        this.commandLogic.func_145758_a(p_70014_1_);
    }

    @Override
    public Block func_145820_n() {
        return Blocks.command_block;
    }

    @Override
    public double getDrag() {
        return CartConstants.STANDARD_DRAG;
    }

    @Override
    public boolean doInteract(EntityPlayer player) {
        if (Game.isNotHost(worldObj)) {
            player.func_146095_a(this.getCommandLogic());
        }
        return true;
    }

    public CommandBlockLogic getCommandLogic() {
        return commandLogic;
    }

    @Override
    public void onActivatorRailPass(int x, int y, int z, boolean powered) {
        if (powered && this.ticksExisted - this.timeExisted >= 4)
        {
            this.commandLogic.func_145755_a(this.worldObj);
            this.timeExisted = this.ticksExisted;
        }
    }

    @Override
    public void func_145781_i(int dataValueId) {
        super.func_145781_i(dataValueId);

        if (dataValueId == 24) {
            try {
                this.commandLogic.func_145750_b(IChatComponent.Serializer.func_150699_a(this.getDataWatcher().getWatchableObjectString(24)));
            } catch (Throwable ignored) {
                
            }
        }
        else if (dataValueId == 23) {
            this.commandLogic.func_145752_a(this.getDataWatcher().getWatchableObjectString(23));
        }
    }
}

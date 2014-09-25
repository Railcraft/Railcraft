/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class EntityCartWork extends CartBase {

    public EntityCartWork(World world) {
        super(world);
    }

    public EntityCartWork(World world, double d, double d1, double d2) {
        this(world);
        setPosition(d, d1 + (double) yOffset, d2);
        motionX = 0.0D;
        motionY = 0.0D;
        motionZ = 0.0D;
        prevPosX = d;
        prevPosY = d1;
        prevPosZ = d2;
    }

    @Override
    public List<ItemStack> getItemsDropped() {
        List<ItemStack> items = new ArrayList<ItemStack>();
        if (RailcraftConfig.doCartsBreakOnDrop()) {
            items.add(new ItemStack(Items.minecart));
            items.add(new ItemStack(Blocks.crafting_table));
        } else {
            items.add(getCartItem());
        }
        return items;
    }

    @Override
    public boolean doInteract(EntityPlayer entityplayer) {
        if (Game.isHost(worldObj)) {
            GuiHandler.openGui(EnumGui.CART_WORK, entityplayer, worldObj, this);
        }
        return true;
    }

    @Override
    public boolean canBeRidden() {
        return false;
    }

    @Override
    public Block func_145820_n() {
        return Blocks.crafting_table;
    }

    @Override
    public double getDrag() {
        return CartConstants.STANDARD_DRAG;
    }
}

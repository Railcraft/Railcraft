package mods.railcraft.common.blocks.single;

import mods.railcraft.common.blocks.ItemBlockEntityDelegate;
import mods.railcraft.common.plugins.color.ColorPlugin;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import static mods.railcraft.common.blocks.single.BlockForceTrackEmitter.DEFAULT_SHADE;

/**
 *
 */
public class ItemForceTrackEmitter extends ItemBlockEntityDelegate {

    public ItemForceTrackEmitter(Block block) {
        super(block);
    }

    @Override
    public void finalizeDefinition() {
        ColorPlugin.instance.register(this, this);
    }

    @Override
    public IItemColor colorHandler() {
        return (stack, index) -> index == 1 ? getColor(stack) : ColorPlugin.NONE_MULTIPLIER;
    }

    public static ItemStack setColor(ItemStack stack, int color) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null) {
            compound = new NBTTagCompound();
        }
        compound.setInteger("color", color);
        stack.setTagCompound(compound);
        return stack;
    }

    public static int getColor(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();
        return compound == null ? DEFAULT_SHADE : compound.getInteger("color");
    }
}

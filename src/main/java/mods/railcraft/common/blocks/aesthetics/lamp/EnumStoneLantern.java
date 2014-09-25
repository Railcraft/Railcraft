/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.lamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import mods.railcraft.common.blocks.aesthetics.brick.BlockBrick;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.modules.ModuleManager;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum EnumStoneLantern {

    ABYSSAL,
    BLEACHEDBONE,
    BLOODSTAINED,
    FROSTBOUND,
    INFERNAL,
    NETHER,
    QUARRIED,
    SANDY,
    SANDSTONE,
    STONE;
    public static final EnumStoneLantern[] VALUES = values();
    public static final Map<String, EnumStoneLantern> NAMES = new HashMap<String, EnumStoneLantern>();
    public static final List<EnumStoneLantern> creativeList = new ArrayList<EnumStoneLantern>();
    private ItemStack source;

    public static void initialize() {
        ABYSSAL.source = BlockBrick.abyssal.getItemStack(BlockBrick.BrickVariant.BLOCK, 1);
        BLEACHEDBONE.source = BlockBrick.bleachedbone.getItemStack(BlockBrick.BrickVariant.BLOCK, 1);
        BLOODSTAINED.source = BlockBrick.bloodstained.getItemStack(BlockBrick.BrickVariant.BLOCK, 1);
        FROSTBOUND.source = BlockBrick.frostbound.getItemStack(BlockBrick.BrickVariant.BLOCK, 1);
        INFERNAL.source = BlockBrick.infernal.getItemStack(BlockBrick.BrickVariant.BLOCK, 1);
        NETHER.source = BlockBrick.nether.getItemStack(BlockBrick.BrickVariant.BLOCK, 1);
        QUARRIED.source = BlockBrick.quarried.getItemStack(BlockBrick.BrickVariant.BLOCK, 1);
        SANDY.source = BlockBrick.sandy.getItemStack(BlockBrick.BrickVariant.BLOCK, 1);
        SANDSTONE.source = new ItemStack(Blocks.stone_slab, 1, 1);
        STONE.source = new ItemStack(Blocks.stone_slab, 1, 0);

        for (EnumStoneLantern lamp : VALUES) {
            NAMES.put(lamp.name(), lamp);

            if (lamp.isEnabled() && lamp.source != null)
                CraftingPlugin.addShapedRecipe(lamp.getItem(1), " S ", " T ", " S ", 'S', lamp.getSource(), 'T', new ItemStack(Blocks.torch));
            creativeList.add(lamp);
        }
    }

    public static EnumStoneLantern fromOrdinal(int id) {
        if (id < 0 || id >= VALUES.length)
            return VALUES[0];
        return VALUES[id];
    }

    public static EnumStoneLantern fromName(String name) {
        EnumStoneLantern lamp = NAMES.get(name);
        if (lamp != null)
            return lamp;
        return ABYSSAL;
    }

    public IIcon getTexture(int side) {
        return InvTools.getBlockFromStack(source).getIcon(ForgeDirection.UP.ordinal(), source.getItemDamage());
    }

    public Block getBlock() {
        return BlockStoneLantern.getBlock();
    }

    public ItemStack getSource() {
        if (source == null) return null;
        return source.copy();
    }

    public ItemStack getItem() {
        return getItem(1);
    }

    public ItemStack getItem(int qty) {
        Block block = getBlock();
        if (block == null) return null;
        return new ItemStack(block, qty, ordinal());
    }

    public String getTag() {
        return "railcraft.stonelamp." + name().replace("_", ".").toLowerCase(Locale.ENGLISH);
    }

    public boolean isEnabled() {
        return ModuleManager.isModuleLoaded(ModuleManager.Module.STRUCTURES) && RailcraftConfig.isSubBlockEnabled(getTag()) && getBlock() != null;
    }

}

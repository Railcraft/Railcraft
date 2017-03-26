/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.anvil;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.IRailcraftItemBlock;
import mods.railcraft.common.core.IRailcraftObject;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAnvilBlock;

import javax.annotation.Nullable;

/**
 * Created by CovertJaguar on 7/15/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemAnvil extends ItemAnvilBlock implements IRailcraftItemBlock {
    public ItemAnvil(Block block) {
        super(block);
    }

    @Override
    public Item getObject() {
        return this;
    }

    @Nullable
    @Override
    public Class<? extends IVariantEnum> getVariantEnum() {
        return ((IRailcraftObject) block).getVariantEnum();
    }

    @Nullable
    @Override
    public IVariantEnum[] getVariants() {
        return ((IRailcraftObject) block).getVariants();
    }
}

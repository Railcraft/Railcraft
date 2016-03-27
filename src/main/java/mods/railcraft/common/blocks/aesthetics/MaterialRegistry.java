/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/

package mods.railcraft.common.blocks.aesthetics;

import mods.railcraft.common.core.Railcraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by CovertJaguar on 3/24/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class MaterialRegistry {
    private static final Map<String, IBlockMaterial> materials = new HashMap<String, IBlockMaterial>();

    private MaterialRegistry() {
    }

    public static void register(IBlockMaterial material) {
        materials.put(material.getRegistryName(), material);
    }

    @Nonnull
    public static IBlockMaterial get(String name) {
        IBlockMaterial mat = materials.get(name);
        if (mat == null)
            mat = materials.get("railcraft:" + name);
        if (mat == null)
            mat = BlockMaterial.fromName(name);
        if (mat == null)
            mat = BlockMaterial.STONE_BRICK;
        return mat;
    }

    @Nonnull
    public static IBlockMaterial get(ResourceLocation name) {
        return get(name.toString());
    }

    public static void tagItemStack(ItemStack stack, String key, IBlockMaterial material) {
        if (stack == null)
            return;
        NBTTagCompound nbt = stack.getSubCompound(Railcraft.MOD_ID, true);
        nbt.setString(key, material.getRegistryName());
    }

    @Nullable
    public static IBlockMaterial from(ItemStack stack, String key) {
        if (stack == null)
            return BlockMaterial.STONE_BRICK;
        NBTTagCompound nbt = stack.getSubCompound(Railcraft.MOD_ID, true);
        if (nbt.hasKey(key))
            return get(nbt.getString(key));
        return BlockMaterial.OLD_ORDINALS.inverse().get(stack.getItemDamage());
    }
}

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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by CovertJaguar on 3/24/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class MaterialRegistry {
    private static final Map<String, BlockMaterial> materials = new HashMap<String, BlockMaterial>();

    private MaterialRegistry() {
    }

    public static void register(BlockMaterial material) {
        materials.put(material.getRegistryName(), material);
    }

    @Nonnull
    public static BlockMaterial get(String name) {
        BlockMaterial mat = materials.get(name);
        if (mat == null)
            mat = materials.get("railcraft:" + name);
        if (mat == null)
            mat = BlockMaterial.fromName(name);
        if (mat == null)
            mat = BlockMaterial.getPlaceholder();
        return mat;
    }

    @Nonnull
    public static BlockMaterial get(ResourceLocation name) {
        return get(name.toString());
    }

    public static void tagItemStack(ItemStack stack, String key, BlockMaterial material) {
        if (stack == null)
            return;
        NBTTagCompound nbt = stack.getSubCompound(Railcraft.MOD_ID, true);
        nbt.setString(key, material.getRegistryName());
    }

    public static BlockMaterial from(ItemStack stack, String key) {
        if (stack == null)
            return BlockMaterial.getPlaceholder();
        NBTTagCompound nbt = stack.getSubCompound(Railcraft.MOD_ID, true);
        if (nbt.hasKey(key))
            return get(nbt.getString(key));
        BlockMaterial material = BlockMaterial.OLD_ORDINALS.inverse().get(stack.getItemDamage());
        if (material != null)
            return material;
        return BlockMaterial.getPlaceholder();
    }

    @Nonnull
    public static Collection<BlockMaterial> getRegisteredMats() {
        return Collections.unmodifiableCollection(materials.values());
    }
}

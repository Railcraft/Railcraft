/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.items.potion;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.core.Railcraft;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import static mods.railcraft.common.util.inventory.InvTools.emptyStack;

public abstract class PotionTypeRailcraft extends PotionType implements IRailcraftObject<PotionType> {

    protected final String name;

    protected PotionTypeRailcraft(String name, PotionEffect... effects) {
        super(name, effects);
        this.name = name;
    }

    @Override
    public String getNamePrefixed(String prefix) {
        return prefix + Railcraft.MOD_ID + '.' + name; // Prevents mod conflicts
    }

    @Override
    public ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
        return emptyStack();
    }

    @Override
    public PotionType getObject() {
        return this;
    }

    @Override
    public void initializeDefinition() {
    }

    @Override
    public void finalizeDefinition() {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initializeClient() {
    }

}

package mods.railcraft.common.items.potion;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.core.IRailcraftObject;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

import static mods.railcraft.common.util.inventory.InvTools.emptyStack;

/**
 *
 */
public class PotionTypeRailcraft extends PotionType implements IRailcraftObject<PotionType> {

    public PotionTypeRailcraft(String name, PotionEffect... effects) {
        super(name, effects);
    }

    @Nullable
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

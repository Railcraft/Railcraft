package mods.railcraft.common.items.potion;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.core.IRailcraftObject;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import static mods.railcraft.common.util.inventory.InvTools.emptyStack;

public abstract class PotionTypeRailcraft extends PotionType implements IRailcraftObject<PotionType> {

    protected PotionTypeRailcraft(String name, PotionEffect... effects) {
        super(name, effects);
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

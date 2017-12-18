package mods.railcraft.common.items.potion;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.common.core.IRailcraftObject;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

import static mods.railcraft.common.util.inventory.InvTools.emptyStack;

/**
 *
 */
public class PotionRailcraft extends Potion implements IRailcraftObject<Potion> {

    private static final ResourceLocation POTION_ICON = RailcraftConstantsAPI.locationOf("textures/misc/potions.png");

    public PotionRailcraft(boolean isBadEffectIn, int liquidColorIn) {
        super(isBadEffectIn, liquidColorIn);
    }

    @Nullable
    @Override
    public ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
        return emptyStack();
    }

    @Override
    public Potion getObject() {
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

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasStatusIcon() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getStatusIconIndex() {
        Minecraft.getMinecraft().renderEngine.bindTexture(POTION_ICON);
        return super.getStatusIconIndex();
    }
}

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
import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.common.core.IRailcraftObject;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import static mods.railcraft.common.util.inventory.InvTools.emptyStack;

public abstract class PotionRailcraft extends Potion implements IRailcraftObject<Potion> {

    private static final ResourceLocation POTION_ICON = RailcraftConstantsAPI.locationOf("textures/misc/potions.png");

    protected PotionRailcraft(boolean isBadEffectIn, int liquidColorIn) {
        super(isBadEffectIn, liquidColorIn);
    }

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

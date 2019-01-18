/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import com.mojang.authlib.GameProfile;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.api.items.IFilterItem;
import mods.railcraft.client.emblems.Emblem;
import mods.railcraft.client.emblems.EmblemToolsClient;
import mods.railcraft.client.render.carts.LocomotiveModelRenderer;
import mods.railcraft.client.render.carts.LocomotiveRenderType;
import mods.railcraft.common.plugins.color.ColorPlugin;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import mods.railcraft.common.util.crafting.LocomotivePaintingRecipe;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemLocomotive extends ItemCart implements ColorPlugin.IColorHandlerItem, IFilterItem {

    private final LocomotiveRenderType renderType;
    private final EnumColor defaultPrimary;
    private final EnumColor defaultSecondary;
    protected final ItemStack sample;

    public ItemLocomotive(IRailcraftCartContainer cart, LocomotiveRenderType renderType, EnumColor primary, EnumColor secondary) {
        super(cart);
        this.renderType = renderType;
        setMaxStackSize(1);
        this.defaultPrimary = primary;
        this.defaultSecondary = secondary;
        this.sample = new ItemStack(this, 1, 0);
        setItemColorData(sample, primary, secondary);
    }

    @Override
    public boolean matches(ItemStack matcher, ItemStack target) {
        return target.getItem() == this && getPrimaryColor(matcher) == getPrimaryColor(target) && getSecondaryColor(matcher) == getSecondaryColor(target);
    }

    @Override
    public void finalizeDefinition() {
        ColorPlugin.instance.register(this);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new LocomotivePaintingRecipe(new ItemStack(this)));
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
        if (!isInCreativeTab(tab))
            return;
        for (String skin : renderType.getRendererTags()) {
            CreativePlugin.addToList(list, renderType.getItemWithRenderer(skin, new ItemStack(this)));
        }
    }

    @Override
    public ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
        return sample.copy();
    }

    //    @Override
//    public void registerIcons(IIconRegister iconRegister) {
//        renderType.registerIcons(iconRegister);
//        blankIcon = iconRegister.registerIcon("railcraft:locomotives/blank");
//    }

    //    @Override
//    public boolean requiresMultipleRenderPasses() {
//        return true;
//    }
//
//    @Override
//    public int getRenderPasses(int metadata) {
//        return 3;
//    }
    @Override
    public ColorPlugin.IColorFunctionItem colorHandler() {
        return (stack, tintIndex) -> {
            switch (tintIndex) {
                case 0:
                    return getPrimaryColor(stack).getHexColor();
                case 1:
                    return getSecondaryColor(stack).getHexColor();
                default:
                    return EnumColor.WHITE.getHexColor();
            }
        };
    }

//    @Override
//    @SideOnly(Side.CLIENT)
//    public IIcon getIcon(ItemStack stack, int pass) {
//        String rendererTag = getModel(stack);
//        LocomotiveModelRenderer renderer = renderType.getRenderer(rendererTag);
//        if (renderer == null)
//            return RenderTools.getMissingTexture();
//        IIcon[] icons = renderer.getItemIcons();
//        if (pass >= icons.length || icons[pass] == null)
//            return blankIcon;
//        return renderer.getItemIcons()[pass];
//    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> info, ITooltipFlag adv) {
        super.addInformation(stack, world, info, adv);
        GameProfile owner = getOwner(stack);
        if (owner.getName() != null && !RailcraftConstantsAPI.UNKNOWN_PLAYER.equalsIgnoreCase(owner.getName())) {
            String format = LocalizationPlugin.translate("gui.railcraft.locomotive.tips.item.owner");
            info.add(String.format(format, owner.getName()));
        }

        String model = getModel(stack);
        LocomotiveModelRenderer renderer = renderType.getRenderer(model);
        String modelName = renderer.getDisplayName();
        String format = LocalizationPlugin.translate("gui.railcraft.locomotive.tips.item.model");
        info.add(String.format(format, modelName));

        EnumColor primary = getPrimaryColor(stack);
        format = LocalizationPlugin.translate("gui.railcraft.locomotive.tips.item.primary");
        info.add(String.format(format, primary.getTranslatedName()));

        EnumColor secondary = getSecondaryColor(stack);
        format = LocalizationPlugin.translate("gui.railcraft.locomotive.tips.item.secondary");
        info.add(String.format(format, secondary.getTranslatedName()));

        float whistle = getWhistlePitch(stack);
        format = LocalizationPlugin.translate("gui.railcraft.locomotive.tips.item.whistle");
        info.add(String.format(format, whistle < 0 ? "???" : String.format("%.2f", whistle)));

        String emblemIdent = getEmblem(stack);
        if (!Strings.isEmpty(emblemIdent) && EmblemToolsClient.packageManager != null) {
            Emblem emblem = EmblemToolsClient.packageManager.getEmblem(emblemIdent);
            if (emblem != null) {
                format = LocalizationPlugin.translate("gui.railcraft.locomotive.tips.item.emblem");
                info.add(String.format(format, emblem.displayName));
            }
        }
    }

    public static void setItemColorData(ItemStack stack, EnumDyeColor primaryColor, EnumDyeColor secondaryColor) {
        setItemColorData(stack, EnumColor.fromDye(primaryColor), EnumColor.fromDye(secondaryColor));
    }

    public static void setItemColorData(ItemStack stack, EnumColor primaryColor, EnumColor secondaryColor) {
        primaryColor.setItemColor(stack, "primaryColor");
        secondaryColor.setItemColor(stack, "secondaryColor");
    }

    public static void setItemWhistleData(ItemStack stack, float whistlePitch) {
        NBTTagCompound nbt = InvTools.getItemData(stack);
        nbt.setFloat("whistlePitch", whistlePitch);
    }

    public static float getWhistlePitch(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null || !nbt.hasKey("whistlePitch"))
            return -1;
        return nbt.getFloat("whistlePitch");
    }

    public static void setOwnerData(ItemStack stack, GameProfile owner) {
        NBTTagCompound nbt = InvTools.getItemData(stack);
        PlayerPlugin.writeOwnerToNBT(nbt, owner);
    }

    public static GameProfile getOwner(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null)
            return new GameProfile(null, RailcraftConstantsAPI.UNKNOWN_PLAYER);
        return PlayerPlugin.readOwnerFromNBT(nbt);
    }

    public static void setEmblem(ItemStack stack, String emblemIdentifier) {
        NBTTagCompound nbt = InvTools.getItemData(stack);
        nbt.setString("emblem", emblemIdentifier);
    }

    public static String getEmblem(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null || !nbt.hasKey("emblem"))
            return "";
        return nbt.getString("emblem");
    }

    public static void setModel(ItemStack stack, String modelTag) {
        NBTTagCompound nbt = InvTools.getItemData(stack);
        nbt.setString("model", modelTag);
    }

    public static String getModel(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null || !nbt.hasKey("model"))
            return "default";
        return nbt.getString("model");
    }

    public static EnumColor getPrimaryColor(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        return EnumColor.readFromNBT(nbt, "primaryColor").orElse(((ItemLocomotive) stack.getItem()).defaultPrimary);
    }

    public static EnumColor getSecondaryColor(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        return EnumColor.readFromNBT(nbt, "secondaryColor").orElse(((ItemLocomotive) stack.getItem()).defaultSecondary);
    }

}

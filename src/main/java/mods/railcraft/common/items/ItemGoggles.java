/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.plugins.forge.*;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemGoggles extends ItemRailcraftArmor {
    private static final String TEXTURE = RailcraftConstants.ARMOR_TEXTURE_FOLDER + "goggles.png";

    public ItemGoggles() {
        super(ItemMaterials.GOGGLES, 0, EntityEquipmentSlot.HEAD);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    public static GoggleAura getCurrentAura(@Nullable ItemStack goggles) {
        GoggleAura aura = GoggleAura.NONE;
        if (goggles != null && goggles.getItem() instanceof ItemGoggles) {
            NBTTagCompound data = goggles.getTagCompound();
            if (data != null)
                aura = GoggleAura.VALUES[data.getByte("aura")];
        }
        return aura;
    }

    public static void incrementAura(@Nullable ItemStack goggles) {
        if (goggles != null && goggles.getItem() instanceof ItemGoggles) {
            NBTTagCompound data = goggles.getTagCompound();
            if (data == null) {
                data = new NBTTagCompound();
                goggles.setTagCompound(data);
            }
            byte aura = data.getByte("aura");
            aura++;
            if (aura >= GoggleAura.VALUES.length)
                aura = 0;
            data.setByte("aura", aura);

            if (getCurrentAura(goggles) == GoggleAura.TRACKING && !RailcraftConfig.isTrackingAuraEnabled())
                incrementAura(goggles);
        }
    }

    @Nullable
    public static ItemStack getGoggles(@Nullable EntityPlayer player) {
        if (player == null)
            return null;
        ItemStack helm = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (!InvTools.isEmpty(helm) && helm.getItem() instanceof ItemGoggles)
            return helm;
        return null;
    }

    public static boolean isPlayerWearing(EntityPlayer player) {
        ItemStack helm = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        return !InvTools.isEmpty(helm) && helm.getItem() instanceof ItemGoggles;
    }

//    @Override
//    public void initializeDefinintion() {
//        BlockHidden.registerBlock();
//        if (BlockHidden.getBlock() != null && RailcraftConfig.isTrackingAuraEnabled())
//            MinecraftForge.EVENT_BUS.register(new TrailTicker());
//    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new ItemStack(this), true,
                "GCG",
                "I I",
                "LLL",
                'C', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.RECEIVER,
                'I', "ingotSteel",
                'L', Items.LEATHER,
                'G', "paneGlassColorless");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        incrementAura(stack);
        if (Game.isClient(world)) {
            GoggleAura aura = getCurrentAura(stack);
            ChatPlugin.sendLocalizedHotBarMessageFromServer(player, "gui.railcraft.goggles.mode", "\u00A75" + aura);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack.copy());
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return TEXTURE;
    }

    @Override
    public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {
        return OreDictPlugin.isOreType("ingotSteel", stack);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean adv) {
        GoggleAura aura = getCurrentAura(stack);
        String mode = LocalizationPlugin.translate("gui.railcraft.goggles.mode");
        String tip = LocalizationPlugin.translate("gui.railcraft.goggles.tips");

        list.add(String.format(mode, "\u00A75" + aura));
        list.add(tip);
    }

    @Override
    public Object getRecipeObject(@Nullable IVariantEnum meta) {
        return this;
    }

    public enum GoggleAura {

        NONE("gui.railcraft.goggles.aura.none"),
        TRACKING("gui.railcraft.goggles.aura.tracking"),
        TUNING("gui.railcraft.goggles.aura.tuning"),
        SHUNTING("gui.railcraft.goggles.aura.shunting"),
        SIGNALLING("gui.railcraft.goggles.aura.signalling"),
        SURVEYING("gui.railcraft.goggles.aura.surveying"),
        WORLDSPIKE("gui.railcraft.goggles.aura.worldspike"),;
        public static final GoggleAura[] VALUES = values();
        private final String locTag;

        GoggleAura(String locTag) {
            this.locTag = locTag;
        }

        @Override
        public String toString() {
            return LocalizationPlugin.translate(locTag);
        }

    }
}

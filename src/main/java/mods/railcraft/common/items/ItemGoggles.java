/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
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
        if (helm != null && helm.getItem() instanceof ItemGoggles)
            return helm;
        return null;
    }

    public static boolean isPlayerWearing(EntityPlayer player) {
        ItemStack helm = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        return helm != null && helm.getItem() instanceof ItemGoggles;
    }

    @Override
    public void initializeDefinintion() {
//        BlockHidden.registerBlock();
//        if (BlockHidden.getBlock() != null && RailcraftConfig.isTrackingAuraEnabled())
//            MinecraftForge.EVENT_BUS.register(new TrailTicker());

        LootPlugin.addLoot(RailcraftItems.GOGGLES, 1, 1, LootPlugin.Type.WORKSHOP);
    }

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
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        incrementAura(stack);
        if (Game.isClient(world)) {
            GoggleAura aura = getCurrentAura(stack);
            ChatPlugin.sendLocalizedChat(player, "railcraft.gui.goggles.mode", "\u00A75" + aura);
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
        String mode = LocalizationPlugin.translate("railcraft.gui.goggles.mode");
        String tip = LocalizationPlugin.translate("railcraft.gui.goggles.tips");

        list.add(String.format(mode, "\u00A75" + aura));
        list.add(tip);
    }

    @Override
    public Object getRecipeObject(@Nullable IVariantEnum meta) {
        return this;
    }

    public enum GoggleAura {

        NONE("railcraft.gui.goggles.aura.none"),
        ANCHOR("railcraft.gui.goggles.aura.anchor"),
        TRACKING("railcraft.gui.goggles.aura.tracking"),
        TUNING("railcraft.gui.goggles.aura.tuning"),
        SURVEYING("railcraft.gui.goggles.aura.surveying"),
        SIGNALLING("railcraft.gui.goggles.aura.signalling");
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

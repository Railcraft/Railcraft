/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.common.blocks.hidden.BlockHidden;
import mods.railcraft.common.blocks.hidden.TrailTicker;
import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.core.IVariantEnum;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.plugins.forge.*;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemGoggles extends ItemArmor implements IRailcraftObject {
    private static final String TEXTURE = RailcraftConstants.ARMOR_TEXTURE_FOLDER + "goggles.png";

    public ItemGoggles() {
        super(ItemMaterials.GOGGLES, 0, 0);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    public static GoggleAura getCurrentAura(ItemStack goggles) {
        GoggleAura aura = GoggleAura.NONE;
        if (goggles != null && goggles.getItem() instanceof ItemGoggles) {
            NBTTagCompound data = goggles.getTagCompound();
            if (data != null)
                aura = GoggleAura.VALUES[data.getByte("aura")];
        }
        return aura;
    }

    public static void incrementAura(ItemStack goggles) {
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

    public static ItemStack getGoggles(EntityPlayer player) {
        if (player == null)
            return null;
        ItemStack helm = player.getCurrentArmor(MiscTools.ArmorSlots.HELM.ordinal());
        if (helm != null && helm.getItem() instanceof ItemGoggles)
            return helm;
        return null;
    }

    public static boolean isPlayerWearing(EntityPlayer player) {
        ItemStack helm = player.getCurrentArmor(MiscTools.ArmorSlots.HELM.ordinal());
        return helm != null && helm.getItem() instanceof ItemGoggles;
    }

    @Override
    public void initializeDefinintion() {
        BlockHidden.registerBlock();
        if (BlockHidden.getBlock() != null && RailcraftConfig.isTrackingAuraEnabled())
            MinecraftForge.EVENT_BUS.register(new TrailTicker());

        LootPlugin.addLoot(RailcraftItems.goggles, 1, 1, LootPlugin.Type.WORKSHOP);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new ItemStack(this), true,
                "GCG",
                "I I",
                "LLL",
                'C', RailcraftItems.circuit, ItemCircuit.EnumCircuit.RECEIVER,
                'I', "ingotSteel",
                'L', Items.LEATHER,
                'G', "paneGlassColorless");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        incrementAura(stack);
        if (Game.isNotHost(world)) {
            GoggleAura aura = getCurrentAura(stack);
            ChatPlugin.sendLocalizedChat(player, "railcraft.gui.goggles.mode", "\u00A75" + aura);
        }
        return stack.copy();
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return TEXTURE;
    }

    @Override
    public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {
        return OreDictPlugin.isOreType("ingotSteel", stack);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean adv) {
        NBTTagCompound data = stack.getTagCompound();
        GoggleAura aura = getCurrentAura(stack);
        String mode = LocalizationPlugin.translate("railcraft.gui.goggles.mode");
        String tip = LocalizationPlugin.translate("railcraft.gui.goggles.tip");

        list.add(String.format(mode, "\u00A75" + aura));
        list.add(tip);
    }

    @Override
    public Object getRecipeObject(IVariantEnum meta) {
        return this;
    }

    @Override
    public void finalizeDefinition() {
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

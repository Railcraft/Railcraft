/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.color;

import com.google.common.primitives.Ints;
import mods.railcraft.api.core.IRailcraftRecipeIngredient;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.material.MapColor;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum EnumColor implements IVariantEnum, IRailcraftRecipeIngredient {

    WHITE(0xF9FFFE, "dyeWhite", "white"),
    ORANGE(0xF9801D, "dyeOrange", "orange"),
    MAGENTA(0xC74EBD, "dyeMagenta", "magenta"),
    LIGHT_BLUE(0x3AB3DA, "dyeLightBlue", "light_blue", "lightBlue"),
    YELLOW(0xFED83D, "dyeYellow", "yellow"),
    LIME(0x80C71F, "dyeLime", "lime"),
    PINK(0xF38BAA, "dyePink", "pink"),
    GRAY(0x474F52, "dyeGray", "gray"),
    SILVER(0x9D9D97, "dyeLightGray", "light_gray", "silver", "lightGray"),
    CYAN(0x169C9C, "dyeCyan", "cyan"),
    PURPLE(0x8932B8, "dyePurple", "purple"),
    BLUE(0x3C44AA, "dyeBlue", "blue"),
    BROWN(0x835432, "dyeBrown", "brown"),
    GREEN(0x5E7C16, "dyeGreen", "green"),
    RED(0xB02E26, "dyeRed", "red"),
    BLACK(0x1D1D21, "dyeBlack", "black"),;
    public static final EnumColor[] VALUES = values();
    public static final EnumColor[] VALUES_INVERTED = values();
    public static final String DEFAULT_COLOR_TAG = "color";
    public static final Map<String, EnumColor> nameMap = new HashMap<>();

    static {
        ArrayUtils.reverse(VALUES_INVERTED);
        for (EnumColor color : VALUES) {
            for (String name : color.names) {
                nameMap.put(name, color);
            }
        }
    }

    private final int hexColor;
    private final String oreTagDyeName;
    private final String[] names;
    private List<ItemStack> dyes;

    EnumColor(int hexColor, String oreTagDyeName, String... names) {
        this.hexColor = hexColor;
        this.oreTagDyeName = oreTagDyeName;
        this.names = names;

    }

    public static EnumColor fromDye(EnumDyeColor dyeColor) {
        return fromOrdinal(dyeColor.getMetadata());
    }

    public static EnumColor fromOrdinal(int id) {
        if (id < 0 || id >= VALUES.length)
            return WHITE;
        return VALUES[id];
    }

    @Nullable
    public static EnumColor fromDyeOreDictTag(String dyeTag) {
        return Arrays.stream(VALUES).filter(color -> color.getDyeOreDictTag().equalsIgnoreCase(dyeTag)).findFirst().orElse(null);
    }

    public static EnumColor fromName(String name) {
        EnumColor color = nameMap.get(name);
        if (color == null)
            return WHITE;
        return color;
    }

    @Nullable
    public static EnumColor fromNameStrict(String name) {
        return nameMap.get(name.toLowerCase(Locale.ENGLISH));
    }

    public static EnumColor getRand() {
        return VALUES[MiscTools.RANDOM.nextInt(VALUES.length)];
    }

    public static EnumColor readFromNBT(@Nullable NBTTagCompound nbt, String tag) {
        if (nbt != null) {
            if (nbt.hasKey(tag, 8))
                return EnumColor.fromName(nbt.getString(tag));
            if (nbt.hasKey(tag, 1))
                return EnumColor.fromOrdinal(nbt.getByte(tag));
        }
        return EnumColor.WHITE;
    }

    @Contract("null->false")
    public static boolean isColored(@Nullable ItemStack stack) {
        if (InvTools.isEmpty(stack))
            return false;
        if (InvTools.isStackEqualToBlock(stack, Blocks.WOOL))
            return true;
        if (stack.getItem() == Items.DYE)
            return true;
        NBTTagCompound nbt = stack.getTagCompound();
        return nbt != null && nbt.hasKey(DEFAULT_COLOR_TAG);
    }

    public static EnumColor fromItemStack(@Nullable ItemStack stack) {
        if (InvTools.isEmpty(stack))
            return EnumColor.WHITE;
        if (InvTools.isStackEqualToBlock(stack, Blocks.WOOL))
            return EnumColor.fromOrdinal(stack.getItemDamage());
        if (stack.getItem() == Items.DYE)
            return EnumColor.fromOrdinal(15 - stack.getItemDamage());
        NBTTagCompound nbt = stack.getTagCompound();
        return EnumColor.readFromNBT(nbt, DEFAULT_COLOR_TAG);
    }

    @Contract("null -> null; !null -> _")
    @Nullable
    public static EnumColor dyeColorOf(@Nullable ItemStack stack) {
        if (InvTools.isEmpty(stack))
            return null;
        int[] ids = OreDictionary.getOreIDs(stack);
        for (EnumColor color : VALUES) {
            if (Ints.contains(ids, OreDictionary.getOreID(color.oreTagDyeName))) {
                return color;
            }
        }
        return null;
    }

    public EnumDyeColor getDye() {
        return EnumDyeColor.byMetadata(ordinal());
    }

    public MapColor getMapColor() {
        return MapColor.getBlockColor(getDye());
    }

    public int getHexColor() {
        return hexColor;
    }

    public EnumColor next() {
        return VALUES[(ordinal() + 1) % VALUES.length];
    }

    public EnumColor previous() {
        return VALUES[(ordinal() + VALUES.length - 1) % VALUES.length];
    }

    public EnumColor inverse() {
        return EnumColor.VALUES[15 - ordinal()];
    }

    public String getTag() {
        return "color." + getBaseTag();
    }

    public String getBaseTag() {
        return getName();
    }

    public String getTranslatedName() {
        return LocalizationPlugin.translate("railcraft." + getTag());
    }

    public String getDyeOreDictTag() {
        return oreTagDyeName;
    }

    public void writeToNBT(NBTTagCompound nbt, String tag) {
        nbt.setString(tag, getName());
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getName() {
        return names[0];
    }

    public ItemStack setItemColor(ItemStack stack) {
        return setItemColor(stack, DEFAULT_COLOR_TAG);
    }

    public ItemStack setItemColor(ItemStack stack, String tag) {
        if (InvTools.isEmpty(stack))
            return InvTools.emptyStack();
        NBTTagCompound nbt = InvTools.getItemData(stack);
        writeToNBT(nbt, tag);
        return stack;
    }

    public boolean isEqual(@Nullable EnumDyeColor dye) {
        return dye != null && getDye() == dye;
    }

    @Nullable
    @Override
    public Object getAlternate(IRailcraftRecipeIngredient container) {
        return null;
    }

    public List<ItemStack> getDyesStacks() {
        if (dyes == null) {
            dyes = new ArrayList<>();
            dyes.addAll(OreDictionary.getOres(getDyeOreDictTag()));
        }
        return dyes;
    }

    @Nullable
    @Override
    public Object getRecipeObject() {
        return getDyeOreDictTag();
    }
}

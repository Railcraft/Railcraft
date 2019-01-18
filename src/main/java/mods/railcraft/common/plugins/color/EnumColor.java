/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.color;

import com.google.common.primitives.Ints;
import mods.railcraft.api.core.IIngredientSource;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.BlockRailcraftSubtyped;
import mods.railcraft.common.blocks.interfaces.IBlockColored;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.util.crafting.Ingredients;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.BlockColored;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum EnumColor implements IVariantEnum, IIngredientSource {

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
    BLACK(0x1D1D21, "dyeBlack", "black"),
    ;
    public static final EnumColor[] VALUES = values();
    public static final EnumColor[] VALUES_INVERTED = values();
    public static final String DEFAULT_COLOR_TAG = "color";
    public static final Map<String, EnumColor> nameMap = new HashMap<>();
    public static final PropertyEnum<EnumColor> PROPERTY = PropertyEnum.create("color", EnumColor.class);

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

    public static @Nullable EnumColor fromDyeOreDictTag(String dyeTag) {
        return Arrays.stream(VALUES).filter(color -> color.getDyeOreDictTag().equalsIgnoreCase(dyeTag)).findFirst().orElse(null);
    }

    public static EnumColor fromName(String name) {
        EnumColor color = nameMap.get(name);
        if (color == null)
            return WHITE;
        return color;
    }

    public static @Nullable EnumColor fromNameStrict(String name) {
        return nameMap.get(name.toLowerCase(Locale.ENGLISH));
    }

    public static EnumColor random() {
        return VALUES[MiscTools.RANDOM.nextInt(VALUES.length)];
    }

    /**
     * Returns true if the item has color information.
     */
    public static boolean isColored(ItemStack stack) {
        if (InvTools.isEmpty(stack))
            return false;
        IBlockState state = InvTools.getBlockStateFromStack(stack);
        if (state.getBlock() instanceof BlockColored)
            return true;
        if (state.getBlock() instanceof IBlockColored)
            return true;
        if (stack.getItem() == Items.DYE)
            return true;
        if (dyeColorOf(stack).isPresent())
            return true;
        NBTTagCompound nbt = stack.getTagCompound();
        return nbt != null && nbt.hasKey(DEFAULT_COLOR_TAG);
    }

    /**
     * Returns the color of an ItemStack.
     *
     * It can be wool, clay, concrete, dye, or dye substitutes.
     *
     * @return The color or {@link EnumColor#WHITE} if no color associated with the item.
     */
    public static Optional<EnumColor> fromItemStack(ItemStack stack) {
        if (InvTools.isEmpty(stack))
            return Optional.empty();
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.hasKey(DEFAULT_COLOR_TAG))
            return EnumColor.readFromNBT(nbt, DEFAULT_COLOR_TAG);
        IBlockState state = InvTools.getBlockStateFromStack(stack);
        if (state.getBlock() instanceof BlockColored)
            return Optional.of(fromDye(state.getValue(BlockColored.COLOR)));
        if (state.getBlock() instanceof IBlockColored)
            return Optional.of(((IBlockColored) state.getBlock()).getColor(state));
        if (state.getBlock() instanceof BlockRailcraftSubtyped)
            if (stack.getItem() == Items.DYE)
                return Optional.of(EnumColor.fromOrdinal(15 - stack.getItemDamage()));
        return dyeColorOf(stack);
    }

    /**
     * If the item can be used as dye, return the dye color.
     *
     * @return The color or null if not a dye item.
     */
    public static Optional<EnumColor> dyeColorOf(ItemStack stack) {
        if (InvTools.isEmpty(stack))
            return Optional.empty();
        int[] ids = OreDictionary.getOreIDs(stack);
        return Arrays.stream(VALUES)
                .filter(color -> Ints.contains(ids, OreDictionary.getOreID(color.oreTagDyeName)))
                .findAny();
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

    @Override
    public String getOreTag() {
        return getDyeOreDictTag();
    }

    public void writeToNBT(NBTTagCompound nbt) {
        writeToNBT(nbt, DEFAULT_COLOR_TAG);
    }

    public void writeToNBT(NBTTagCompound nbt, String tag) {
        nbt.setString(tag, getName());
    }

    public static Optional<EnumColor> readFromNBT(@Nullable NBTTagCompound nbt) {
        return readFromNBT(nbt, DEFAULT_COLOR_TAG);
    }

    public static Optional<EnumColor> readFromNBT(@Nullable NBTTagCompound nbt, String tag) {
        if (nbt != null) {
            if (nbt.hasKey(tag, NBTPlugin.EnumNBTType.STRING.ordinal()))
                return Optional.of(EnumColor.fromName(nbt.getString(tag)));
            if (nbt.hasKey(tag, NBTPlugin.EnumNBTType.BYTE.ordinal()))
                return Optional.of(EnumColor.fromOrdinal(nbt.getByte(tag)));
        }
        return Optional.empty();
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

    public List<ItemStack> getDyesStacks() {
        if (dyes == null) {
            dyes = new ArrayList<>();
            dyes.addAll(OreDictionary.getOres(getDyeOreDictTag()));
        }
        return dyes;
    }

    @Override
    public Ingredient getIngredient() {
        return Ingredients.from(getDyeOreDictTag());
    }
}

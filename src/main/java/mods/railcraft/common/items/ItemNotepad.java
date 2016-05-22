package mods.railcraft.common.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.blocks.machine.gamma.TileLoaderBase;
import mods.railcraft.common.blocks.machine.gamma.TileLoaderFluidBase;
import mods.railcraft.common.blocks.machine.gamma.TileLoaderItemBase;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.PhantomInventory;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * An item for copying settings from one block to another.
 *
 * It could use some kind of generic interface perhaps.
 *
 * Created by Forecaster on 09/05/2016 for the Railcraft project.
 */
public class ItemNotepad extends ItemRailcraft {
    public static ItemNotepad item;
    private static IIcon itemIconFull;

    public ItemNotepad() {
        setMaxStackSize(1);
        setMaxDamage(50);
    }

    public static void registerItem() {
        if (item == null) {
            String tag = "railcraft.tool.notepad";
            if (RailcraftConfig.isItemEnabled(tag)) {
                item = new ItemNotepad();
                item.setUnlocalizedName(tag);
                RailcraftRegistry.register(item);
            }
        }
    }

    private static void setPasteMode(ItemStack stack, PasteMode mode) {
        if (stack != null && stack.getItem() instanceof ItemNotepad) {
            NBTTagCompound nbt = InvTools.getItemDataRailcraft(stack);
            nbt.setByte("pasteMode", (byte) mode.ordinal());
        }
    }

    private static PasteMode getPasteMode(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemNotepad) {
            NBTTagCompound nbt = InvTools.getItemDataRailcraft(stack);
            return PasteMode.fromOrdinal(nbt.getByte("pasteMode"));
        }
        return PasteMode.ALL;
    }

    private static PasteMode nextPasteMode(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemNotepad) {
            PasteMode pasteMode = getPasteMode(stack);
            pasteMode = pasteMode.next();
            setPasteMode(stack, pasteMode);
            return pasteMode;
        }
        return PasteMode.ALL;
    }

    private static void setContents(ItemStack stack, EnumMap<Contents, NBTTagCompound> contents) {
        if (stack != null && stack.getItem() instanceof ItemNotepad) {

            NBTTagCompound contentTag = new NBTTagCompound();
            for (Map.Entry<Contents, NBTTagCompound> entry : contents.entrySet()) {
                contentTag.setTag(entry.getKey().nbtTag, entry.getValue());
            }

            NBTTagCompound nbt = InvTools.getItemDataRailcraft(stack);
            nbt.setTag("contents", contentTag);
        }
    }

    @Nonnull
    private static EnumMap<Contents, NBTTagCompound> getContents(ItemStack stack) {
        EnumMap<Contents, NBTTagCompound> contents = new EnumMap<Contents, NBTTagCompound>(Contents.class);
        if (stack != null && stack.getItem() instanceof ItemNotepad) {
            NBTTagCompound nbt = InvTools.getItemDataRailcraft(stack);
            if (nbt.hasKey("contents")) {
                nbt = nbt.getCompoundTag("contents");
                for (Contents content : Contents.VALUES) {
                    if (nbt.hasKey(content.nbtTag)) {
                        contents.put(content, nbt.getCompoundTag(content.nbtTag));
                    }
                }
            }
        }
        return contents;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon("railcraft:tool.notepad.empty");
        itemIconFull = iconRegister.registerIcon("railcraft:tool.notepad.filled");
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean adv) {
        String contentString;
        EnumMap<Contents, NBTTagCompound> contents = getContents(stack);
        if (contents.isEmpty()) {
            contentString = LocalizationPlugin.translate("item.railcraft.tool.notepad.tip.contents.empty");
        } else {
            List<String> contentTypes = new ArrayList<String>();
            for (Contents content : contents.keySet()) {
                contentTypes.add(LocalizationPlugin.translate(content.locTag));
            }
            contentString = StringUtils.join(contentTypes, ", ");
        }
        info.add(LocalizationPlugin.translate("item.railcraft.tool.notepad.tip.contents", contentString));

        PasteMode pasteMode = getPasteMode(stack);
        info.add(LocalizationPlugin.translate("item.railcraft.tool.notepad.tip.mode", EnumChatFormatting.DARK_PURPLE + pasteMode.toString()));

        super.addInformation(stack, player, info, adv);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        NBTTagCompound nbt = InvTools.getItemDataRailcraft(stack);

        if (player.isSneaking()) {
            nbt.removeTag("contents");
        } else {
            PasteMode pasteMode = nextPasteMode(stack);
            if (Game.isNotHost(world))
                ChatPlugin.sendLocalizedChatFromClient(player, "item.railcraft.tool.notepad.tip.mode", EnumChatFormatting.DARK_PURPLE + pasteMode.toString());
        }
        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        //Dunno why this is needed, but without this override it stops working properly.
        return true;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (Game.isHost(world)) {
            if (player.isSneaking()) // COPY
            {
                EnumMap<Contents, NBTTagCompound> contents = new EnumMap<Contents, NBTTagCompound>(Contents.class);
                for (Contents contentType : Contents.VALUES) {
                    NBTTagCompound data = contentType.copy(tileEntity);
                    if (data != null)
                        contents.put(contentType, data);
                }
                if (contents.isEmpty()) {
                    //TODO: Fix in 1.8 to use getDisplayName
                    ChatPlugin.sendLocalizedChatFromServer(player, "item.railcraft.tool.notepad.action.copy.fail");
                } else {
                    setContents(stack, contents);
                    //TODO: Fix in 1.8 to use getDisplayName
                    if (tileEntity instanceof RailcraftTileEntity)
                        ChatPlugin.sendLocalizedChatFromServer(player, "item.railcraft.tool.notepad.action.copy", ((RailcraftTileEntity) tileEntity).getLocalizationTag());
                    player.getCurrentEquippedItem().damageItem(1, player);
                }
            } else // PASTE
            {
                EnumMap<Contents, NBTTagCompound> contents = getContents(stack);
                if (contents.isEmpty()) {
                    ChatPlugin.sendLocalizedChatFromServer(player, "item.railcraft.tool.notepad.action.empty");
                } else {
                    PasteMode pasteMode = getPasteMode(stack);
                    boolean pasted = false;
                    for (Map.Entry<Contents, NBTTagCompound> entry : getContents(stack).entrySet()) {
                        if (pasteMode.allows(entry.getKey()))
                            pasted |= entry.getKey().paste(tileEntity, entry.getValue());
                    }
                    if (pasted) {
                        //TODO: Fix in 1.8 to use getDisplayName
                        if (tileEntity instanceof RailcraftTileEntity)
                            ChatPlugin.sendLocalizedChatFromServer(player, "item.railcraft.tool.notepad.action.paste", ((RailcraftTileEntity) tileEntity).getLocalizationTag());
                    } else
                        //TODO: Fix in 1.8 to use getDisplayName
                        ChatPlugin.sendLocalizedChatFromServer(player, "item.railcraft.tool.notepad.action.paste.fail");
                }
            }
        }
        return !world.isRemote;
    }

    @Override
    public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {
        return true;
    }

    @Override
    public IIcon getIconIndex(ItemStack stack) {
        NBTTagCompound tag = InvTools.getItemDataRailcraft(stack);
        if (tag.hasKey("contents")) {
            return itemIconFull;
        }
        return itemIcon;
    }

    @Override
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
        return getIcon(stack, renderPass);
    }

    @Override
    public IIcon getIcon(ItemStack stack, int renderPass) {
        return getIconIndex(stack);
    }

    private enum Contents {
        FILTER_CART("filter.cart", "item.railcraft.tool.notepad.tip.contents.filter.cart") {
            @Override
            NBTTagCompound copy(Object target) {
                if (target instanceof TileLoaderBase) {
                    PhantomInventory cartFilters = ((TileLoaderBase) target).getCartFilters();
                    NBTTagCompound nbt = new NBTTagCompound();
                    cartFilters.writeToNBT("inv", nbt);
                    return nbt;
                }
                return null;
            }

            @Override
            boolean paste(Object target, NBTTagCompound nbt) {
                if (target instanceof TileLoaderBase) {
                    ((TileLoaderBase) target).getCartFilters().readFromNBT("inv", nbt);
                    return true;
                }
                return false;
            }
        },
        FILTER_ITEMS("filter.items", "item.railcraft.tool.notepad.tip.contents.filter.items") {
            @Override
            NBTTagCompound copy(Object target) {
                if (target instanceof TileLoaderItemBase) {
                    PhantomInventory itemFilters = ((TileLoaderItemBase) target).getItemFilters();
                    NBTTagCompound nbt = new NBTTagCompound();
                    itemFilters.writeToNBT("inv", nbt);
                    return nbt;
                }
                return null;
            }

            @Override
            boolean paste(Object target, NBTTagCompound nbt) {
                if (target instanceof TileLoaderItemBase) {
                    ((TileLoaderItemBase) target).getItemFilters().readFromNBT("inv", nbt);
                    return true;
                }
                return false;
            }
        },
        FILTER_FLUID("filter.fluid", "item.railcraft.tool.notepad.tip.contents.filter.fluid") {
            @Override
            NBTTagCompound copy(Object target) {
                if (target instanceof TileLoaderFluidBase) {
                    PhantomInventory itemFilters = ((TileLoaderFluidBase) target).getFluidFilter();
                    NBTTagCompound nbt = new NBTTagCompound();
                    itemFilters.writeToNBT("inv", nbt);
                    return nbt;
                }
                return null;
            }

            @Override
            boolean paste(Object target, NBTTagCompound nbt) {
                if (target instanceof TileLoaderFluidBase) {
                    ((TileLoaderFluidBase) target).getFluidFilter().readFromNBT("inv", nbt);
                    return true;
                }
                return false;
            }
        };
        public static final Contents[] VALUES = values();
        private final String nbtTag;
        private final String locTag;

        Contents(String nbtTag, String locTag) {
            this.nbtTag = nbtTag;
            this.locTag = locTag;
        }

        @Nullable
        abstract NBTTagCompound copy(Object target);

        abstract boolean paste(Object target, NBTTagCompound nbt);
    }

    private enum PasteMode {
        ALL("item.railcraft.tool.notepad.tip.mode.all", Contents.VALUES),
        CART_FILTER("item.railcraft.tool.notepad.tip.mode.cart.filter", Contents.FILTER_CART),
        LOADER_FILTERS("item.railcraft.tool.notepad.tip.mode.loader.filters", Contents.FILTER_ITEMS, Contents.FILTER_ITEMS);
        public static final PasteMode[] VALUES = values();
        private final String locTag;
        private final EnumSet<Contents> allows;

        PasteMode(String locTag, Contents... allows) {
            this.locTag = locTag;
            this.allows = EnumSet.copyOf(Arrays.asList(allows));
        }

        public static PasteMode fromOrdinal(int id) {
            if (id < 0 || id >= VALUES.length)
                return ALL;
            return VALUES[id];
        }

        public boolean allows(Contents content) {
            return allows.contains(content);
        }

        @Override
        public String toString() {
            return LocalizationPlugin.translate(locTag);
        }

        public PasteMode next() {
            return VALUES[(ordinal() + 1) % VALUES.length];
        }

//        public PasteMode previous() {
//            return VALUES[(ordinal() + VALUES.length - 1) % VALUES.length];
//        }

    }

}

package mods.railcraft.common.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.blocks.machine.gamma.TileLoaderBase;
import mods.railcraft.common.blocks.machine.gamma.TileLoaderFluidBase;
import mods.railcraft.common.blocks.machine.gamma.TileLoaderItemBase;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.tooltips.ToolTip;
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
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;

/**
 * An item for copying settings from one block to another.
 *
 * It could use some kind of generic interface perhaps.
 *
 * Created by Forecaster on 09/05/2016 for the Railcraft project.
 */
public class ItemTemplateTool extends ItemRailcraft {
    public static ItemTemplateTool item;
    private static IIcon itemIcon2;

    public ItemTemplateTool() {
        setMaxStackSize(1);
        setMaxDamage(50);
    }

    public static void registerItem() {
        if (item == null) {
            String tag = "railcraft.tool.notepad";
            if (RailcraftConfig.isItemEnabled(tag)) {
                item = new ItemTemplateTool();
                item.setUnlocalizedName(tag);
                RailcraftRegistry.register(item);
            }
        }
    }

    private static void setPasteMode(ItemStack stack, PasteMode mode) {
        if (stack != null && stack.getItem() instanceof ItemTemplateTool) {
            NBTTagCompound nbt = InvTools.getItemDataRailcraft(stack);
            nbt.setByte("pasteMode", (byte) mode.ordinal());
        }
    }

    private static PasteMode getPasteMode(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemTemplateTool) {
            NBTTagCompound nbt = InvTools.getItemDataRailcraft(stack);
            return PasteMode.fromOrdinal(nbt.getByte("pasteMode"));
        }
        return PasteMode.ALL;
    }

    private static PasteMode nextPasteMode(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemTemplateTool) {
            PasteMode pasteMode = getPasteMode(stack);
            pasteMode = pasteMode.next();
            setPasteMode(stack, pasteMode);
            return pasteMode;
        }
        return PasteMode.ALL;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon("railcraft:tool.notepad.empty");
        itemIcon2 = iconRegister.registerIcon("railcraft:tool.notepad.filled");
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean adv) {
        String storedFilters;
        PasteMode pasteMode = getPasteMode(stack);
        NBTTagCompound tag = InvTools.getItemDataRailcraft(stack);
        if (tag.hasKey("tileEntityType")) {
            if (tag.getString("tileEntityType").equals("item"))
                storedFilters = "item.railcraft.tool.notepad.tip.contents0";
            else if (tag.getString("tileEntityType").equals("fluid"))
                storedFilters = "item.railcraft.tool.notepad.tip.contents1";
            else
                storedFilters = "item.railcraft.tool.notepad.tip.contents2";
        } else
            storedFilters = "§4Empty";

        ToolTip tooltip = ToolTip.buildToolTip("item.railcraft.tool.notepad.tip");
        if (tooltip != null)
            info.addAll(tooltip.convertToStrings());
        info.add(LocalizationPlugin.translate(storedFilters));
        info.add(LocalizationPlugin.translate("item.railcraft.tool.notepad.tip.mode", pasteMode));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        NBTTagCompound nbt = InvTools.getItemDataRailcraft(stack);

        if (player.isSneaking()) {
            if (nbt.hasKey("cartFilters"))
                nbt.removeTag("cartFilters");
            if (nbt.hasKey("tileEntityType"))
                nbt.removeTag("tileEntityType");
            if (nbt.hasKey("itemFilters"))
                nbt.removeTag("itemFilters");
            if (nbt.hasKey("fluidFilters"))
                nbt.removeTag("fluidFilters");
        } else {
            PasteMode pasteMode = nextPasteMode(stack);
            if (Game.isNotHost(world))
                ChatPlugin.sendLocalizedChatFromClient(player, "item.railcraft.tool.notepad.tip.mode", "\u00A75" + pasteMode);
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
        if (tileEntity instanceof TileLoaderBase) {
            if (Game.isHost(world)) {
                NBTTagCompound nbt = InvTools.getItemDataRailcraft(stack);
                if (player.isSneaking()) // COPY
                {
                    PhantomInventory cartFilters = ((TileLoaderBase) tileEntity).getCartFilters();
                    cartFilters.writeToNBT("cartFilters", nbt);
                    if (tileEntity instanceof TileLoaderItemBase) {
                        PhantomInventory itemFilters = ((TileLoaderItemBase) tileEntity).getItemFilters();
                        itemFilters.writeToNBT("itemFilters", nbt);
                        nbt.setString("tileEntityType", "item");
                        if (nbt.hasKey("fluidFilters"))
                            nbt.removeTag("fluidFilters");
                    } else if (tileEntity instanceof TileLoaderFluidBase) {
                        PhantomInventory fluidFilters = ((TileLoaderFluidBase) tileEntity).getFluidFilter();
                        fluidFilters.writeToNBT("fluidFilters", nbt);
                        nbt.setString("tileEntityType", "fluid");
                        if (nbt.hasKey("itemFilters"))
                            nbt.removeTag("itemFilters");
                    }
                    ChatPlugin.sendLocalizedChatFromServer(player, "item.railcraft.tool.notepad.action.copy", ((RailcraftTileEntity) tileEntity).getLocalizationTag());
//                    player.getCurrentEquippedItem().damageItem(1, player);
                } else // PASTE
                {
                    PasteMode pasteMode = getPasteMode(stack);
                    boolean pasted = false;
                    if (nbt.hasKey("cartFilters") && pasteMode.allows(PasteMode.CART_FILTER)) {
                        ((TileLoaderBase) tileEntity).getCartFilters().readFromNBT("cartFilters", nbt);
                        pasted = true;
                    }
                    if (pasteMode.allows(PasteMode.LOADER_TEMPLATE)) {
                        if (nbt.hasKey("itemFilters") && tileEntity instanceof TileLoaderItemBase) {
                            ((TileLoaderItemBase) tileEntity).getItemFilters().readFromNBT("itemFilters", nbt);
                            pasted = true;
                        } else if (nbt.hasKey("fluidFilters") && tileEntity instanceof TileLoaderFluidBase) {
                            ((TileLoaderFluidBase) tileEntity).getFluidFilter().readFromNBT("fluidFilters", nbt);
                            pasted = true;
                        }
                    }
                    if (pasted) {
                        player.getCurrentEquippedItem().damageItem(1, player);
                        ChatPlugin.sendLocalizedChatFromServer(player, "item.railcraft.tool.notepad.action.paste", ((RailcraftTileEntity) tileEntity).getLocalizationTag());
                    } else
                        ChatPlugin.sendLocalizedChatFromServer(player, "item.railcraft.tool.notepad.action.empty");

                }
            }
            return !world.isRemote;
        }
        return false;
    }

    @Override
    public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {
        return true;
    }

    @Override
    public IIcon getIconIndex(ItemStack stack) {
        NBTTagCompound tag = InvTools.getItemDataRailcraft(stack);
        if (tag.hasKey("tileEntityType")) {
            return itemIcon2;
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

    private enum PasteMode {
        ALL("item.railcraft.tool.notepad.tip.mode.all"),
        CART_FILTER("item.railcraft.tool.notepad.tip.mode.cart.filter"),
        LOADER_TEMPLATE("item.railcraft.tool.notepad.tip.mode.loader.template");
        public static final PasteMode[] VALUES = values();
        private final String locTag;

        PasteMode(String locTag) {
            this.locTag = locTag;
        }

        public static PasteMode fromOrdinal(int id) {
            if (id < 0 || id >= VALUES.length)
                return ALL;
            return VALUES[id];
        }

        public boolean allows(PasteMode mode) {
            return this == mode || this == ALL;
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

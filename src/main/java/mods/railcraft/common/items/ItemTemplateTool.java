package mods.railcraft.common.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.common.blocks.machine.gamma.TileLoaderBase;
import mods.railcraft.common.blocks.machine.gamma.TileLoaderFluidBase;
import mods.railcraft.common.blocks.machine.gamma.TileLoaderItemBase;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.inventory.PhantomInventory;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import java.util.List;

/**
 * Created by Forecaster on 09/05/2016 for the Railcraft project.
 */
public class ItemTemplateTool extends ItemRailcraft
{
    public static ItemTemplateTool item;
    public static IIcon itemIcon2;
    private static int pasteModes = 3;

    public static void registerItem() {
        if (item == null) {
            String tag = "railcraft.tool.template.tool";
            if (RailcraftConfig.isItemEnabled(tag)) {
                item = new ItemTemplateTool();
                item.setUnlocalizedName(tag);
                item.setMaxStackSize(1);
                RailcraftRegistry.register(item);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon("railcraft:tool.template.tool.empty");
        itemIcon2 = iconRegister.registerIcon("railcraft:tool.template.tool.filled");
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean adv)
    {
        String storedFilters;
        int pasteMode = getPasteMode(stack);
        String pasteModeString;
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && tag.hasKey("tileEntityType"))
        {
            if (tag.getString("tileEntityType").equals("item"))
                storedFilters = "item.railcraft.tool.template.tool.tip.contents0";
            else if (tag.getString("tileEntityType").equals("fluid"))
                storedFilters = "item.railcraft.tool.template.tool.tip.contents1";
            else
                storedFilters = "item.railcraft.tool.template.tool.tip.contents2";
        }
        else
            storedFilters = "§4Empty";
        switch (pasteMode)
        {
            case (0): pasteModeString = "item.railcraft.tool.template.tool.tip.mode0"; break;
            case (1): pasteModeString = "item.railcraft.tool.template.tool.tip.mode1"; break;
            case (2): pasteModeString = "item.railcraft.tool.template.tool.tip.mode2"; break;
            default: pasteModeString = null;
        }
        List<String> tooltip = ToolTip.buildToolTip("item.railcraft.tool.template.tool.tip").convertToStrings();
        if (tooltip != null)
            info.addAll(tooltip);
        info.add(LocalizationPlugin.translate(storedFilters));
        if (pasteModeString != null)
            info.add(LocalizationPlugin.translate(pasteModeString));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (!world.isRemote)
        {
            NBTTagCompound compound = stack.getTagCompound();
            if (compound == null)
                compound = new NBTTagCompound();

            if (player.isSneaking())
            {
                if (compound.hasKey("cartFilters"))
                    compound.removeTag("cartFilters");
                if (compound.hasKey("tileEntityType"))
                    compound.removeTag("tileEntityType");
                if (compound.hasKey("itemFilters"))
                    compound.removeTag("itemFilters");
                if (compound.hasKey("fluidFilters"))
                    compound.removeTag("fluidFilters");
            }
            else
            {
                int newMode = nextPasteMode(stack);
                ChatPlugin.sendLocalizedChat(player, LocalizationPlugin.translate("item.railcraft.tool.template.tool.tip.mode" + newMode));
            }
        }
        return stack;
    }

    public int nextPasteMode(ItemStack stack)
    {
        int mode = 0;
        if (stack != null)
        {
            NBTTagCompound compound = stack.getTagCompound();
            if (compound == null)
                compound = new NBTTagCompound();
            if (compound.hasKey("pasteMode"))
            {
                mode = compound.getInteger("pasteMode");
                mode++;
                if (mode > 2) //0 all, 1 carts, 2 items/fluid
                    mode = 0;
                compound.setInteger("pasteMode", mode);
            }
            else
            {
                mode = 1;
                compound.setInteger("pasteMode", 1);
            }
            stack.setTagCompound(compound);
        }
        return mode;
    }

    public int getPasteMode(ItemStack stack)
    {
        if (stack != null)
        {
            NBTTagCompound compound = stack.getTagCompound();
            if (compound != null)
                if (compound.hasKey("pasteMode"))
                    return compound.getInteger("pasteMode");
        }
        return 0;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        //Dunno why this is needed, but without this override it stops working properly.
        return true;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote)
            return false;

        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if (tileEntity != null && tileEntity instanceof TileLoaderBase)
        {
            NBTTagCompound compound = stack.getTagCompound();
            if (player.isSneaking()) // COPY
            {
                if (compound == null)
                    compound = new NBTTagCompound();

                PhantomInventory cartFilters = ((TileLoaderBase) tileEntity).getCartFilters();
                cartFilters.writeToNBT("cartFilters", compound);
                if (tileEntity instanceof TileLoaderItemBase)
                {
                    PhantomInventory itemFilters = ((TileLoaderItemBase) tileEntity).getItemFilters();
                    itemFilters.writeToNBT("itemFilters", compound);
                    compound.setString("tileEntityType", "item");
                    if (compound.hasKey("fluidFilters"))
                        compound.removeTag("fluidFilters");
                    ChatPlugin.sendLocalizedChat(player, LocalizationPlugin.translate("item.railcraft.tool.template.tool.oncopy"), LocalizationPlugin.translate(((TileLoaderItemBase) tileEntity).getLocalizationTag()));
                }
                else if (tileEntity instanceof TileLoaderFluidBase)
                {
                    PhantomInventory fluidFilters = ((TileLoaderFluidBase) tileEntity).getFluidFilter();
                    fluidFilters.writeToNBT("fluidFilters", compound);
                    compound.setString("tileEntityType", "fluid");
                    if (compound.hasKey("itemFilters"))
                        compound.removeTag("itemFilters");
                    ChatPlugin.sendLocalizedChat(player, LocalizationPlugin.translate("item.railcraft.tool.template.tool.oncopy"), LocalizationPlugin.translate(((TileLoaderFluidBase) tileEntity).getLocalizationTag()));
                }
                stack.setTagCompound(compound);
            }
            else // PASTE
            {
                if (compound != null && compound.hasKey("cartFilters"))
                {
                    int pasteMode = getPasteMode(stack);
                    if (compound.hasKey("cartFilters") && (pasteMode == 0 || pasteMode == 1))
                    {
                        ((TileLoaderBase) tileEntity).getCartFilters().readFromNBT("cartFilters", compound);
                    }
                    if (compound.hasKey("itemFilters") && tileEntity instanceof TileLoaderItemBase && (pasteMode == 0 || pasteMode == 2))
                    {
                        ((TileLoaderItemBase) tileEntity).getItemFilters().readFromNBT("itemFilters", compound);
                        ChatPlugin.sendLocalizedChat(player, LocalizationPlugin.translate("item.railcraft.tool.template.tool.onpaste"), LocalizationPlugin.translate(((TileLoaderItemBase) tileEntity).getLocalizationTag()));
                    }
                    else if (compound.hasKey("fluidFilters") && tileEntity instanceof TileLoaderFluidBase && (pasteMode == 0 || pasteMode == 2))
                    {
                        ((TileLoaderFluidBase) tileEntity).getFluidFilter().readFromNBT("fluidFilters", compound);
                        ChatPlugin.sendLocalizedChat(player, LocalizationPlugin.translate("item.railcraft.tool.template.tool.onpaste"), LocalizationPlugin.translate(((TileLoaderFluidBase) tileEntity).getLocalizationTag()));
                    }
                }
                else
                    ChatPlugin.sendLocalizedChat(player, LocalizationPlugin.translate("item.railcraft.tool.template.tool.nodata"));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player)
    {
        return true;
    }

    @Override
    public IIcon getIconIndex(ItemStack stack)
    {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && tag.hasKey("tileEntityType"))
        {
            return itemIcon2;
        }
        return itemIcon;
    }

    @Override
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining)
    {
        return this.getIcon(stack, renderPass);
    }

    public IIcon getIcon(ItemStack stack, int renderPass)
    {
        return this.getIconIndex(stack);
    }


}

/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import com.google.common.base.Optional;
import mods.railcraft.common.plugins.forge.DataManagerPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketBuilder;
import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

import static mods.railcraft.common.util.inventory.InvTools.emptyStack;
import static mods.railcraft.common.util.inventory.InvTools.isEmpty;

/**
 * A simple Jukebox cart.
 */
public final class EntityCartJukebox extends CartBase {

    private static final DataParameter<Optional<ItemStack>> PLAYING_MUSIC = DataManagerPlugin.create(DataSerializers.OPTIONAL_ITEM_STACK);
    @Nullable
    private ItemStack record = emptyStack();

    public EntityCartJukebox(World world) {
        super(world);
    }

    public EntityCartJukebox(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    {
        dataManager.register(PLAYING_MUSIC, Optional.absent());
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.JUKEBOX;
    }

    @Override
    public boolean doInteract(EntityPlayer player) {
        if (Game.isHost(worldObj)) {
            if (!isEmpty(record)) {
                InvTools.spewItem(record, worldObj, posX, posY, posZ);
                record = emptyStack();
                dataManager.set(PLAYING_MUSIC, Optional.absent());
            }
            EnumHand hand = player.getActiveHand();
            ItemStack stack = player.getHeldItem(hand);
            if (isEmpty(stack) || !(stack.getItem() instanceof ItemRecord))
                return true;
            record = stack;
            if (!player.capabilities.isCreativeMode)
                player.setHeldItem(hand, emptyStack());
            player.addStat(StatList.RECORD_PLAYED);
            dataManager.set(PLAYING_MUSIC, Optional.of(record));
            SoundEvent sound = ReflectionHelper.getPrivateValue(ItemRecord.class, (ItemRecord) stack.getItem(), 1);
            PacketBuilder.instance().sendMovingSoundPacket(sound, SoundCategory.RECORDS, this, SoundHelper.MovingSoundType.RECORD);
        }
        return true;
    }

    public boolean isPlayingMusic() {
        return dataManager.get(PLAYING_MUSIC).isPresent();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (key == PLAYING_MUSIC) {
            Optional<ItemStack> itemStackOptional = dataManager.get(PLAYING_MUSIC);
            if (itemStackOptional.isPresent()) {
                Item item = itemStackOptional.get().getItem();
                if (item instanceof ItemRecord)
                    Minecraft.getMinecraft().ingameGUI.setRecordPlayingMessage(((ItemRecord) item).getRecordNameLocal());
            }
        }
    }

    @Override
    public IBlockState getDefaultDisplayTile() {
        return Blocks.JUKEBOX.getDefaultState().withProperty(BlockJukebox.HAS_RECORD, isPlayingMusic());
    }

    @Override
    public void killMinecart(DamageSource par1DamageSource) {
        super.killMinecart(par1DamageSource);
        if (!isEmpty(record))
            InvTools.spewItem(record, worldObj, posX, posY, posZ);
    }
}

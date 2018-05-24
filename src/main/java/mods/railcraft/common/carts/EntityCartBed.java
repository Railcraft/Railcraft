package mods.railcraft.common.carts;

import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 *
 */
public class EntityCartBed extends EntityCartBasic {

    private WeakReference<EntityPlayer> lastSleeper = new WeakReference<>(null);
    private boolean wokeUp = false;
    private boolean rideAfterSleep = false;
    private boolean shouldSleep = false;

    @SuppressWarnings("unused")
    public EntityCartBed(World world) {
        super(world);
    }

    @SuppressWarnings("unused")
    public EntityCartBed(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    {
        if (Game.isHost(world))
            MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.BED;
    }

    @Override
    public boolean canBeRidden() {
        return true;
    }

    @Override
    public IBlockState getDefaultDisplayTile() {
        return Blocks.CARPET.getDefaultState().withProperty(BlockCarpet.COLOR, EnumDyeColor.GRAY);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (Game.isClient(world)) {
            return;
        }

        EntityPlayer sleeper = lastSleeper.get();
        if (sleeper != null) {
            if (sleeper.getRidingEntity() != this)
                sleeper.startRiding(this, true);
            // Riding enforced! Otherwise player dismounts once sleeps
            if (rideAfterSleep)
                sendPlayerRiding((EntityPlayerMP) sleeper);
            if (!wokeUp) {
                sleeper.setPosition(getPosition().getX(), getPosition().getY(), getPosition().getZ());
                return;
            }
            wokeUp = false;
            lastSleeper = new WeakReference<>(null);
        }

        Entity rider = getFirstPassenger();
        if (!(rider instanceof EntityPlayer))
            return;
        EntityPlayer player = (EntityPlayer) rider;

        if (shouldSleep && world.provider.isSurfaceWorld() && !player.isPlayerSleeping()) {
            shouldSleep = false;
            EntityPlayer.SleepResult sleepResult = player.trySleep(getPosition());
            if (sleepResult == EntityPlayer.SleepResult.NOT_SAFE) {
                ChatPlugin.sendLocalizedChatFromServer(player, "tile.bed.notSafe");
            } else if (sleepResult == EntityPlayer.SleepResult.NOT_POSSIBLE_NOW) {
                ChatPlugin.sendLocalizedChatFromServer(player, "tile.bed.noSleep");
            }
        }
    }

    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (Game.isHost(world) && passenger instanceof EntityPlayerMP) {
            ChatPlugin.sendLocalizedChatFromServer((EntityPlayerMP) passenger, "gui.railcraft.cart.bed.key");
        }
    }

    @Override
    public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
        //No rider removal!
    }

    @SubscribeEvent
    public void onPlayerSleep(PlayerSleepInBedEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        // Hmm, player is most likely right, although we cannot really guarantee yet!
        if (event.getPos().equals(getPosition()) && player == getFirstPassenger()) {
            event.setResult(trySleep(player));
            if (event.getResultStatus() == EntityPlayer.SleepResult.OK) {
                lastSleeper = new WeakReference<>(player);
                rideAfterSleep = true;
            }
        }
    }

    @SubscribeEvent
    public void onLocationCheck(SleepingLocationCheckEvent event) {
        if (event.getEntityPlayer() == lastSleeper.get()) {
            event.setResult(Event.Result.ALLOW);
        }
    }

    @SubscribeEvent
    public void onWakeUp(PlayerWakeUpEvent event) {
        if (event.getEntityPlayer() == lastSleeper.get()) {
            wokeUp = true;
        }
    }

    private EntityPlayer.SleepResult trySleep(EntityPlayer player) {
        if (player.isPlayerSleeping() || !player.isEntityAlive()) {
            return EntityPlayer.SleepResult.OTHER_PROBLEM;
        }

        if (!player.world.provider.isSurfaceWorld()) {
            return EntityPlayer.SleepResult.NOT_POSSIBLE_HERE;
        }

        if (player.world.isDaytime()) {
            return EntityPlayer.SleepResult.NOT_POSSIBLE_NOW;
        }

        double d0 = 8.0D;
        double d1 = 5.0D;
        List<EntityMob> list = player.world.getEntitiesWithinAABB(EntityMob.class,
                AABBFactory.start()
                        .fromAABB(player.getEntityBoundingBox())
                        .expandXAxis(d0)
                        .expandYAxis(d1)
                        .expandZAxis(d0)
                        .build());

        if (!list.isEmpty()) {
            return EntityPlayer.SleepResult.NOT_SAFE;
        }

        player.setPosition(posX, posY, posZ);

        startSleeping(player);
        player.setPosition(getPosition().getX(), getPosition().getY(), getPosition().getZ());

        if (!player.world.isRemote) {
            player.world.updateAllPlayersSleepingFlag();
        }

        return EntityPlayer.SleepResult.OK;
    }

    private static void startSleeping(EntityPlayer player) {
        ReflectionHelper.setPrivateValue(EntityPlayer.class, player, true, 26);
        ReflectionHelper.setPrivateValue(EntityPlayer.class, player, 0, 28);
    }

    private void sendPlayerRiding(EntityPlayerMP player) {
        player.connection.sendPacket(new SPacketSetPassengers(this));
    }

    public void attemptSleep() {
        shouldSleep = true;
    }

    @Override
    public void setDead() {
        super.setDead();
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @Nullable
    protected Entity getFirstPassenger() {
        List<Entity> passengers = getPassengers();
        return passengers.isEmpty() ? null : passengers.get(0);
    }
}

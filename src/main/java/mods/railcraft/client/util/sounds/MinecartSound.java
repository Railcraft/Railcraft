/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/

package mods.railcraft.client.util.sounds;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;

/**
 * Created by CovertJaguar on 5/25/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class MinecartSound extends MovingSound {
    private final EntityMinecart minecart;
    private float distance;

    public MinecartSound(SoundEvent soundEvent, EntityMinecart minecartIn) {
        super(soundEvent, SoundCategory.NEUTRAL);
        this.minecart = minecartIn;
        this.repeat = true;
        this.repeatDelay = 0;
    }

    /**
     * Like the old updateEntity(), except more generic.
     */
    @Override
    public void update() {
        if (minecart.isDead) {
            this.donePlaying = true;
        } else {
            this.xPosF = (float) minecart.posX;
            this.yPosF = (float) minecart.posY;
            this.zPosF = (float) minecart.posZ;
            float speedMaybe = MathHelper.sqrt_double(minecart.motionX * minecart.motionX + minecart.motionZ * minecart.motionZ);

            //TODO: this is wrong, its based on speed, normally we don't care about speed
            if (speedMaybe >= 0.01F) {
                this.distance = MathHelper.clamp_float(distance + 0.0025F, 0.0F, 1.0F);
                this.volume = 0.0F + MathHelper.clamp_float(speedMaybe, 0.0F, 0.5F) * 0.7F;
            } else {
                this.distance = 0.0F;
                this.volume = 0.0F;
            }
        }
    }
}

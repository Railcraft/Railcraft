/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.util.sounds;

import mods.railcraft.common.carts.EntityCartJukebox;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

/**
 * Created by CovertJaguar on 5/25/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class JukeboxSound extends MovingSound {
    protected final EntityCartJukebox cart;

    public JukeboxSound(SoundEvent soundEvent, SoundCategory category, EntityCartJukebox cart) {
        super(soundEvent, category);
        this.cart = cart;
        this.volume = 1f;
        this.attenuationType = AttenuationType.LINEAR;
    }

    /**
     * Like the old updateEntity(), except more generic.
     */
    @Override
    public void update() {
        if (cart.isDead || cart.music != this) {
            this.donePlaying = true;
        } else {
            this.xPosF = (float) cart.posX;
            this.yPosF = (float) cart.posY;
            this.zPosF = (float) cart.posZ;
        }
    }
}

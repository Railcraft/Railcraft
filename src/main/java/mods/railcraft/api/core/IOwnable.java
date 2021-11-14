/*
 * ******************************************************************************
 *  Copyright 2011-2015 CovertJaguar
 *
 *  This work (the API) is licensed under the "MIT" License, see LICENSE.md for details.
 * ***************************************************************************
 */
package mods.railcraft.api.core;

import com.mojang.authlib.GameProfile;

/**
 * Implemented by objects that can be owned.
 * <p/>
 * Among other uses, when used on a Tile Entity, the Magnifying Glass can be used to inspect the owner.
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public interface IOwnable {
    /**
     * Returns the GameProfile of the owner of the object.
     *
     * @return
     */
    GameProfile getOwner();

    /**
     * Returns a localization tag (object-tag.name) that can be used in chat messages and such.
     *
     * @return
     */
    String getLocalizationTag();
}

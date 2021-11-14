/*
 * ******************************************************************************
 *  Copyright 2011-2015 CovertJaguar
 *
 *  This work (the API) is licensed under the "MIT" License, see LICENSE.md for details.
 * ***************************************************************************
 */

package mods.railcraft.api.tracks;

import com.mojang.authlib.GameProfile;

/**
 * This interface provides a means for addons to hook into Routing Tracks and
 * change the ticket on the fly. Be warned, security is the responsibility of
 * the addon.
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public interface IRoutingTrack {

    /**
     * This function will be used to construct a ticket item for the Routing
     * Track.
     *
     * @param dest The Destination routing String
     * @param title The title displayed to ticket holders
     * @param owner IMPORTANT: This must be not be user defined. It should be
     * locked to the user who placed the block/turtle/whatever that is trying to
     * set the ticket.
     * @return true if the setting succeeded
     */
    boolean setTicket(String dest, String title, GameProfile owner);

    /**
     * Wipes the existing ticket.
     */
    void clearTicket();

}

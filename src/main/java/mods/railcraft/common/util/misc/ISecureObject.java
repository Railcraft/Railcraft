/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.misc;

import mods.railcraft.api.core.IOwnable;
import mods.railcraft.common.gui.buttons.IMultiButtonState;
import mods.railcraft.common.gui.buttons.MultiButtonController;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public interface ISecureObject<T extends IMultiButtonState> extends IOwnable {

    MultiButtonController<T> getLockController();

    /*
     * Indicates if this object is locked
     */
    boolean isSecure();

}

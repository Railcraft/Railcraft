/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.buttons;

import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class MultiButtonController<T extends IMultiButtonState> {

    private int currentState;
    private final T[] validStates;

    @SafeVarargs
    private MultiButtonController(int startState, T... validStates) {
        this.currentState = startState;
        this.validStates = validStates;
    }

    private MultiButtonController(MultiButtonController<T> controller) {
        this.currentState = controller.currentState;
        this.validStates = controller.validStates;
    }

    @SafeVarargs
    public static <T extends IMultiButtonState> MultiButtonController<T> create(int startState, T... validStates) {
        return new MultiButtonController<>(startState, validStates);
    }

    public static <T extends IMultiButtonState> MultiButtonController<T> create(MultiButtonController<T> controller) {
        return new MultiButtonController<>(controller);
    }

    public MultiButtonController<T> copy() {
        return create(this);
    }

    public T[] getValidStates() {
        return validStates;
    }

    public int incrementState() {
        int newState = currentState + 1;
        if (newState >= validStates.length) {
            newState = 0;
        }
        currentState = newState;
        return currentState;
    }

    public int decrementState() {
        int newState = currentState - 1;
        if (newState < 0) {
            newState = validStates.length - 1;
        }
        currentState = newState;
        return currentState;
    }

    public void setCurrentState(int state) {
        currentState = state;
    }

    public void setCurrentState(T state) {
        for (int i = 0; i < validStates.length; i++) {
            if (validStates[i] == state) {
                currentState = i;
                return;
            }
        }
    }

    public int getCurrentState() {
        return currentState;
    }

    public T getButtonState() {
        return validStates[currentState];
    }

    public boolean is(T state) {
        return getButtonState() == state;
    }

    public void writeToNBT(NBTTagCompound nbt, String tag) {
        nbt.setString(tag, getButtonState().name());
    }

    public void readFromNBT(NBTTagCompound nbt, String tag) {
        if (nbt.getTag(tag) instanceof NBTTagString) {
            String name = nbt.getString(tag);
            for (int i = 0; i < validStates.length; i++) {
                if (validStates[i].name().equals(name)) {
                    currentState = i;
                    break;
                }
            }
        } else if (nbt.getTag(tag) instanceof NBTTagByte) {
            currentState = nbt.getByte(tag);
        }
    }

}

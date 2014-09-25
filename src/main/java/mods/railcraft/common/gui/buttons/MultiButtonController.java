/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.buttons;

import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 * @param <T>
 */
public class MultiButtonController<T extends IMultiButtonState> {

    private int currentState;
    private final T[] validStates;

    public MultiButtonController(int startState, T... validStates) {
        this.currentState = startState;
        this.validStates = validStates;
    }

    protected MultiButtonController(MultiButtonController<T> controller) {
        this.currentState = controller.currentState;
        this.validStates = controller.validStates;
    }

    public MultiButtonController<T> copy() {
        return new MultiButtonController(this);
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

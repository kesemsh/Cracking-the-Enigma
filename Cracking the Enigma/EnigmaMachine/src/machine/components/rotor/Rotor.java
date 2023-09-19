package machine.components.rotor;

import java.io.Serializable;

public interface Rotor extends Rotatable, Serializable, Cloneable {
    int getRotorID();

    int getRotorPosition();

    int getNotchPosition();

    boolean isNotchReached();

    void setPosition(int position);

    int getCurrentPositionKeyIndex();

    int translate(Direction directionOfTranslation, int indexToTranslate);

    Rotor clone();
}
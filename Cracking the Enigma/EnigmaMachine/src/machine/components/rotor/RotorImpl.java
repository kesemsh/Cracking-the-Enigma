package machine.components.rotor;

import java.util.List;
import java.util.Map;

public class RotorImpl implements Rotor {
    private int rotorPosition;
    private final int rotorID;
    private final int notchPosition;
    private final Map<Direction, List<Integer>> directionToIndexTranslationList;
    private final Map<Integer, Integer> keyIndexToRightSidePositionInRotor;
    private final Map<Integer, Integer> rightSidePositionInRotorToKeyIndex;
    private final int keyCount;

    public RotorImpl(int rotorID, int notchPosition, Map<Direction, List<Integer>> directionToIndexTranslationList, Map<Integer, Integer> keyIndexToRightSidePositionInRotor, Map<Integer, Integer> rightSidePositionInRotorToKeyIndex, int keyCount) {
        this.rotorID = rotorID;
        this.notchPosition = notchPosition;
        this.directionToIndexTranslationList = directionToIndexTranslationList;
        this.keyIndexToRightSidePositionInRotor = keyIndexToRightSidePositionInRotor;
        this.rightSidePositionInRotorToKeyIndex = rightSidePositionInRotorToKeyIndex;
        this.keyCount = keyCount;
    }

    @Override
    public int getRotorID() { return rotorID; }

    @Override
    public int getRotorPosition() {
        return rotorPosition;
    }

    @Override
    public int getNotchPosition() {
        return notchPosition;
    }

    @Override
    public boolean isNotchReached() {
        return notchPosition == rotorPosition;
    }

    @Override
    public void setPosition(int keyIndex) {
        rotorPosition = keyIndexToRightSidePositionInRotor.get(keyIndex);
    }

    @Override
    public int getCurrentPositionKeyIndex() {
        return rightSidePositionInRotorToKeyIndex.get(getRotorPosition());
    }

    @Override
    public void rotate() {
        rotorPosition = ++rotorPosition % keyCount;
    }

    @Override
    public int translate(Direction directionOfTranslation, int indexToTranslate) {
        return directionToIndexTranslationList.get(directionOfTranslation).get(indexToTranslate);
    }

    @Override
    public Rotor clone() {
        try {
            RotorImpl clone = (RotorImpl) super.clone();

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

    }
}
package machine.components.translators.reflector;

import object.numbering.RomanNumber;

import java.util.List;

public class ReflectorImpl implements Reflector {
    private final RomanNumber reflectorID;
    private final List<Integer> indexTranslationList;

    public ReflectorImpl(RomanNumber reflectorID, List<Integer> indexTranslationList) {
        this.reflectorID = reflectorID;
        this.indexTranslationList = indexTranslationList;
    }

    @Override
    public int translate(int indexToTranslate) {
        return indexTranslationList.get(indexToTranslate);
    }
}
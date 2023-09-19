package machine.components.translators.plugs;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PlugBoardImpl implements PlugBoard {
    private final List<Integer> indexTranslationList;

    public PlugBoardImpl(int keyCount) {
        indexTranslationList = IntStream.rangeClosed(0, keyCount - 1).boxed().collect(Collectors.toList());
    }

    @Override
    public int translate(int indexToTranslate) {
        return indexTranslationList.get(indexToTranslate);
    }

    @Override
    public void setPlugPair(int firstKeyIndex, int secondKeyIndex) {
        indexTranslationList.set(firstKeyIndex, secondKeyIndex);
        indexTranslationList.set(secondKeyIndex, firstKeyIndex);
    }
}
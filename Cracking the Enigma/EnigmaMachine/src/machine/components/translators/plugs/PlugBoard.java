package machine.components.translators.plugs;

import machine.components.translators.Translator;

public interface PlugBoard extends Translator {
    void setPlugPair(int firstKeyIndex, int secondKeyIndex);
}
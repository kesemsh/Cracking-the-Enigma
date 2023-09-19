package machine.components.translators;

import java.io.Serializable;

public interface Translator extends Serializable {
    int translate(int indexToTranslate);
}
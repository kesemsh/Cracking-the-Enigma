package machine.components.translators.reflector;

import machine.components.translators.Translator;

public interface Reflector extends Translator, Cloneable {
    Reflector clone();
}
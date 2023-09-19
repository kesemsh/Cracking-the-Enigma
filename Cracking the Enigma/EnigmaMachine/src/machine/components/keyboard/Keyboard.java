package machine.components.keyboard;

import java.io.Serializable;
import java.util.*;

public class Keyboard implements Serializable {
   private final Map<Character, Integer> keyToIndex;
   private final Map<Integer, Character> indexToKey;
   private final int keyCount;
   private final Set<Character> allKeys;

   public Keyboard(Map<Character, Integer> keyToIndex) {
      this.keyToIndex = keyToIndex;
      indexToKey = new HashMap<>();
      keyCount = keyToIndex.size();

      keyToIndex.keySet().forEach(x -> indexToKey.put(keyToIndex.get(x), x));
      allKeys = new HashSet<>(keyToIndex.keySet());
   }

   public List<Character> getAllKeys() {
      return new ArrayList<>(allKeys);
   }

   public boolean isKeyInKeyboard(Character keyToCheck) {
      return keyToIndex.containsKey(keyToCheck);
   }

   public int getKeyCount() {
      return keyCount;
   }

   public int getIndexForKey(Character key) {
      return keyToIndex.get(key);
   }

   public Character getKeyForIndex(int index) {
      return indexToKey.get(index);
   }
}
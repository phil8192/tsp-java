package net.parasec.tsp;

import java.util.Map;
import java.util.LinkedHashMap;


public class LRUCache extends LinkedHashMap<Long,Short> {
  private final static int INITIAL_CAP = 1024;
  private final static float LOAD_FACTOR = 0.75f;
  private final int max;


  public LRUCache(int max) {
    super(Math.min(max, INITIAL_CAP), LOAD_FACTOR, true);
    this.max = max;
  }

  protected boolean removeEldestEntry(Map.Entry eldest) {
    return (size() > max);
  }

  public int getMax() {
    return max;
  }
}

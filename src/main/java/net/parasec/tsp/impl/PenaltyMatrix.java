package net.parasec.tsp.impl;

public interface PenaltyMatrix {
  int getPenalty(int i, int j);
  void incPenalty(int i, int j);
  void clear();
}

package net.parasec.tsp;

public interface PenaltyMatrix {
  int getPenalty(int i, int j);
  int incPenalty(int i, int j);
  void clear();
}

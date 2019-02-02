package net.parasec.tsp.algo.gls;

public interface PenaltyMatrix {
  int getPenalty(int i, int j);
  int incPenalty(int i, int j);
  void clear();
}

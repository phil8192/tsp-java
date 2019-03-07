package net.parasec.tsp.algo.gls;

public class NullPenaltyMatrix implements PenaltyMatrix {
  @Override
  public int getPenalty(int i, int j) {
    return 0;
  }

  @Override
  public int incPenalty(int i, int j) {
    return 0;
  }
}

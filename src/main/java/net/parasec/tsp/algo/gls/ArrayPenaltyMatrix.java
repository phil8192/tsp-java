package net.parasec.tsp.algo.gls;

public class ArrayPenaltyMatrix implements PenaltyMatrix {

  private final int[] penalties;
  private final int numCities;

  public ArrayPenaltyMatrix(int numCities) {
    if(numCities > Math.pow(2, 16))
      throw new IllegalArgumentException("try MassiveMatrix instead.");
    this.penalties = new int[numCities * (numCities - 1) / 2];
    this.numCities = numCities;
  }

  private int position(int x, int y) {
    int i = Math.min(x, y), j = Math.max(x, y);
    int n = i + 1;
    int offset = i * numCities + j;
    return offset - n * (n + 1) / 2; // rm diag + triangle.
  }

  public int getPenalty(int i, int j) {
    return penalties[position(i, j)];
  }

  public int incPenalty(int i, int j) {
    int pos = position(i, j);
    int penalty = penalties[pos] + 1;
    penalties[pos] = penalty;
    return penalty;
  }

}

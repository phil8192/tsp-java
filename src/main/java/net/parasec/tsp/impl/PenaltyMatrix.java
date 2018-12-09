package net.parasec.tsp.impl;

public class PenaltyMatrix {

  private final int[] penalties;
  private final int numCities;

  public PenaltyMatrix(int numCities) {
    this.penalties = new int[numCities*numCities];
    this.numCities = numCities;
  }

  public int getPenalty(int i, int j) {
    return penalties[i*numCities + j];
  }

  public void incPenalty(int i, int j) {
    penalties[i*numCities + j]++;
  }

  public void clear() {
    for(int i = 0; i < penalties.length; i++) {
      penalties[i] = 0;
    }
  }
}

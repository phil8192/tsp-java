package net.parasec.tsp.algo.gls;

// not tested.
public class BigMatrix implements PenaltyMatrix {
  //private static int MAX_ARRAY_LENGTH = (int) (Math.pow(2, 31) - 3);
  private static int MAX_CITIES = (int) Math.pow(2, 16); // max 2^16 cities per array.
  private static int MAX_ARRAY_LENGTH = MAX_CITIES*(MAX_CITIES-1)/2;
  private int numCities;
  private short[][] subArrays;

  public BigMatrix(int numCities) {
    this.numCities = numCities;
    long size = numCities * (numCities - 1) / 2;
    int numSubArrays = (int) Math.ceil(size / (double) MAX_ARRAY_LENGTH);
    subArrays = new short[numSubArrays][MAX_ARRAY_LENGTH];
  }

  private long position(final int x, final int y) {
    final int i = Math.min(x, y), j = Math.max(x, y);
    final long n = i + 1;
    final long offset = i * numCities + j;
    return offset - n * (n + 1) / 2;
  }

  @Override
  public int getPenalty(int i, int j) {
    long pos = position(i, j);
    int I = (int) pos / MAX_ARRAY_LENGTH;
    int J = (int) pos % MAX_ARRAY_LENGTH;
    return subArrays[I][J];
  }

  @Override
  public int incPenalty(int i, int j) {
    long pos = position(i, j);
    int I = (int) pos / MAX_ARRAY_LENGTH;
    int J = (int) pos % MAX_ARRAY_LENGTH;
    return ++subArrays[I][J];
  }
}

package net.parasec.tsp.santa;

import net.parasec.tsp.algo.Point;

public class SantaPoint extends Point {

  private final boolean prime;

  public SantaPoint(int id, double x, double y, boolean active, boolean prime) {
    super(id, x, y, active);
    this.prime = prime;
  }

  public boolean isPrime() {
    return prime;
  }

  public Point copy() {
    return new SantaPoint(getId(), getX(), getY(), isActive(), isPrime());
  }
}
package net.parasec.tsp.impl;

public class SantaPoint extends Point {

  private final boolean prime;

  public SantaPoint(int id, double x, double y) {
    super(id, x, y, false);
    this.prime = _isPrime(id);
  }

  // copy constructor
  private SantaPoint(int id, double x, double y, boolean prime) {
    super(id, x, y);
    this.prime = prime;
    setActive(isActive());
  }

  private boolean _isPrime(int n) {
    if(n == 0 || n == 1 || n % 2 == 0) return false;
    if(n == 2) return true;
    for(int i = 3, lim = ((int) Math.round(Math.sqrt(n))) + 1; i < lim; i += 2) {
      if(n % i == 0) {
        return false;
      }
    }
    return true;
  }

  public boolean isPrime() {
    return prime;
  }

  public Point copy() {
    return new SantaPoint(getId(), getX(), getY()), isPrime();
  }

}
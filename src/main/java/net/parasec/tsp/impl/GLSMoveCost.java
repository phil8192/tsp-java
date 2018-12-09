package net.parasec.tsp.impl;

public class GLSMoveCost implements TwoOptMoveCost {

  private final int[] penalties;
  private double lamda;


  public GLSMoveCost(final int[] penalties, double lamda) {
    this.penalties = penalties;
    this.lamda = lamda;
  }

  public double getLamda() {
    return lamda;
  }

  public void setLamda(double lamda) {
    this.lamda = lamda;
  }

  private double getPenalty(Point from, Point to) {
    return 0d;
  }

  public double moveCost(Point a, Point b, Point c, Point d) {

    // distance delta: original edges (ab) (cd), candidate edges (ac) (bd).
    double d_ab = a.distance(b), d_cd = c.distance(d);
    double d_ac = a.distance(c), d_bd = b.distance(d);
    double deltaD = (d_ac+d_bd) - (d_ab+d_cd);

    // penalty delta
    double p_ab = getPenalty(a, b), p_cd = getPenalty(c, d);
    double p_ac = getPenalty(a, c), p_bd = getPenalty(b, d);
    double deltaP = lamda * ((p_ac+p_bd) - (p_ab+p_cd));

    return deltaD+deltaP;
  }
}

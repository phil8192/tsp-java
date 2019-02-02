package net.parasec.tsp.cost;

import net.parasec.tsp.algo.gls.PenaltyMatrix;
import net.parasec.tsp.algo.Point;

public class GLSMoveCost implements TwoOptMoveCost {

  protected final PenaltyMatrix penalties;
  protected double lamda;
  protected int numCities;


  public GLSMoveCost(final PenaltyMatrix penalties, double lamda, int numCities) {
    this.penalties = penalties;
    this.lamda = lamda;
    this.numCities = numCities;
  }

  public double getLamda() {
    return lamda;
  }

  public void setLamda(double lamda) {
    this.lamda = lamda;
  }

  protected double getPenalty(Point from, Point to) {
    return penalties.getPenalty(from.getId(), to.getId());
  }

  public double moveCost(Point a, Point b, Point c, Point d,
                         int a_idx, int b_idx, int c_idx, int d_idx,
                         Point[] tour) {

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

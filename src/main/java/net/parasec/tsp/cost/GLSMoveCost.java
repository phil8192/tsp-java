package net.parasec.tsp.cost;

import net.parasec.tsp.algo.gls.PenaltyMatrix;
import net.parasec.tsp.algo.Point;
import net.parasec.tsp.util.Maths;

public class GLSMoveCost implements TwoOptMoveCost {

  private final PenaltyMatrix penalties;
  protected double lambda;


  public GLSMoveCost(final PenaltyMatrix penalties, double lambda) {
    this.penalties = penalties;
    this.lambda = lambda;
  }

  protected double getPenalty(Point from, Point to) {
    return penalties.getPenalty(from.getId(), to.getId());
  }

  public double moveCost(Point a, Point b, Point c, Point d,
                         int a_idx, int b_idx, int c_idx, int d_idx,
                         Point[] tour) {

    double _ab = a._distance(b), _cd = c._distance(d); // current
    double _ac = a._distance(c), _bd = b._distance(d); // new

    if(_ab < _ac && _cd < _bd) return 1;

    double d_ab = Maths.sqrt(_ab), d_cd = Maths.sqrt(_cd);
    double d_ac = Maths.sqrt(_ac), d_bd = Maths.sqrt(_bd);
    double deltaD = (d_ac + d_bd) - (d_ab + d_cd);

    double ab_pen = getPenalty(a, b), cd_pen = getPenalty(c, d);
    double ac_pen = getPenalty(a, c), bd_pen = getPenalty(b, d);
    double cur_penalty = ab_pen + cd_pen;
    double new_penalty = ac_pen + bd_pen;
    double deltaP = lambda * (new_penalty - cur_penalty);

    return deltaD + deltaP;
  }
}

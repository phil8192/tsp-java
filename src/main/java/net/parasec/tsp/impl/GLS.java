package net.parasec.tsp.impl;

import net.parasec.tsp.TSP;
import java.util.ArrayList;
import java.io.IOException;


public class GLS implements TSP {

  private int maxPenalty = 0 ;
  private int numPenalties = 0;


  public double optimise(Point[] points, double score) {

    // rat783
    // 0.0001: best = 9278.5988 (247180) (60.03s) (penalties = 1641 max_penalty = 842)
    //  0.001: best = 8921.7626 (259567) (60.28s) (penalties = 4784 max_penalty = 224)
    //   0.01: best = 8855.4163 (204174) (57.67s) (penalties = 14106 max_penalty = 54)
    //  0.025: best = 8846.6683 (191518) (57.26s) (penalties = 22467 max_penalty = 32)
    //   0.03: best = 8852.2957 (223703) (67.69s) (penalties = 27302 max_penalty = 31)
    //   0.04: best = 8847.0568 (206710) (64.62s) (penalties = 30691 max_penalty = 26)
    //   0.05: best = 8845.8809 (175955) (55.61s) (penalties = 31312 max_penalty = 21)
    //  0.075: best = 8849.9977 (291332) (93.81s) (penalties = 53570 max_penalty = 20
    //    0.1: best = 8851.3869 (164248) (53.79s) (penalties = 44185 max_penalty = 13)
    //    0.2: best = 8879.8965 (203609) (68.47s) (penalties = 73010 max_penalty = 10)
    //    0.3: best = 8892.9067 (151628) (54.70s) (penalties = 75256 max_penalty = 7)
    //      1: best = 9154.7613 (109239) (53.25s) (penalties = 98417 max_penalty = 3)

    // lru+mmf (not worth it..)
    // best = 8847.2004 (175895) (179.35s) (penalties = 31311 max_penalty = 21)
    // just mmf
    // best = 8845.8809 (175955) (84.06s) (penalties = 31312 max_penalty = 21)

    // best = 8845.8809 (175955) (183.47s) (penalties = 31312 max_penalty = 21)
    final double a = 0.05; //0.5; // https://pdfs.semanticscholar.org/bbd8/1fa7eb9acaef4115c92c4a40eb4040ad036c.pdf: suggests betwen 0.125 and 0.5 for 2-opt. (higher values = more agressive)

    final int penaltyClear = 10000000; // original implementation resets penalty matrix every millionoth iteration.

    PenaltyMatrix penalties=null;
    // 175955 = 55.61s
    //penalties = new ArrayPenaltyMatrix(points.length);
    // 175955 = 89.43s
    try{penalties = new BFPM(points.length);}catch(IOException e){e.printStackTrace();}
    GLSMoveCost gmc = new GLSMoveCost(penalties, 0, points.length);
    FLS fls = new FLS(gmc);
    System.out.println("start opt 1");
    double bestScore = fls.optimise(points, score); // orignal cost (all penalties = 0)
    double augScore = bestScore;
    Point[] bestPoints = Point.copy(points);

    // "cost of a local minimum tour produced by local search
    // (e.g. first local minimum before penalties are applied)"
    // https://pdfs.semanticscholar.org/bbd8/1fa7eb9acaef4115c92c4a40eb4040ad036c.pdf
    gmc.setLamda(a * (bestScore/points.length));

    double l = System.currentTimeMillis();
    //for(int i = 0; i < 2000000; i++) {
    for(int i = 0; i < 10000000; i++) {

      if(i % penaltyClear == 0) {
        penalties.clear();
      }

      penalise(points, penalties);
      augScore = getAugmentedScore(points, penalties, gmc.getLamda());

      System.out.println("start opt " + i);
      augScore = fls.optimise(points, augScore);
      score = Point.distance(points);

      System.out.printf("score = %.4f. aug = %.4f (%d).\n", score, augScore, i);

      if(score < bestScore) { // non-augmented score.
        bestPoints = Point.copy(points);
        bestScore = score;
        System.out.printf("best = %.4f (%d) (%.2fs) (penalties = %d max_penalty = %d)\n", bestScore, i, (System.currentTimeMillis()-l)/1000.0, numPenalties, maxPenalty);
      }
    }
    for(int i = 0; i < points.length; i++) {
      points[i] = bestPoints[i];
    }
    return bestScore;
  }

  private void penalise(Point[] points, PenaltyMatrix penalties) {
    ArrayList<Point> maxUtilFeatures = new ArrayList<Point>();
    // get features (edges) which maximise the utility cost/(penalty+1).
    double maxUtil = 0;
    for(int i = 0, j = 1; i < points.length; i++, j = (j+1) % points.length) {
      Point from = points[i], to = points[j];
      double distance = from.distance(to);
      int penalty = penalties.getPenalty(from.getId(), to.getId());
      double utility = distance/(penalty+1);
      if(utility > maxUtil) {
        maxUtilFeatures.clear();
        maxUtilFeatures.add(from);
        maxUtilFeatures.add(to);
        maxUtil = utility;
      } else if(utility == maxUtil) {
        maxUtilFeatures.add(from);
        maxUtilFeatures.add(to);
      }
    }
    // increase penalty for features which maximise the utility.
    //System.out.println("penalise " + maxUtilFeatures.size()/2 + " features");
    for(int i = 0, len = maxUtilFeatures.size(); i < len; i += 2) { // note/todo: how often is there actually > 1 feature? probably no harm doing one at a time (others will be candidates later.)
      Point from = maxUtilFeatures.get(i), to = maxUtilFeatures.get(i+1);
      int penalty = penalties.incPenalty(from.getId(), to.getId());
      if(penalty > maxPenalty) {
        maxPenalty = penalty;
      }
      if(penalty == 1) {
        numPenalties++;
      }
      //System.out.println("(" + from.getId() + "," + to.getId() + ") = " + penalties.getPenalty(from.getId(), to.getId()));
      //penalties.incPenalty(to.getId(), from.getId()); // ?
      from.setActive(true);
      to.setActive(true);
    }
  }

  private double getAugmentedScore(Point[] points, PenaltyMatrix penalties, double lamda) {
    double d = points[points.length-1].distance(points[0]);
    d += penalties.getPenalty(points[points.length-1].getId(), points[0].getId());
    for(int i = 1; i < points.length; i++) {
      final Point pre = points[i-1], cur = points[i];
      d += pre.distance(cur);
      if(i % 10 == 0 && !pre.isPrime()) {
        d *= 1.1;
      }
      d += lamda * penalties.getPenalty(pre.getId(), cur.getId());
    }
    return d;
  }
}

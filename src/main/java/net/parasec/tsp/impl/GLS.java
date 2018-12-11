package net.parasec.tsp.impl;

import net.parasec.tsp.TSP;
import java.util.ArrayList;
import java.io.IOException;


public class GLS implements TSP {

  public double optimise(Point[] points, double score) {
    final double l = 0.3;
    final int penaltyClear = 10000000; // original implementation resets penalty matrix every millionoth iteration.

    //PenaltyMatrix penalties = new ArrayPenaltyMatrix(points.length);
    PenaltyMatrix penalties=null;
    try{penalties = new BFPM(points.length);}catch(IOException e){e.printStackTrace();}
    GLSMoveCost gmc = new GLSMoveCost(penalties, 0, points.length);
    FLS fls = new FLS(gmc);
    System.out.println("start opt 1");
    double bestScore = fls.optimise(points, score); // orignal cost (all penalties = 0)
    double augScore = bestScore;
    Point[] bestPoints = Point.copy(points);

    gmc.setLamda(((int) Math.round(l * (bestScore/points.length))));

    //for(int i = 0; i < 2000000; i++) {
    for(int i = 0; i < 1000000; i++) {

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
        gmc.setLamda(((int) Math.round(l * (bestScore/points.length))));
        System.out.printf("best = %.4f (%d)\n", bestScore, i);
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
      double utility = distance/penalty;
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
    //System.out.println("penalise " + maxUtilFeatures.size() + " features");
    for(int i = 0, len = maxUtilFeatures.size(); i < len; i += 2) {
      Point from = maxUtilFeatures.get(i), to = maxUtilFeatures.get(i+1);
      penalties.incPenalty(from.getId(), to.getId());
      penalties.incPenalty(to.getId(), from.getId()); // ?
      from.setActive(true);
      to.setActive(true);
    }
  }

  private double getAugmentedScore(Point[] points, PenaltyMatrix penalties, int lamda) {
    double d = points[points.length-1].distance(points[0]);
    d += penalties.getPenalty(points[points.length-1].getId(), points[0].getId());
    for(int i = 1; i < points.length; i++) {
      d += points[i-1].distance(points[i]);
      d += lamda * penalties.getPenalty(points[i-1].getId(), points[i].getId());
    }
    return d;
  }
}

package net.parasec.tsp.impl;

import net.parasec.tsp.TSP;
import java.util.ArrayList;


public class GLS implements TSP {

  public double optimise(Point[] points, double score) {
    int[] penalties = new int[points.length * points.length];
    GLSMoveCost gmc = new GLSMoveCost(penalties, 0);
    FLS fls = new FLS(gmc);
    double bestScore = fls.optimise(points, score); // orignal cost (all penalties = 0)
    double augmentedScore = bestScore;
    Point[] bestPoints = Point.copy(points);
    final double l = 0.3;
    gmc.setLamda(((int) Math.round(l * (bestScore/points.length))));
    System.out.printf("score = %.4f\n", bestScore);

    for(int i = 0; i < 10; i++) {

      penalise(points, penalties);
      augmentedScore = fls.optimise(points, augmentedScore);
      score = Point.distance(points);

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

  private void penalise(Point[] points, int[] penalties) {
    ArrayList<Point> maxUtilFeatures = new ArrayList<Point>();
    // get features (edges) which maximise the utility cost/(penalty+1).
    double maxUtil = 0;
    for(int i = 0, j = 1; i < points.length; i++, j = j+1 % points.length) {
      Point from = points[i], to = points[j];
      double distance = from.distance(to);
      int penalty = penalties[from.getId()*points.length + to.getId()];
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
    for(int i = 0, len = maxUtilFeatures.size(); i < len; i += 2) {
      Point from = maxUtilFeatures.get(i), to = maxUtilFeatures.get(i+1);
      penalties[from.getId()*points.length + to.getId()]++;
      penalties[to.getId()*points.length + from.getId()]++;
      from.setActive(true);
      to.setActive(true);
    }
  }
}

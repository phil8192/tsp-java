package net.parasec.tsp.impl;

import net.parasec.tsp.TSP;
import java.util.ArrayList;


public class GLS implements TSP {

  public double optimise(Point[] points) {
    int[] penalties = new int[points.length * points.length];
    FLS fls = new FLS();
    Point[] bestPoints = points;
    double bestScore = fls.optimise(points);
    System.out.printf("score = %.4f\n", bestScore);
    for(int i = 0; i < 10; i++) {
      Point[] pointsCopy = Point.copy(bestPoints);

      penalise(pointsCopy, penalties, bestScore, 0.3);

      double t = System.currentTimeMillis();
      double minimaScore = fls.optimise(pointsCopy);
      //System.out.println("fls time = " + (System.currentTimeMillis() - t) + "ms.");

      if(minimaScore < bestScore) {
        bestPoints = pointsCopy;
        bestScore = minimaScore;
        System.out.printf("best = %.4f (%d)\n", bestScore, i);
      }
    }
    for(int i = 0; i < points.length; i++) {
      points[i] = bestPoints[i];
    }
    return 0.0;
  }

  private void penalise(Point[] points, int[] penalties,
                        double bestScore, double l) {
    int lambda = (int) Math.round(l * (bestScore/points.length));
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

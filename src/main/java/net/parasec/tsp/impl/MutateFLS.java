package net.parasec.tsp.impl;

import net.parasec.tsp.TSP;

public class MutateFLS implements TSP {
  public double optimise(Point[] points, double score) {
    java.util.SplittableRandom prng = new java.util.SplittableRandom();
    FLS fls = new FLS();
    Point[] bestPoints = points;
    double bestScore = fls.optimise(points, score);
    System.out.printf("score = %.4f\n", bestScore);
    double l = System.currentTimeMillis();
    for(int i = 0; i < 1000000; i++) {
      Point[] pointsCopy = Point.copy(bestPoints);
      fls.mutate(pointsCopy);
      double mutantScore = Point.distance(pointsCopy);
      double t = System.currentTimeMillis();
      double minimaScore = fls.optimise(pointsCopy, mutantScore);
      //System.out.printf("minima = %.4f\n", minimaScore);
      if(minimaScore < bestScore) {
        bestPoints = pointsCopy;
        bestScore = minimaScore;
        System.out.printf("best = %.4f (%d) (%.2fs)\n", bestScore, i, System.currentTimeMillis()-l);
      }
    }
    for(int i = 0; i < points.length; i++) {
      points[i] = bestPoints[i];
    }
    return bestScore;
  }
}

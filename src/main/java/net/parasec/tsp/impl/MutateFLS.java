package net.parasec.tsp.impl;

import net.parasec.tsp.TSP;

public class MutateFLS implements TSP {
  public double optimise(Point[] points, double score) {
    java.util.SplittableRandom prng = new java.util.SplittableRandom();
    FLS fls = new FLS();
    Point[] bestPoints = points;
    double bestScore = fls.optimise(points, score);
    System.out.printf("score = %.4f\n", bestScore);
    for(int i = 0; i < 25000; i++) {
      Point[] pointsCopy = Point.copy(bestPoints);
      //for(int j = 0, len = prng.nextInt(1, 5); j < len; j++) {
        fls.mutate(pointsCopy);
      //}
      double t = System.currentTimeMillis();
      double minimaScore = fls.optimise(pointsCopy, bestScore);
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
    return bestScore;
  }
}

package net.parasec.tsp.impl;

public class MutateFLS {

  public double optimise(Point[] points) {
    FLS fls = new FLS();
    Point[] bestPoints = points;
    double bestScore = fls.optimise(points);
    System.out.printf("score = %.4f\n", bestScore);
    for(int i = 0; i < 10000; i++) {
      Point[] pointsCopy = Point.copy(bestPoints);
      //for(int j = 0; j < 100; j++) {
      fls.mutate(pointsCopy);
      //}
      double minimaScore = fls.optimise(pointsCopy);
      //System.out.printf("score = %.4f\n", minimaScore);
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

package net.parasec.tsp.impl;

public class MutateFLS {

  public double optimise(Point[] points) {
    java.util.SplittableRandom prng = new java.util.SplittableRandom();
    FLS fls = new FLS();
    Point[] bestPoints = points;
    double bestScore = fls.optimise(points);
    System.out.printf("score = %.4f\n", bestScore);
    for(int i = 0; i < 25000; i++) {
      Point[] pointsCopy = Point.copy(bestPoints);
      //for(int j = 0, len = prng.nextInt(1, 5); j < len; j++) {
        fls.mutate(pointsCopy);
      //}
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
    return bestScore;
  }
}

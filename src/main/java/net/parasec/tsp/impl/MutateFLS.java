package net.parasec.tsp.impl;

import net.parasec.tsp.DumpPoints;
import net.parasec.tsp.TSP;

public class MutateFLS implements TSP {
  public double optimise(Point[] points, double score) {
    //java.util.SplittableRandom prng = new java.util.SplittableRandom();
    FLS fls = new FLS();
    Point[] bestPoints = points;
    double bestScore = fls.optimise(points, score);
    System.out.printf("score = %.4f\n", bestScore);
    double l = System.currentTimeMillis();
    for(int i = 0; i < 1000000; i++) {
      Point[] pointsCopy = Point.copy(bestPoints);

      //for(int j = 0; j < prng.nextInt(1, 100); j++) {
        fls.mutate(pointsCopy);
      //}

      double mutantScore = Point.distance(pointsCopy);
      //System.out.println("start opt");
      double minimaScore = fls.optimise(pointsCopy, mutantScore);
      //System.out.println("end opt");
      if(minimaScore < bestScore) {
        bestPoints = pointsCopy;
        bestScore = minimaScore;
        System.out.printf("best = %.4f (%d) (%.2fs)\n", bestScore, i, (System.currentTimeMillis()-l)/1000.0);
        DumpPoints.dump(bestPoints, "/tmp/best3.points");
      }
    }
    for(int i = 0; i < points.length; i++) {
      points[i] = bestPoints[i];
    }
    return bestScore;
  }
}

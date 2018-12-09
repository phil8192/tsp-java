package net.parasec.tsp;

import net.parasec.tsp.impl.Point;

public class TSPMain {
  public static void main(String[] args) {
    Point[] points = PointsReader.read(args[0]);
    String algo = args.length == 2 ? "mutate_fls" : args[2];
    long l = System.currentTimeMillis();
    double score = Point.distance(points); // starting score.
    System.out.printf("initial score = %.4f\n", score);
    double error = TSPSolver.instance(algo).optimise(points, score);
    DumpPoints.dump(points, args[1]);
    System.out.printf("tour length = %.4f\ttime = %.2f seconds.\n", +
        error, (System.currentTimeMillis() - l)/1000d);
  }
}

package net.parasec.tsp;

import net.parasec.tsp.impl.Point;

public final class TSPMain {
  public static void main(String[] args) {
    Point[] points = PointsReader.read(args[0]);
    TSP tsp = new TSPSolver();
    double error = tsp.solve(points, "mutate_fls");
    DumpPoints.dump(points, args[1]);
    System.out.printf("tour length = %.4f\toptimisation time = %.2f seconds.\n", +
        error, (System.currentTimeMillis() - l)/1000d);
  }
}

package net.parasec.tsp;

import net.parasec.tsp.algo.Point;
import net.parasec.tsp.algo.TSP;
import net.parasec.tsp.cost.BasicTwoOptMoveCost;
import net.parasec.tsp.cost.GLSMoveCost;
import net.parasec.tsp.distance.DefaultDistance;
import net.parasec.tsp.distance.TourDistance;
import net.parasec.tsp.algo.gls.ArrayPenaltyMatrix;
import net.parasec.tsp.algo.gls.GLS;
import net.parasec.tsp.algo.gls.PenaltyMatrix;
import net.parasec.tsp.algo.fls.FLS;
import net.parasec.tsp.io.DumpPoints;
import net.parasec.tsp.io.PointsReader;


public class TSPMain {

  public static void main(String[] args) {

    String input = args[0];
    String output = args[1];
    String algo = args[2];

    PointsReader.PointParser<Point> pointParser = new PointsReader.PointParser<Point>() {
      @Override
      public Point parse(String[] line) {
        return new Point(Integer.parseInt(line[0]),
            Double.parseDouble(line[1]),
            Double.parseDouble(line[2]));
      }
    };

    PointsReader<Point> pointPointsReader = new PointsReader<>();
    Point[] points = pointPointsReader.read(input, pointParser);

    TourDistance<Point> tourDistance = new DefaultDistance();
    double initialScore = tourDistance.distance(points);

    TSP fls = new FLS(new BasicTwoOptMoveCost());
    double newScore = fls.optimise(points, initialScore);
    System.out.printf("FLS original tour length = %.2f new tour length = %.2f\n", initialScore, newScore);
    DumpPoints.dump(points, output);

    if(algo.equals("gls_fls")) {
      // args: max_runs, alpha, matrix_location
      if(args.length < 4) {
        System.err.println("gls_fls requires a <max_runs> parameter.");
        System.exit(0);
      }
      int maxRuns = Integer.parseInt(args[3]);
      PenaltyMatrix penaltyMatrix = new ArrayPenaltyMatrix(points.length);
      double alpha = 0.05;
      double lambda = alpha * (newScore / points.length);
      fls = new FLS(new GLSMoveCost(penaltyMatrix, lambda));
      TSP gls = new GLS(tourDistance, fls, penaltyMatrix, maxRuns, output);
      double glsScore = gls.optimise(points, newScore);
      System.out.printf("GLS original tour length = %.2f new tour length = %.2f\n", newScore, glsScore);
    }
  }
}
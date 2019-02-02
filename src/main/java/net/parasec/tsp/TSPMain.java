package net.parasec.tsp;

import net.parasec.tsp.impl.*;

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

    TwoOptMoveCost twoOptMoveCost = new BasicTwoOptMoveCost();
    TSP tsp = new FLS(twoOptMoveCost);

    if(algo.equals("gls_fls")) {
      int maxRuns = Integer.parseInt(args[3]);
      PenaltyMatrix penaltyMatrix = new ArrayPenaltyMatrix(points.length);
      tsp = new GLS(tourDistance, tsp, penaltyMatrix, maxRuns, output);
    }

    double newScore = tsp.optimise(points, initialScore);

    DumpPoints.dump(points, output);
  }
}
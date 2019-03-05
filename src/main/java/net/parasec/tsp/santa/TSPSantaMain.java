package net.parasec.tsp.santa;

import net.parasec.tsp.algo.fls.FLS;
import net.parasec.tsp.io.PointsReader;
import net.parasec.tsp.algo.TSP;
import net.parasec.tsp.cost.GLSMoveCost;
import net.parasec.tsp.distance.TourDistance;
import net.parasec.tsp.algo.gls.BFPM;
import net.parasec.tsp.algo.gls.GLS;
import net.parasec.tsp.algo.gls.PenaltyMatrix;

import java.io.IOException;

// for https://www.kaggle.com/c/traveling-santa-2018-prime-paths
public class TSPSantaMain {

  public static void main(String[] args) throws IOException {

    String input = args[0];
    String output = args[1];
    String penalty_file = args[2];
    int maxRuns = Integer.parseInt(args[3]);
    boolean active = Boolean.parseBoolean(args[4]);

    PointsReader.PointParser<SantaPoint> pointParser = new PointsReader.PointParser<SantaPoint>() {

      private boolean _isPrime(int n) {
        if(n == 0 || n == 1 || n % 2 == 0) {
          return false;
        }
        if(n == 2) {
          return true;
        }
        for(int i = 3, lim = ((int) Math.round(Math.sqrt(n))) + 1; i < lim; i += 2) {
          if(n % i == 0) {
            return false;
          }
        }
        return true;
      }

      @Override
      public SantaPoint parse(String[] line) {
        int id = Integer.parseInt(line[0]);
        boolean prime = _isPrime(id);
        return new SantaPoint(id,
            Double.parseDouble(line[1]),
            Double.parseDouble(line[2]),
            active,
            prime);
      }
    };

    PointsReader<SantaPoint> pointsReader = new PointsReader<>();
    SantaPoint[] points = pointsReader.read(input, pointParser);

    TourDistance<SantaPoint> tourDistance = new SantaDistance();
    double score = tourDistance.distance(points);
    System.out.printf("Initial tour length = %.2f\n", score);

    PenaltyMatrix penaltyMatrix = new BFPM(points.length, penalty_file);
    double alpha = 0.05;
    double lambda = alpha * (score / points.length);
    FLS fls = new FLS(new GLSMoveCost(penaltyMatrix, lambda));
    TSP gls = new GLS(tourDistance, fls, penaltyMatrix, maxRuns, output);
    double finalScore = gls.optimise(points, score);
    System.out.printf("GLS final tour length = %.2f\n", finalScore);
  }
}
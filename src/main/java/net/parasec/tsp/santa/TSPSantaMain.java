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

  public static void main(String[] args) {

    String penaltyMatrixFile = args[0]; // /mnt/nvme/phil/bfm.matrix
    int maxRuns = Integer.parseInt(args[1]);
    String output = args[1];

    PointsReader.PointParser<SantaPoint> pointParser = new PointsReader.PointParser<SantaPoint>() {
      @Override
      public SantaPoint parse(String[] line) {
        return new SantaPoint(Integer.parseInt(line[0]),
            Double.parseDouble(line[1]),
            Double.parseDouble(line[2]));
      }
    };

    PointsReader<SantaPoint> pointsReader = new PointsReader<>();
    SantaPoint[] points = pointsReader.read(args[0], pointParser);

    PenaltyMatrix penalties = null;
    try {
      penalties = new BFPM(points.length, penaltyMatrixFile);
    }catch(IOException e){
      e.printStackTrace();
    }

    GLSMoveCost gmc = new SantaGLSMoveCost(penalties, 0, points.length);
    TSP fls = new FLS(gmc);

    TourDistance<SantaPoint> tourDistance = new SantaDistance();
    TSP gls = new GLS(tourDistance, fls, penalties, maxRuns, output);

    double score = tourDistance.distance(points);
    gls.optimise(points, score);

  }
}
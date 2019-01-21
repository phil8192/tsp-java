package net.parasec.tsp;

import net.parasec.tsp.impl.*;

import java.io.IOException;

// for https://www.kaggle.com/c/traveling-santa-2018-prime-paths
public class TSPSantaMain {

  public static void main(String[] args) {

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
      penalties = new BFPM(points.length, "/mnt/nvme/phil/bfm.matrix");
    }catch(IOException e){
      e.printStackTrace();
    }

    GLSMoveCost gmc = new SantaGLSMoveCost(penalties, 0, points.length);
    TSP fls = new FLS(gmc);

    TourDistance<SantaPoint> tourDistance = new SantaDistance();
    TSP gls = new GLS(tourDistance, fls, penalties, 1000000,"/tmp/best_gls.points");

    double score = tourDistance.distance(points);
    gls.optimise(points, score);

  }
}

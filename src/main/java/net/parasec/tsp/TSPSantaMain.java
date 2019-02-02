package net.parasec.tsp;

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
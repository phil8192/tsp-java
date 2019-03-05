package net.parasec.tsp.algo.gls;

import net.parasec.tsp.io.DumpPoints;
import net.parasec.tsp.algo.Point;
import net.parasec.tsp.algo.TSP;
import net.parasec.tsp.distance.TourDistance;

import java.util.ArrayList;


public class GLS implements TSP {

  private final TourDistance tourDistance;
  private final TSP localSearch;
  private final PenaltyMatrix penalties;
  private final int maxRuns;
  private final String output;


  public GLS(TourDistance tourDistance, TSP localSearch, PenaltyMatrix penalties, int maxRuns, String output) {
    this.tourDistance = tourDistance;
    this.localSearch = localSearch;
    this.penalties = penalties;
    this.maxRuns = maxRuns;
    this.output = output;
  }

  public double optimise(Point[] points, double score) {

    double bestScore = localSearch.optimise(points, score); // original cost (all penalties = 0)

    double augScore = bestScore;
    Point[] bestPoints = Point.copy(points);

    for(int i = 0; i < maxRuns; i++) {
      penalise(points, penalties);
      augScore = localSearch.optimise(points, augScore);
      score = tourDistance.distance(points);

     if(score < bestScore) { // non-augmented score.
        bestPoints = Point.copy(points);
        bestScore = score;
        System.out.printf("GLS round %d length = %.4f\n", i, bestScore);
        DumpPoints.dump(bestPoints, output);
      }

    }
    for(int i = 0; i < points.length; i++) {
      points[i] = bestPoints[i];
    }
    return bestScore;
  }

  private void penalise(Point[] points, PenaltyMatrix penalties) {
    ArrayList<Point> maxUtilFeatures = new ArrayList<>();
    // get features (edges) which maximise the utility cost/(penalty+1).
    double maxUtil = 0;
    for(int i = 0, j = 1; i < points.length; i++, j = (j + 1) % points.length) {
      Point from = points[i], to = points[j];
      double distance = from.distance(to);
      int penalty = penalties.getPenalty(from.getId(), to.getId());
      double utility = distance / (penalty + 1);
      if(utility > maxUtil) {
        maxUtilFeatures.clear();
        maxUtilFeatures.add(from);
        maxUtilFeatures.add(to);
        maxUtil = utility;
      } else if(utility == maxUtil) {
        maxUtilFeatures.add(from);
        maxUtilFeatures.add(to);
      }
    }

    // increase penalty for features which maximise the utility.
    for(int i = 0, len = maxUtilFeatures.size(); i < len; i += 2) {
      Point from = maxUtilFeatures.get(i), to = maxUtilFeatures.get(i + 1);
      int edgeFrom = from.getId();
      int edgeTo = to.getId();
      penalties.incPenalty(edgeFrom, edgeTo);
      from.setActive(true);
      to.setActive(true);
    }
  }
}
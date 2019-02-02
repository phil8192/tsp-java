package net.parasec.tsp;

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

    // rat783 optimal = 8806.

    // rat783
    // 0.0001: best = 9278.5988 (247180) (60.03s) (penalties = 1641 max_penalty = 842)
    //  0.001: best = 8921.7626 (259567) (60.28s) (penalties = 4784 max_penalty = 224)
    //   0.01: best = 8855.4163 (204174) (57.67s) (penalties = 14106 max_penalty = 54)
    //  0.025: best = 8846.6683 (191518) (57.26s) (penalties = 22467 max_penalty = 32)
    //   0.03: best = 8852.2957 (223703) (67.69s) (penalties = 27302 max_penalty = 31)
    //   0.04: best = 8847.0568 (206710) (64.62s) (penalties = 30691 max_penalty = 26)
    //   0.05: best = 8845.8809 (175955) (55.61s) (penalties = 31312 max_penalty = 21)
    //  0.075: best = 8849.9977 (291332) (93.81s) (penalties = 53570 max_penalty = 20
    //    0.1: best = 8851.3869 (164248) (53.79s) (penalties = 44185 max_penalty = 13)
    //    0.2: best = 8879.8965 (203609) (68.47s) (penalties = 73010 max_penalty = 10)
    //    0.3: best = 8892.9067 (151628) (54.70s) (penalties = 75256 max_penalty = 7)
    //      1: best = 9154.7613 (109239) (53.25s) (penalties = 98417 max_penalty = 3)


    // without triangle
    // best = 8844.1745 (194537) (126.22s) (penalties = 10881 max_penalty = 73)


    // with triangle
    // best = 8844.3483 (455303) (50.73s) (penalties = 17500 max_penalty = 101)
    // best = 8844.3264 (1176885) (136.47s) (penalties = 30239 max_penalty = 149)
    // best = 8843.9242 (1455928) (170.52s) (penalties = 34298 max_penalty = 169)
    // best = 8843.0274 (1583371) (186.37s) (penalties = 36073 max_penalty = 171)
    // best = 8843.0068 (2970446) (364.77s) (penalties = 52361 max_penalty = 225)
    // best = 8842.9950 (2970474) (364.77s) (penalties = 52361 max_penalty = 225)


    // best = 8842.9950 (1127653) (118.21s) (penalties = 19953 max_penalty = 220)   0.025
    // best = 8842.9950 (2970474) (360.63s) (penalties = 52361 max_penalty = 225) 0.05
    // best = 8842.9950 (1216367) (148.81s) (penalties = 39262 max_penalty = 119) 0.075
    // best = 8842.9950 (953709) (109.89s) (penalties = 39960 max_penalty = 92) 0.1
    // best = 8842.9950 (1797569) (231.09s) (penalties = 72804 max_penalty = 100) 0.15

    // with triangle + deltaP >= 0
    // best = 8844.1745 (194537) (127.37s) (penalties = 10881 max_penalty = 73)
    // same as without triangle: cur_penalty will always be >= 1 during GLS (dont look bits set to 1)


    // edge penalties..
    // best = 8844.0546 (853271) (532.46s) (penalties = 25045 max_penalty = 132)
    // best = 8843.7396 (1648109) (1022.95s) (penalties = 37031 max_penalty = 172)
    // best = 8843.0392 (1648110) (1022.95s) (penalties = 37031 max_penalty = 172)
    // best = 8843.0274 (2045430) (1270.90s) (penalties = 42116 max_penalty = 192)
    // best = 8842.9950 (2085605) (1295.77s) (penalties = 42660 max_penalty = 192)

    // lru+mmf (not worth it..)
    // best = 8847.2004 (175895) (179.35s) (penalties = 31311 max_penalty = 21)
    // just mmf
    // best = 8845.8809 (175955) (84.06s) (penalties = 31312 max_penalty = 21)

    // best = 8845.8809 (175955) (183.47s) (penalties = 31312 max_penalty = 21)
    //final double a = 0.05; //0.5; // https://pdfs.semanticscholar.org/bbd8/1fa7eb9acaef4115c92c4a40eb4040ad036c.pdf: suggests betwen 0.125 and 0.5 for 2-opt. (higher values = more agressive)
    //final double a = 0.025;

    //PenaltyMatrix penalties=null;
    // 175955 = 55.61s
    //penalties = new ArrayPenaltyMatrix(points.length);
    // 175955 = 89.43s
    //try{penalties = new BFPM(points.length, "/mnt/nvme/phil/bfm.matrix");}catch(IOException e){e.printStackTrace();}
    //GLSMoveCost gmc = new SantaGLSMoveCost(penalties, 0, points.length); //GLSMoveCost(penalties, 0, points.length);
    //FLS fls = new FLS(gmc);

    double bestScore = localSearch.optimise(points, score); // original cost (all penalties = 0)
    double augScore = bestScore;
    Point[] bestPoints = Point.copy(points);


    // "cost of a local minimum tour produced by local search
    // (e.g. first local minimum before penalties are applied)"
    // https://pdfs.semanticscholar.org/bbd8/1fa7eb9acaef4115c92c4a40eb4040ad036c.pdf
    //gmc.setLamda(a * (bestScore/points.length));

    for(int i = 0; i < maxRuns; i++) {

      penalise(points, penalties);

      augScore = localSearch.optimise(points, augScore);

      score = tourDistance.distance(points);


      if(score < bestScore) { // non-augmented score.
        bestPoints = Point.copy(points);
        bestScore = score;


        System.out.printf("best = %.4f (%d)\n", bestScore, i);
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
      penalties.incPenalty(from.getId(), to.getId());
      from.setActive(true);
      to.setActive(true);
    }
  }
}
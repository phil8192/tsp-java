package net.parasec.tsp;

import net.parasec.tsp.impl.*;


public class TSPSolver {
  public static TSP instance(String solver, String intermediateOutput) {
    if(solver == null) throw new IllegalArgumentException();
    TSP tspSolver;
    switch(solver) {
      case "fls":
        tspSolver = new FLS();
        break;
      case "gls_fls":
        // TourDistance tourDistance, TSP localSearch, PenaltyMatrix penalties, int maxRuns, String output) {
        TourDistance tourDistance = new DefaultDistance();
        TSP localSearch = new FLS();
        PenaltyMatrix penaltyMatrix = new ArrayPenaltyMatrix(numCites);

        tspSolver = new GLS(tourDistance, localSearch, penaltyMatrix, intermediateOutput);
        break;
      default:
        String m = solver + " not recognised. try: <fls, mutate_fls, gls_fls>";
        throw new UnsupportedOperationException(m);
    }
    return tspSolver;
  }
}

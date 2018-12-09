package net.parasec.tsp;

import net.parasec.tsp.impl.Point;
import net.parasec.tsp.impl.FLS;
import net.parasec.tsp.impl.MutateFLS;
import net.parasec.tsp.impl.GLS;

public class TSPSolver {
  public static TSP instance(String solver) {
    if(solver == null) throw new IllegalArgumentException();
    TSP tspSolver;
    if(solver.equals("fls")) {
      tspSolver = new FLS();
    } else if(solver.equals("mutate_fls")) {
      tspSolver = new MutateFLS();
    } else if(solver.equals("gls_fls")) {
      tspSolver = new GLS();
    } else {
      String m = solver + " not recognised. try: <fls, mutate_fls, gls_fls>";
      throw new UnsupportedOperationException(m);
    }
    System.out.println("using " + solver + " optimiser.");
    return tspSolver;
  }
}

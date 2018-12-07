package net.parasec.tsp;

import net.parasec.tsp.impl.Point;
import net.parasec.tsp.impl.FLS;
import net.parasec.tsp.impl.MutateFLS;
import net.parasec.tsp.impl.GLS;

public class TSPSolver {
  public double solve(Point[] points, String solver) {
    TSP tspSolver;
    if(solver.equals("fls")) {
      tspSolver = new FLS();
    } else if(solver.equals("mutate_fls")) {
      tspSolver = new MutateFLS();
    } else if(Sovler.equals("gls_fls")) {
      tspSolver = new GLS();
    }
  }
  return tspSovler.optimise(points);
}

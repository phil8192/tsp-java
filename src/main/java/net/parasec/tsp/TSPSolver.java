package net.parasec.tsp;

import net.parasec.tsp.impl.*;

import java.io.IOException;

public class TSPSolver {
  public static TSP instance(String solver) {
    if(solver == null) throw new IllegalArgumentException();
    TSP tspSolver;
    if(solver.equals("fls")) {
      PenaltyMatrix penalties=null;
      try{penalties = new BFPM(197769, "/mnt/nvme/phil/bfm3.matrix");}catch(IOException e){e.printStackTrace();}
      GLSMoveCost gmc = new SantaGLSMoveCost(penalties, 0, 197769); //GLSMoveCost(penalties, 0, points.length);
      tspSolver = new FLS(gmc);
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

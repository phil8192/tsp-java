package net.parasec.tsp;

import net.parasec.tsp.impl.FLS;
import net.parasec.tsp.impl.Point;
import net.parasec.tsp.impl.MutateFLS;

public final class TSPSolver implements TSP {
    public double solve(final Point[] points) {
        //final FLS fls = new FLS();
        //return fls.optimise(points);
        MutateFLS mutateFLS = new MutateFLS();
        return mutateFLS.optimise(points);
    }
}

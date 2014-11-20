package net.parasec.tsp;

import net.parasec.tsp.impl.Point;

public final class TSPMain {
    // args[0] = .tsp
    // args[1] output:
    //   each line = vertex
    //   line>line = edge.
    public static void main(final String[] args) {
	final Point[] points = PointsReader.read(args[0]); 
	final TSP tsp = new TSPSolver();
	
	// error = distance found.
	final long l = System.currentTimeMillis();
	final double error = tsp.solve(points);
	
	DumpPoints.dump(points, args[1]);

	System.out.printf("tour length = %.4f\toptimisation time = %.2f seconds.\n", error, (System.currentTimeMillis() - l)/1000d);	
    }
}

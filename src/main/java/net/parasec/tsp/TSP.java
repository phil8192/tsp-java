package net.parasec.tsp;

import net.parasec.tsp.impl.Point;

public interface TSP {
    double optimise(Point[] points, double score);
}

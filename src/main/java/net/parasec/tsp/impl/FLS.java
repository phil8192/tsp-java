package net.parasec.tsp.impl;

import net.parasec.tsp.TSP;

// Fast Local Search, 2-Opt "Dont look bits"
public final class FLS implements TSP {

  private final java.util.SplittableRandom prng = new java.util.SplittableRandom();

    /**
     * 2-Opt a tour.
     * removes 2 edges, then reconstructs a tour.
     * in general (from http://en.wikipedia.org/wiki/2-opt):
     *   1. take route[0] to route[i-1] and add them in order to new_route
     *   2. take route[i] to route[k] and add them in reverse order to new_route
     *   3. take route[k+1] to end and add them in order to new_route
     *
     * note that reverse is called with min(from,to)+1, max(from,to). in a
     * 2-opt move, 2 edges are removed, leaving 2 disconnected sub-tours.
     * either one of these subtours is then reversed and the 2 subtours
     * reconnected in a different (shorter) way.
     *
     * Bentley, in his experiements on TSP heuristics paper notes
     * that since either subtour can be reversed, it is best to
     * reverse the shortest one, otherwise an arbitrary reversal
     * will be N/2 array accesses.
     *
     * this implementation currently reverses whatever subtour does
     * not wrap around -which could be the larger of the two.
     */
    private void reverse(final Point[] x, final int from, final int to) {
        for(int i = from, j = to; i < j; i++, j--) {
            final Point tmp = x[i];
            x[i] = x[j];
            x[j] = tmp;
        }
    }

    /**
     * a tour is a circle. wrap around.
     */
    private int wrap(final int i, final int max) {
        return (max+i) % max;
    }

    /**
     * cost of a 2-Opt.
     * cost of replacing existing edges (ab), (cd) with new edges (ac) (bd).
     * returns the delta of a 2-Opt move. a negative delta indicates that
     * performing this 2-Opt will result in a shorter tour, and a positive delta
     * indicates that this 2-Opt will result in a longer tour.
     *
     * this function is the main hotspot in the optimisation. it is not feasible
     * to pre-compute a matrix (a lookup table) for a tour with N cities, since
     * this will be O(N^2) and the most compact representation will be (N^2-N)/2.
     *
     * good optimisation: most of the time the algorithm is evaluating bad moves,
     * in the obvious case where 2 edge exchanges would result in 2 longer
     * edges, avoid 4 square root operations by comparing squares. this results
     * in a 40% speed up in this code.
     */
    private double moveCost(final Point a, final Point b,
                            final Point c, final Point d) {

	// original edges (ab) (cd)
	final double _ab = a._distance(b), _cd = c._distance(d);

	// candidate edges (ac) (bd)
	final double _ac = a._distance(c), _bd = b._distance(d);

	// triangle of inequality: at least 1 edge will be shorter.
	// if both will be longer, there will be no improvement.
	// return a positive delta to indicate no improvement.
	if(_ab < _ac && _cd < _bd)
	    return 1;

	// otherwise must calculate distance delta.
	return (Maths.sqrt(_ac) + Maths.sqrt(_bd)) -
	       (Maths.sqrt(_ab) + Maths.sqrt(_cd));
    }

    /**
     * set active bits for 4 vertices making up edges ab, cd.
     */
    private void activate(final Point a, final Point b,
			  final Point c, final Point d) {
        a.setActive(true); b.setActive(true);
	c.setActive(true); d.setActive(true);
    }

    public void mutate(Point[] points) {
        // 0 1 2 3 4 5 [6 7] [8 9]
        int randomCityA = prng.nextInt(points.length - 3);
        int randomCityB = randomCityA + 2 + prng.nextInt(points.length - randomCityA - 3);
        reverse(points, randomCityA + 1, randomCityB);
        points[randomCityA].setActive(true);
        points[randomCityA + 1].setActive(true);
        points[randomCityB].setActive(true);
        points[randomCityB + 1].setActive(true);
    }

    /**
     * try to find a move from the current city.
     * given the current city, search for a 2-opt move that will result in
     * an improvement to the tour length. the edge before the current city,
     * (prevPoint,currenPoint) and after (currentPoint,nextPoint) are compared
     * to all over edges (c,d), starting at (c=currentPoint+2, d=currentPoint+3)
     * until an improvement is found.
     */
    private double findMove(final int current, final Point currentPoint,
			    final Point[] points, final int numCities) {

	// previous and next city index and point object.
	final int prev = wrap(current-1, numCities);
        final int next = wrap(current+1, numCities);
	final Point prevPoint = points[prev];
	final Point nextPoint = points[next];

	// iterate through pairs (i,j) where i = current+2 j = current+3
	// until i = current+numCities-2, j = current+numCities-1.
	// if points = {0,1,2,3,4,5,6,7,8,9}, current = 4, this will produce:
	// (6,7) (7,8) (8,9) (9,0) (0,1) (1,2) (2,3)
	for(int i = wrap(current+2, numCities), j = wrap(current+3, numCities);
                j != current;
                i = j, j = wrap(j+1, numCities)) {

            final Point c = points[i];
            final Point d = points[j];

	    // previous edge:
	    // see if swaping the current 2 edges:
	    // (prevPoint, currentPoint) (c, d) to:
	    // (prevPoint, c) (currentPoint, d)
	    // will result in an improvement. if so, set active bits for
	    // the 4 vertices involved and reverse everything between:
	    // (currentPoint, c).
            final double delta1 = moveCost(prevPoint, currentPoint, c, d);
            if(delta1 < 0) {
                activate(prevPoint, currentPoint, c, d);
                reverse(points, Math.min(prev, i)+1, Math.max(prev, i));
                return delta1;
            }

	    // next edge:
	    // see if swaping the current 2 edges:
	    // (currentPoint, nextPoint) (c, d) to:
	    // (currentPoint, c) (nextPoint, d)
	    // will result in an improvement. if so, set active bits for
	    // the 4 vertices involved and reverse everything between:
	    // (nextPoint, c).
            final double delta2 = moveCost(currentPoint, nextPoint, c, d);
            if(delta2 < 0) {
                activate(currentPoint, nextPoint, c, d);
                reverse(points, Math.min(current, i)+1, Math.max(current, i));
                return delta2;
            }

        }
	return 0.0;
    }

    /**
     * optimise a tour.
     * return a 2-Optimal tour.
     */
    public double optimise(final Point[] points) {
/*
      boolean any = false;
      for(int i = 0; i < points.length; i++) {
        if(points[i].isActive()) {
          System.out.println("init active " + i);
          any = true;
        }
      }
*/
	// total tour distance
        double best = Point.distance(points);
  //      if(!any) {
  //        return best;
  //      }


	//System.out.printf("tour length = %.4f\n", best);

	// total number of cities in the tour
        final int numCities = points.length;

	// numCities - visited = total number of active cities.
	// current = current city being explored.
        int visited = 0, current = 0;

	// terminate when a full rotation of of static order from city 1:N
	// has completed without making a move (when all cities are inactive).
	// the resulting tour (points) will be "2-Optimal" -that is, no further
	// imrovements are possible (local optima).
        while(visited < numCities) {
            final Point currentPoint = points[current];
            if(currentPoint.isActive()) {
    //            System.out.println("active = " + current);
	        // from the current city, try to find a move.
		final double modified = findMove(current, currentPoint,
		        points, numCities);

		// if a move was found, go to previous city.
		// best is += modified delta.
                if(modified < 0) {
                    current = wrap(current-1, numCities);
                    visited = 0;
                    best += modified;
                    //System.out.println("move found: " + best);
                    continue;
                }
                  //System.out.println("no move");
                  currentPoint.setActive(false);

            } else {
              //System.out.println("inactive " + current);
            }

	    // if city is inactive or no moves found, go to next city.
            current = wrap(current+1, numCities);
            visited++;
        }
        return best;
    }

}

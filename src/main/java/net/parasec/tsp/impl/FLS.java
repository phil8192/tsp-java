package net.parasec.tsp.impl;

import net.parasec.tsp.TSP;

// Fast Local Search, 2-Opt "Dont look bits"
public class FLS implements TSP {

  private final java.util.SplittableRandom prng = new java.util.SplittableRandom(); // todo: MutantFLS extends FLS
  private final TwoOptMoveCost twoOptMoveCost;


  public FLS() {
    this(new BasicTwoOptMoveCost());
  }

  public FLS(TwoOptMoveCost twoOptMoveCost) {
    this.twoOptMoveCost = twoOptMoveCost;
  }

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
  * set active bits for 4 vertices making up edges ab, cd.
  */
  private void activate(final Point a, final Point b,
                        final Point c, final Point d) {
    a.setActive(true); b.setActive(true);
    c.setActive(true); d.setActive(true);
  }

  public void mutate(Point[] points) { // todo: MutantFLS extends FLS
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

    final TwoOptMoveCost twoOptMoveCost = this.twoOptMoveCost;

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

      /*
      final double delta1 = twoOptMoveCost.moveCost(prevPoint, currentPoint, c, d);
      if(delta1 < 0) {
        activate(prevPoint, currentPoint, c, d);
        reverse(points, Math.min(prev, i)+1, Math.max(prev, i));
        return delta1;
      }
      */
      double curCost = Point.distance(points);
      reverse(points, Math.min(prev, i)+1, Math.max(prev, i));
      double newCost1 = Point.distance(points);
      double delta1 = newCost1 - curCost;
      if(delta1 < 0) {
        activate(prevPoint, currentPoint, c, d);
        return delta1;
      } else {
        reverse(points, Math.min(prev, i)+1, Math.max(prev, i));
      }

      // next edge:
      // see if swaping the current 2 edges:
      // (currentPoint, nextPoint) (c, d) to:
      // (currentPoint, c) (nextPoint, d)
      // will result in an improvement. if so, set active bits for
      // the 4 vertices involved and reverse everything between:
      // (nextPoint, c).

      /*
      final double delta2 = twoOptMoveCost.moveCost(currentPoint, nextPoint, c, d);
      if(delta2 < 0) {
        activate(currentPoint, nextPoint, c, d);
        reverse(points, Math.min(current, i)+1, Math.max(current, i));
        return delta2;
      }
      */
      reverse(points, Math.min(current, i)+1, Math.max(current, i));
      final double newCost2 = Point.distance(points);
      final double delta2 = newCost2 - curCost;
      if(delta2 < 0) {
        activate(currentPoint, nextPoint, c, d);
        return delta2;
      } else {
        reverse(points, Math.min(current, i)+1, Math.max(current, i));
      }


    }
    return 0d;
  }

  /**
  * optimise a tour.
  * return a 2-Optimal tour.
  */
  public double optimise(final Point[] points, double score) {

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
        // from the current city, try to find a move.
        final double modified = findMove(current, currentPoint,
        points, numCities);

        // if a move was found, go to previous city.
        // best is += modified delta.
        if(modified < 0) {
          current = wrap(current-1, numCities);
          visited = 0;
          score += modified;
          continue;
        }
        currentPoint.setActive(false);
      }

      // if city is inactive or no moves found, go to next city.
      current = wrap(current+1, numCities);
      visited++;
    }
    return score;
  }

}

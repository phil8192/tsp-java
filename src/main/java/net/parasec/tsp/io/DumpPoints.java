package net.parasec.tsp.io;

import net.parasec.tsp.algo.Point;

import java.io.*;

public final class DumpPoints {
  public static void dump(Point[] points, String file) {
    try {
      OutputStream os = null;
      try {
        os = new BufferedOutputStream(new FileOutputStream(new File(file)));
        for(Point p : points) {
          os.write((p + "\n").getBytes());
        }
        os.write((points[0] + "\n").getBytes());
      } finally {
        if(os != null)
          os.close();
      }
    } catch(IOException e) {
      System.err.println(e);
    }
  }
}
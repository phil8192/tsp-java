package net.parasec.tsp.io;

import net.parasec.tsp.algo.Point;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/*
 * reads in tsp files in the tsplib format here:
 * http://www.math.uwaterloo.ca/tsp/data/
 */
public final class TSPReader {
  private static final String REGX
      = "^([0-9]+)\\s+([0-9]+\\.[0-9]+|[0-9]+)\\s+([0-9]+\\.[0-9]+|[0-9]+)$";
  private static final String CHARSET = "US-ASCII";
  private static final String NL = System.getProperty("line.separator");

  public static final Point[] read(final String tspFile) {
    try {
      FileInputStream fis = null;
      FileChannel fc = null;
      Scanner scanner = null;
      try {
        fis = new FileInputStream(tspFile);
        fc = fis.getChannel();
        final MappedByteBuffer byteBuffer
            = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
        final Charset charset = Charset.forName(CHARSET);
        final CharsetDecoder decoder = charset.newDecoder();
        final CharBuffer charBuffer = decoder.decode(byteBuffer);
        final ArrayList<Point> points = new ArrayList<Point>();
        final Pattern pat = Pattern.compile(REGX);
        scanner = new Scanner(charBuffer).useDelimiter(NL);
        while(scanner.hasNext()) {
          final String line = scanner.next();
          final Matcher m = pat.matcher(line);
          if(m.find()) {
            final MatchResult mr = m.toMatchResult();
            final int id = Integer.parseInt(mr.group(1));
            final double x = Double.parseDouble(mr.group(2));
            final double y = Double.parseDouble(mr.group(3));
            points.add(new Point(id, x, y));
          }
        }
        return points.toArray(new Point[]{});
      } finally {
        if(scanner != null) {
          scanner.close();
        }
        if(fc != null) {
          fc.close();
        }
        if(fis != null) {
          fis.close();
        }
      }
    } catch(final IOException e) {
      System.err.println(e);
    }
    return null;
  }
}

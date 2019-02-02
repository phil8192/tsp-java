package net.parasec.tsp;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

/*
 * reads in simple point file. rows = points, cols = x y.
 * tsplib format can be pre-processed into this format with:
 *
 * cat tsplibformat.file |grep "^[0-9]" |awk '{print $2 " " $3}'
 */
public class PointsReader<E extends Point> {
  private static final String CHARSET = "US-ASCII";
  private static final String NL = System.getProperty("line.separator");

  public interface PointParser<E extends Point> {
    E parse(String[] line);
  }

  public E[] read(final String tspFile, PointParser<E> pointParser) {
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
        final List<E> points = new ArrayList<>();
        scanner = new Scanner(charBuffer).useDelimiter(NL);
        while(scanner.hasNext()) {
          final String line = scanner.next();
          final String[] sline = line.split("\\s");

          E point = pointParser.parse(sline);
          points.add(point);

        }
        return points.toArray((E[]) Array.newInstance(points.get(0).getClass(), points.size()));
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
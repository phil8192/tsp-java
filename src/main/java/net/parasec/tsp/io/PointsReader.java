package net.parasec.tsp.io;

import net.parasec.tsp.algo.Point;

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
 * https://www.iwr.uni-heidelberg.de/groups/comopt/software/TSPLIB95/
 *
 * reads in simple point file. rows = points, cols = x y.
 * TSPLIB format can be pre-processed into this format with:
 *
 * cat tsplibformat.file |grep "^[0-9]" |awk '{print $2 " " $3}'
 */
public class PointsReader<E extends Point> {
  private static final String CHARSET = "US-ASCII";
  private static final String NL = System.getProperty("line.separator");

  public interface PointParser<E extends Point> {
    E parse(String[] line);
  }

  public E[] read(String tspFile, PointParser<E> pointParser) {
    try {
      FileInputStream fis = null;
      FileChannel fc = null;
      Scanner scanner = null;
      try {
        fis = new FileInputStream(tspFile);
        fc = fis.getChannel();
        MappedByteBuffer byteBuffer
            = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
        Charset charset = Charset.forName(CHARSET);
        CharsetDecoder decoder = charset.newDecoder();
        CharBuffer charBuffer = decoder.decode(byteBuffer);
        List<E> points = new ArrayList<>();
        scanner = new Scanner(charBuffer).useDelimiter(NL);
        while(scanner.hasNext()) {
          String line = scanner.next();
          String[] sline = line.split("\\s");

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
    } catch(IOException e) {
      System.err.println(e);
    }
    return null;
  }

}
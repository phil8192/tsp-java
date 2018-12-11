package net.parasec.tsp.impl;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

// memory mapped.
public class BFPM implements PenaltyMatrix {
  private static final int MAPPING_SIZE = 1 << 30; // 1024*1024*1024 bytes/1 gig.
  private final RandomAccessFile raf;
  private final List<ByteBuffer> mappings = new ArrayList<>();
  private final long numCities;


  public BFPM(int numCities) throws IOException {
    this.numCities = numCities;
    this.raf = new RandomAccessFile("/home/phil/tsp-java-gls/bfm.matrix", "rw");
    try {
      long size = 2L * numCities*(numCities-1) / 2; // triangle - diag.
      for (long offset = 0; offset < size; offset += MAPPING_SIZE) {
        long size2 = Math.min(size - offset, MAPPING_SIZE);
        mappings.add(raf.getChannel()
            .map(FileChannel.MapMode.READ_WRITE, offset, size2));
      }
    } catch (IOException e) {
      raf.close();
      e.printStackTrace();
    }
  }

  private long position(int x, int y) {
    int i = Math.min(x, y), j = Math.max(x, y);
    long n = i+1;
    long offset = i*numCities + j;
    return offset - n*(n+1)/2; // rm diag + triangle.
  }

  public int getPenalty(int i, int j) {
    long p = position(i, j) * 2L;
    int mapN = (int) (p / MAPPING_SIZE);
    int offN = (int) (p % MAPPING_SIZE);
    return mappings.get(mapN).getShort(offN);
  }

  public void incPenalty(int i, int j) {
    long p = position(i, j) * 2L;
    int mapN = (int) (p / MAPPING_SIZE);
    int offN = (int) (p % MAPPING_SIZE);
    mappings.get(mapN).putShort(offN, (short) (mappings.get(mapN).getShort(offN) + 1));
  }

  public void clear() {

  }
}

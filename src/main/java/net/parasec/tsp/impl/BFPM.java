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
  private final int numCities;


  public BFPM(int numCities) throws IOException {
    this.numCities = numCities;
    this.raf = new RandomAccessFile("/mnt/raid/phil/bfm.matrix", "rw");
    try {
      long size = 4L * numCities * numCities;
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
    return (long) y*numCities + x;
  }

  public int getPenalty(int i, int j) {
    long p = position(i, j) * 4L;
    int mapN = (int) (p / MAPPING_SIZE);
    int offN = (int) (p % MAPPING_SIZE);
    return mappings.get(mapN).getInt(offN);
  }

  public void incPenalty(int i, int j) {
    long p = position(i, j) * 4L;
    int mapN = (int) (p / MAPPING_SIZE);
    int offN = (int) (p % MAPPING_SIZE);
    mappings.get(mapN).putInt(offN, mappings.get(mapN).getInt(offN)+1);
  }

  public void clear() {

  }
}

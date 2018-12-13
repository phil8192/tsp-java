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
  //private final LRUCache lru;

  public BFPM(int numCities) throws IOException {
    this.numCities = numCities;
    //this.lru = new LRUCache(numCities*1000);
    this.raf = new RandomAccessFile("/mnt/nvme/phil/bfm.matrix", "rw");
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

  private long position(final int x, final int y) {
    final int i = Math.min(x, y), j = Math.max(x, y);
    final long n = i+1;
    final long offset = i*numCities + j;
    return offset - n*(n+1)/2; // rm diag + triangle.
  }

  public int getPenalty(final int i, final int j) {
    final long p = position(i, j) * 2L;
    //Short penalty = lru.get(p);
    //if(penalty == null) {
    //  if(lru.size() < lru.getMax()) {
    //    penalty = 0;
    //  } else {
    final int mapN = (int) (p / MAPPING_SIZE);
    final int offN = (int) (p % MAPPING_SIZE);
    return mappings.get(mapN).getShort(offN);
    // penalty = mappings.get(mapN).getShort(offN);
    //lru.put(p, penalty);
    //  }
    //}
    //return penalty;
  }

  public int incPenalty(final int i, int j) {
    final long p = position(i, j) * 2L;
    final int mapN = (int) (p / MAPPING_SIZE);
    final int offN = (int) (p % MAPPING_SIZE);
    final ByteBuffer b = mappings.get(mapN);
    short penalty = (short) (b.getShort(offN) + 1);
    //lru.put(p, penalty);
    b.putShort(offN, penalty);
    return penalty;
  }

  public void clear() {

  }
}

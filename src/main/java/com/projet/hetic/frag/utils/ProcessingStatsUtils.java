package com.projet.hetic.frag.utils;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.projet.hetic.frag.model.Chunk;

import lombok.Getter;

@Getter
public class ProcessingStatsUtils {
  private final AtomicInteger order = new AtomicInteger(0);
  private final AtomicInteger offset = new AtomicInteger(0);
  private final AtomicLong totalCompressedSize = new AtomicLong(0);

  public void updateStats(Chunk chunk) {
    order.getAndIncrement();
    offset.addAndGet(chunk.getSizeOriginal());
    totalCompressedSize.addAndGet(chunk.getSizeCompressed());
  }
}
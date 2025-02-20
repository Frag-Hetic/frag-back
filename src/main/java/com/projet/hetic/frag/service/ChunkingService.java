package com.projet.hetic.frag.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.projet.hetic.frag.config.ChunkingConfig;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChunkingService {
  private final ChunkingConfig config;

  public Stream<byte[]> chunkFile(InputStream inputStream) throws IOException {
    List<byte[]> chunks = new ArrayList<>();
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    byte[] window = new byte[config.getWindowSize()];
    int bytesRead;
    int windowIndex = 0;
    int rollingHash = 0;
    int breakpointMask = Integer.decode(config.getBreakpointMask());

    while ((bytesRead = inputStream.read()) != -1) {
      buffer.write(bytesRead);

      rollingHash = ((rollingHash << 1) + bytesRead) & 0xFFFF;
      if (buffer.size() > config.getWindowSize()) {
        rollingHash -= window[windowIndex];
      }
      window[windowIndex] = (byte) bytesRead;
      windowIndex = (windowIndex + 1) % config.getWindowSize();

      if ((rollingHash & breakpointMask) == 0 && buffer.size() >= config.getChunkMinSize()
          || buffer.size() >= config.getChunkMaxSize()) {
        chunks.add(buffer.toByteArray());
        buffer.reset();
      }
    }

    if (buffer.size() > 0) {
      chunks.add(buffer.toByteArray());
    }

    return chunks.stream();
  }
}
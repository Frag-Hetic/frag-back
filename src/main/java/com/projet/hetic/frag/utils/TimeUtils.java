package com.projet.hetic.frag.utils;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

@Component
public class TimeUtils {
  public String getFormattedProcessingTime(Long processingTime) {
    if (processingTime == null) {
      return "00:00:00.000";
    }

    long hours = TimeUnit.MILLISECONDS.toHours(processingTime);
    long minutes = TimeUnit.MILLISECONDS.toMinutes(processingTime) % 60;
    long seconds = TimeUnit.MILLISECONDS.toSeconds(processingTime) % 60;
    long milliseconds = processingTime % 1000;

    return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds);
  }

}

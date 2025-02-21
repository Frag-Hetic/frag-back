package com.projet.hetic.frag.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TimeUtilsTest {

  private TimeUtils timeUtils;

  @BeforeEach
  void setUp() {
    timeUtils = new TimeUtils();
  }

  @Test
  void getFormattedProcessingTime_WithNullInput_ShouldReturnDefaultFormat() {
    // Act
    String result = timeUtils.getFormattedProcessingTime(null);

    // Assert
    assertThat(result).isEqualTo("00:00:00.000");
  }

  @Test
  void getFormattedProcessingTime_WithZeroMilliseconds_ShouldReturnZeroTime() {
    // Act
    String result = timeUtils.getFormattedProcessingTime(0L);

    // Assert
    assertThat(result).isEqualTo("00:00:00.000");
  }

  @Test
  void getFormattedProcessingTime_WithMillisecondsOnly_ShouldFormatCorrectly() {
    // Act
    String result = timeUtils.getFormattedProcessingTime(123L);

    // Assert
    assertThat(result).isEqualTo("00:00:00.123");
  }

  @Test
  void getFormattedProcessingTime_WithSecondsAndMilliseconds_ShouldFormatCorrectly() {
    // Arrange
    long timeInMillis = 1234L; // 1 seconde et 234 millisecondes

    // Act
    String result = timeUtils.getFormattedProcessingTime(timeInMillis);

    // Assert
    assertThat(result).isEqualTo("00:00:01.234");
  }

  @Test
  void getFormattedProcessingTime_WithMinutesSecondsAndMilliseconds_ShouldFormatCorrectly() {
    // Arrange
    long timeInMillis = 61234L; // 1 minute, 1 seconde et 234 millisecondes

    // Act
    String result = timeUtils.getFormattedProcessingTime(timeInMillis);

    // Assert
    assertThat(result).isEqualTo("00:01:01.234");
  }

  @Test
  void getFormattedProcessingTime_WithHoursMinutesSecondsAndMilliseconds_ShouldFormatCorrectly() {
    // Arrange
    long timeInMillis = 3661234L; // 1 heure, 1 minute, 1 seconde et 234 millisecondes

    // Act
    String result = timeUtils.getFormattedProcessingTime(timeInMillis);

    // Assert
    assertThat(result).isEqualTo("01:01:01.234");
  }
}
package com.gmail.collinsmith70.checksumer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class ProgressBar {
  public static final int PRECISION = 50;
  public static final int MAX_LENGTH = PRECISION + 10;
  private static final String PROGRESS_FORMAT;
  static {
    PROGRESS_FORMAT = String.format("%%7s [%%-%ds]", PRECISION);
  }

  static final String DELETE_STRING;
  static {
    DELETE_STRING = StringUtils.repeat('\b', MAX_LENGTH);
  }

  private String progress;
  private long total;

  public ProgressBar(long total) {
    this(0, total);
  }

  public ProgressBar(long current, long total) {
    this.total = total;
  }

  public String update(long current) {
    Validate.isTrue(current >= 0, "The value must be greater than zero: %d", current);
    current = Math.min(current, total);

    double percent = (double) current / total;
    int progress = (int) Math.floor(percent * PRECISION);
    this.progress = String.format(PROGRESS_FORMAT,
        String.format("%.02f%%", 100.0 * percent),
        StringUtils.repeat('=', progress));

    return this.progress;
  }

  @Override
  public String toString() {
    return progress;
  }
}
package com.gmail.collinsmith70.checksumer;

/**
 * Ascii progress meter. On completion this will reset itself,
 * so it can be reused
 * <br /><br />
 * 100% ==================================================
 */
public class ProgressBar {
  private StringBuilder progress;

  /**
   * initialize progress bar properties.
   */
  public ProgressBar() {
    init();
  }

  /**
   * called whenever the progress bar needs to be updated.
   * that is whenever progress was made.
   *
   * @param done  an int representing the work done so far
   * @param total an int representing the total work
   */
  public void update(long done, long total) {
    double percent = (double)done / total * 100;
    int extrachars = ((int)percent / 2) - this.progress.length();

    while (extrachars-- > 0) {
      progress.append('=');
    }

    String percentage = String.format("%.02f%%", percent);
    System.out.printf("\r%7s [%-50s]", percentage, progress);

    if (done >= total) {
      System.out.flush();
      System.out.println();
      init();
    }
  }

  private void init() {
    this.progress = new StringBuilder(60);
  }
}
package com.gmail.collinsmith70.checksumer;

import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;

import javax.xml.bind.DatatypeConverter;

public class ChecksumCalculator implements Runnable {

  private final Path path;
  private final MessageDigest messageDigest;
  private final ResultCallback resultCallback;
  private final IOExceptionCallback IOExceptionCallback;
  private final ProgressCallback progressCallback;

  public ChecksumCalculator(Path p, MessageDigest messageDigest, ResultCallback resultCallback,
                            IOExceptionCallback IOExceptionCallback,
                            ProgressCallback progressCallback) {
    Validate.isTrue(p != null);
    Validate.isTrue(messageDigest != null);
    Validate.isTrue(resultCallback != null);
    Validate.isTrue(IOExceptionCallback != null);

    this.path = p;
    this.messageDigest = messageDigest;
    this.resultCallback = resultCallback;
    this.IOExceptionCallback = IOExceptionCallback;
    this.progressCallback = progressCallback;
  }

  @Override
  public void run() {
    try {
      InputStream is = Files.newInputStream(path);
      DigestInputStream dis = new DigestInputStream(is, messageDigest);

      long total = Files.size(path);
      long offset = 0;
      byte[] buffer = new byte[8192];
      try {
        ProgressBar progressBar = new ProgressBar();
        int read;
        while ((read = dis.read(buffer)) != -1) {
          offset += read;
          if (progressCallback != null) {
            progressCallback.onProgress(offset, total);
          }
        }
      } finally {
        dis.close();
      }

      String result = DatatypeConverter.printHexBinary(messageDigest.digest());
      resultCallback.onResult(result);
    } catch (IOException e) {
      IOExceptionCallback.onIOException(e);
    }
  }

  public interface ProgressCallback {
    void onProgress(long progress, long total);
  }

  public interface ResultCallback {
    void onResult(String hash);
  }

  public interface IOExceptionCallback {
    void onIOException(IOException e);
  }

}

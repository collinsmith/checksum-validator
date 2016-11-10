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
  private final IOExceptionCallback IOExceptionCallback;

  private String result;
  private long total;
  private long offset;
  private boolean started;

  public ChecksumCalculator(Path p, MessageDigest messageDigest,
                            IOExceptionCallback IOExceptionCallback) {
    Validate.isTrue(p != null);
    Validate.isTrue(messageDigest != null);
    Validate.isTrue(IOExceptionCallback != null);

    this.path = p;
    this.messageDigest = messageDigest;
    this.IOExceptionCallback = IOExceptionCallback;

    this.started = false;
  }

  public long getOffset() {
    return offset;
  }

  public long getTotal() {
    return total;
  }

  public double getProgress() {
    return (double)offset / total;
  }

  public boolean hasStarted() {
    return started;
  }

  public String getResult() {
    return result;
  }

  @Override
  public void run() {
    try {
      InputStream is = Files.newInputStream(path);
      DigestInputStream dis = new DigestInputStream(is, messageDigest);

      this.result = "Calculating...";
      this.total = Files.size(path);
      this.offset = 0;
      this.started = true;
      byte[] buffer = new byte[8192];
      try {
        int read;
        while ((read = dis.read(buffer)) != -1) {
          offset += read;
        }
      } finally {
        dis.close();
      }

      this.result = DatatypeConverter.printHexBinary(messageDigest.digest());
    } catch (IOException e) {
      IOExceptionCallback.onIOException(e);
    }
  }

  public interface IOExceptionCallback {
    void onIOException(IOException e);
  }

}

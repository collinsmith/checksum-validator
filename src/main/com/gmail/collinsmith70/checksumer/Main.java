package com.gmail.collinsmith70.checksumer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.List;
import java.util.Set;

public class Main {

  private static final Options OPTIONS = new Options();
  static {
    OPTIONS.addOption("h", "help", false, "Prints this message");
    OPTIONS.addOption("m", "mode", true, "Algorithm to use (Defaults to MD5)");
    OPTIONS.addOption("d", "diff", true, "Checks the difference between this hash and the computed one");
    OPTIONS.addOption("v", "verbose", false, "Outputs progress of calculation");
  }

  private static final long updateDelay = 25;

  public static void main(String... args) throws ParseException, IOException, InterruptedException {
    CommandLineParser parser = new DefaultParser();
    CommandLine cli = parser.parse(OPTIONS, args);

    boolean verbose = cli.hasOption("v");

    if (cli.hasOption("h")) {
      printHelp();
      System.exit(0);
    }

    MessageDigest messageDigest = null;
    String algorithm = cli.getOptionValue("m", "md5");
    try {
      messageDigest = MessageDigest.getInstance(algorithm);
    } catch (NoSuchAlgorithmException e) {
      System.out.println("Unsupported mode given: " + algorithm);
      printAlgorithms();
      System.exit(0);
    }

    List<String> argsList = cli.getArgList();
    if (argsList.isEmpty()) {
      printHelp();
      System.exit(0);
    } else if (argsList.size() > 1) {
      System.out.println("Invalid number of arguments. Expected 1, found " + argsList.size());
      System.exit(0);
    }

    String arg = argsList.get(0);
    Path file = Paths.get(arg);
    if (verbose) {
      System.out.println("Opening " + file);
    }

    ChecksumCalculator calculator
        = new ChecksumCalculator(file, messageDigest, e -> e.printStackTrace());

    Thread t = new Thread(calculator);
    t.setName("ChecksumCalculator");
    t.start();

    if (verbose) {
      ProgressBar progressBar = null;
      while (t.isAlive()) {
        if (calculator.hasStarted()) {
          if (progressBar == null) {
            progressBar = new ProgressBar(calculator.getTotal());
          } else {
            System.out.print(ProgressBar.DELETE_STRING);
          }

          progressBar.update(calculator.getOffset());
          System.out.print(progressBar);
        }

        Thread.sleep(updateDelay);
      }

      if (progressBar == null) {
        progressBar = new ProgressBar(calculator.getTotal());
      } else {
        System.out.print(ProgressBar.DELETE_STRING);
      }

      progressBar.update(calculator.getTotal());
      System.out.println(progressBar);
    } else {
      t.join();
    }

    String result = calculator.getResult();
    if (cli.hasOption("d")) {
      String validate = cli.getOptionValue("d");
      if (!result.equalsIgnoreCase(validate)) {
        System.out.println("Calculated hash does not match!");
      } else {
        System.out.println("Calculated hash matches!");
      }
    } else {
      System.out.println(result);
    }

  }

  public static void printHelp() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("chk", OPTIONS);
    System.out.println();
    printAlgorithms();
    System.exit(0);
  }

  public static void printAlgorithms() {
    System.out.println("Available Algorithms:");
    Provider[] providers = Security.getProviders();
    for (Provider p : providers) {
      //String providerStr = String.format("%s/%s/%f\n", p.getName(), p.getInfo(), p.getVersion());
      Set<Provider.Service> services = p.getServices();
      for (Provider.Service s : services) {
        if ("MessageDigest".equals(s.getType())) {
          System.out.printf("%s\t%s%n", s.getAlgorithm(), s.getClassName());
        }
      }
    }
  }

  public static String byteToHexString(byte[] b) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < b.length; i++) {
      builder.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
    }

    return builder.toString();
  }

}

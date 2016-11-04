package com.gmail.collinsmith70.checksumer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.List;
import java.util.Set;

public class Main {

  private static final Options OPTIONS = new Options();
  static {
    OPTIONS.addOption("?", "help", true, "Prints this message");
    OPTIONS.addOption("m", "mode", true, "Algorithm to use (only md5 supported currently)");
    OPTIONS.addOption("v", "validate", true, "Validates that the hash against this string");
  }

  public static void main(String... args) throws ParseException, IOException {
    CommandLineParser parser = new DefaultParser();
    CommandLine cli = parser.parse(OPTIONS, args);

    if (cli.hasOption("?")) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("chk", OPTIONS);
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
    String file = argsList.get(0);
    System.out.println("Digesting " + file);
    try (InputStream is = Files.newInputStream(Paths.get(file));
         DigestInputStream dis = new DigestInputStream(is, messageDigest)) {
    }

    String result = byteToHexString(messageDigest.digest());
    if (cli.hasOption("v")) {
      String validate = cli.getOptionValue("v");
      if (result.equalsIgnoreCase(validate)) {
        System.out.println("The hashes are equal");
      } else {
        System.out.println("The hashes are not equal");
      }
    } else {
      System.out.println(result);
    }
  }

  public static void printAlgorithms() {
    System.out.println("Supported Modes:");
    Provider[] providers = Security.getProviders();
    for (Provider p : providers) {
      String providerStr = String.format("%s/%s/%f\n", p.getName(), p.getInfo(), p.getVersion());
      Set<Provider.Service> services = p.getServices();
      for (Provider.Service s : services) {
        if ("MessageDigest".equals(s.getType())) {
          System.out.printf("\t%s/%s/%s", s.getType(),
              s.getAlgorithm(), s.getClassName());
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
package org.some.codeadvent.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day01 {

  public static void main(String[] args) throws IOException {

    String calibrationInput = "/day01-input.txt";
    int result = recover(calibrationInput, Day01::recoverCharDigit);
    System.out.println("Sum: " + result);
    result = recover(calibrationInput, Day01::recoverRegex);
    System.out.println("Sum: " + result);
    result = recover(calibrationInput, Day01::recoverBySubstring);
    System.out.println("Sum: " + result);
  }

  private static int recover(String calibrationFileName, ToIntFunction<String> decipher) throws IOException {
    InputStream calibrationFile = Day01.class.getResourceAsStream(calibrationFileName);
    if (calibrationFile == null) {
      throw new IllegalStateException("Resource with the calibration data could not be found");
    }
    try (BufferedReader br = new BufferedReader(new InputStreamReader(calibrationFile))) {
      return br.lines()
//          .peek(System.out::println)
          .mapToInt(decipher)
//          .peek(System.out::println)
          .sum();
    }
  }

  private static int recoverCharDigit(String line) {
    char first = 0;
    char last = 0;

    for (char c: line.toCharArray()) {
      if (Character.isDigit(c)) {
        if (first == 0) {
          first = c;
        }
        last = c;
      }
    }
    String result = ("" + first) + last;
    return Integer.parseInt(result);
  }

  private static final Pattern digitPattern = Pattern.compile("(\\d|one|two|three|four|five|six|seven|eight|nine)");

  private static int recoverRegex(String line) {
    Matcher matcher = digitPattern.matcher(line);
    int first = 0;
    int last = 0;

    while (matcher.find()) {
      String group = matcher.group();
      int digit = transform(group);
      if (first == 0) {
        first = digit;
      }
      last = digit;
    }
    return 10 * first + last;
  }

  private static final List<String> listOfDigits = List.of("1", "2", "3", "4", "5", "6", "7", "8", "9",
      "one", "two", "three", "four", "five", "six", "seven", "eight", "nine");

  private static int recoverBySubstring(String line) {
    int first = 0;
    int last = 0;

    for (int i = 0; i < line.length(); i++) {
      for (String token: listOfDigits) {
        if (line.startsWith(token, i)) {
          if (first == 0) {
            first = transform(token);
          }
          last = transform(token);
        }
      }
    }
    return 10 * first + last;
  }

  private static int transform(String token) {
    return switch (token) {
      case "1", "one" -> 1;
      case "2", "two" -> 2;
      case "3", "three" -> 3;
      case "4", "four" -> 4;
      case "5", "five" -> 5;
      case "6", "six" -> 6;
      case "7", "seven" -> 7;
      case "8", "eight" -> 8;
      case "9", "nine" -> 9;

      default -> throw new IllegalArgumentException("Could not convert token '%s' to the digit: ".formatted(token));
    };
  }
}

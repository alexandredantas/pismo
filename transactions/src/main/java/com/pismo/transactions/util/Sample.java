package com.pismo.transactions.util;

public class Sample {
  public static TailCall<Long> square(long number, int max) {
    if(max > number) {
      return () -> square(number + number, max);
    } else {
      return TailCalls.done(number);
    }
  }

  public static void main(String[] args) {
    int max = 5000;

    System.out.println(square(1, max).invoke());
  }
}

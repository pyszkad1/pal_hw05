package pal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // Read the input
        String[] firstLine = br.readLine().split(" ");
        int Pmax = Integer.parseInt(firstLine[0]);
        long Mmax = Long.parseLong(firstLine[1]);
        int N = Integer.parseInt(firstLine[2]);

        String[] secondLine = br.readLine().split(" ");
        long[] sequence = new long[N];
        for (int i = 0; i < N; i++) {
            sequence[i] = Long.parseLong(secondLine[i]);
        }

        // Generate all prime numbers up to Pmax
        List<Long> primes = generatePrimes(Pmax);

        // Compute all possible values of M
        List<Long> possibleMs = computePossibleMs(primes, Mmax);

        // Determine the parameters (A, C, M)
        for (long M : possibleMs) {
            for (long A = 1; A < M; A++) {
                if (!isValidA(A, M, primes)) continue;
                for (long C = 1; C < M; C++) {
                    if (gcd(C, M) != 1) continue;
                    if (isValidLCG(A, C, M, sequence)) {
                        System.out.println(A + " " + C + " " + M);
                        return;
                    }
                }
            }
        }
    }

    // Generate primes up to Pmax using the Sieve of Eratosthenes
    public static List<Long> generatePrimes(int Pmax) {
        boolean[] isPrime = new boolean[Pmax + 1];
        for (int i = 2; i <= Pmax; i++) isPrime[i] = true;
        for (int p = 2; p * p <= Pmax; p++) {
            if (isPrime[p]) {
                for (int i = p * p; i <= Pmax; i += p) {
                    isPrime[i] = false;
                }
            }
        }
        List<Long> primes = new ArrayList<>();
        for (int i = 5; i <= Pmax; i++) {
            if (isPrime[i]) primes.add((long) i);
        }
        return primes;
    }

    // Compute all valid values of M
    public static List<Long> computePossibleMs(List<Long> primes, long Mmax) {
        List<Long> possibleMs = new ArrayList<>();
        int numPrimes = primes.size();
        int limit = 1 << numPrimes;
        for (int mask = 1; mask < limit; mask++) {
            long product = 1;
            for (int i = 0; i < numPrimes; i++) {
                if ((mask & (1 << i)) != 0) {
                    product *= primes.get(i);
                    if (product > Math.sqrt(Mmax)) break;
                }
            }
            long M = product * product;
            if (M <= Mmax) {
                possibleMs.add(M);
            }
        }
        return possibleMs;
    }

    // Check if A satisfies the constraints
    public static boolean isValidA(long A, long M, List<Long> primes) {
        long Aminus1 = A - 1;
        for (long prime : primes) {
            if (M % (prime * prime) == 0 && Aminus1 % prime != 0) return false;
        }
        return M % 4 != 0 || Aminus1 % 4 == 0;
    }

    // Check if the LCG parameters generate the correct sequence
    public static boolean isValidLCG(long A, long C, long M, long[] sequence) {
        long x = sequence[0];
        for (int i = 1; i < sequence.length; i++) {
            x = (A * x + C) % M;
            if (x != sequence[i]) return false;
        }
        return true;
    }

    // Compute GCD of two numbers
    public static long gcd(long a, long b) {
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
}

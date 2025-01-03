package pal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // Read input
        String[] firstLine = br.readLine().split(" ");
        int Pmax = Integer.parseInt(firstLine[0]);
        long Mmax = Long.parseLong(firstLine[1]);
        int N = Integer.parseInt(firstLine[2]);

        String[] secondLine = br.readLine().split(" ");
        long[] sequence = new long[N];
        for (int i = 0; i < N; i++) {
            sequence[i] = Long.parseLong(secondLine[i]);
        }

        // Generate primes up to Pmax
        List<Long> primes = generatePrimes(Pmax);
        List<Long> possibleMs = computePossibleMs(primes, Mmax);

        // Try each possible M
        for (long M : possibleMs) {
            long A = computeA(sequence, M);
            if (A == -1 || !isValidA(A, M, primes)) continue;
            long C = computeC(sequence, A, M);
            if (gcd(C, M) == 1 && isValidLCG(A, C, M, sequence)) {
                System.out.println(A + " " + C + " " + M);
                return;
            }
        }
    }

    // Generate primes up to Pmax using Sieve of Eratosthenes
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

    // Compute all possible values of M based on prime squares
    public static List<Long> computePossibleMs(List<Long> primes, long Mmax) {
        List<Long> possibleMs = new ArrayList<>();
        int numPrimes = primes.size();
        int limit = 1 << numPrimes;

        // Iterate over all subsets of primes to calculate squared products
        for (int mask = 1; mask < limit; mask++) {
            long product = 1;
            for (int i = 0; i < numPrimes; i++) {
                if ((mask & (1 << i)) != 0) {
                    product *= primes.get(i);
                    if (product > Math.sqrt(Mmax)) break;
                }
            }
            long M = product * product;

            if (M <= Mmax && M > 0) {
                possibleMs.add(M);
            }
        }
        return possibleMs;
    }

    // Compute A using modular inverse
    public static long computeA(long[] sequence, long M) {
        long x1 = sequence[0];
        long x2 = sequence[1];
        long x3 = sequence[2];

        // Compute differences
        long delta1 = (x2 - x1 + M) % M;
        long delta2 = (x3 - x2 + M) % M;

        // Compute modular inverse of delta1 modulo M
        long inv = multiplicativeInverse(delta1, M);
        if (inv == -1) return -1;  // Skip if inverse doesn't exist

        // Compute A
        long A = multiplyMod(delta2, inv, M);
        return A;
    }

    public static long computeC(long[] sequence, long A, long M) {
        long x1 = sequence[0];
        long x2 = sequence[1];

        // Compute C
        long C = (x2 - multiplyMod(A, x1, M) + M) % M;
        return C;
    }

    // Check if A satisfies conditions based on prime factorization of M
    public static boolean isValidA(long A, long M, List<Long> primes) {
        if (A < 0) return false;
        long Aminus1 = A - 1;
        for (long prime : primes) {

            if (M % (prime * prime) == 0 && Aminus1 % prime != 0) return false;
        }
        return M % 4 != 0 || Aminus1 % 4 == 0;
    }

    // Validate if the generated LCG sequence matches input
    public static boolean isValidLCG(long A, long C, long M, long[] sequence) {
        long x = sequence[0];
        for (int i = 1; i < sequence.length; i++) {
            // Using the optimized multiplyMod method here
            x = multiplyMod(A, x, M); // Efficient multiplication with modulo
            x = (x + C) % M; // Adding C and applying modulo
            if (x != sequence[i]) return false;
        }
        return true;
    }

    // Modular multiplication with overflow prevention
    public static long multiplyMod(long a, long b, long m) {
        long result = 0;
        a = a % m;

        while (b > 0) {
            if (b % 2 == 1) {
                result = (result + a) % m;
            }
            a = (a * 2) % m;
            b /= 2;
        }

        return result;
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

    // Modular multiplicative inverse (Extended Euclidean Algorithm)
    public static long multiplicativeInverse(long a, long modulus) {
        long s = 0, r = modulus, old_s = 1, old_r = a;
        while (r != 0) {
            long quotient = old_r / r;
            long temp = r;
            r = old_r - quotient * r;
            old_r = temp;
            temp = s;
            s = old_s - quotient * s;
            old_s = temp;
        }
        return old_r > 1 ? -1 : (old_s < 0 ? old_s + modulus : old_s);
    }
}

package com.sqshq.robotsystem.processor.service;

import org.springframework.stereotype.Service;

/**
 * Stateless service which calculates dummy CPU-intensive task
 */
@Service
public class ProcessorServiceImpl implements ProcessorService {

    @Override
    public int compute(int n) {
        return nthPrime(n);
    }

    private int nthPrime(int n) {
        int candidate, count;
        for (candidate = 2, count = 0; count < n; ++candidate) {
            if (isPrime(candidate)) {
                ++count;
            }
        }
        return candidate - 1;
    }

    private boolean isPrime(int n) {
        for (int i = 2; i < n; ++i) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}

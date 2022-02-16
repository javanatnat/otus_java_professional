package ru.otus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Counter {
    private static final Logger logger = LoggerFactory.getLogger(Counter.class);
    private static final long SLEEP_MILLIS = 1_000L;

    private final CountKeeper countKeeper;
    private boolean switcher = false;

    public Counter(int minVal, int maxVal) {
        countKeeper = new CountKeeper(minVal, maxVal);
    }

    private void action(boolean action) {
        Thread currentThread = Thread.currentThread();
        CountKeeper ownKeeper = new CountKeeper(countKeeper);

        while (!currentThread.isInterrupted()) {
            synchronized (this) {
                try {
                    while (action == switcher) {
                        wait();
                    }

                    logger.info("{}", ownKeeper.updAndGetCurrVal());

                    switcher = action;
                    sleep();
                    notifyAll();

                } catch (InterruptedException e) {
                    currentThread.interrupt();
                }
            }
        }
    }

    private static void sleep() {
        try {
            Thread.sleep(SLEEP_MILLIS);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        Counter counter = new Counter(1, 10);

        new Thread(() -> counter.action(true)).start();
        new Thread(() -> counter.action(false)).start();
    }

    private static class CountKeeper {
        private final int minVal;
        private final int maxVal;

        private int currVal;
        private int increment = 1;

        public CountKeeper(CountKeeper countKeeper) {
            this(countKeeper.getMinVal(), countKeeper.getMaxVal());
        }

        public CountKeeper(int minVal, int maxVal) {
            if (minVal == maxVal) {
                throw new IllegalArgumentException();
            }

            if (minVal > maxVal) {
                this.minVal = maxVal;
                this.maxVal = minVal;
            } else {
                this.minVal = minVal;
                this.maxVal = maxVal;
            }

            this.currVal = minVal - 1;
        }

        public int updAndGetCurrVal() {
            currVal = currVal + getIncrement();
            return currVal;
        }

        private int getIncrement() {
            if (currVal + 1 > maxVal) {
                increment = -1;
            } else if (currVal - 1 < minVal) {
                increment = 1;
            }
            return increment;
        }

        private int getMinVal() {
            return minVal;
        }

        private int getMaxVal() {
            return maxVal;
        }
    }
}

package mobi.monaca.framework.util;

import java.util.ArrayList;

import android.util.Log;

public class BenchmarkTimer {

    protected static class Entry {
        final String label;
        final long time;

        public Entry(String label, long time) {
            this.label = label;
            this.time = time;
        }
    }

    static protected ArrayList<Entry> entryList = new ArrayList<Entry>();

    static public void start() {
        synchronized (entryList) {
            mark("start");
        }
    }

    static public void finish() {
        synchronized (entryList) {
            mark("finish");
            dump();

            entryList = new ArrayList<Entry>();
        }
    }

    static protected void dump() {
        Entry prev = null;
        Log.d(BenchmarkTimer.class.getSimpleName(),
                "---------------------------------------------");
        for (Entry entry : entryList) {
            if (prev != null) {
                Log.d(BenchmarkTimer.class.getSimpleName(),
                        String.format(" %30s > %-10d", "", entry.time
                                - prev.time));
            }
            Log.d(BenchmarkTimer.class.getSimpleName(),
                    String.format(" %30s ", entry.label));

            prev = entry;
        }
        Log.d(BenchmarkTimer.class.getSimpleName(),
                "---------------------------------------------");
    }

    static public void mark(String label) {
        synchronized (entryList) {
            entryList.add(new Entry(label, System.currentTimeMillis()));
            Log.d(BenchmarkTimer.class.getSimpleName(),
                    "================> mark: " + label + " <==================");

        }
    }
}

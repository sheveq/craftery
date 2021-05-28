package szewek.craftery.util;

import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.SnapshotStateKt;
import kotlin.Pair;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TimeLogManager {
    private static final Map<String, TimeHistory> historyMap = new ConcurrentHashMap<>();

    public static final MutableState<Long> lastLog = SnapshotStateKt.mutableStateOf(System.nanoTime(), SnapshotStateKt.structuralEqualityPolicy());

    public static void logNano(String name, long nano) {
        var ll = System.nanoTime();
        var d = ll - nano;
        historyMap.computeIfAbsent(name, k -> new TimeHistory()).add(d);
        lastLog.setValue(ll);
    }

    public static String formatDuration(long d) {
        var ms = d / 1000_000L;
        var ns = d % 1000_000L;
        return ms + " ms " + ns + " ns";
    }

    public static List<Pair<String, Long>> averages() {
        return historyMap.entrySet()
                .stream()
                .map(e -> new Pair<>(e.getKey(), e.getValue().avg()))
                .sorted((l, r) -> (int) (r.getSecond() - l.getSecond()))
                .collect(Collectors.toUnmodifiableList());
    }

    public static final class TimeHistory {
        private final long[] measuredTimes = new long[24];
        private int size = 0;
        private int offset = 0;

        private void add(long t) {
            if (size == 24) {
                var o = (offset + 1) % 24;
                measuredTimes[o] = t;
                offset = o;
            } else {
                measuredTimes[++size] = t;
            }
        }

        private long sum() {
            long s = 0;
            for (int i = 0; i < 24; i++) {
                s += measuredTimes[i];
            }
            return s;
        }

        private long avg() {
            return sum() / size;
        }
    }
}

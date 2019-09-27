package statistics.api.storage;

import java.sql.Timestamp;
import java.util.*;

public class Smart {
    private Video storage;

    private LinkedList<Double> durationPerSecond;
    private LinkedList<Integer> countPerSecond;
    private String lastUpdate = null;
    private double count = 0, sum = 0, avg = 0;
    private Map<String, Object> max, min;

    public Smart() {
        storage = Video.getInstance();
        durationPerSecond = new LinkedList<>();
        countPerSecond = new LinkedList<>();
        long time = System.currentTimeMillis();
        max = new HashMap<>(){{
            put("time", time);
            put("value", 0.0);
        }};
        min = new HashMap<>(){{
            put("time", time);
            put("value", -1.0);
        }};

        for (int i = 0; i < 60; i++) {
            durationPerSecond.add(i, 0.0);
            countPerSecond.add(i, 0);
        }

        startTimedUpdate();
    }

    private void startTimedUpdate() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Timestamp now = new Timestamp(System.currentTimeMillis());
                reviewStatus(Long.toString(now.getTime()));
                reviewMaxMin(now);
            }
        }, 1000, 1000);
    }

    public void add(Map<String, Object> video) {
        storage.add(video);
        addVideoStatistics(
                new Timestamp(System.currentTimeMillis()),
                (String) video.get("timestamp"),
                (double) video.get("duration")
        );
    }

    public List<Double> getList(String timestamp) {
        return storage.getList(timestamp);
    }

    public void deleteAll() {
        storage.deleteAll();
    }

    public Map<String, Object> getStatistics() {
        return new HashMap<>(){{
            put("sum", sum);
            put("count", count);
            put("avg", avg);
            put("max", max.get("value"));
            put("min", min.get("value"));
        }};
    }

    private synchronized void addVideoStatistics(Timestamp now, String videoTime, double videoDuration){
        Timestamp time = new Timestamp(Long.parseLong(videoTime));
        int secondsDiff = (int) (now.getTime() - time.getTime()) / 1000;

        if(secondsDiff >= 0 && secondsDiff < 60) {
            updateSecondsStatus(secondsDiff, videoDuration);
            reviewMaxMin(time, videoDuration);
            reviewStatus();
        }
    }

    private synchronized void reviewStatus() {
        updateCount();
        updateSum();
        updateAvg();
    }

    private synchronized void reviewStatus(String nowValue) {
        if(!nowValue.equals(lastUpdate)) {
            skipSecond(nowValue);
            lastUpdate = nowValue;
        }
        reviewStatus();
    }

    private void skipSecond(String nowValue) {
            removeLastSecondStatus(nowValue);
            addCurrentSecond(nowValue);
    }

    private void reviewMaxMin(Timestamp now) {
        Long maxTime = (Long) max.get("time");
        Long minTime = (Long) min.get("time");
        int maxDiff = (int) (now.getTime() - maxTime) / 1000;
        int minDiff = (int) (now.getTime() - minTime) / 1000;

        if(maxDiff >= 60)
            getNewMax(now.getTime());

        if(minDiff >= 60)
            getNewMin(now.getTime());
    }

    private void reviewMaxMin(Timestamp time, double duration) {
        Long maxTime = (Long) max.get("time");
        Long minTime = (Long) min.get("time");
        int maxDiff = (int) (time.getTime() - maxTime) / 1000;
        int minDiff = (int) (time.getTime() - minTime) / 1000;

        if(maxDiff >= 60)
            getNewMax(time.getTime());

        if(duration > (double) max.get("value")) {
            max = new HashMap<>() {{
                put("time", time.getTime());
                put("value", duration);
            }};
        }

        if(minDiff >= 60)
            getNewMin(time.getTime());

        double minVal = (double) min.get("value");
        if(minVal == -1.0 || duration < minVal) {
            min = new HashMap<>() {{
                put("time", time.getTime());
                put("value", duration);
            }};
        }
    }

    private void getNewMax(long now) {
        double maxDuration = 0;
        long time = 0;
        for (int i = 0; i < 60; i++) {
            double currentMax = Collections.max(storage.getList(Long.toString(now-i)));
            if(currentMax > maxDuration) {
                maxDuration = currentMax;
                time = now-i;
            }
        }

        double finalMaxDuration = maxDuration;
        long finalTime = time;
        max = new HashMap<>(){{
            put("time", finalTime);
            put("value", finalMaxDuration);
        }};
    }

    private void getNewMin(long now) {
        double minDuration = 0;
        long time = 0;
        for (int i = 0; i < 60; i++) {
            double currentMax = Collections.min(storage.getList(Long.toString(now-i)));
            if(currentMax > minDuration) {
                minDuration = currentMax;
                time = now-i;
            }
        }

        double finalMinDuration = minDuration;
        long finalTime = time;
        min = new HashMap<>(){{
            put("time", finalTime);
            put("value", finalMinDuration);
        }};
    }

    private void updateAvg() {
        avg = sum/count;
    }

    private void updateCount() {
        count = countPerSecond.stream().mapToInt(secondCount -> secondCount).sum();
    }

    private void updateSum() {
        sum = durationPerSecond.stream().mapToDouble(secondDuration -> secondDuration).sum();
    }

    private synchronized void addCurrentSecond(String nowValue) {
        if(countPerSecond.size() == 59 && durationPerSecond.size() == 59) {
            countPerSecond.addFirst(0);
            durationPerSecond.addFirst(0.0);
        }
    }

    private synchronized void removeLastSecondStatus(String nowValue) {
        if(durationPerSecond.size() > 0 && countPerSecond.size() > 0) {
            durationPerSecond.removeLast();
            countPerSecond.removeLast();
        }
    }

    private synchronized void updateSecondsStatus(int secondsDiff, double duration) {
        double currentDuration = durationPerSecond.remove(secondsDiff);
        currentDuration += duration;
        durationPerSecond.add(secondsDiff, currentDuration);

        int currentCount = countPerSecond.remove(secondsDiff);
        currentCount++;
        countPerSecond.add(secondsDiff, currentCount);
    }
}

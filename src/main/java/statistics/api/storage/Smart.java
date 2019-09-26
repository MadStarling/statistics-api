package statistics.api.storage;

import java.sql.Timestamp;
import java.util.*;

public class Smart {
    private Video storage;

    private LinkedList<Double> durationPerSecond;
    private LinkedList<Integer> countPerSecond;
    private String lastUpdate = null;
    private double count = 0, sum = 0, avg = 0;
    private Map<Long, Double> max, min;

    public Smart() {
        storage = Video.getInstance();
        durationPerSecond = new LinkedList<>();
        countPerSecond = new LinkedList<>();

        for (int i = 0; i <= 60; i++)
            durationPerSecond.add(i, 0.0);

        startTimedUpdate();
    }

    private void startTimedUpdate() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Timestamp now = new Timestamp(System.currentTimeMillis());
                updateDurations(Long.toString(now.getTime()));
                reviewMaxMin(now);
            }
        }, 1000, 1000);
    }

    public void add(Map<String, Object> video) {
        storage.add(video);
        new Thread(() -> updateDurations(
                new Timestamp(System.currentTimeMillis()),
                (String) video.get("timestamp"),
                (double) video.get("duration")
        )).start();
    }

    public List<Double> getList(String timestamp) {
        return storage.getList(timestamp);
    }

    public void deleteAll() {
        storage.deleteAll();
    }

    private void updateDurations(Timestamp now, String timestamp, double duration){
        Timestamp time = new Timestamp(Long.parseLong(timestamp));
        int secondsDiff = (int) (now.getTime() - time.getTime()) / 1000;
        String nowValue = Long.toString(now.getTime());
        if(!nowValue.equals(lastUpdate))
            updateDurations(nowValue);

        if(secondsDiff >= 0 && secondsDiff < 60) {
            updateSecondStatus(secondsDiff, duration);
            reviewMaxMin(time, duration);
        }
    }

    private void updateDurations(String nowValue) {
        if(!nowValue.equals(lastUpdate)) {
            removeLastSecondStatus(nowValue);
            addCurrentSecond(nowValue);
            updateStatus();
            lastUpdate = nowValue;
        }
    }

    private synchronized void updateStatus() {
        updateCount();
        updateSum();
        updateAvg();
    }

    private void reviewMaxMin(Timestamp now) {
        Long maxTime = max.entrySet().iterator().next().getKey();
        Long minTime = min.entrySet().iterator().next().getKey();
        int maxDiff = (int) (now.getTime() - maxTime) / 1000;
        int minDiff = (int) (now.getTime() - minTime) / 1000;

        if(maxDiff >= 60)
            new Thread(() -> getNewMax(now.getTime())).start();

        if(minDiff >= 60)
            new Thread(() -> getNewMin(now.getTime())).start();
    }

    private void reviewMaxMin(Timestamp moment, double duration) {
        double currentMax = max.entrySet().iterator().next().getValue();
        double currentMin = min.entrySet().iterator().next().getValue();

        if(duration > currentMax) {
            max = new HashMap<>(){{
                put(moment.getTime(), duration);
            }};
        }

        if(duration < currentMin) {
            min = new HashMap<>(){{
                put(moment.getTime(), duration);
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
            put(finalTime, finalMaxDuration);
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
            put(finalTime, finalMinDuration);
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
        countPerSecond.addFirst(0);
        durationPerSecond.addFirst(0.0);
    }

    private synchronized void removeLastSecondStatus(String nowValue) {
        durationPerSecond.removeLast();
        countPerSecond.removeLast();
    }

    private synchronized void updateSecondStatus(int secondsDiff, double duration) {
        double currentDuration = durationPerSecond.get(secondsDiff);
        currentDuration += duration;
        durationPerSecond.add(secondsDiff, currentDuration);

        int currentCount = countPerSecond.get(secondsDiff);
        currentCount++;
        countPerSecond.add(secondsDiff, currentCount);
    }
}

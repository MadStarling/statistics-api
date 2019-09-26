package statistics.api.storage;

import java.sql.Timestamp;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class Smart {
    private Video storage;

    private LinkedList<Double> durationPerSecond;
    private LinkedList<Integer> countPerSecond;
    private String lastUpdate = null;
    private double count = 0, sum = 0, avg = 0, max = 0, min = 0;

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
            }
        }, 1000, 1000);
    }

    public void add(Map<String, Object> video) {
        storage.add(video);
        updateDurations(
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

    private void updateDurations(Timestamp now, String timestamp, double duration){
        Timestamp time = new Timestamp(Long.parseLong(timestamp));
        int secondsDiff = (int) (now.getTime() - time.getTime()) / 1000;
        String nowValue = Long.toString(now.getTime());
        if(!nowValue.equals(lastUpdate))
            updateDurations(nowValue);

        if(secondsDiff >= 0 && secondsDiff < 60)
            updateSecondStatus(secondsDiff, duration);
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
        synchronized (this){
            updateCount();
            updateSum();
            updateAvg();
        }
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

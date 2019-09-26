package statistics.api;

import java.sql.Timestamp;
import java.util.*;

public class StorageService {
    private VideoStorage storage;
    private LinkedList<Double> durations;
    private String lastUpdate = null;
    private Timer timer;

    public StorageService() {
        storage = VideoStorage.getInstance();
        durations = new LinkedList<>();

        for (int i = 0; i <= 60; i++)
            durations.add(i, 0.0);

        startTimer();
    }

    private void startTimer() {
        timer = new Timer();
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

    private synchronized void updateDurations(Timestamp now, String timestamp, double duration){
        Timestamp time = new Timestamp(Long.parseLong(timestamp));
        int secondsDiff = (int) (now.getTime() - time.getTime()) / 1000;
        String nowValue = Long.toString(now.getTime());
        if(!nowValue.equals(lastUpdate))
            updateDurations(nowValue);

        if(secondsDiff <= 60)
            addDuration(secondsDiff, duration);
    }

    private synchronized void updateDurations(String nowValue) {
        if(!nowValue.equals(lastUpdate)) {
            durations.removeLast();
            durations.addFirst(0.0);
        }
    }

    private void addDuration(int secondsDiff, double duration) {
        Double currentValue = durations.get(secondsDiff);
        currentValue += duration;
        durations.add(secondsDiff, currentValue);
    }
}

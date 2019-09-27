package statistics.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import statistics.api.storage.Smart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

class SmartTest {
    private static Smart smart;

    @BeforeEach
    void initTest() {
        smart = new Smart();
    }

    void addVideo(String timestamp, double duration){
        Map<String, Object> video = new HashMap<>(){{
            put("duration", duration);
            put("timestamp", timestamp);
        }};
        smart.add(video);
    }

    @Test
    void testAdd(){
        String timestamp = "1876429872198";
        addVideo(timestamp, 200.3);

        List<Double> firstResult = smart.getList(timestamp);
        assertEquals(1, firstResult.size());
        assertEquals(200.3, (double) firstResult.get(0));

        addVideo(timestamp, 105.17);

        List<Double> secondResult = smart.getList(timestamp);
        assertEquals(2, firstResult.size());
        assertEquals(105.17, (double) firstResult.get(1));
    }

    @Test
    void testDelete(){
        String timestamp = "1876429872198";
        addVideo(timestamp, 200.3);
        addVideo(timestamp, 105.17);

        List<Double> firstResult = smart.getList(timestamp);
        assertTrue(firstResult.size() > 0);
        smart.deleteAll();

        assertNull(smart.getList(timestamp));
    }

    @Test
    void testStatistics() throws InterruptedException {
        addVideo(Long.toString(System.currentTimeMillis()), 200.3);
        addVideo(Long.toString(System.currentTimeMillis()), 19.28);
        addVideo(Long.toString(System.currentTimeMillis()), 152.3);
        Thread.sleep(1000);
        addVideo(Long.toString(System.currentTimeMillis()), 206.3);
        Thread.sleep(1000);
        addVideo(Long.toString(System.currentTimeMillis()), 20.3);

        Map<String, Object> statistics = smart.getStatistics();
        assertEquals(598.48, statistics.get("sum"));
        assertEquals(5.0, statistics.get("count"));
        assertEquals(119.696, statistics.get("avg"));
        assertEquals(206.3, statistics.get("max"));
        assertEquals(19.28, statistics.get("min"));
    }
}
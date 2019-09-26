package statistics.api;

import org.junit.jupiter.api.Test;
import statistics.api.storage.Smart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SmartTest {
    private static Smart service;

    void addVideo(String timestamp, double duration){
        Map<String, Object> video = new HashMap<String, Object>(){{
            put("duration", duration);
            put("timestamp", timestamp);
        }};
        service.add(video);
    }

    @Test
    void testAdd(){
        String timestamp = "1876429872198";
        addVideo(timestamp, 200.3);

        List<Double> firstResult = service.getList(timestamp);
        assertEquals(1, firstResult.size());
        assertEquals(200.3, (double) firstResult.get(0));

        addVideo(timestamp, 105.17);

        List<Double> secondResult = service.getList(timestamp);
        assertEquals(2, firstResult.size());
        assertEquals(105.17, (double) firstResult.get(1));
    }

    @Test
    void testDelete(){
        String timestamp = "1876429872198";
        addVideo(timestamp, 200.3);
        addVideo(timestamp, 105.17);

        List<Double> firstResult = service.getList(timestamp);
        assertTrue(firstResult.size() > 0);
        service.deleteAll();

        assertNull(service.getList(timestamp));
    }
}
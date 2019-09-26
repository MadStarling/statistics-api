package statistics.api;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;

class VideoStorageTest {
    private static VideoStorage storage;

    @BeforeAll
    static void initTest(){
        storage = VideoStorage.getInstance();
    }

    @Test
    public void testAdd(){
        assertTrue(storage.add(new HashMap<String, Object>(){{
            put("duration", 200.3);
            put("timestamp", "1876429872198");
        }}));
    }

    @Test
    public void testDelete(){
        assertTrue(storage.add(new HashMap<String, Object>(){{
            put("duration", 200.3);
            put("timestamp", "1876429872198");
        }}));
    }
}
package statistics.api;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ApiController.class)
class ApiControllerTest {
    @Autowired
    private MockMvc mock;
    @MockBean
    private ApiController controller;

    @Test
    void testVideoPost() throws Exception {
        Map<String, Object> video = new HashMap<>(){{
            put("duration", 200.3);
            put("timestamp", Long.toString(System.currentTimeMillis()));
        }};


        mock.perform(post("/videos", video)).andExpect(status().isCreated());
    }

    @Test
    void testVideoPostAncient() throws Exception {
        Map<String, Object> video = new HashMap<>(){{
            put("duration", 200.3);
            put("timestamp", Long.toString(System.currentTimeMillis()));
        }};


        mock.perform(post("/videos", video)).andExpect(status().isNoContent());
    }

    @Test
    void testVideoDelete() throws Exception {
        Map<String, Object> video = new HashMap<>(){{
            put("duration", 200.3);
            put("timestamp", Long.toString(System.currentTimeMillis()));
        }};


        mock.perform(delete("/videos")).andExpect(status().isNoContent());
    }

    @Test
    void testStatistics() throws Exception {
        mock.perform(post("/videos", new HashMap<>(){{
            put("duration", 200.3);
            put("timestamp", Long.toString(System.currentTimeMillis()));
        }})).andExpect(status().isCreated());
        mock.perform(post("/videos", new HashMap<>(){{
            put("duration", 19.28);
            put("timestamp", Long.toString(System.currentTimeMillis()));
        }})).andExpect(status().isCreated());
        mock.perform(post("/videos", new HashMap<>(){{
            put("duration", 152.3);
            put("timestamp", Long.toString(System.currentTimeMillis()));
        }})).andExpect(status().isCreated());

        Thread.sleep(1000);

        mock.perform(post("/videos", new HashMap<>(){{
            put("duration", 206.3);
            put("timestamp", Long.toString(System.currentTimeMillis()));
        }})).andExpect(status().isCreated());

        Thread.sleep(1000);

        mock.perform(post("/videos", new HashMap<>(){{
            put("duration", 20.3);
            put("timestamp", Long.toString(System.currentTimeMillis()));
        }})).andExpect(status().isCreated());

        Thread.sleep(1000);

        MvcResult response = mock.perform(delete("/videos")).andExpect(status().isNoContent()).andReturn();
        Map<String, Object> statistics = new JSONObject(response.getResponse().getContentAsString()).toMap();

        assertEquals(598.48, statistics.get("sum"));
        assertEquals(5.0, statistics.get("count"));
        assertEquals(119.696, statistics.get("avg"));
        assertEquals(206.3, statistics.get("max"));
        assertEquals(19.28, statistics.get("min"));
    }
}
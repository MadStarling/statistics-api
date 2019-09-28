package statistics.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import statistics.api.storage.Smart;
import java.util.Map;

@RestController
public class ApiController {
    @Autowired
    private Smart smart;

    @PostMapping("/videos")
    public ResponseEntity<String> addVideo(@RequestBody Map<String, Object> videoInfo) {
        smart.add(videoInfo);
        if(isRecent(videoInfo))
            return new ResponseEntity<>("", HttpStatus.CREATED);
        else
            return new ResponseEntity<>("", HttpStatus.NO_CONTENT);
    }

    private boolean isRecent(Map<String, Object> videoInfo) {
        long time = Long.parseLong(String.valueOf(videoInfo.get("timestamp")));
        int secondsDiff = (int) (System.currentTimeMillis() - time) / 1000;

        return secondsDiff < 60;
    }

    @DeleteMapping("videos")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVideos() {
        smart.deleteAll();
    }

    @GetMapping("/statistics")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getStatistics(){
        return smart.getStatistics();
    }
}

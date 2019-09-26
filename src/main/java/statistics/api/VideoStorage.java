package statistics.api;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

public class VideoStorage {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private MultiValueMap<String, Double> storageBag;
    private static VideoStorage instance = null;

    private VideoStorage(){
        storageBag = new LinkedMultiValueMap<>();
    }

    public static VideoStorage getInstance(){
        if(instance == null){
            synchronized (VideoStorage.class){
                if(instance==null)
                    instance = new VideoStorage();
            }
        }
        return instance;
    }

    public void add(Map<String, Object> video){
        storageBag.add((String) video.get("timestamp"), (double) video.get("duration"));
    }

    public void deleteAll(){
        storageBag = new LinkedMultiValueMap<>();
    }

    public List<Double> getList(String timestamp) {
        return storageBag.get(timestamp);
    }
}

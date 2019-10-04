package statistics.api.storage;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

public class Video {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private MultiValueMap<String, Double> storageBag;
    private static Video instance = null;

    private Video(){
        storageBag = new LinkedMultiValueMap<>();
    }

    public static Video getInstance(){
        if(instance == null){
            synchronized (Video.class){
                if(instance==null)
                    instance = new Video();
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

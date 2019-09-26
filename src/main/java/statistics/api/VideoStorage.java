package statistics.api;

import java.util.Map;

public class VideoStorage {
    private static VideoStorage instance = null;

    private VideoStorage(){

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

    public boolean add(Map video){
        return false;
    }

    public boolean deleteAll(){
        return false;
    }
}

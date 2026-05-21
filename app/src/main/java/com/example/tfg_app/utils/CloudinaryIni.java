package com.example.tfg_app.utils;
import com.cloudinary.android.MediaManager;
import android.app.Application;
import java.util.HashMap;
import java.util.Map;


public class CloudinaryIni extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", CloudinaryConfig.CLOUD_NAME);
        config.put("api_key", CloudinaryConfig.API_KEY);
        config.put("api_secret", CloudinaryConfig.API_SECRET);

        MediaManager.init(this, config);
    }
}
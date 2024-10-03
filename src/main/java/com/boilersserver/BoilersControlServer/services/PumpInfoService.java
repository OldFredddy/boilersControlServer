package com.boilersserver.BoilersControlServer.services;

// package com.boilersserver.BoilersControlServer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class PumpInfoService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String PUMPS_INFO_KEY = "pumpsInfo";

    @Autowired
    public PumpInfoService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setPumpsInfo(String[] pumpsInfo) {
        // Serialize the String[] into a single String
        String pumpsInfoStr = String.join(",", pumpsInfo);
        redisTemplate.opsForValue().set(PUMPS_INFO_KEY, pumpsInfoStr);
    }

    public String[] getPumpsInfo() {
        String pumpsInfoStr = redisTemplate.opsForValue().get(PUMPS_INFO_KEY);
        if (pumpsInfoStr != null && !pumpsInfoStr.isEmpty()) {
            return pumpsInfoStr.split(",");
        } else {
            // Return default values if pumpsInfo is null or empty
            String[] defaultPumpsInfo = new String[28];
            Arrays.fill(defaultPumpsInfo, "0");
            return defaultPumpsInfo;
        }
    }
}

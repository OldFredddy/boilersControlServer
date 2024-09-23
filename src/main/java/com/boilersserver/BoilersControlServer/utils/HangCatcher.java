package com.boilersserver.BoilersControlServer.utils;

import com.boilersserver.BoilersControlServer.entities.Boiler;
import com.boilersserver.BoilersControlServer.entities.GasEngineStation;
import com.boilersserver.BoilersControlServer.entities.PumpStation;
import com.boilersserver.BoilersControlServer.services.BoilersDataService;
import com.boilersserver.BoilersControlServer.services.GasEngineDataService;
import com.boilersserver.BoilersControlServer.services.PumpStationDataService;
import com.boilersserver.BoilersControlServer.services.TelegramService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service
public class HangCatcher {
    private static final float BOILER_TPOD_HIGH = 150.0f;
    private static final float BOILER_TPOD_LOW = 0.0f;
    private static final float BOILER_PPOD_HIGH = 2.0f;
    private static final float BOILER_PPOD_LOW = -1001.0f;
    private final TelegramService telegramService;
    private final BoilersDataService boilersDataService;
    private final PumpStationDataService pumpStationDataService;
    private final GasEngineDataService gasEngineDataService;
    private final ArrayList<ArrayList<String>> tPodArr = new ArrayList<>();
    private final ArrayList<ArrayList<String>> tStreetArr = new ArrayList<>();
    private ArrayList<String> engineTempArr = new ArrayList<>();
    private final RedisTemplate<String, String> redisTemplate;
    long thresholdTime = TimeUnit.MINUTES.toMillis(5);

    @Autowired
    public HangCatcher(TelegramService telegramService, BoilersDataService boilersDataService,
                       PumpStationDataService pumpStationDataService, GasEngineDataService gasEngineDataService,
                       RedisTemplate<String, String> redisTemplate) {
        this.telegramService = telegramService;
        this.boilersDataService = boilersDataService;
        this.redisTemplate = redisTemplate;
        this.pumpStationDataService = pumpStationDataService;
        this.gasEngineDataService = gasEngineDataService;
        for (int i = 0; i < 14; i++) {
            tPodArr.add(new ArrayList<>());
            tStreetArr.add(new ArrayList<>());
        }
        engineTempArr = new ArrayList<>();
    }

    @Scheduled(initialDelay = 10000, fixedRate = 2000)
    public void compareAndNotify() throws TelegramApiException, InterruptedException {
        for (int i = 0; i < boilersDataService.getBoilers().size(); i++) {
            Boiler boiler = boilersDataService.getBoilers().get(i);
            PumpStation pumpStation = pumpStationDataService.getPumpStation();
            long currentTime = System.currentTimeMillis();
            boiler.setLastUpdated(currentTime);
            long lastUpdatedTime = boiler.getLastUpdated();
            String boilerKey = "boiler:" + boiler.getId();
            Gson gson = new Gson();
            String boilerJson = gson.toJson(boiler);
            String historyKey = "history:" + boilerKey;
            if (checkBoilerValuesRanges(boiler)){
                redisTemplate.opsForZSet().add(historyKey, boilerJson, currentTime);
            }
            String pumpStationKey = "pumpstation:" + "0";
            pumpStation.setLastUpdated(currentTime);
            Gson pumpStationGson = new Gson();
            String pumpStationJson = pumpStationGson.toJson(pumpStation);
            String pumpStationHistoryKey = "phistory:" + pumpStationKey;
            if (checkPumpStationValuesRanges(pumpStation)){
                redisTemplate.opsForZSet().add(pumpStationHistoryKey, pumpStationJson, currentTime);
            }
            String tPod = boiler.getTPod();
            String tStreet = boiler.getTUlica();
            updateList(tPodArr.get(i), tPod);
            updateList(tStreetArr.get(i), tStreet);
            if (areAllElementsEqual(tPodArr.get(i),lastUpdatedTime, thresholdTime)&&areAllElementsEqual(tStreetArr.get(i),lastUpdatedTime, thresholdTime)) {
                if (boiler.getIsOk() != 2){
                    boiler.setIsOk(2, boiler.getVersion() + 1);
                    telegramService.sendAttention(i, "Нет данных от котельной " + telegramService.boilerNames[i]);
                }
            }
        }
        GasEngineStation gasEngine = gasEngineDataService.getGasEngineStation();
        long currentTime = System.currentTimeMillis();
        gasEngine.setLastUpdated(currentTime);
        long engineLastUpdatedTime = gasEngine.getLastUpdated();
        String engineKey = "engine:0";
        Gson gson = new Gson();
        String engineJson = gson.toJson(gasEngine);
        String engineHistoryKey = "history:" + engineKey;
            redisTemplate.opsForZSet().add(engineHistoryKey, engineJson, currentTime);
        String engineTemp = gasEngine.getEngineTemp();
        updateList(engineTempArr, engineTemp); // Теперь engineTempArr это одиночный список, не массив списков.
        if (areAllElementsEqual(engineTempArr, engineLastUpdatedTime, thresholdTime)) {
            if (gasEngine.getIsOk() != 2){
                gasEngine.setIsOk(2, gasEngine.getVersion() + 1);
                telegramService.sendAttentionGasEngine(0, "Нет связи с газомоторной станцией");
            }
        }
    }
    @Scheduled(fixedRate = 20000)
    private void cleanupOldEntries() {
        long cutoff = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(8);
        for (int i = 0; i < 14; i++) {
            String boilerHistoryKey = "history:boiler:" + i;
            redisTemplate.opsForZSet().removeRangeByScore(boilerHistoryKey, 0, cutoff);
            String pumpStationHistoryKey = "phistory:pumpstation:0";
            redisTemplate.opsForZSet().removeRangeByScore(pumpStationHistoryKey, 0, cutoff);
        }
        String engineHistoryKey = "history:engine:0";
        redisTemplate.opsForZSet().removeRangeByScore(engineHistoryKey, 0, cutoff);
    }

    private void updateList(List<String> list, String newValue) {
        if (list.size() >= 75) {
            list.remove(0);
        }
        list.add(newValue);
    }

    private static boolean areAllElementsEqual(List<String> list, long lastUpdatedTime, long thresholdTime) {
        if (list.size() > 1 && new HashSet<>(list).size() == 1) {
            long currentTime = System.currentTimeMillis();
            return (currentTime - lastUpdatedTime) > thresholdTime;
        }
        return false;
    }
    private boolean checkBoilerValuesRanges(Boiler boiler){
        if (Float.parseFloat(boiler.getTPod())>BOILER_TPOD_HIGH) {
            return false;
        }
        if (Float.parseFloat(boiler.getTPod())<BOILER_TPOD_LOW) {
            return false;
        }
        if (Float.parseFloat(boiler.getPPod())>BOILER_PPOD_HIGH) {
            return false;
        }
        if (Float.parseFloat(boiler.getPPod()) < BOILER_PPOD_LOW) {
            return false;
        }
        if (Float.parseFloat(boiler.getTUlica()) > BOILER_TPOD_HIGH) {
            return false;
        }
        if (Float.parseFloat(boiler.getTUlica()) < -100.0f) {
            return false;
        }
        return true;
    }
    private boolean checkPumpStationValuesRanges(PumpStation pumpStation) {
        try {
            float t1 = Float.parseFloat(pumpStation.getMagicIndicator1());
            float t2 = Float.parseFloat(pumpStation.getMagicIndicator2());
            float t3 = Float.parseFloat(pumpStation.getMagicIndicator3());
            float t4 = Float.parseFloat(pumpStation.getMagicIndicator4());

            if (!isValueInRange(t1) || !isValueInRange(t2) || !isValueInRange(t3) || !isValueInRange(t4)) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    private boolean isValueInRange(float value) {
        return value >= -5.0f && value <= 30.0f;
    }
}


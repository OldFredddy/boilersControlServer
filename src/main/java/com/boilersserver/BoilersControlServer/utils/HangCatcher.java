package com.boilersserver.BoilersControlServer.utils;

import com.boilersserver.BoilersControlServer.entities.Boiler;
import com.boilersserver.BoilersControlServer.services.BoilersDataService;
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

@Service
public class HangCatcher {
    private static final int BOILER_TPOD_HIGH = 150;
    private static final int BOILER_TPOD_LOW = 0;
    private static final float BOILER_PPOD_HIGH = 2;
    private static final float BOILER_PPOD_LOW = 0;
    private final TelegramService telegramService;
    private final BoilersDataService boilersDataService;
    private final ArrayList<ArrayList<String>> tPodArr = new ArrayList<>();
    private final ArrayList<ArrayList<String>> tStreetArr = new ArrayList<>();
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public HangCatcher(TelegramService telegramService, BoilersDataService boilersDataService, RedisTemplate<String, String> redisTemplate) {
        this.telegramService = telegramService;
        this.boilersDataService = boilersDataService;
        this.redisTemplate = redisTemplate;
        for (int i = 0; i < 14; i++) {
            tPodArr.add(new ArrayList<>());
            tStreetArr.add(new ArrayList<>());
        }
    }

    @Scheduled(fixedRate = 3000)
    public void compareAndNotify() throws TelegramApiException, InterruptedException {
        for (int i = 0; i < boilersDataService.getBoilers().size(); i++) {
            Boiler boiler = boilersDataService.getBoilers().get(i);
            long currentTime = System.currentTimeMillis();
            boiler.setLastUpdated(currentTime);
            String boilerKey = "boiler:" + boiler.getId();
            Gson gson = new Gson();
            String boilerJson = gson.toJson(boiler);
            String historyKey = "history:" + boilerKey;
            if (checkBoilerValuesRanges(boiler)){
                redisTemplate.opsForZSet().add(historyKey, boilerJson, currentTime);
            }
            String tPod = boiler.getTPod();
            String tStreet = boiler.getTUlica();
            updateList(tPodArr.get(i), tPod);
            updateList(tStreetArr.get(i), tStreet);
            if (areAllElementsEqual(tPodArr.get(i))&&areAllElementsEqual(tStreetArr.get(i))) {
                if (boiler.getIsOk() != 2){
                    boiler.setIsOk(2, boiler.getVersion() + 1);
                    telegramService.sendAttention(i, "Нет данных от котельной " + telegramService.boilerNames[i]);
                }
            }
        }
    }
    @Scheduled(fixedRate = 10000)
    private void cleanupOldEntries() {
        long cutoff = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(8);
        for (int i = 0; i < 14; i++) {
            String historyKey = "history:boiler:" + i;
            redisTemplate.opsForZSet().removeRangeByScore(historyKey, 0, cutoff);
        }
    }

    private void updateList(List<String> list, String newValue) {
        if (list.size() >= 250) {
            list.remove(0);
        }
        list.add(newValue);
    }

    private static boolean areAllElementsEqual(List<?> list) {
        return list.size() > 1 && new HashSet<>(list).size() == 1;
    }
    private boolean checkBoilerValuesRanges(Boiler boiler){
        if (Integer.parseInt(boiler.getTPod())>BOILER_TPOD_HIGH) {
            return false;
        }
        if (Integer.parseInt(boiler.getTPod())<BOILER_TPOD_LOW) {
            return false;
        }
        if (Integer.parseInt(boiler.getPPod())>BOILER_PPOD_HIGH) {
            return false;
        }
        if (Integer.parseInt(boiler.getPPod()) < BOILER_PPOD_LOW) {
            return false;
        }
        if (Integer.parseInt(boiler.getTUlica()) > BOILER_TPOD_HIGH) {
            return false;
        }
        if (Integer.parseInt(boiler.getTUlica()) < BOILER_TPOD_LOW) {
            return false;
        }
        return true;
    }
}


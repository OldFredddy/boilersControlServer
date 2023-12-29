package com.boilersserver.BoilersControlServer;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class HangCatcher {
    private final TelegramService telegramService;
    private final BoilersDataService boilersDataService;
    private final ArrayList<ArrayList<String>> tPodArr = new ArrayList<>();

    @Autowired
    public HangCatcher(TelegramService telegramService, BoilersDataService boilersDataService) {
        this.telegramService = telegramService;
        this.boilersDataService = boilersDataService;
        for (int i = 0; i < 14; i++) {
            tPodArr.add(new ArrayList<>());
        }
    }

    @Scheduled(fixedRate = 3000)
    public void compareAndNotify() throws TelegramApiException, InterruptedException {
        for (int i = 0; i < 14; i++) {
            String tPod = boilersDataService.getBoilers().get(i).getTPod();
            updateList(tPodArr.get(i), tPod);
            if (areAllElementsEqual(tPodArr.get(i))) {
            if (boilersDataService.getBoilers().get(i).getIsOk()!=2){
                boilersDataService.getBoilers().get(i).setIsOk(2,boilersDataService.getBoilers().get(i).getVersion()+1);
                telegramService.sendAttention(i, "Нет данных от котельной " + telegramService.boilerNames[i]);
             }
            }
        }
    }

    private void updateList(List<String> list, String newValue) {
        if (list.size() >= 150) {
            list.remove(0);
        }
        list.add(newValue);
    }

    public static boolean areAllElementsEqual(List<?> list) {
        return list.size() > 1 && new HashSet<>(list).size() == 1;
    }
}
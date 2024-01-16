package com.boilersserver.BoilersControlServer;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RESTController {
    private final TelegramService telegramService;
    private final BoilersDataService boilersDataService;
    private final TemperatureCorrections temperatureCorrections;
    @Autowired
    public RESTController(TelegramService telegramService, BoilersDataService boilersDataService,TemperatureCorrections temperatureCorrections) {
        this.telegramService = telegramService;
        this.boilersDataService = boilersDataService;
        this.temperatureCorrections = temperatureCorrections;
    }
    @CrossOrigin(origins = "*")
    @GetMapping("/getparams")
    public ResponseEntity<String> getParams() {
        try {
            List<Boiler> boilers = boilersDataService.getBoilers();
            Gson gson = new Gson();
            String json = gson.toJson(boilers);
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
    @CrossOrigin(origins = "*")
    @PostMapping("/settemperaturecorrections")
    public String setNewCorrections(@RequestBody String[] temperatureCorrectionsArr) {
        try {
            temperatureCorrections.setCorrectionTpod(temperatureCorrectionsArr);
            boilersDataService.setCorrectionsTpod(temperatureCorrections.getCorrectionTpod());
            return "Success";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    @CrossOrigin(origins = "*")
    @PostMapping("/setAlarmCorrections")
    public String setAlarmCorrections(@RequestBody String[] temperatureCorrectionsArr) {
        try {
            temperatureCorrections.setTAlarmCorrectionFromUsers(temperatureCorrectionsArr);
            boilersDataService.setCorrectionsTAlarm(temperatureCorrections.getTAlarmCorrectionFromUsers());
            return "Success";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    @CrossOrigin(origins = "*")
    @PostMapping("/avaryreset")
    public String setAvaryReset() {
        try {
            for (int i = 0; i <boilersDataService.getBoilers().size() ; i++) {
                boilersDataService.getBoilers().get(i).setIsOk(1,boilersDataService.getBoilers().get(i).getVersion()+1);//0-waiting 1 - good 2 - error
            }
            telegramService.resetError();
            return "Успех!";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}

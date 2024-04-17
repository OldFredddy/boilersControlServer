package com.boilersserver.BoilersControlServer;

import com.boilersserver.BoilersControlServer.entities.Boiler;
import com.boilersserver.BoilersControlServer.entities.GudimParams;
import com.boilersserver.BoilersControlServer.entities.PumpStation;
import com.boilersserver.BoilersControlServer.entities.TemperatureCorrections;
import com.boilersserver.BoilersControlServer.services.BoilersDataService;
import com.boilersserver.BoilersControlServer.services.GudimDataService;
import com.boilersserver.BoilersControlServer.services.PumpStationDataService;
import com.boilersserver.BoilersControlServer.services.TelegramService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RESTController {
    private final TelegramService telegramService;
    private final BoilersDataService boilersDataService;
    private final TemperatureCorrections temperatureCorrections;
    private final GudimDataService gudimDataService;
    private final PumpStationDataService pumpStationDataService;
    @Autowired
    public RESTController(TelegramService telegramService, BoilersDataService boilersDataService,
                          TemperatureCorrections temperatureCorrections, GudimDataService gudimDataService,
                          PumpStationDataService pumpStationDataService) {
        this.gudimDataService = gudimDataService;
        this.telegramService = telegramService;
        this.boilersDataService = boilersDataService;
        this.temperatureCorrections = temperatureCorrections;
        this.pumpStationDataService = pumpStationDataService;
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
    @CrossOrigin(origins = "*")
    @PostMapping("/setGudimParams")
    public String setGudimParams(@RequestBody GudimParams gudimParams) {
        try {
            gudimDataService.refreshData(gudimParams);
            return "Success";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    @CrossOrigin(origins = "*")
    @PostMapping("/setPumpStationParams")
    public String setPumpStationParams(@RequestBody PumpStation pumpStation) {
        try {
            pumpStationDataService.refreshData(pumpStation);
            return "Success";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    @CrossOrigin(origins = "*")
    @PostMapping("/setMagicIndicators")
    public String setPumpStationParams(@RequestBody List<String> magicIndicators) {
        try {
            pumpStationDataService.setMagicIndicators(magicIndicators);
            return "Success";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}

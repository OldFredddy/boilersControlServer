package com.boilersserver.BoilersControlServer.services;

import com.boilersserver.BoilersControlServer.entities.GasEngineStation;
import com.boilersserver.BoilersControlServer.utils.JsonMapper;
import com.boilersserver.BoilersControlServer.entities.Boiler;
import com.boilersserver.BoilersControlServer.entities.TemperatureCorrections;
import com.google.gson.Gson;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class BoilersDataService {
    @Getter
    private List<Boiler> boilers = new ArrayList<>();
    @Getter
    private TemperatureCorrections corrections = new TemperatureCorrections();
    private final int BOILERS_COUNT = 14;
    @Autowired
    private volatile GasEngineDataService gasEngineDataService;
    private final WebClient webClient;
    private final HttpClient httpClient;
    private final ConnectionProvider connectionProvider;
    private final AtomicBoolean isUpdateInProgress = new AtomicBoolean(false);
    private final AtomicBoolean isUpdateInProgress2 = new AtomicBoolean(false);
    private static final String IP = "http://46.61.160.6:4567";//85.175.232.186:4567
    public BoilersDataService(WebClient.Builder webClientBuilder) {
        for (int i = 0; i < BOILERS_COUNT; i++) {
            Boiler boiler = new Boiler();
            boiler.setId(i);
            boiler.setIsOk(1,1);
            boiler.setPPod("0.4");
            boiler.setTAlarm("65");
            boiler.setTPlan("70");
            boiler.setTPod("68");
            boiler.setImageResId(2);
            boiler.setTUlica("0");
            boiler.setPPodHighFixed("-1");
            boiler.setPPodLowFixed("-1");
            boiler.setTPodFixed("-1");
            boilers.add(boiler);
        }
        connectionProvider = ConnectionProvider.builder("custom")
                .maxConnections(500)
                .maxIdleTime(Duration.ofMinutes(30))
                .maxLifeTime(Duration.ofHours(1))
                .build();

        this.httpClient = HttpClient.create(connectionProvider);
        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(IP)
                .build();
        // webClient = webClientBuilder.baseUrl(IP).build();;
    }
    @PreDestroy
    public void onDestroy() {
        this.connectionProvider.dispose();
        System.out.println("clean BoilersDataService");
    }
    public String getCorrectionTpodAtIndex(int index) {
        String[] corrections = getCorrections().getCorrectionTpod();
        if (corrections == null || corrections.length <= index || corrections[index] == null) {
            return "0";
        }
        return corrections[index];
    }
    public boolean forFirstStart = true;
    public void refreshBoilersParams(List<Boiler> boilersList){
        for (int i = 0; i < boilersList.size(); i++) {
            Boiler oldBoiler = this.boilers.get(i);
            Boiler newBoiler = boilersList.get(i);

            boolean isValueChanged = false;

            if (!oldBoiler.getTPod().equals(newBoiler.getTPod())) {
                this.boilers.get(i).setTPod(newBoiler.getTPod());
                isValueChanged = true;
            }
            if (!oldBoiler.getPPod().equals(newBoiler.getPPod())) {
                this.boilers.get(i).setPPod(newBoiler.getPPod());
                isValueChanged = true;
            }
            if (!oldBoiler.getTUlica().equals(newBoiler.getTUlica())) {
                this.boilers.get(i).setTUlica(newBoiler.getTUlica());
                isValueChanged = true;
            }
            this.boilers.get(i).setTPodFixed(boilersList.get(i).getTPodFixed());
            this.boilers.get(i).setPPodHighFixed(boilersList.get(i).getPPodHighFixed());
            this.boilers.get(i).setPPodLowFixed(boilersList.get(i).getPPodLowFixed());
            this.boilers.get(i).setTPlan(boilersList.get(i).getTPlan());
            this.boilers.get(i).setImageResId(boilersList.get(i).getImageResId());
            this.boilers.get(i).setId(boilersList.get(i).getId());
            this.boilers.get(i).setTAlarm(boilersList.get(i).getTAlarm());
            this.boilers.get(i).setLastUpdated(System.currentTimeMillis());
            if (isValueChanged) {
                oldBoiler.setLastValueChangedTime(System.currentTimeMillis());
            }
        }
        if (forFirstStart){
            for (Boiler boiler : this.boilers) {
                boiler.setIsOk(1, 2);
                boiler.setLastValueChangedTime(System.currentTimeMillis());
            }
            forFirstStart=false;
        }
        isUpdateInProgress.set(false);
    }
    public void setBoilersDataFromRest(List<Boiler> boilers){
        for (int i = 0; i < boilers.size(); i++) {
            Boiler oldBoiler = this.boilers.get(i);
            Boiler newBoiler = boilers.get(i);

            boolean isValueChanged = false;

            if (!oldBoiler.getTPod().equals(newBoiler.getTPod())) {
                this.boilers.get(i).setTPod(newBoiler.getTPod());
                isValueChanged = true;
            }
            if (!oldBoiler.getPPod().equals(newBoiler.getPPod())) {
                this.boilers.get(i).setPPod(newBoiler.getPPod());
                isValueChanged = true;
            }
            if (!oldBoiler.getTUlica().equals(newBoiler.getTUlica())) {
                this.boilers.get(i).setTUlica(newBoiler.getTUlica());
                isValueChanged = true;
            }
            this.boilers.get(i).setTPodFixed(boilers.get(i).getTPodFixed());
            this.boilers.get(i).setPPodHighFixed(boilers.get(i).getPPodHighFixed());
            this.boilers.get(i).setPPodLowFixed(boilers.get(i).getPPodLowFixed());
            this.boilers.get(i).setTPlan(boilers.get(i).getTPlan());
            this.boilers.get(i).setImageResId(boilers.get(i).getImageResId());
            this.boilers.get(i).setId(boilers.get(i).getId());
            this.boilers.get(i).setTAlarm(boilers.get(i).getTAlarm());
            this.boilers.get(i).setLastUpdated(System.currentTimeMillis());
            if (isValueChanged) {
                oldBoiler.setLastValueChangedTime(System.currentTimeMillis());
            }
        }
        if (forFirstStart){
            for (Boiler boiler : this.boilers) {
                boiler.setIsOk(1, 2);
                boiler.setLastValueChangedTime(System.currentTimeMillis());
            }
            forFirstStart=false;
        }
        gasEngineDataService.getGasEngineStation().setExhaustGasTemperatureBoiler7(this.boilers.get(6).getPPod()); // TODO ТЕСТЫ ПРОВЕРЬ
        isUpdateInProgress.set(false);
    }
    public void fetchBoilerData(List<Boiler> boilers) {
        if (isUpdateInProgress.compareAndSet(false, true)) {
            getBoilersFromClient()
                    .subscribe(
                            boilersList -> {

                            },
                            error -> {
                                System.err.println("Ошибка при получении данных: " + error.getMessage());
                                isUpdateInProgress.set(false);
                            }
                    );

        }
        if (isUpdateInProgress2.compareAndSet(false, true)) {
            getCorrectionsTpodFromClient()
                    .subscribe(
                            temperatureCorrections -> {
                                // Здесь обновляем данные
                                this.corrections = temperatureCorrections;
                                isUpdateInProgress2.set(false);
                            },
                            error -> {
                                System.err.println("Ошибка при получении данных: " + error.getMessage());
                                isUpdateInProgress2.set(false);
                            }
                    );
        }
    }
    public void setCorrectionsTpod(String[] corrections1){
        this.corrections.setCorrectionTpod(corrections1);
        setCorrectionsTpodToClient(corrections.getCorrectionTpod())
                .subscribe(
                        response -> System.out.println("Ответ сервера: " + response),
                        error -> System.err.println("Ошибка: " + error.getMessage())
                );
    }
    public void setCorrectionsTAlarm(String[] corrections1){
        this.corrections.setTAlarmCorrectionFromUsers(corrections1);
        setTAlarmToClient(corrections.getTAlarmCorrectionFromUsers())
                .subscribe(
                        response -> System.out.println("Ответ сервера: " + response),
                        error -> System.err.println("Ошибка: " + error.getMessage())
        );
    }
     public Mono<List<Boiler>> getBoilersFromClient() {
             return webClient.get()
                             .uri("/getclientparams")
                             .retrieve()
                             .bodyToMono(String.class)
                             .map(JsonMapper::mapJsonToBoilers);
         }

    public Mono<String[]> setCorrectionsTpodToClient(String[] correctionsTpod) {
        Gson gson = new Gson();
        String json = gson.toJson(correctionsTpod);
        return webClient.post()
                .uri("/setclientparamstPod")
                .body(Mono.just(json), String.class)
                .retrieve()
                .bodyToMono(String[].class);
    }
    public Mono<TemperatureCorrections> getCorrectionsTpodFromClient() {
        return webClient.get()
                .uri("/getcorrect")
                .retrieve()
                .bodyToMono(String.class)
                .map(JsonMapper::mapJsonToCorrections);
    }
    public Mono<String[]> setTAlarmToClient(String[] correctionsTAlarm) {// TODO тут проверить и дописать
        Gson gson = new Gson();
        String json = gson.toJson(correctionsTAlarm);
        return webClient.post()
                .uri("/setclientparamstAlarm")
                .body(Mono.just(correctionsTAlarm), String[].class)
                .retrieve()
                .bodyToMono(String[].class);
    }
}
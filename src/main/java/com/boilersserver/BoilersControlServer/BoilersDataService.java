package com.boilersserver.BoilersControlServer;

import com.google.gson.Gson;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
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
    private final WebClient webClient;
    private final HttpClient httpClient;
    private final ConnectionProvider connectionProvider;
    private final AtomicBoolean isUpdateInProgress = new AtomicBoolean(false);
    private final AtomicBoolean isUpdateInProgress2 = new AtomicBoolean(false);
    private static final String IP = "http://85.175.232.186:4567";//85.175.232.186:4567
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
    @Scheduled(fixedRate = 3000)
    public void fetchBoilerData() {
        if (isUpdateInProgress.compareAndSet(false, true)) {
            getBoilersFromClient()
                    .subscribe(
                            boilersList -> {
                                int[] tempErrors=new int[14];
                                for (int i = 0; i < boilers.size(); i++) {
                                    tempErrors[i]=boilers.get(i).getIsOk();
                                }
                                this.boilers = boilersList;
                                for (int i = 0; i < boilers.size(); i++) {
                                    boilers.get(i).setIsOk(tempErrors[i],2);
                                }
                                isUpdateInProgress.set(false);
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
    public Mono<String[]> setTAlarmToClient(String[] correctionsTAlarm) {// Метод для отправки корректировок tAlarm TODO тут проверить и дописать, тут быть повнимательнее
        Gson gson = new Gson();
        String json = gson.toJson(correctionsTAlarm);
        return webClient.post()
                .uri("/setclientparamstAlarm")
                .body(Mono.just(correctionsTAlarm), String[].class)
                .retrieve()
                .bodyToMono(String[].class);
    }
}
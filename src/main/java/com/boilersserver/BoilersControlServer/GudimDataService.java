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
public class GudimDataService {
    @Getter
    private GudimParams gudimParams = new GudimParams();
    private final AtomicBoolean isUpdateInProgress = new AtomicBoolean(false);
    private final AtomicBoolean isUpdateInProgress2 = new AtomicBoolean(false);
    public GudimDataService(WebClient.Builder webClientBuilder) {
            GudimParams gudimParams1 = new GudimParams();
            gudimParams1.setId(0);
            gudimParams1.setIsOk(2,1);
            gudimParams1.setLastUpdated(0);
            gudimParams1.setVersion(0);
            gudimParams1.setInTownTpod("99");
            gudimParams1.setReserv1Lvl("99");
            gudimParams1.setReserv2Lvl("99");
            gudimParams1.setReserv1Tpod("99");
            gudimParams1.setReserv2Tpod("99");
            gudimParams1.setWell1Tpod("99");
            gudimParams1.setWell2Tpod("99");
            gudimParams1.setTUlica("5");
            gudimParams=gudimParams1;
    }
    @PreDestroy
    public void onDestroy() {
        System.out.println("clean GudimDataService");
    }
    public boolean forFirstStart = true;

    public void refreshGudimData(GudimParams gudimParams){
        this.gudimParams=gudimParams;
    }
}
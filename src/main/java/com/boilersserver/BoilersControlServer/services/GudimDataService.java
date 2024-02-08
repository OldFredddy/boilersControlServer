package com.boilersserver.BoilersControlServer.services;

import com.boilersserver.BoilersControlServer.entities.GudimParams;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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
            gudimParams1.setStreet("5");
            gudimParams1.setInTownFlow("20");
            gudimParams=gudimParams1;
    }
    @PreDestroy
    public void onDestroy() {
        System.out.println("clean GudimDataService");
    }
    public boolean forFirstStart = true;

    public void refreshData(GudimParams gudimParams){
        gudimParams.setInTownFlow("***");
        gudimParams.setWell2Tpod("***");
        gudimParams.setReserv2Lvl("***");
        this.gudimParams=gudimParams;
    }
}
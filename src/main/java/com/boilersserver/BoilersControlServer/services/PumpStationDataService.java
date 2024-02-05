package com.boilersserver.BoilersControlServer.services;

import com.boilersserver.BoilersControlServer.entities.GudimParams;
import com.boilersserver.BoilersControlServer.entities.PumpStation;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class PumpStationDataService {
    @Getter
    private PumpStation pumpStation = new PumpStation();
    public PumpStationDataService(WebClient.Builder webClientBuilder) {
            PumpStation pumpStation1 = new PumpStation();
            pumpStation1.setId(0);
            pumpStation1.setIsOk(2,1);
            pumpStation1.setLastUpdated(0);
            pumpStation1.setVersion(0);
            pumpStation1.setFromPumpStationTpod("99");
            pumpStation1.setForCityFlow("30");
            pumpStation1.setReserv1Lvl("99");
            pumpStation1.setReserv2Lvl("99");
            pumpStation1.setReserv1Tpod("99");
            pumpStation1.setReserv2Tpod("99");
            pumpStation1.setStreet("5");
            pumpStation1.setMagicIndicator1("99");
            pumpStation1.setMagicIndicator1("99");
            pumpStation1.setMagicIndicator1("99");
            pumpStation1.setMagicIndicator1("99");
            pumpStation=pumpStation1;
    }
    @PreDestroy
    public void onDestroy() {
        System.out.println("clean PumpStationDataService");
    }
    public boolean forFirstStart = true;

    public void refreshData(PumpStation pumpStation){
        this.pumpStation=pumpStation;
    }
}
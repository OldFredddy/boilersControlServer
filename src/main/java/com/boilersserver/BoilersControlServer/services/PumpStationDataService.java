package com.boilersserver.BoilersControlServer.services;

import com.boilersserver.BoilersControlServer.entities.GudimParams;
import com.boilersserver.BoilersControlServer.entities.PumpStation;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class PumpStationDataService {
    @Getter
    private PumpStation pumpStation = new PumpStation();
    private List<String> magicIndicatorsBuffer = new ArrayList<>();
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
            pumpStation1.setMagicIndicator1("***");
            pumpStation1.setMagicIndicator1("***");
            pumpStation1.setMagicIndicator1("***");
            pumpStation1.setMagicIndicator1("***");
            pumpStation=pumpStation1;
            magicIndicatorsBuffer.add("5*");
            magicIndicatorsBuffer.add("5*");
            magicIndicatorsBuffer.add("5*");
            magicIndicatorsBuffer.add("5*");
    }
    @PreDestroy
    public void onDestroy() {
        System.out.println("clean PumpStationDataService");
    }
    public boolean forFirstStart = true;

    public void refreshData(PumpStation pumpStation){
        this.pumpStation=pumpStation;
        this.pumpStation.setMagicIndicator1(magicIndicatorsBuffer.get(0));
        this.pumpStation.setMagicIndicator2(magicIndicatorsBuffer.get(1));
        this.pumpStation.setMagicIndicator3(magicIndicatorsBuffer.get(2));
        this.pumpStation.setMagicIndicator4(magicIndicatorsBuffer.get(3));
    }
    public void setMagicIndicators(List<String> magicIndicators){
        magicIndicatorsBuffer.set(0,magicIndicators.get(0));
        magicIndicatorsBuffer.set(1,magicIndicators.get(1));
        magicIndicatorsBuffer.set(2,magicIndicators.get(2));
        magicIndicatorsBuffer.set(3,magicIndicators.get(3));
        this.pumpStation.setMagicIndicator1(magicIndicators.get(0));
        this.pumpStation.setMagicIndicator2(magicIndicators.get(1));
        this.pumpStation.setMagicIndicator3(magicIndicators.get(2));
        this.pumpStation.setMagicIndicator4(magicIndicators.get(3));
    }
}
package com.boilersserver.BoilersControlServer.services;

import com.boilersserver.BoilersControlServer.entities.GasEngineStation;
import com.boilersserver.BoilersControlServer.entities.GudimParams;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class GasEngineDataService {
    @Getter
    private GasEngineStation gasEngineStation = new GasEngineStation();
    public GasEngineDataService(WebClient.Builder webClientBuilder) {
        GasEngineStation gasEngineStation1 = new GasEngineStation();
        gasEngineStation1.setId(0);
        gasEngineStation1.setIsOk(2,1);
        gasEngineStation1.setLastUpdated(0);
        gasEngineStation1.setVersion(0);
        gasEngineStation1.setEngineTemp("5");
        gasEngineStation1.setRadiatorTemp("5");
        gasEngineStation1.setRoomTemp("5");
        gasEngineStation1.setGeneratorTemp("5");
        gasEngineStation1.setNormalEngineTemp("5");
        gasEngineStation1.setNormalRadiatorTemp("5");
        gasEngineStation1.setNormalGeneratorTemp("5");
        gasEngineStation1.setExhaustGasTemperatureBoiler7("5");
        gasEngineStation = gasEngineStation1;
    }
    @PreDestroy
    public void onDestroy() {
        System.out.println("clean GasEngineDataService");
    }


    public void refreshData(GasEngineStation gasEngineStation){
        this.gasEngineStation=gasEngineStation;
    }
}
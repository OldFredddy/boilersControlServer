package com.boilersserver.BoilersControlServer.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class GasEngineStation {
    private int isOk;  // 0-waiting, 1 - good, 2 - error
    @Setter private int isOkFromBoiler7;  // 0-waiting, 1 - good, 2 - error
    @Setter private String engineTemp;
    @Setter private String roomTemp;
    @Setter private String generatorTemp;
    @Setter private String radiatorTemp;
    @Setter private String normalEngineTemp;
    @Setter private String normalRoomTemp;
    @Setter private String normalGeneratorTemp;
    @Setter private String normalRadiatorTemp;
    @Setter private String exhaustGasTemperatureBoiler7;  //Gas Station from Boiler7 PLC
    @Setter private Integer id;
    @Setter private long version;
    @Setter private long lastUpdated;
    public void setIsOk(int isOk, long newVersion) {
        if (newVersion > this.version) {
            this.isOk = isOk;
            this.version = newVersion;
        }
    }
}

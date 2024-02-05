package com.boilersserver.BoilersControlServer.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class PumpStation {
    private int isOk;  // 0-waiting, 1 - good, 2 - error
    @Setter private String fromPumpStationTpod;
    @Setter private String Reserv1Tpod;
    @Setter private String Reserv2Tpod;
    @Setter private String Reserv1Lvl;
    @Setter private String Reserv2Lvl;
    @Setter private String forCityFlow;
    @Setter private Integer id;
    @Setter private long version;
    @Setter private long lastUpdated;
    @Setter private String magicIndicator1;
    @Setter private String magicIndicator2;
    @Setter private String magicIndicator3;
    @Setter private String magicIndicator4;
    @Setter private String street;
    public void setIsOk(int isOk, long newVersion) {
        if (newVersion > this.version) {
            this.isOk = isOk;
            this.version = newVersion;
        }
    }
}

package com.boilersserver.BoilersControlServer.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class GudimParams {
    private int isOk;  // 0-waiting, 1 - good, 2 - error
    @Setter private String well1Tpod;
    @Setter private String well2Tpod;
    @Setter private String reserv1Tpod;
    @Setter private String reserv2Tpod;
    @Setter private String reserv1Lvl;
    @Setter private String reserv2Lvl;
    @Setter private String inTownTpod;
    @Setter private Integer id;
    @Setter private String inTownFlow;
    @Setter private long version;
    @Setter private long lastUpdated;
    @Setter private String street;
    public void setIsOk(int isOk, long newVersion) {
        if (newVersion > this.version) {
            this.isOk = isOk;
            this.version = newVersion;
        }
    }
}

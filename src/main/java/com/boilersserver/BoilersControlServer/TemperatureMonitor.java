package com.boilersserver.BoilersControlServer;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.LinkedList;

public class TemperatureMonitor {
    private static final int BOILER_MAKATROVYH=-6;
    @Getter
    @Setter
    private int lowLimit;
    @Getter
    @Setter
    private int highLimit;

    public boolean isTemperatureAnomaly(String currentTemp, String tStreet,int numberOfBoiler, String fixedTpod,
                                        String correctTplan, String correctFromUsers) {
        int correct = Integer.parseInt(correctTplan);
        int tStreetInt = (int) Math.round(Double.parseDouble(tStreet));
        double tPlan = tStreetInt * tStreetInt * 0.00886 - 0.803 * tStreetInt + 54;


        if (!fixedTpod.equals("-1")) {
            tPlan= Integer.parseInt(fixedTpod);
        }
        if (numberOfBoiler == 8){
            LocalTime currentTime = LocalTime.now();
            LocalTime start = LocalTime.of(22, 20);
            LocalTime end = LocalTime.of(7, 40);
            if (currentTime.isAfter(start) && currentTime.isBefore(end)) {
                tPlan+=BOILER_MAKATROVYH;
            }
        }
        tPlan-=5;
            tPlan+=correct;
            tPlan+=Integer.parseInt(correctFromUsers);
            int currentTempInt = (int) Math.round(Double.parseDouble(currentTemp));
            setLowLimit((int) (tPlan - 15));
            setHighLimit((int) (tPlan + 12));
            return currentTempInt < (tPlan - 15) || currentTempInt > (tPlan + 12);
    }

}

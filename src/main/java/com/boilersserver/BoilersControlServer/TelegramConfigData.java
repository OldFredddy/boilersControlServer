package com.boilersserver.BoilersControlServer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TelegramConfigData {
    private int[] fixedTpod;
    private float[] fixedPpodHigh;
    private float[] fixedPpodLow;

}
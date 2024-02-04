package com.boilersserver.BoilersControlServer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class JsonMapper {

    public static List<Boiler> mapJsonToBoilers(String json) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Boiler>>(){}.getType();
        return gson.fromJson(json, listType);
    }
    public static TemperatureCorrections mapJsonToCorrections(String json) {
        Gson gson = new Gson();
        Type listType = new TypeToken<TemperatureCorrections>(){}.getType();
        return gson.fromJson(json, listType);
    }
    public static TemperatureCorrections mapJsonToGudimParams(String json) {
        Gson gson = new Gson();
        Type listType = new TypeToken<GudimParams>(){}.getType();
        return gson.fromJson(json, listType);
    }
    public static TemperatureCorrections mapJsonToPumpStation(String json) {
        Gson gson = new Gson();
        Type listType = new TypeToken<PumpStation>(){}.getType();
        return gson.fromJson(json, listType);
    }
}
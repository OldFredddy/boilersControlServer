package com.boilersserver.BoilersControlServer.entities;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Tokens {

    @Value("${key1}")
    private String key1;

    @Value("${key2}")
    private String key2;

    public String getKey1() {
        return key1;
    }

    public String getKey2() {
        return key2;
    }

}

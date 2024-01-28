package com.boilersserver.BoilersControlServer;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class BoilerLog {
    @Id
    private String id;
    private Boiler boiler;
    private String errorDesc;
    private long timestamp;
}
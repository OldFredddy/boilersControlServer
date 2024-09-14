package com.boilersserver.BoilersControlServer.services;


import com.boilersserver.BoilersControlServer.entities.*;
import com.boilersserver.BoilersControlServer.utils.Graphics;
import com.boilersserver.BoilersControlServer.utils.TemperatureMonitor;
import com.boilersserver.BoilersControlServer.utils.ZvonokPostService;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Component
public class TelegramService extends TelegramLongPollingBot {
    Graphics graphics;
    static volatile boolean keepRunning = true;
    static volatile int boilerControlNum = -1;
    private final boolean[] secondAttempt={false,false,false,false,false,false,false,false,false,false,false,false,false,false};
    public String[] boilerNames = {
            "Котельная «Склады Мищенко»",                   //0   кот№1 Склады Мищенко
            "Котельная «Выставка Ендальцева»",              //1   кот№2 Ендальцев         (датчик на базе)
            "Котельная «ЧукотОптТорг»",                     //2   кот№3 ЧукотОптТорг      (датчик на базе)
            "Котельная «ЧСБК новая»",                       //3   кот№4 "ЧСБК Новая"
            "Котельная «Офис СВТ»",                         //4   кот№5 офис "СВТ"
            "Котельная «Общежитие на Южной»",               //5   кот№6 общежитие на Южной
            "Котельная «Офис ЧСБК»",                        //6   кот№7 офис ЧСБК
            "Котельная «Рынок»",                            //7   кот№8 "Рынок"
            "Котельная «Макатровых»",                       //8   кот№9 Макатровых
            "Котельная ДС «Сказка»",                        //9   кот№10  "Д/С Сказка"
            "Котельная «Полярный»",                         //10  кот№11 Полярный
            "Котельная «Департамент»",                      //11  кот№12 Департамент
            "Котельная «Офис ЧСБК квартиры»",               //12  кот№13 квартиры в офисе
            "Котельная Шишкина"                             //13  кот№14 ТО Шишкина
    };
    private volatile boolean enableCallService=false;
    public float[] normalPvxHigh={0.5f, 0.5f, 0.5f, 0.5f, 0.35f, 0.35f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f,0.5f, 0.53f};
    public float[] normalPvxLow= {0.3f, 0.3f, 0.3f, 0.3f, 0.12f, 0.3f, 0.02f, 0.32f, 0.30f, 0.32f, 0.32f, 0.32f, 0.02f, 0.22f};
    private Timer timer = new Timer();
    private Timer timerSilintReset = new Timer();
    public boolean [] errorsArray = {false, false, false, false, false, false, false, false, false, false, false, false, false, false};
    public boolean [] disableAlertsBoilers = {false, false, false, false, false, false, false, false, false, false, false, false, false, false};
    public boolean [] pressureErrorsArray = {false, false, false, false, false, false, false, false, false, false, false, false, false, false};
    public boolean [] gasEngineErrorsArray = {false, false, false, false};
    public boolean [] temperatureErrorsArray = {false, false, false, false, false, false, false, false, false, false, false, false, false, false};
    static volatile Integer messageId = -1;
    List<Long> clientsId = new ArrayList<>();
    List<Long> clientsIdGasEngine = new ArrayList<>();
    Long chiefOfWasteWaterStation = 7084589354L;
    public boolean checkForAvary =true;
    public boolean disableAlertsGasStation =false;
    private static final String TEMPERATURE_PROBLEM_MESSAGE = "Проблема в температуре подачи!";
    private static final String PRESSURE_PROBLEM_LOW_MESSAGE = "Проблема в давлении! Ниже допустимого!";
    private static final String PRESSURE_PROBLEM_HIGH_MESSAGE = "Проблема в давлении! Превышение!";
    private static final String INVALID_VALUE = "-1000";
    private static final long SLEEP_TIME = 2000L;
    private static final long LONG_SLEEP_TIME = 2800L;
    private static final long LONG_LONG_SLEEP_TIME = 4500L;
    private static final long SHORT_SLEEP_TIME = 2800L;
    private static final long SUPER_SHORT_SLEEP_TIME = 800L;
    private volatile BoilersDataService boilersDataService;
    private volatile GudimDataService gudimDataService;
    private volatile PumpStationDataService pumpStationDataService;
    private volatile GasEngineDataService gasEngineDataService;
    private volatile BoilerLoggingService boilerLoggingService;
    private TemperatureMonitor temperatureMonitor = new TemperatureMonitor();
    Tokens tokens;
    double currentPpodHigh;
    double currentPpodLow;

    @Autowired
    public TelegramService(BoilersDataService boilersDataService, Tokens tokens, Graphics graphics,
                           BoilerLoggingService boilerLoggingService, GudimDataService gudimDataService,
                           PumpStationDataService pumpStationDataService, GasEngineDataService gasEngineDataService)  {
        this.boilersDataService = boilersDataService;
        this.tokens = tokens;
        this.graphics=graphics;
        this.boilerLoggingService = boilerLoggingService;
        this.gudimDataService = gudimDataService;
        this.pumpStationDataService = pumpStationDataService;
        this.gasEngineDataService = gasEngineDataService;
    }
    AtomicBoolean[] flagSilentReset = new AtomicBoolean[14];
    @PostConstruct
    public void init() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
            System.out.println("Bot started");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
           keepRunning=false;
           timer.cancel();
            System.out.println("ShutdownHook executed");
        }));
       clientsId.add(6290939545L);//TODO enter by xml or DB
       clientsId.add(1102774002L);
       clientsId.add(6588122746L);
       clientsId.add(1589552937L);
       clientsIdGasEngine.add(1102774002L);
       clientsIdGasEngine.add(chiefOfWasteWaterStation);
       //clientsId.add(5164539595L);
        for (int i = 0; i < flagSilentReset.length; i++) {
            flagSilentReset[i] = new AtomicBoolean(false);
        }
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId("@BoilersAnadyr");
        sendMessage.setText(getBoilersParamsText(errorsArray)+"\n"+
                getGudimParamsTable1(gudimDataService.getGudimParams())+"\n"+
                getPumpStationTableView(pumpStationDataService.getPumpStation())+"\n"+
                getGasStationParamsTable(gasEngineDataService.getGasEngineStation()));
        sendMessage.setParseMode("Markdown");
        try {
            Message message = execute(sendMessage);
            messageId = message.getMessageId();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        monitorThread2.start();
        pressureMonitor.start();
        gasEngineTemperatureMonitor.start();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    Thread.sleep(SHORT_SLEEP_TIME);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId("@BoilersAnadyr");
                try {
                    LocalTime currentTime = LocalTime.now();
                    LocalTime timePlusNine = currentTime.plusHours(9);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                    String formattedTime = timePlusNine.format(formatter);
                    sendMessage.setText(formattedTime+"\n"+ getBoilersParamsText(errorsArray)+"\n"+
                            getGudimParamsTable1(gudimDataService.getGudimParams())+"\n"+
                            getPumpStationTableView(pumpStationDataService.getPumpStation())+"\n"+
                            getGasStationParamsTable(gasEngineDataService.getGasEngineStation()));
                    sendMessage.setParseMode("Markdown");
                    Message message = execute(sendMessage);
                    Thread.sleep(LONG_SLEEP_TIME);
                    DeleteMessage deleteMessage = new DeleteMessage("@BoilersAnadyr",messageId);
                    Thread.sleep(SLEEP_TIME);
                    messageId = message.getMessageId();
                    Thread.sleep(SLEEP_TIME);
                    execute(deleteMessage);
                    Thread.sleep(SLEEP_TIME);
                } catch (TelegramApiException | InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < errorsArray.length; i++) {
                    trySilentReset(i);
                }
            }
        }, 5 * 60 * 1000, 5 * 60 * 1000);
        timerSilintReset.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (AtomicBoolean atomicBoolean : flagSilentReset) {
                    atomicBoolean.set(false);
                }

            }
        }, 32  * 1000, 37  * 1000);
    }

    Thread monitorThread2 = new Thread(()->{
        while (keepRunning){
            try {
                try {
                    Thread.sleep(LONG_LONG_SLEEP_TIME);
                } catch (InterruptedException e) {
                    continue;
                }
                refreshMessage(getBoilersParamsText(errorsArray)+"\n"+
                        getGudimParamsTable1(gudimDataService.getGudimParams())+"\n"+
                        getPumpStationTableView(pumpStationDataService.getPumpStation())+"\n"+
                        getGasStationParamsTable(gasEngineDataService.getGasEngineStation()));

                Thread.sleep(SLEEP_TIME);                                    //блок проверки аварий
                for (int i = 0; i < boilersDataService.getBoilers().size(); i++) {
                       Boiler boiler = boilersDataService.getBoilers().get(i);
                        if (temperatureErrorsArray[i]){
                            continue;
                        }
                       if (!temperatureErrorsArray[i]) {
                           if ((temperatureMonitor.isTemperatureAnomaly(boiler.getId(),
                                   boilersDataService.getCorrectionTpodAtIndex(i),
                                  boiler))) { //TODO ОШИПКА
                               if (!flagSilentReset[i].get()) {
                                   sendAttention(i, "Проблема в температуре подачи!\n" + "Верхний предел: " + temperatureMonitor.getHighLimit() + " °C" +
                                           "\nНижний предел: " + temperatureMonitor.getLowLimit() + " °C");
                                   temperatureErrorsArray[i]=true;
                                   Thread.sleep(SLEEP_TIME);
                               } else {
                                   flagSilentReset[i].set(false);
                                   errorsArray[i] = true;
                                   temperatureErrorsArray[i]=true;
                               }
                           }
                       }
                }

                System.gc();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (InterruptedException | TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    });
    Thread pressureMonitor = new Thread(()->{
        while (keepRunning){
            try {
                try {
                    Thread.sleep(LONG_LONG_SLEEP_TIME);
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    continue;
                }
                for (int i = 0; i < boilersDataService.getBoilers().size(); i++) {
                    if (pressureErrorsArray[i]){
                        continue;
                    }
                    if (boilersDataService.getBoilers().get(i).getPPodLowFixed().equals("-1.0")||boilersDataService.getBoilers().get(i).getPPodHighFixed().equals("-1.0")){
                        currentPpodHigh=normalPvxHigh[i];
                        currentPpodLow=normalPvxLow[i];
                    } else {
                        currentPpodHigh=Double.parseDouble(boilersDataService.getBoilers().get(i).getPPodHighFixed());
                        currentPpodLow= Double.parseDouble(boilersDataService.getBoilers().get(i).getPPodLowFixed());
                    }
                    if (!pressureErrorsArray[i]) {
                        if ((!boilersDataService.getBoilers().get(i).getPPod().equals(INVALID_VALUE)) &&            //pressure errors check
                                ((Float.parseFloat(boilersDataService.getBoilers().get(i).getPPod()) < currentPpodLow) ||
                                        (Float.parseFloat(boilersDataService.getBoilers().get(i).getPPod()) > currentPpodHigh))) {
                            if (!flagSilentReset[i].get()) {
                                String message;
                                if (Float.parseFloat(boilersDataService.getBoilers().get(i).getPPod()) < currentPpodLow) {
                                    message = "Проблема в давлении! Ниже допустимого!";
                                } else {
                                    message = "Проблема в давлении! Превышение!";
                                }
                                sendAttention(i, message);
                                pressureErrorsArray[i]=true;
                                Thread.sleep(2000);
                            } else {
                                flagSilentReset[i].set(false);
                                errorsArray[i] = true;
                                pressureErrorsArray[i]=true;
                            }
                        }
                    }
                }
                System.gc();
            } catch (RuntimeException e) {
                e.printStackTrace();
                continue;
            } catch (InterruptedException | TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    });
    Thread gasEngineTemperatureMonitor = new Thread(()->{
        while (keepRunning){
            try {
                try {
                    Thread.sleep(LONG_LONG_SLEEP_TIME);
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    continue;
                }
                for (int i = 0; i < gasEngineErrorsArray.length; i++) {
                    if (!gasEngineErrorsArray[i]){
                        if (Math.abs(Float.parseFloat(gasEngineDataService.getGasEngineStation().getEngineTemp())-Float
                                .parseFloat(gasEngineDataService.getGasEngineStation().getNormalEngineTemp()))>10){
                            sendAttentionGasEngine(0,"");
                        }
                        if (Math.abs(Float.parseFloat(gasEngineDataService.getGasEngineStation().getGeneratorTemp())-Float
                                .parseFloat(gasEngineDataService.getGasEngineStation().getNormalGeneratorTemp()))>10){
                            sendAttentionGasEngine(1,"");
                        }
                        if (Math.abs(Float.parseFloat(gasEngineDataService.getGasEngineStation().getRadiatorTemp())-Float
                                .parseFloat(gasEngineDataService.getGasEngineStation().getNormalRadiatorTemp()))>10){
                            sendAttentionGasEngine(2,"");
                        }
                    }
                }
                System.gc();
            } catch (RuntimeException e) {
                e.printStackTrace();
                continue;
            } catch (InterruptedException | TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    });
    private void refreshMessage(String text){
        LocalTime currentTime = LocalTime.now();
        LocalTime timePlusNine = currentTime.plusHours(9);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = timePlusNine.format(formatter);
        text=formattedTime+"\n"+text;
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId("@BoilersAnadyr");
        editMessage.setMessageId(messageId);
        editMessage.setText(text);
        editMessage.setParseMode("Markdown");
        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    public String getBoilersParamsText(boolean[] errorsArray) {
        StringBuilder result = new StringBuilder();
        String[] tPod = new String[boilersDataService.getBoilers().size()];
        String[] pVx = new String[boilersDataService.getBoilers().size()];
        String[] tStreet = new String[boilersDataService.getBoilers().size()];

        for (int i = 0; i<boilersDataService.getBoilers().size();i++){
           tPod[i]=boilersDataService.getBoilers().get(i).getTPod();
           pVx[i]=boilersDataService.getBoilers().get(i).getPPod();
           tStreet[i]=boilersDataService.getBoilers().get(i).getTUlica();
        }
        tPod[2]="*";
        pVx[2]="*";
        tStreet[2]="*";
        String[][] data = {
                tPod,
                pVx,
                tStreet
        };
        int[] maxLengths = new int[5];
        maxLengths[0] = String.valueOf(boilerNames.length).length();
        maxLengths[1] = Arrays.stream(data[0]).filter(Objects::nonNull).mapToInt(String::length).max().orElse(0);
        maxLengths[2] = Arrays.stream(data[1]).filter(Objects::nonNull).mapToInt(String::length).max().orElse(0);
        maxLengths[3] = Arrays.stream(data[2]).filter(Objects::nonNull).mapToInt(String::length).max().orElse(0);
        maxLengths[4] = 1;
        int maxWidth = 25;
        for (int i = 0; i < maxLengths.length; i++) {
            maxLengths[i] = Math.min(maxLengths[i], maxWidth);
        }
        String format = "| %" + maxLengths[0] + "s | %" + maxLengths[1] + "s | %" + maxLengths[2] + "s | %" + maxLengths[3] + "s | %" + maxLengths[4] + "s \n";
        String header = String.format(format, "№", "Tпод", "Pпод", "Tул", "S");
        int maxWidth1 = maxLengths[0] + maxLengths[1] + maxLengths[2] + maxLengths[3] + maxLengths[4] + 9; // 9 - spaces and table borders
        int tStreetInt = Math.round(Float.parseFloat(boilersDataService.getBoilers().get(1).getTUlica()));
        int tPlan = Math.round(tStreetInt * tStreetInt * 0.00886f - 0.803f * tStreetInt + 54);
        String centeredText = ("              По графику: " + tPlan+"°C");
        result.append(centeredText).append("\n");
        result.append("```\n").append(header);
        String units = String.format(format, "", "°C", "МПа", "°C", "");
        result.append(units);
        for (int i = 0; i < boilerNames.length; i++) {
            String emoji = errorsArray[i] ? "🔴" : "🟢";
            String formattedRow = String.format(format, (i + 1), data[0][i] == null ? "" : data[0][i], data[1][i] == null ? "" : data[1][i], data[2][i] == null ? "" : data[2][i], emoji);
            result.append(formattedRow);
        }

        return result.toString();
    }

    public String getGudimParamsTable1(GudimParams gudimParams) {
        StringBuilder result = new StringBuilder();
        result.append("Насосная ст. 1-го подъема - Гудым\n");
        if (gudimParams.getStreet()!="5"){
            result.append("Температура воздуха: "+gudimParams.getStreet()+"°C"+"\n");
        } else {
            result.append("Температура воздуха: "+boilersDataService.getBoilers().get(2).getTUlica()+"°C"+"\n");
        }
        String[] headers = {"***", "Тпод", "Ур. воды", "Расх", "S"};
        Object[][] rows = {
                {"Ск.1", gudimParams.getWell1Tpod(), "", "", getStatusEmoji(gudimParams.getIsOk())},
                {"Ск.2", gudimParams.getWell2Tpod(), "", "", getStatusEmoji(gudimParams.getIsOk())},
                {"Рез. 1", gudimParams.getReserv1Tpod(), gudimParams.getReserv1Lvl(), "", getStatusEmoji(gudimParams.getIsOk())},
                {"Рез. 2", gudimParams.getReserv2Tpod(), gudimParams.getReserv2Lvl(), "", getStatusEmoji(gudimParams.getIsOk())},
                {"В гор.", gudimParams.getInTownTpod(), "", gudimParams.getInTownFlow(), getStatusEmoji(gudimParams.getIsOk())}
        };
        int[] maxLengths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            maxLengths[i] = headers[i].length(); // начальное значение - длина заголовка
            for (Object[] row : rows) {
                if (row[i] != null) {
                    maxLengths[i] = Math.max(maxLengths[i], row[i].toString().length());
                }
            }
        }
        String format = "";
        for (int i = 0; i < maxLengths.length; i++) {
            format += "| %-" + maxLengths[i] + "s ";
        }
        format += "\n";
        // Формируем заголовок таблицы
        result.append(String.format(format, (Object[]) headers));
        String units = String.format(format, "", "°C", "м", "м3/ч", "");
        result.append(units);
        // Формируем строки таблицы с учетом центрирования
        for (Object[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                // Центрирование содержимого каждой ячейки
                String cellValue = (row[i] != null) ? row[i].toString() : "";
                int spaceToAdd = maxLengths[i] - cellValue.length();
                int paddingBefore = spaceToAdd / 2;
                int paddingAfter = spaceToAdd - paddingBefore;
                String formatValue = "".repeat(paddingBefore) + cellValue + "".repeat(paddingAfter);//TODO вот тут проследить за пробелами
                row[i] = formatValue;
            }
            result.append(String.format(format, row));
        }

        return result.toString();
    }
    public String getPumpStationTableView(PumpStation pumpStation) {
        StringBuilder result = new StringBuilder();
        result.append("Насосная ст. 3-го подъема - У.Копи\n");
        if (pumpStation.getStreet()!="5"){
            result.append("Температура воздуха: "+pumpStation.getStreet()+"°C"+"\n");
        } else {
            result.append("Температура воздуха: "+boilersDataService.getBoilers().get(2).getTUlica()+"°C"+"\n");
        }
        String[] headers = {"***", "Тпод", "Ур. воды", "Расх", "S"};
        Object[][] rows = {
                {"В город", "", "" , pumpStation.getForCityFlow(), getStatusEmoji(pumpStation.getIsOk())},
                {"С Гудыма", pumpStation.getFromPumpStationTpod(), "", "", getStatusEmoji(pumpStation.getIsOk())},
                {"Рез. 1", pumpStation.getReserv1Tpod(), pumpStation.getReserv1Lvl(), "", getStatusEmoji(pumpStation.getIsOk())},
                {"Рез. 2", pumpStation.getReserv2Tpod(), pumpStation.getReserv2Lvl(), "", getStatusEmoji(pumpStation.getIsOk())},
                {"Д.1", pumpStation.getMagicIndicator1(), "", "", getStatusEmoji(pumpStation.getIsOk())},
                {"Д.2", pumpStation.getMagicIndicator2(), "", "", getStatusEmoji(pumpStation.getIsOk())},
                {"Д.3", pumpStation.getMagicIndicator3(), "", "", getStatusEmoji(pumpStation.getIsOk())},
                {"Д.4", pumpStation.getMagicIndicator4(), "", "", getStatusEmoji(pumpStation.getIsOk())}
        };
        int[] maxLengths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            maxLengths[i] = headers[i].length(); // начальное значение - длина заголовка
            for (Object[] row : rows) {
                if (row[i] != null) {
                    maxLengths[i] = Math.max(maxLengths[i], row[i].toString().length());
                }
            }
        }
        String format = "";
        for (int i = 0; i < maxLengths.length; i++) {
            // Добавляем логику центрирования без добавления дополнительных отступов
            format += "| %-" + maxLengths[i] + "s ";
        }
        format += "\n";
        result.append(String.format(format, (Object[]) headers));
        String units = String.format(format, "", "°C", "м", "м3/ч", "");
        result.append(units);
        // Формируем строки таблицы с учетом центрирования
        for (Object[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                // Центрирование содержимого каждой ячейки
                String cellValue = (row[i] != null) ? row[i].toString() : "";
                int spaceToAdd = maxLengths[i] - cellValue.length();
                int paddingBefore = spaceToAdd / 2;
                int paddingAfter = spaceToAdd - paddingBefore;
                String formatValue = "".repeat(paddingBefore) + cellValue + "".repeat(paddingAfter);//TODO вот тут проследить за пробелами
                row[i] = formatValue;
            }
            result.append(String.format(format, row));
        }

        return result.toString();
    }
    public String getGasStationParamsTable(GasEngineStation gasEngineStation) {
        StringBuilder result = new StringBuilder();
        result.append("Газомоторная станция - ВОС\n");
        String[] headers = {"***", " Т  ", "***", "***", "S"};
        Object[][] rows = {
                {"Двигатель", gasEngineStation.getEngineTemp(), "" , "", getStatusEmoji(gasEngineStation.getIsOk())},
                {"Радиатор", gasEngineStation.getRadiatorTemp(), "", "", getStatusEmoji(gasEngineStation.getIsOk())},
                {"Генератор", gasEngineStation.getGeneratorTemp(), "", "", getStatusEmoji(gasEngineStation.getIsOk())},
                {"Помещение", gasEngineStation.getRoomTemp(), "", "", getStatusEmoji(gasEngineStation.getIsOk())}
        };
        int[] maxLengths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            maxLengths[i] = headers[i].length(); // начальное значение - длина заголовка
            for (Object[] row : rows) {
                if (row[i] != null) {
                    maxLengths[i] = Math.max(maxLengths[i], row[i].toString().length());
                }
            }
        }
        String format = "";
        for (int i = 0; i < maxLengths.length; i++) {
            // Добавляем логику центрирования без добавления дополнительных отступов
            format += "| %-" + maxLengths[i] + "s ";
        }
        format += "\n";
        result.append(String.format(format, (Object[]) headers));
        String units = String.format(format, "", "°C", "", "", "");
        result.append(units);
        // Формируем строки таблицы с учетом центрирования
        for (Object[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                // Центрирование содержимого каждой ячейки
                String cellValue = (row[i] != null) ? row[i].toString() : "";
                int spaceToAdd = maxLengths[i] - cellValue.length();
                int paddingBefore = spaceToAdd / 2;
                int paddingAfter = spaceToAdd - paddingBefore;
                String formatValue = "".repeat(paddingBefore) + cellValue + "".repeat(paddingAfter);//TODO вот тут проследить за пробелами
                row[i] = formatValue;
            }
            result.append(String.format(format, row));
        }
        result.append("```\n");
        return result.toString();
    }
    private String getStatusEmoji(int status) {
        switch (status) {
            case 0: return "🟢"; // waiting
            case 1: return "🟢"; // good
            case 2: return "🔴"; // error
            default: return "❓"; // unknown
        }
    }

    public void sendAttention(int boilerIndex, String comment) throws TelegramApiException, InterruptedException {
        errorsArray[boilerIndex]=true;
        boilersDataService.getBoilers().get(boilerIndex).setIsOk(2,boilersDataService.getBoilers().get(boilerIndex).getVersion()+1);  //0-waiting 1 - good 2 - error
         if (enableCallService){
                 ZvonokPostService.call("+79140808817");//TODO Replace with xml|.conf
                 ZvonokPostService.call("+79145353244");
             }
         String msgText=boilerNames[boilerIndex] + "\n" + "Аварийное значение!" + " Общие параметры на момент аварии:" + "\n"
                 + "\uD83D\uDD25 Температура уходящей воды: " + boilersDataService.getBoilers().get(boilerIndex).getTPod() + " °C" + "\n"
                 + "⚖️\uD83D\uDCA8 Давление в системе отопления: " + boilersDataService.getBoilers().get(boilerIndex).getPPod() + " МПа" + "\n" + comment;
        if (!disableAlertsBoilers[boilerIndex]){
            for (int i = 0; i < clientsId.size() ; i++) {
                SendMessage message1 = new SendMessage();
                message1.setChatId(clientsId.get(i));      // чат id
                message1.setText(msgText);
                Message message = execute(message1);
               // avaryMessageID[i] = message.getMessageId();
                Message message2 = execute(Messages.avaryKeyboard(String.valueOf(clientsId.get(i))));
                Thread.sleep(100);
            }
            if (boilerIndex == 3) {
                SendMessage tempMessage = new SendMessage();
                tempMessage.setChatId(chiefOfWasteWaterStation);
                tempMessage.setText(msgText);
                Message message3 = execute(tempMessage);
                Message message4 = execute(Messages.avaryKeyboard(String.valueOf(chiefOfWasteWaterStation)));
                Thread.sleep(100);
            }
        }

        //boilerLoggingService.logBoilerStatus(boilersDataService.getBoilers().get(boilerIndex),msgText);
    }
    public void sendAttentionGasEngine(int paramId, String comment) throws TelegramApiException, InterruptedException {

        gasEngineDataService.getGasEngineStation().setIsOk(2,gasEngineDataService.getGasEngineStation().getVersion()+1);  //0-waiting 1 - good 2 - error
        String msgText="Газомоторная станция ВОС" + "\n" + "Аварийное значение!" + "Параметры на момент аварии:" + "\n"
                + "\uD83D\uDD25 Температура двигателя: " + gasEngineDataService.getGasEngineStation().getEngineTemp() + " °C" + "\n"
                + "⚖️\uD83D\uDCA8 Температура радиатора: " + gasEngineDataService.getGasEngineStation().getRadiatorTemp() + " °C" + "\n"
                + "\uD83D\uDD25  Температура генератора: " + gasEngineDataService.getGasEngineStation().getGeneratorTemp()+ " °C" + "\n"
                + "Нормальные значения:"+ "\n"
                + "\uD83D\uDD25 Нормальная температура двигателя: " + gasEngineDataService.getGasEngineStation().getNormalEngineTemp() + " °C" + "\n"
                + "⚖️\uD83D\uDCA8 Нормальная температура радиатора: " + gasEngineDataService.getGasEngineStation().getNormalRadiatorTemp() + " °C" + "\n"
                + "\uD83D\uDD25  Нормальная температура генератора: " + gasEngineDataService.getGasEngineStation().getNormalGeneratorTemp()+ " °C" + "\n"+
                comment;
       if (!gasEngineErrorsArray[paramId]) {
           if (!disableAlertsGasStation) {
               for (int i = 0; i < clientsIdGasEngine.size(); i++) {
                   SendMessage message1 = new SendMessage();
                   message1.setChatId(clientsIdGasEngine.get(i));      // чат id
                   message1.setText(msgText);
                   Message message = execute(message1);
                   Message message2 = execute(Messages.avaryKeyboard(String.valueOf(clientsIdGasEngine.get(i))));
                   Thread.sleep(100);
               }
           }
       }
        gasEngineErrorsArray[paramId]=true;
    }
    private void trySilentReset(int boilerIndex) {
            if (errorsArray[boilerIndex]) {
                flagSilentReset[boilerIndex].set(true);
                errorsArray[boilerIndex] = false;
                temperatureErrorsArray[boilerIndex]=false;
                pressureErrorsArray[boilerIndex]=false;
                boilersDataService.getBoilers().get(boilerIndex).setIsOk(1,boilersDataService.getBoilers().get(boilerIndex).getVersion()+1);
                //boilerLoggingService.logBoilerStatus(boilersDataService.getBoilers().get(boilerIndex),"Ошибка котельной с индексом " + boilerIndex + " была успешно сброшена в тихом режиме.");
            }
    }
    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        Map<String, Integer> boilerIndices = new HashMap<>();
        for (int i = 0; i < boilersDataService.getBoilers().size(); i++) {
            boilerIndices.put(String.format("boiler%d", i), i);
        }
        String callbackData="-1";
        if (update.getCallbackQuery()!=null) {
             callbackData = update.getCallbackQuery().getData(); // данные обратного вызова
        }
        if (update.hasMessage()){
            if (update.getMessage().getText().contains("Добавь меня")) {
                long senderId = update.getMessage().getFrom().getId();
                System.out.println(senderId);
                System.out.println(senderId);
                System.out.println(senderId);
                clientsId.add(senderId);
            }else {
                try {
                    String chatId=update.getMessage().getChatId().toString();
                    execute(Messages.startKeyboard(chatId));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }

        }
        if (update.hasCallbackQuery()){
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String callData = callbackQuery.getData();
            long messageId = callbackQuery.getMessage().getMessageId();
            long chatId = callbackQuery.getMessage().getChatId();
            if (update.getCallbackQuery().getData().equals("avaryReset")){
                for (int i = 0; i < errorsArray.length; i++) {
                    errorsArray[i]=false;
                    pressureErrorsArray[i]=false;
                    temperatureErrorsArray[i]=false;
                    secondAttempt[i]=false;
                }
                for (int i = 0; i < gasEngineErrorsArray.length; i++) {
                    gasEngineErrorsArray[i] = false;
                }
                gasEngineDataService.getGasEngineStation().setIsOk(1,gasEngineDataService.getGasEngineStation().getVersion()+1);
                checkForAvary=true;
                SendMessage message = new SendMessage(update.getCallbackQuery().getMessage().getChatId().toString(), "Ошибки сброшены!");
                execute(message);
                Thread.sleep(LONG_SLEEP_TIME);
            }
            if (update.getCallbackQuery().getData().equals("enableCallService")){
                    for (int i = 0; i < clientsId.size(); i++) {
                        SendMessage message = new SendMessage(update.getCallbackQuery().getMessage().getChatId().toString(), "Звонки включены!");
                        try {
                            Message message2 = execute(message);
                           // avary2MessageID[i] = message2.getMessageId();
                            Thread.sleep(LONG_SLEEP_TIME);
                        } catch (TelegramApiException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        enableCallService = true;
                    }
            }
            if (update.getCallbackQuery().getData().equals("disableCallService")){
                    for (int i = 0; i < clientsId.size(); i++) {
                        SendMessage message = new SendMessage(update.getCallbackQuery().getMessage().getChatId().toString(), "Звонки выключены!");
                        try {
                            Message message2 = execute(message);
                            //avary2MessageID[i] = message2.getMessageId();
                            Thread.sleep(LONG_SLEEP_TIME);
                        } catch (TelegramApiException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        enableCallService = false;
                    }
            }
            if (update.getCallbackQuery().getData().equals("increaseTpod") || update.getCallbackQuery().getData().equals("decreaseTpod") ||
                    update.getCallbackQuery().getData().equals("increaseTAlarm") || update.getCallbackQuery().getData().equals("decreaseTAlarm")) {

                String[] correctForScadaForSend = {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"};
                String[] correctForAlarmForSend = {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"};
                try {
                    if (update.getCallbackQuery().getData().equals("increaseTpod")) {
                        SendMessage message = new SendMessage(update.getCallbackQuery().getMessage().getChatId().toString(),
                                "Запрос на +3 отправлен!\n"+"Текущая температура в контроллере:"+boilersDataService.getBoilers().get(boilerControlNum).getTPlan());
                        execute(message);
                        correctForScadaForSend[boilerControlNum] = String.valueOf(Integer.parseInt(correctForScadaForSend[boilerControlNum]) + 3);
                        boilersDataService.setCorrectionsTpod(correctForScadaForSend);
                        Thread.sleep(SUPER_SHORT_SLEEP_TIME);
                    }
                    if (update.getCallbackQuery().getData().equals("decreaseTpod")) {
                        SendMessage message = new SendMessage(update.getCallbackQuery().getMessage().getChatId().toString(),
                                "Запрос на -3 отправлен!\n"+"Текущая температура в контроллере:"+boilersDataService.getBoilers().get(boilerControlNum).getTPlan());
                        execute(message);
                        correctForScadaForSend[boilerControlNum] = String.valueOf(Integer.parseInt(correctForScadaForSend[boilerControlNum]) - 3);
                        boilersDataService.setCorrectionsTpod(correctForScadaForSend);
                        Thread.sleep(SUPER_SHORT_SLEEP_TIME);
                    }
                    if (update.getCallbackQuery().getData().equals("increaseTAlarm")) {
                        SendMessage message = new SendMessage(update.getCallbackQuery().getMessage().getChatId().toString(),
                                "Запрос на +3 (Alarm) отправлен!\n"+"Текущая температура аварии:"+boilersDataService.getBoilers().get(boilerControlNum).getTAlarm());
                        execute(message);
                        correctForAlarmForSend[boilerControlNum] = String.valueOf(Integer.parseInt(correctForAlarmForSend[boilerControlNum]) + 3);
                        boilersDataService.setCorrectionsTAlarm(correctForAlarmForSend);
                        Thread.sleep(SUPER_SHORT_SLEEP_TIME);
                    }
                    if (update.getCallbackQuery().getData().equals("decreaseTAlarm")) {
                        SendMessage message = new SendMessage(update.getCallbackQuery().getMessage().getChatId().toString(),
                                "Запрос на -3 (Alarm) отправлен!\n"+"Текущая температура аварии:"+boilersDataService.getBoilers().get(boilerControlNum).getTAlarm());
                        execute(message);
                        correctForAlarmForSend[boilerControlNum] = String.valueOf(Integer.parseInt(correctForAlarmForSend[boilerControlNum]) - 3);
                        boilersDataService.setCorrectionsTAlarm(correctForAlarmForSend);
                        Thread.sleep(SUPER_SHORT_SLEEP_TIME);
                    }
                } catch (TelegramApiException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                enableCallService = false;
            }
            if (callData.equals("bControl")) {
                long senderId = update.getCallbackQuery().getFrom().getId();
                if (clientsId.contains(senderId)){
                    InlineKeyboardMarkup markupInline = Messages.chooseBoilerKeyboardMarkup();
                    EditMessageText newMessage = new EditMessageText(
                            String.valueOf(chatId),
                            (int) messageId,
                            null,
                            "Выберите котельную",
                            null,
                            null,
                            markupInline,
                            null
                    );

                    try {
                        execute(newMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (update.getCallbackQuery().getData().contains("boiler")){
                try {
                    boilerControlNum=extractBoilerControlNum(update.getCallbackQuery().getData());
                    InlineKeyboardMarkup controlMarkup = Messages.controlKeyboardMarkup(disableAlertsBoilers[boilerControlNum]);
                    EditMessageText newMessage = new EditMessageText(
                            String.valueOf(chatId),
                            (int) messageId,
                            null,
                            boilerNames[boilerControlNum],
                            null,
                            null,
                            controlMarkup,
                            null
                    );
                    execute(newMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                enableCallService=false;
            }
            if (callData.equals("getTpodGraphic") || callData.equals("getPpodGraphic") || callData.equals("getTulicaGraphic")){
                Image image = null;
                EditMessageText newMessage = new EditMessageText(
                        String.valueOf(chatId),
                        (int) messageId,
                        null,
                        " ⏳ Загрузка...",
                        null,
                        null,
                        null,
                        null
                );
                execute(newMessage);
                switch (callData) {
                    case "getTpodGraphic":
                        image = graphics.getGraphics(graphics.getSortedTpodList(String.valueOf(boilerControlNum)));
                        break;
                    case "getPpodGraphic":
                        image = graphics.getGraphics(graphics.getSortedpPodList(String.valueOf(boilerControlNum)));
                        break;
                    case "getTulicaGraphic":
                        image = graphics.getGraphics(graphics.getSortedTulicaList(String.valueOf(boilerControlNum)));
                        break;
                }
                if (image != null) {
                    try {
                        File outputFile = new File("graph.jpeg");
                        ImageIO.write((RenderedImage) image, "jpeg", outputFile);
                        SendPhoto sendPhotoRequest = new SendPhoto();
                        sendPhotoRequest.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
                        sendPhotoRequest.setPhoto(new InputFile(outputFile));
                        String caption = graphics.getCaption(callData);
                        sendPhotoRequest.setCaption(boilerNames[boilerControlNum]+"\n"+caption);
                        try {
                            DeleteMessage deleteMessage = new DeleteMessage();
                            deleteMessage.setChatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()));
                            deleteMessage.setMessageId((int) messageId);
                            execute(deleteMessage);
                            execute(sendPhotoRequest);
                            outputFile.delete();
                            execute(Messages.startKeyboard(String.valueOf(update.getCallbackQuery().getMessage().getChatId())));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (callData.equals("graphicsButton")){
                try {
                    InlineKeyboardMarkup graphicsMarkup = Messages.graphicsKeyboard();
                    EditMessageText newMessage = new EditMessageText(
                            String.valueOf(chatId),
                            (int) messageId,
                            null,
                            boilerNames[boilerControlNum],
                            null,
                            null,
                            graphicsMarkup,
                            null
                    );
                    execute(newMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            if (callData.equals("goBackToControl")){
                try {
                    InlineKeyboardMarkup controlMarkup = Messages.controlKeyboardMarkup(disableAlertsBoilers[boilerControlNum]);
                    EditMessageText newMessage = new EditMessageText(
                            String.valueOf(chatId),
                            (int) messageId,
                            null,
                            boilerNames[boilerControlNum],
                            null,
                            null,
                            controlMarkup,
                            null
                    );
                    execute(newMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                enableCallService=false;
            }
            if (callData.equals("goBack")) {
                InlineKeyboardMarkup markupInline = Messages.chooseBoilerKeyboardMarkup();

                EditMessageText newMessage = new EditMessageText(
                        String.valueOf(chatId),
                        (int) messageId,
                        null,
                        "Выберите котельную",
                        null,
                        null,
                        markupInline,
                        null
                );
                try {
                    execute(newMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            if (callData.equals("disableAlerts=true")|callData.equals("disableAlerts=false")){
                if (callData.equals("disableAlerts=true")){
                    disableAlertsBoilers[boilerControlNum]=true;
                }else {
                    disableAlertsBoilers[boilerControlNum]=false;
                }
                InlineKeyboardMarkup controlMarkup = Messages.controlKeyboardMarkup(disableAlertsBoilers[boilerControlNum]);
                EditMessageText newMessage = new EditMessageText(
                        String.valueOf(chatId),
                        (int) messageId,
                        null,
                        boilerNames[boilerControlNum],
                        null,
                        null,
                        controlMarkup,
                        null
                );
                execute(newMessage);
            }
        }
    }

    int numErrBoiler = 0;

    public int extractBoilerControlNum(String data) {
        Pattern pattern = Pattern.compile("boiler(\\d+)");
        Matcher matcher = pattern.matcher(data);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            return 2;
        }
    }
    public void resetError(){
        for (int i = 0; i < errorsArray.length; i++) {
            errorsArray[i]=false;
            secondAttempt[i]=false;
            pressureErrorsArray[i]=false;
            temperatureErrorsArray[i]=false;
        }
        checkForAvary=true;
    }
    @Override
    public String getBotUsername() {
        return "@BoilerControlAN_bot";
    }
    @Override
    public String getBotToken() {
        return tokens.getKey1();
    }
    public void stop() {
        timer.cancel();
        monitorThread2.interrupt();
        timer=null;
        timerSilintReset.cancel();
        timerSilintReset=null;
        pressureMonitor.interrupt();
        monitorThread2=null;
        gasEngineTemperatureMonitor=null;
        System.gc();
    }
}


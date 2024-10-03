package com.boilersserver.BoilersControlServer.entities;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class Messages {
    public static InlineKeyboardMarkup chooseBoilerKeyboardMarkup() {
        // SendMessage message = new SendMessage();
        // message.setChatId(chatId);
        // message.setText("Выберите котельную:");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> buttonList0 = new ArrayList<>();
        List<InlineKeyboardButton> buttonList1 = new ArrayList<>();
        List<InlineKeyboardButton> buttonList2 = new ArrayList<>();
        List<InlineKeyboardButton> buttonList3 = new ArrayList<>();
        List<InlineKeyboardButton> buttonList4 = new ArrayList<>();
        List<InlineKeyboardButton> buttonList5 = new ArrayList<>();
        List<InlineKeyboardButton> buttonList6 = new ArrayList<>();
        List<InlineKeyboardButton> buttonList7 = new ArrayList<>();
        List<InlineKeyboardButton> buttonList8 = new ArrayList<>();
        List<InlineKeyboardButton> buttonList9 = new ArrayList<>();
        List<InlineKeyboardButton> buttonList10 = new ArrayList<>();
        List<InlineKeyboardButton> buttonList11 = new ArrayList<>();
        List<InlineKeyboardButton> buttonList12 = new ArrayList<>();
        List<InlineKeyboardButton> buttonList13 = new ArrayList<>();
        InlineKeyboardButton boiler0 = new InlineKeyboardButton();  boiler0.setCallbackData("boiler0");
        InlineKeyboardButton boiler1 = new InlineKeyboardButton();  boiler1.setCallbackData("boiler1");
        InlineKeyboardButton boiler2 = new InlineKeyboardButton();  boiler2.setCallbackData("boiler2");
        InlineKeyboardButton boiler3 = new InlineKeyboardButton();  boiler3.setCallbackData("boiler3");
        InlineKeyboardButton boiler4 = new InlineKeyboardButton();  boiler4.setCallbackData("boiler4");
        InlineKeyboardButton boiler5 = new InlineKeyboardButton();  boiler5.setCallbackData("boiler5");
        InlineKeyboardButton boiler6 = new InlineKeyboardButton();  boiler6.setCallbackData("boiler6");
        InlineKeyboardButton boiler7 = new InlineKeyboardButton();  boiler7.setCallbackData("boiler7");
        InlineKeyboardButton boiler8 = new InlineKeyboardButton();  boiler8.setCallbackData("boiler8");
        InlineKeyboardButton boiler9 = new InlineKeyboardButton();  boiler9.setCallbackData("boiler9");
        InlineKeyboardButton boiler10 = new InlineKeyboardButton();  boiler10.setCallbackData("boiler10");
        InlineKeyboardButton boiler11 = new InlineKeyboardButton();  boiler11.setCallbackData("boiler11");
        InlineKeyboardButton boiler12 = new InlineKeyboardButton();  boiler12.setCallbackData("boiler12");
        InlineKeyboardButton boiler13 = new InlineKeyboardButton();  boiler13.setCallbackData("boiler13");
        boiler0.setText("👨‍🦰 Котельная «Склады Мищенко»");
        boiler1.setText("👨‍🦳 Котельная «Выставка Ендальцева»");
        boiler2.setText("🏢 Котельная «ЧукотОптТорг»");
        boiler3.setText("🏭 Котельная «ЧСБК новая»");
        boiler4.setText("\uD83C\uDFEC Котельная «Офис СВТ»");
        boiler5.setText("\uD83C\uDFD8\uFE0F Котельная «Общежитие на Южной»");
        boiler6.setText("🏭 Котельная «Офис ЧСБК»");
        boiler7.setText("🛍️ Котельная «Рынок»");
        boiler8.setText("\uD83C\uDF31 Котельная «Макатровых»");
        boiler9.setText("🎠 Котельная ДС «Сказка»");
        boiler10.setText("❄️ Котельная «Полярный»");
        boiler11.setText("🏛️ Котельная «Департамент»");
        boiler12.setText("\uD83C\uDFE2 Котельная «Офис ЧСБК квартиры»");
        boiler13.setText("\uD83C\uDF30 Котельная Шишкина");
        buttonList0.add(boiler0); buttonList1.add(boiler1); buttonList2.add(boiler2); buttonList3.add(boiler3);
        buttonList4.add(boiler4); buttonList5.add(boiler5); buttonList6.add(boiler6);
        buttonList7.add(boiler7); buttonList8.add(boiler8); buttonList9.add(boiler9); buttonList10.add(boiler10); buttonList11.add(boiler11);
        buttonList12.add(boiler12); buttonList13.add(boiler13);
        rowList.add(buttonList0);
        rowList.add(buttonList1);
        rowList.add(buttonList2);
        rowList.add(buttonList3);
        rowList.add(buttonList4);
        rowList.add(buttonList5);
        rowList.add(buttonList6);
        rowList.add(buttonList7);
        rowList.add(buttonList8);
        rowList.add(buttonList9);
        rowList.add(buttonList10);
        rowList.add(buttonList11);
        rowList.add(buttonList12);
        rowList.add(buttonList13);
        inlineKeyboardMarkup.setKeyboard(rowList);
     //   message.setReplyMarkup(inlineKeyboardMarkup);
        return inlineKeyboardMarkup;
    }

    public static SendMessage startKeyboard(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите действие:");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> buttonList2 = new ArrayList<>();
        InlineKeyboardButton boilerControl = new InlineKeyboardButton();
        boilerControl.setText("\uD83D\uDD79Управление котельными");
        boilerControl.setCallbackData("bControl");
        buttonList2.add(boilerControl);
        rowList.add(buttonList2);
        inlineKeyboardMarkup.setKeyboard(rowList);
        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;
    }
    public static SendMessage avaryKeyboard(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите действие:");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> buttonList = new ArrayList<>();
        List<InlineKeyboardButton> buttonList2 = new ArrayList<>();
        List<InlineKeyboardButton> buttonList3 = new ArrayList<>();
        List<InlineKeyboardButton> buttonList4 = new ArrayList<>();
        InlineKeyboardButton sensorView = new InlineKeyboardButton();
        sensorView.setText("Сброс аварии");
        sensorView.setCallbackData("avaryReset");
        InlineKeyboardButton boilerControl = new InlineKeyboardButton();
        boilerControl.setText("\uD83D\uDD79Управление котельными");
        boilerControl.setCallbackData("bControl");
        buttonList.add(sensorView);
        buttonList2.add(boilerControl);
        rowList.add(buttonList);
        rowList.add(buttonList2);
        inlineKeyboardMarkup.setKeyboard(rowList);
        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;
    }
    public static InlineKeyboardMarkup controlKeyboardMarkup(boolean allowAlertForBoilers, String[] pumpsInfo,int boilerControlNum) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> buttonList2 = new ArrayList<>();
        List<InlineKeyboardButton> buttonList3 = new ArrayList<>();
        List<InlineKeyboardButton> buttonList4 = new ArrayList<>();
        List<InlineKeyboardButton> buttonList5 = new ArrayList<>();
        List<InlineKeyboardButton> buttonList6 = new ArrayList<>();
        List<InlineKeyboardButton> buttonList7 = new ArrayList<>();
        InlineKeyboardButton enableCallServiceButton = new InlineKeyboardButton();
        enableCallServiceButton.setText("Включить звонки");
        enableCallServiceButton.setCallbackData("enableCallService");
        InlineKeyboardButton disableCallServiceButton = new InlineKeyboardButton();
        disableCallServiceButton.setText("Выключить звонки");
        disableCallServiceButton.setCallbackData("disableCallService");
        InlineKeyboardButton increaseTpodButton = new InlineKeyboardButton();
        increaseTpodButton.setText("+3°C");
        increaseTpodButton.setCallbackData("increaseTpod");
        InlineKeyboardButton decreaseTpodButton = new InlineKeyboardButton();
        decreaseTpodButton.setText("-3°C");
        decreaseTpodButton.setCallbackData("decreaseTpod");
        InlineKeyboardButton showGraphics = new InlineKeyboardButton();
        showGraphics.setText("\uD83D\uDCC8Статистика");
        showGraphics.setCallbackData("graphicsButton");
        InlineKeyboardButton increaseTAlarmButton = new InlineKeyboardButton();
        increaseTAlarmButton.setText("+3°С (Alarm)");
        increaseTAlarmButton.setCallbackData("increaseTAlarm");
        InlineKeyboardButton decreaseTAlarmButton = new InlineKeyboardButton();
        decreaseTAlarmButton.setText("-3°С (Alarm)");
        decreaseTAlarmButton.setCallbackData("decreaseTAlarm");

        int pumpIndex1 = boilerControlNum * 2;
        int pumpIndex2 = pumpIndex1 + 1;

        String pump1Status = pumpsInfo[pumpIndex1];
        String pump2Status = pumpsInfo[pumpIndex2];

        String pump1Text = "Насос 1 " + (pump1Status.equals("1") ? "✅" : "❌");
        String pump2Text = "Насос 2 " + (pump2Status.equals("1") ? "✅" : "❌");

        InlineKeyboardButton pump1Button = new InlineKeyboardButton();
        pump1Button.setText(pump1Text);
        pump1Button.setCallbackData("togglePump1");

        InlineKeyboardButton pump2Button = new InlineKeyboardButton();
        pump2Button.setText(pump2Text);
        pump2Button.setCallbackData("togglePump2");
        List<InlineKeyboardButton> pumpButtons = new ArrayList<>();
        pumpButtons.add(pump1Button);
        pumpButtons.add(pump2Button);
        buttonList2.add(enableCallServiceButton);
        buttonList3.add(disableCallServiceButton);
        buttonList4.add(increaseTpodButton);
        buttonList4.add(decreaseTpodButton);
        buttonList4.add(increaseTAlarmButton);
        buttonList4.add(decreaseTAlarmButton);
        buttonList5.add(showGraphics);

        List<InlineKeyboardButton> backButtonRow = new ArrayList<>();
        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("↩️Назад");
        backButton.setCallbackData("goBack");
        backButtonRow.add(backButton);

        rowList.add(buttonList2);
        rowList.add(buttonList3);
        rowList.add(buttonList4);
        rowList.add(buttonList5);
        rowList.add(pumpButtons);

        if (allowAlertForBoilers){
            List<InlineKeyboardButton> disableAlertsButtonRow = new ArrayList<>();
            InlineKeyboardButton disableAlertsButton = new InlineKeyboardButton();
            disableAlertsButton.setText("\uD83D\uDE80Включить уведомления");
            disableAlertsButton.setCallbackData("disableAlerts=false");
            disableAlertsButtonRow.add(disableAlertsButton);
            rowList.add(disableAlertsButtonRow);
        } else {
            List<InlineKeyboardButton> disableAlertsButtonRow = new ArrayList<>();
            InlineKeyboardButton disableAlertsButton = new InlineKeyboardButton();
            disableAlertsButton.setText("\uD83D\uDCF5Отключить уведомления");
            disableAlertsButton.setCallbackData("disableAlerts=true");
            disableAlertsButtonRow.add(disableAlertsButton);
            rowList.add(disableAlertsButtonRow);
        }

        rowList.add(backButtonRow);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup graphicsKeyboard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> buttonList1 = new ArrayList<>();
        List<InlineKeyboardButton> buttonList2 = new ArrayList<>();
        List<InlineKeyboardButton> buttonList3 = new ArrayList<>();
        InlineKeyboardButton getTpodButton = new InlineKeyboardButton();
        getTpodButton.setText("\uD83C\uDF21\uFE0FТемпература подачи");
        getTpodButton.setCallbackData("getTpodGraphic");
        InlineKeyboardButton getPpodButton = new InlineKeyboardButton();
        getPpodButton.setText("\uD83C\uDF9B\uFE0FДавление на подаче");
        getPpodButton.setCallbackData("getPpodGraphic");
        InlineKeyboardButton getTulicaButton = new InlineKeyboardButton();
        getTulicaButton.setText("❄️Темепратура улицы");
        getTulicaButton.setCallbackData("getTulicaGraphic");
        buttonList1.add(getTpodButton);
        buttonList2.add(getPpodButton);
        buttonList3.add(getTulicaButton);
        List<InlineKeyboardButton> backButtonRow = new ArrayList<>();
        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("↩️Назад");
        backButton.setCallbackData("goBackToControl");
        backButtonRow.add(backButton);
        rowList.add(buttonList1);
        rowList.add(buttonList2);
        rowList.add(buttonList3);
        rowList.add(backButtonRow);
        inlineKeyboardMarkup.setKeyboard(rowList);
        // message.setReplyMarkup(inlineKeyboardMarkup);
        return inlineKeyboardMarkup;
    }
    public static SendMessage backKeyboard(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Обратно к котельным:");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> buttonList = new ArrayList<>();
        InlineKeyboardButton sensorView = new InlineKeyboardButton();
        sensorView.setText("↩️Назад");
        sensorView.setCallbackData("backToBoilers");
        buttonList.add(sensorView);
        rowList.add(buttonList);
        inlineKeyboardMarkup.setKeyboard(rowList);
        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;
    }

}

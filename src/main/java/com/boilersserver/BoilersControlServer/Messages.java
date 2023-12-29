package com.boilersserver.BoilersControlServer;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class Messages {

    public static SendMessage chooseBoilerKeyboard(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите котельную:");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> buttonList0 = new ArrayList<>(); List<InlineKeyboardButton> buttonList1 = new ArrayList<>(); List<InlineKeyboardButton> buttonList2 = new ArrayList<>();
        List<InlineKeyboardButton> buttonList3 = new ArrayList<>(); List<InlineKeyboardButton> buttonList7 = new ArrayList<>(); List<InlineKeyboardButton> buttonList9 = new ArrayList<>();
        List<InlineKeyboardButton> buttonList10 = new ArrayList<>(); List<InlineKeyboardButton> buttonList11 = new ArrayList<>();
        InlineKeyboardButton boiler0 = new InlineKeyboardButton();  boiler0.setCallbackData("boiler0");
        InlineKeyboardButton boiler1 = new InlineKeyboardButton();  boiler1.setCallbackData("boiler1");
        InlineKeyboardButton boiler2 = new InlineKeyboardButton();  boiler2.setCallbackData("boiler2");
        InlineKeyboardButton boiler3 = new InlineKeyboardButton();  boiler3.setCallbackData("boiler3");
        InlineKeyboardButton boiler7 = new InlineKeyboardButton();  boiler7.setCallbackData("boiler7");
        InlineKeyboardButton boiler9 = new InlineKeyboardButton();  boiler9.setCallbackData("boiler9");
        InlineKeyboardButton boiler10 = new InlineKeyboardButton();  boiler10.setCallbackData("boiler10");
        InlineKeyboardButton boiler11 = new InlineKeyboardButton();  boiler11.setCallbackData("boiler11");
        boiler0.setText("👨‍🦰 Склады Мищенко");
        boiler1.setText("👨‍🦳 Ендальцев");
        boiler2.setText("🏢 Офис ЧукотОптТорга");
        boiler3.setText("🏭 ЧСБК база");
        boiler7.setText("🛍️ Рынок");
        boiler9.setText("🎠 ДС Сказка");
        boiler10.setText("❄️ Полярный");
        boiler11.setText("🏛️ Департамент");
        buttonList0.add(boiler0); buttonList1.add(boiler1); buttonList2.add(boiler2); buttonList3.add(boiler3);
        buttonList7.add(boiler7); buttonList9.add(boiler9); buttonList10.add(boiler10); buttonList11.add(boiler11);
        rowList.add(buttonList0); rowList.add(buttonList1);rowList.add(buttonList2); rowList.add(buttonList3);
        rowList.add(buttonList7); rowList.add(buttonList9);rowList.add(buttonList10); rowList.add(buttonList11);
        inlineKeyboardMarkup.setKeyboard(rowList);
        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;
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
    public static SendMessage controlKeyboard(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите действие:");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> buttonList2 = new ArrayList<>();
        List<InlineKeyboardButton> buttonList3 = new ArrayList<>();
        List<InlineKeyboardButton> buttonList4 = new ArrayList<>();
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
        buttonList2.add(enableCallServiceButton);
        buttonList3.add(disableCallServiceButton);
        buttonList4.add(increaseTpodButton);
        buttonList4.add(decreaseTpodButton);
        rowList.add(buttonList2);
        rowList.add(buttonList3);
        rowList.add(buttonList4);
        inlineKeyboardMarkup.setKeyboard(rowList);
        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;
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

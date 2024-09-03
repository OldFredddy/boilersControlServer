package com.boilersserver.BoilersControlServer.utils;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ZvonokPostService {
    public static void call(String phoneNum) {
        try {
            // Создаем HTTP-клиент
            CloseableHttpClient httpClient = HttpClients.createDefault();

            // Создаем объект HTTP POST
            HttpPost httpPost = new HttpPost("https://zvonok.com/manager/cabapi_external/api/v1/phones/call/");

            // Создаем список параметров и добавляем их в объект HTTP POST
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("public_key", "6e811417e957124fbbab0a88dc5b78b9"));
            params.add(new BasicNameValuePair("phone", phoneNum));
            params.add(new BasicNameValuePair("campaign_id", "1938831005"));
            params.add(new BasicNameValuePair("text", "Kotelnye v opasnoti"));
            //https://zvonok.com/manager/cabapi_external/api/v1/phones/call/?campaign_id=864945889&phone=%2B79140808817&public_key=6e811417e957124fbbab0a88dc5b78b9&text=%D0%9F%D1%80%D0%B8%D0%BC%D0%B5%D1%80+%D1%82%D0%B5%D0%BA%D1%81%D1%82%D0%B0+%D1%80%D0%BE%D0%BB%D0%B8%D0%BA%D0%B0%2C+%D1%81%D0%BE%D0%B7%D0%B4%D0%B0%D0%BD%D0%BD%D0%BE%D0%B3%D0%BE+%D1%81+%D0%BF%D0%BE%D0%BC%D0%BE%D1%89%D1%8C%D1%8E%3Clang+xml%3Alang%3D%22en-US%22%3EAPI%3C%2Flang%3E&speaker=Maxim
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            // Выполняем запрос
            CloseableHttpResponse response = httpClient.execute(httpPost);

            // Получаем и выводим ответ от сервера
            String responseString = EntityUtils.toString(response.getEntity());
            System.out.println(responseString);

            // Закрываем HTTP-клиент и ответ
            response.close();
            httpClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void removeCallByPhoneNumber( String phoneNumber) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        int campaignId=1938831005;
        HttpPost httpPost = new HttpPost("https://zvonok.com/manager/cabapi_external/api/v1/phones/remove_call/");  // замените "pathRemoveCall" на URL эндпоинта
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("public_key", "6e811417e957124fbbab0a88dc5b78b9"));
        params.add(new BasicNameValuePair("campaign_id", Integer.toString(campaignId)));
        params.add(new BasicNameValuePair("phone", phoneNumber));
        httpPost.setEntity(new UrlEncodedFormEntity(params));
        CloseableHttpResponse response = httpClient.execute(httpPost);
        try {
            String responseString = EntityUtils.toString(response.getEntity());
            System.out.println(responseString);
        } finally {
            response.close();
        }
    }
    public static void checkStatus(String phoneNumber) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        int campaignId = 1938831005;
        String publicApiKey = "6e811417e957124fbbab0a88dc5b78b9";
        String url = String.format(
                "https://zvonok.com/manager/cabapi_external/api/v1/phones/calls_by_phone/?public_key=%s&campaign_id=%d&phone=%s",
                publicApiKey, campaignId, phoneNumber
        );
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        try {
            String responseString = EntityUtils.toString(response.getEntity());
            System.out.println(responseString);
        } finally {
            response.close();
        }
    }
}


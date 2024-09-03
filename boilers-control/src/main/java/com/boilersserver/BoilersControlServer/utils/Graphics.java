package com.boilersserver.BoilersControlServer.utils;
import com.boilersserver.BoilersControlServer.entities.Boiler;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.axes.LinearRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.graphics.Insets2D;
import de.erichseifert.gral.graphics.DrawingContext;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import com.google.gson.Gson;
import de.erichseifert.gral.plots.lines.SmoothLineRenderer2D;
import de.erichseifert.gral.plots.points.PointRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
@Component
public class Graphics {
    private final RedisTemplate<String, String> redisTemplate;
    @Autowired
    public Graphics(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Image getGraphics(ArrayList<String> yValues) {
        DataTable data = new DataTable(Double.class, Double.class);
        LocalDateTime startTime = LocalDateTime.now(ZoneId.of("UTC")).plusHours(9);

        for (int i = 0; i < yValues.size(); i++) {
            double y = Double.parseDouble(yValues.get(i));
            double x = startTime.plusSeconds(3L * i).toEpochSecond(ZoneOffset.UTC);
            data.add(x, y);
        }

        XYPlot plot = new XYPlot(data);
        LineRenderer lines = new SmoothLineRenderer2D() {
        };
        Random rand = new Random();
        Color lineColor = rand.nextBoolean() ? Color.RED : Color.GREEN;
        lines.setColor(lineColor);
        plot.setLineRenderers(data, lines);
        plot.setBackground(Color.BLACK);
        plot.setInsets(new Insets2D.Double(0.0, 0.0, 0.0, 0.0));
        plot.setPointRenderers(data, (PointRenderer) null);
        AxisRenderer xAxisRenderer = new LinearRenderer2D();
        AxisRenderer yAxisRenderer = new LinearRenderer2D();
        xAxisRenderer.setLabel(new de.erichseifert.gral.graphics.Label("X-axis"));
        yAxisRenderer.setLabel(new de.erichseifert.gral.graphics.Label("Y-axis"));
        xAxisRenderer.setTickLabelsVisible(true);
        yAxisRenderer.setTickLabelsVisible(true);
        plot.setAxisRenderer(XYPlot.AXIS_X, xAxisRenderer);
        plot.setAxisRenderer(XYPlot.AXIS_Y, yAxisRenderer);
        BufferedImage image = new BufferedImage(3500, 1000, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        plot.setBounds(0, 0, image.getWidth(), image.getHeight());
        DrawingContext context = new DrawingContext(g2);
        plot.draw(context);

        return image;
    }
    public  ArrayList<String> getSortedTpodList( String boilerId) {
        String historyKey = "history:boiler:" + boilerId;
        Gson gson = new Gson();
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
        Set<String> boilerJsons = zSetOps.range(historyKey, 0, -1);
        if (boilerJsons == null) {
            return new ArrayList<>();
        }
        List<Boiler> boilers = boilerJsons.stream()
                .map(json -> gson.fromJson(json, Boiler.class))
                .sorted((b1, b2) -> Long.compare(b1.getLastUpdated(), b2.getLastUpdated()))
                .collect(Collectors.toList());
        ArrayList<String> tPodList = new ArrayList<>();
        for (Boiler boiler : boilers) {
            tPodList.add(boiler.getTPod());
        }
        return tPodList;
    }
    public  ArrayList<String> getSortedpPodList( String boilerId) {
        String historyKey = "history:boiler:" + boilerId;
        Gson gson = new Gson();
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
        Set<String> boilerJsons = zSetOps.range(historyKey, 0, -1);
        if (boilerJsons == null) {
            return new ArrayList<>();
        }
        List<Boiler> boilers = boilerJsons.stream()
                .map(json -> gson.fromJson(json, Boiler.class))
                .sorted((b1, b2) -> Long.compare(b1.getLastUpdated(), b2.getLastUpdated()))
                .collect(Collectors.toList());
        ArrayList<String> pPodList = new ArrayList<>();
        for (Boiler boiler : boilers) {
            pPodList.add(boiler.getPPod());
        }
        return pPodList;
    }
    public  ArrayList<String> getSortedTulicaList( String boilerId) {
        String historyKey = "history:boiler:" + boilerId;
        Gson gson = new Gson();
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
        Set<String> boilerJsons = zSetOps.range(historyKey, 0, -1);
        if (boilerJsons == null) {
            return new ArrayList<>();
        }
        List<Boiler> boilers = boilerJsons.stream()
                .map(json -> gson.fromJson(json, Boiler.class))
                .sorted((b1, b2) -> Long.compare(b1.getLastUpdated(), b2.getLastUpdated()))
                .collect(Collectors.toList());

        ArrayList<String> tUlicaList = new ArrayList<>();
        for (Boiler boiler : boilers) {
            tUlicaList.add(boiler.getTUlica());
        }
        return tUlicaList;
    }
    public String getCaption(String callBackData){
        String res="";
        if (callBackData.equals("getTpodGraphic")){
            res="[Температура подачи]";
        }
        if (callBackData.equals("getPpodGraphic")){
            res="[Давление подачи]";
        }
        if (callBackData.equals("getTulicaGraphic")){
            res="[Температура улицы]";
        }
        return res;
    }
}

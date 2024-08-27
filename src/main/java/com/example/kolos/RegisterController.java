package com.example.kolos;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.css.RGBColor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;
import java.sql.*;

@RestController
public class RegisterController {
    ArrayList<JSONObject> tokenList = new ArrayList<JSONObject>();
    ArrayList<JSONObject> activeTokens;


    @PostMapping("/register")
    private String postToken(){
        String token = UUID.randomUUID().toString();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String time = LocalDateTime.now().format(df);
        String jsonFile = String.format("{\"token\":\"%s\", \"time\":\"%s\"}" ,token ,time);
        tokenList.add(new JSONObject(jsonFile));
        return jsonFile;
    }

    @GetMapping("/tokens")
    private ArrayList<JSONObject> getTokens(){
        ArrayList<JSONObject> lastTokens = new ArrayList<JSONObject>();
        String tempJson;
        String tempToken;
        LocalDateTime tempTime;
        String tempValidation;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for(JSONObject i: tokenList){
            tempToken = i.getString("token");
            tempTime = LocalDateTime.parse(i.getString("time"), df);
            if(LocalDateTime.now().getMinute()-tempTime.getMinute()>=5){
                tempValidation = "nieaktywny";
            }
            else
                tempValidation = "aktywny";
            tempJson = String.format("{\"token\":\"%s\", \"time\":\"%s\", \"validation\":\"%s\"}",
                    tempToken ,i.getString("time"), tempValidation);
            System.out.println(tempJson);
            lastTokens.add(new JSONObject(tempJson));
            activeTokens.add(new JSONObject(tempJson));
        }


        return lastTokens;
    }

    @GetMapping("/image")
    private String getImage(){
        try {
            byte[] fileContent = FileUtils.readFileToByteArray(new File("img.png"));
            String encodedString = Base64.getEncoder().encodeToString(fileContent);
            String imgCode = String.format("data:image/png;base64, %s", encodedString);
            System.out.println(imgCode);
            String html = String.format("<html><img src='%s' th:width='512' th:height='512'></html> ", imgCode);
            return html;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/pixel")
    private int setPixel(@RequestBody PixelChange pl){

        if((pl.getX()>512 || pl.getX()<0) || ((pl.getY()>512 || pl.getY()<0)))
            return 400;
        int code = 403;
        for(JSONObject jo : activeTokens){
            if(Objects.equals(jo.getString("token"), pl.getId())){
                if(Objects.equals(jo.getString("validation"), "aktywny"))
                    code = 200;
                break;
            }
        }
        if(code == 200) {
            try {
                BufferedImage image = ImageIO.read(new File("img.png"));
                int rgb = Integer.parseInt(pl.getColor());
                image.setRGB(pl.getX(), pl.getY(),rgb);
                DBHandler.createDb();
                DBHandler.insertValues(pl.getId(), pl.getX(), pl.getY(), pl.getColor());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }



        return code;
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }



}

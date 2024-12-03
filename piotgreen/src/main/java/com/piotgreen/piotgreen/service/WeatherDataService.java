package com.piotgreen.piotgreen.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piotgreen.piotgreen.dto.WeatherData;
import com.piotgreen.piotgreen.dto.WeatherGeneralData;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherDataService {
    private final String apiUrl = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";

    @Value("${weather.key}")
    private String serviceKey;


    public List<WeatherData> getWeatherData(String baseDate, String baseTime,String nx,String ny) {
        String pageNo="1";
        String numOfRows="1000";
        String dataType="JSON";
//        String baseDate = "20241129";
//        String baseTime = "0500";
//        String nx = "55";
//        String ny = "127";

        try {
            // 매개변수 값만 URL 인코딩
//            URLEncoder.encode("id를 잘못 입력하셨습니다.", "utf-8");

            // URL 생성 및 매개변수 인코딩
            StringBuilder urlBuilder = new StringBuilder(apiUrl);
            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey);
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(pageNo, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode(numOfRows, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode(dataType, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(baseTime, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8"));


            // HttpURLConnection 설정
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            // 응답 코드 확인
            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // 응답 데이터 읽기
            BufferedReader rd;
            if (responseCode >= 200 && responseCode <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
            }

            StringBuilder result = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            rd.close();
            conn.disconnect();

            // 결과 출력
            System.out.println("Response: " + result);

//            url = URLEncoder.encode(url, StandardCharsets.UTF_8);
//
//            // 요청 보내기
//            RestTemplate restTemplate = new RestTemplate();
//            log.info(serviceKey);
//            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
//            String jsonResponse = responseEntity.getBody();
//
//            log.info(jsonResponse);
//            // ObjectMapper로 JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(String.valueOf(result));

            // 필요한 데이터 노드로 이동
            JsonNode itemsNode = rootNode.path("response").path("body").path("items").path("item");

            // WeatherData 리스트 생성
            List<WeatherData> weatherDataList = new ArrayList<>();
            for (JsonNode itemNode : itemsNode) {
                WeatherData weatherData = new WeatherData(
                        itemNode.path("baseDate").asText(),
                        itemNode.path("baseTime").asText(),
                        itemNode.path("category").asText(),
                        itemNode.path("fcstDate").asText(),
                        itemNode.path("fcstTime").asText(),
                        itemNode.path("fcstValue").asText(),
                        itemNode.path("nx").asText(),
                        itemNode.path("ny").asText()
                );
                weatherDataList.add(weatherData);
            }

            return weatherDataList;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // 오류 발생 시 빈 리스트 반환
        }
    }

    public List<WeatherGeneralData> getWeatherGeneralData(String baseDate, String baseTime,String nx,String ny) throws UnsupportedEncodingException {
        List<WeatherData> weatherDataList = getWeatherData( baseDate,  baseTime, nx, ny);
        Map<String, WeatherGeneralData> generalDataMap = new HashMap<>();
        // 특정 forecast 시간별로 TMP, WSD, SKY, POP 카테고리를 그룹화
        for (WeatherData weatherData : weatherDataList) {
            String newName = weatherData.getFcstDate() + weatherData.getFcstTime();
            if (!generalDataMap.containsKey(newName)) {
                WeatherGeneralData initWeatherGeneralData = new WeatherGeneralData();
                initWeatherGeneralData.setFcstDate(weatherData.getFcstDate());
                initWeatherGeneralData.setFcstTime(weatherData.getFcstTime());
                generalDataMap.put(newName, initWeatherGeneralData);
            }
            if (generalDataMap.containsKey(newName)) {
                WeatherGeneralData weatherGeneralData = generalDataMap.get(newName);
                if (weatherData.getCategory().compareTo("TMP") == 0){
                    weatherGeneralData.setTemperature(weatherData.getFcstValue());
                }
                else if(weatherData.getCategory().compareTo("WSD") == 0){
                    weatherGeneralData.setWindSpeed(weatherData.getFcstValue());
                }
                else if(weatherData.getCategory().compareTo("SKY") == 0){
                    weatherGeneralData.setSkyCondition(weatherData.getFcstValue());
                }
                else if(weatherData.getCategory().compareTo("POP") == 0){
                    weatherGeneralData.setPropabilityOfPrecipitation(weatherData.getFcstValue());
                }
                else if(weatherData.getCategory().compareTo("PCP") == 0){
                    if (weatherData.getFcstValue().compareTo("강수없음") == 0){
                        weatherGeneralData.setPrecipitation("0");
                    }
                    else{
                        weatherGeneralData.setPrecipitation(weatherData.getFcstValue());
                    }
                }
            }
        }
        List<WeatherGeneralData> generalDataList = new ArrayList<>();
        for (WeatherGeneralData weatherGeneralData : generalDataMap.values()) {
            generalDataList.add(weatherGeneralData);
        }
        // 두 단계 정렬
        generalDataList.sort(Comparator
                .comparing(WeatherGeneralData::getFcstDate) // 1차 정렬: fcstDate
                .thenComparing(WeatherGeneralData::getFcstTime)); // 2차 정렬: fcstTime


        return generalDataList;
    }



}

package com.example.a1117p.osam.user;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHttpURLConnection {
    static String cookie = "", email = "", name = "", lineEnd = "\r\n", twoHyphens = "--";


    public static String request(String _url, HashMap<String, String> _params, String method) {
        return request(_url, _params, false, method);
    }

    public static String request(String _url, HashMap<String, String> _params, boolean iscookie, String method) {

        // HttpURLConnection 참조 변수.
        HttpURLConnection urlConn = null;
        // URL 뒤에 붙여서 보낼 파라미터.
        StringBuffer sbParams = new StringBuffer();

        /**
         * 1. StringBuffer에 파라미터 연결
         * */
        // 보낼 데이터가 없으면 파라미터를 비운다.
        if (_params == null)
            sbParams.append("");
            // 보낼 데이터가 있으면 파라미터를 채운다.
        else {
            // 파라미터가 2개 이상이면 파라미터 연결에 &가 필요하므로 스위칭할 변수 생성.
            boolean isAnd = false;
            // 파라미터 키와 값.
            String key;
            String value;

            for (Map.Entry<String, String> parameter : _params.entrySet()) {
                key = parameter.getKey();
                value = parameter.getValue();

                // 파라미터가 두개 이상일때, 파라미터 사이에 &를 붙인다.
                if (isAnd)
                    sbParams.append("&");

                sbParams.append(key).append("=").append(value);

                // 파라미터가 2개 이상이면 isAnd를 true로 바꾸고 다음 루프부터 &를 붙인다.
                if (!isAnd)
                    if (_params.size() >= 2)
                        isAnd = true;
            }
        }

        /**
         * 2. HttpURLConnection을 통해 web의 데이터를 가져온다.
         * */
        try {
            URL url = new URL(_url);
            urlConn = (HttpURLConnection) url.openConnection();

            if (iscookie && !cookie.equals("")) {
                urlConn.setRequestProperty("Cookie", cookie); //쿠키값을 서버에게 전달하는 코드


            }

            // [2-1]. urlConn 설정.
            urlConn.setRequestMethod(method); // URL 요청에 대한 메소드 설정 : POST.
            urlConn.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
            urlConn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");
            if (method.equals("POST")) {
                // [2-2]. parameter 전달 및 데이터 읽어오기.
                String strParams = sbParams.toString(); //sbParams에 정리한 파라미터들을 스트링으로 저장. 예)id=id1&pw=123;
                OutputStream os = urlConn.getOutputStream();
                os.write(strParams.getBytes(Charset.forName("UTF-8"))); // 출력 스트림에 출력.
                os.flush(); // 출력 스트림을 플러시(비운다)하고 버퍼링 된 모든 출력 바이트를 강제 실행.
                os.close(); // 출력 스트림을 닫고 모든 시스템 자원을 해제.
            }
            // [2-3]. 연결 요청 확인.
            // 실패 시 null을 리턴하고 메서드를 종료.
            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                return String.valueOf(urlConn.getResponseCode());


            Map<String, List<String>> header = urlConn.getHeaderFields();
            if (header.containsKey("Set-Cookie")) {
                List<String> cookies = header.get("Set-Cookie");
                for (int i = 0; i < cookies.size(); i++)
                    if (cookies.get(i).contains("public_user")) {
                        String tmp = URLDecoder.decode(cookies.get(i).split("=")[1]);
                        tmp = tmp.replace("; Path", "").replace("j:", "");
                        JSONObject object = (JSONObject) new JSONParser().parse(tmp);
                        name = (String) object.get("userName");
                        email = (String) object.get("userEmail");
                    } else if (cookies.get(i).contains("user")) {
                        cookie = cookies.get(i);
                    }
            }

            // [2-4]. 읽어온 결과물 리턴.
            // 요청한 URL의 출력물을 BufferedReader로 받는다.
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), Charset.forName("UTF-8")));

            // 출력물의 라인과 그 합에 대한 변수.
            String line;
            String page = "";

            // 라인을 받아와 합친다.
            while ((line = reader.readLine()) != null) {
                page += line;
            }

            return page;

        } catch (MalformedURLException e) { // for URL.
            e.printStackTrace();
        } catch (IOException e) { // for openConnection().
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if (urlConn != null)
                urlConn.disconnect();
        }

        return "error";

    }

    public static String requestWith_File(String _url, HashMap<String, String> _params, boolean iscookie, String method, File file) {

        // HttpURLConnection 참조 변수.
        HttpURLConnection urlConn = null;
        // URL 뒤에 붙여서 보낼 파라미터.
        StringBuffer sbParams = new StringBuffer();
        String boundary = "androidupload";
        /**
         * 1. StringBuffer에 파라미터 연결
         * */
        // 보낼 데이터가 없으면 파라미터를 비운다.
        String delimiter = twoHyphens + boundary + lineEnd; // --androidupload\r\n


        if (_params == null)
            sbParams.append("");
            // 보낼 데이터가 있으면 파라미터를 채운다.
        else {
            // 파라미터가 2개 이상이면 파라미터 연결에 &가 필요하므로 스위칭할 변수 생성.

            // 파라미터 키와 값.
            String key;
            String value;

            for (Map.Entry<String, String> parameter : _params.entrySet()) {
                key = parameter.getKey();
                value = parameter.getValue();


                sbParams.append(delimiter);
                sbParams.append("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd + lineEnd + value + lineEnd);


            }
            if (file != null) {
                sbParams.append(delimiter);
                sbParams.append("Content-Disposition: form-data; name=\"profile\";filename=\"" + file.getName() + "\"" + lineEnd);
            }
        }


        /**
         * 2. HttpURLConnection을 통해 web의 데이터를 가져온다.
         * */
        try {
            URL url = new URL(_url);
            urlConn = (HttpURLConnection) url.openConnection();

            if (iscookie && !cookie.equals("")) {
                urlConn.setRequestProperty("Cookie", cookie); //쿠키값을 서버에게 전달하는 코드


            }

            // [2-1]. urlConn 설정.
            urlConn.setRequestMethod(method); // URL 요청에 대한 메소드 설정 : POST.
            urlConn.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
            urlConn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");

            DataOutputStream ds;
            if (method.equals("POST")) {
                urlConn.setRequestProperty("ENCTYPE", "multipart/form-data");
                urlConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                // [2-2]. parameter 전달 및 데이터 읽어오기.
                String strParams = sbParams.toString(); //sbParams에 정리한 파라미터들을 스트링으로 저장. 예)id=id1&pw=123;
                ds = new DataOutputStream(urlConn.getOutputStream());
                ds.write(strParams.getBytes(Charset.forName("UTF-8"))); // 출력 스트림에 출력.
                byte[] buffer;
                int maxBufferSize = 1024;
                if (file != null) {
                    ds.writeBytes(lineEnd);
                    FileInputStream fStream = new FileInputStream(file);
                    buffer = new byte[maxBufferSize];
                    int length = -1;
                    while ((length = fStream.read(buffer)) != -1) {
                        ds.write(buffer, 0, length);
                    }
                    ds.writeBytes(lineEnd);
                    ds.writeBytes(lineEnd);
                    ds.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd); // requestbody end
                    fStream.close();
                } else {
                    ds.writeBytes(lineEnd);
                    ds.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd); // requestbody end
                }


            } else {
                ds = new DataOutputStream(urlConn.getOutputStream());

                ds.writeBytes(lineEnd);
                ds.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd); // requestbody end
            }
            ds.flush(); // 출력 스트림을 플러시(비운다)하고 버퍼링 된 모든 출력 바이트를 강제 실행.
            ds.close(); // 출력 스트림을 닫고 모든 시스템 자원을 해제.

            // [2-3]. 연결 요청 확인.
            // 실패 시 null을 리턴하고 메서드를 종료.
            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                return String.valueOf(urlConn.getResponseCode());


            Map<String, List<String>> header = urlConn.getHeaderFields();
            if (header.containsKey("Set-Cookie")) {
                List<String> cookies = header.get("Set-Cookie");
                for (int i = 0; i < cookies.size(); i++)
                    if (cookies.get(i).contains("public_user")) {
                        String tmp = URLDecoder.decode(cookies.get(i).split("=")[1]);
                        tmp = tmp.replace("; Path", "").replace("j:", "");
                        JSONObject object = (JSONObject) new JSONParser().parse(tmp);
                        name = (String) object.get("userName");
                        email = (String) object.get("userEmail");
                    } else if (cookies.get(i).contains("user")) {
                        cookie = cookies.get(i);
                    }
            }

            // [2-4]. 읽어온 결과물 리턴.
            // 요청한 URL의 출력물을 BufferedReader로 받는다.
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), Charset.forName("UTF-8")));

            // 출력물의 라인과 그 합에 대한 변수.
            String line;
            String page = "";

            // 라인을 받아와 합친다.
            while ((line = reader.readLine()) != null) {
                page += line;
            }

            return page;

        } catch (MalformedURLException e) { // for URL.
            e.printStackTrace();
        } catch (IOException e) { // for openConnection().
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if (urlConn != null)
                urlConn.disconnect();
        }

        return "error";

    }

}
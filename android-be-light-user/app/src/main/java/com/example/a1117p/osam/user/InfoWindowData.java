package com.example.a1117p.osam.user;


import org.json.simple.JSONObject;

class InfoWindowData {
    String hostAddress, hostName, hostTel, hostPostalCode, hostIntro, term,hostImage;
    long hostIdx;
    double dist, score;

    InfoWindowData(JSONObject object) {
        this.hostAddress = (String) object.get("hostAddress");
        this.hostName = (String) object.get("hostName");
        this.hostTel = (String) object.get("hostTel");
        this.hostPostalCode = (String) object.get("hostPostalCode");
        this.hostIdx = (Long) object.get("hostIdx");
        this.term = "open  " + object.get("hostOpenTime") + " ~ close  " + object.get("hostCloseTime");
        this.hostIntro = (String) object.get("hostIntro");
        try {
            this.dist = (Double) object.get("distance");
        } catch (Exception e) {
            this.dist = (Long) object.get("distance");
        }
        this.score = Double.parseDouble((String) object.get("reviewScoreAvg"));

        hostImage = (String) object.get("hostImage");
        if(hostImage!=null&&!hostImage.equals(""))
            hostImage="https://be-light.store"+hostImage;
    }
}
package com.example.mayank.smarthome.model;


public class Recommendation {

    public String recommendationRoomName;
    public String recommendationText;
    public int recommendationRoomPhotoId;

    public Recommendation(String recommendationRoom, String recommendationText, int recommendationPhotoId) {
        recommendationRoomName = recommendationRoom;
        this.recommendationText = recommendationText;
        this.recommendationRoomPhotoId = recommendationPhotoId;
    }


}

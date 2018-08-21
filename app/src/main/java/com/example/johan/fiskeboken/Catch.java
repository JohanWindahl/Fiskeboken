package com.example.johan.fiskeboken;

import org.joda.time.DateTime;
import java.io.Serializable;

//This class represents one of the catches which is shown as one card in the main log
public class Catch implements Serializable {
    private String fishType;
    private String description;
    private DateTime date;
    private Double weight;
    private Double height;
    private Double gpsLat;
    private Double gpsLong;
    private Integer removeImage;

    //Constructors
    public Catch() {}
    public Catch(String fishType, String description, DateTime date, Double weight, Double height, Double gpsLat, Double gpsLong) {
        this.fishType = fishType;
        this.description = description;
        this.date = date;
        this.weight = weight;
        this.height = height;
        this.gpsLat = gpsLat;
        this.gpsLong = gpsLong;
        this.removeImage = R.drawable.ic_delete;
    }

    //Getters and Setters
    public Double getGpsLat() {
        return gpsLat;
    }
    public void setGpsLat(Double gpsLat) {this.gpsLat = gpsLat;}

    public Double getGpsLong() {return gpsLong;}
    public void setGpsLong(Double gpsLong) {this.gpsLong = gpsLong;}

    public Integer getRemoveImage() {return removeImage;}
    public void setRemoveImage(Integer removeImage) {
        this.removeImage = removeImage;
    }

    public String getFishType() {return fishType;}
    public void setFishType(String fishType) {
        this.fishType = fishType;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public DateTime getDate() {
        return date;
    }
    public void setDate(DateTime date) {
        this.date = date;
    }

    public Double getWeight() {
        if (!AppSettings.isKg()) {
            return weight*AppSettings.getLbsMultipel();
        }
        return weight;
    }
    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getHeight() {
        if (!AppSettings.isM()) {
            return height*AppSettings.getInchesMultipel();
        }
        return height;
    }
    public void setHeight(Double height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "Catch{" +
                "fishType='" + fishType + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", weight=" + weight +
                ", height=" + height +
                ", gpsLat=" + gpsLat +
                ", gpsLong=" + gpsLong +
                '}';
    }

}


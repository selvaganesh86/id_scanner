package com.example.idscanner;

public class scandetails {

    private String location;
    private String result;
    private String timestamp;

    public scandetails(String location, String result, String timestamp) {
        this.location = location;
        this.result = result;
        this.timestamp = timestamp;
    }

    public scandetails() {
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

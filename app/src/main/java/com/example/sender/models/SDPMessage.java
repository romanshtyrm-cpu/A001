package com.example.sender.models;

public class SDPMessage {
    public String type;
    public String sdp;

    public SDPMessage() {}

    public SDPMessage(String type, String sdp) {
        this.type = type;
        this.sdp = sdp;
    }
}

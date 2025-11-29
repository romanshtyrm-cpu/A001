package com.example.sender.models;

public class IceCandidateMessage {

    public String sdpMid;
    public int sdpMLineIndex;
    public String candidate;

    public IceCandidateMessage() {}

    public IceCandidateMessage(String mid, int idx, String cand) {
        this.sdpMid = mid;
        this.sdpMLineIndex = idx;
        this.candidate = cand;
    }
}

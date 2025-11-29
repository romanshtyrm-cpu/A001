package com.example.sender.firebase;

import com.example.sender.models.IceCandidateMessage;
import com.example.sender.models.SDPMessage;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseSignalingSender {

    private final DatabaseReference baseRef;

    public FirebaseSignalingSender(String sessionId) {
        baseRef = FirebaseDatabase.getInstance().getReference("signaling").child(sessionId).child("sender");
    }

    public void sendOffer(SDPMessage offer) {
        baseRef.child("offer").setValue(offer);
    }

    public void sendIce(IceCandidateMessage ice) {
        baseRef.child("ice").push().setValue(ice);
    }
}

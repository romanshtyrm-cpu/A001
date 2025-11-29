package com.example.sender.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.sender.models.IceCandidateMessage;
import com.example.sender.models.SDPMessage;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseSignalingListener {

    private static final String TAG = "FirebaseSignalingListener";
    private final DatabaseReference answerRef;
    private final DatabaseReference remoteIceRef;

    public interface Callback {
        void onSDP(SDPMessage sdp);
        void onIce(IceCandidateMessage ice);
    }

    public FirebaseSignalingListener(String sessionId, Callback callback) {
        DatabaseReference base = FirebaseDatabase.getInstance().getReference("signaling").child(sessionId).child("receiver");
        answerRef = base.child("answer");
        remoteIceRef = base.child("ice");

        answerRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;
                SDPMessage sdp = snapshot.getValue(SDPMessage.class);
                if (sdp != null) {
                    callback.onSDP(sdp);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });

        remoteIceRef.addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(@NonNull DataSnapshot snapshot, String prevChildKey) {
                IceCandidateMessage ice = snapshot.getValue(IceCandidateMessage.class);
                if (ice != null) callback.onIce(ice);
            }
            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, String prevChildKey) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, String prevChildKey) {}
            @Override public void onCancelled(@NonNull DatabaseError error) { Log.e(TAG, "ice listener cancelled: " + error); }
        });
    }
}

package com.example.sender.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseCommandListener {

    public interface CommandCallback {
        void onStart();
        void onStop();
    }

    public FirebaseCommandListener(CommandCallback callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("commands").child("app1");

        ref.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                if (!snap.exists()) return;
                String cmd = snap.getValue(String.class);
                if (cmd == null) return;

                if (cmd.equalsIgnoreCase("START")) callback.onStart();
                else if (cmd.equalsIgnoreCase("STOP")) callback.onStop();
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }
}

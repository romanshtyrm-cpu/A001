# App1_Sender (Android)

This is a ready Android project (minimal) for **App1 - Sender** (WebRTC audio sender).
It uses Firebase Realtime Database for signaling (offer/answer/ICE) and listens to commands
at `/commands/app1` (values: "START" / "STOP").

## Important - you MUST do these before building:
1. Create a Firebase Android project and add your app package `com.example.sender`.
2. Download `google-services.json` from Firebase and place it into `app/`.
3. Enable **Realtime Database** in Firebase Console. For testing, you may temporarily set rules to public:
   ```
   {
     "rules": {
       ".read": true,
       ".write": true
     }
   }
   ```
4. Build and install on an Android device (minSdk 24+).

## How it works
- The foreground service `AudioSendService` starts on app launch and listens to `/commands/app1`.
- Write `"START"` to that node to begin creating an offer and streaming microphone audio to the receiver.
- The offer is written to `/signaling/session1/sender/offer`. Local ICE candidates are pushed to `/signaling/session1/sender/ice`.
- The receiver (App2) must respond by writing an answer to `/signaling/session1/receiver/answer` and its ICE candidates to `/signaling/session1/receiver/ice`.

## Notes / Next steps
- Add STUN/TURN servers into `WebRTCManager#createPeerConnection` for reliability (iceServers).
- Implement App2 receiver (I can generate it on request).
- Secure your RTDB rules before production.


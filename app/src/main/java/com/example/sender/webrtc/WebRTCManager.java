package com.example.sender.webrtc;

import android.content.Context;
import android.util.Log;

import com.example.sender.firebase.FirebaseSignalingListener;
import com.example.sender.firebase.FirebaseSignalingSender;
import com.example.sender.models.IceCandidateMessage;
import com.example.sender.models.SDPMessage;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.PeerConnection.RTCConfiguration;
import org.webrtc.RtpSender;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

import java.util.ArrayList;
import java.util.List;

public class WebRTCManager {

    private static final String TAG = "WebRTCManager";
    private final Context context;
    private final PeerConnectionFactory factory;
    private PeerConnection peerConnection;
    private AudioSource audioSource;
    private AudioTrack localAudioTrack;
    private FirebaseSignalingSender signalingSender;
    private FirebaseSignalingListener signalingListener;

    // session id (both apps must use same session id)
    private final String sessionId = "session1";

    public WebRTCManager(Context context) {
        this.context = context;
        this.factory = PeerConnectionFactoryProvider.get(context);

        signalingSender = new FirebaseSignalingSender(sessionId);
        signalingListener = new FirebaseSignalingListener(sessionId, new FirebaseSignalingListener.Callback() {
            @Override
            public void onSDP(SDPMessage sdp) {
                if ("answer".equals(sdp.type)) {
                    Log.d(TAG, "Received remote answer");
                    SessionDescription remoteDesc = new SessionDescription(SessionDescription.Type.ANSWER, sdp.sdp);
                    if (peerConnection != null) peerConnection.setRemoteDescription(new SimpleSdpObserver(), remoteDesc);
                }
            }

            @Override
            public void onIce(IceCandidateMessage ice) {
                Log.d(TAG, "Received remote ICE");
                if (peerConnection != null) {
                    IceCandidate candidate = new IceCandidate(ice.sdpMid, ice.sdpMLineIndex, ice.candidate);
                    peerConnection.addIceCandidate(candidate);
                }
            }
        });
    }

    public void startCall() {
        Log.d(TAG, "startCall");
        createPeerConnection();

        // Create offer
        MediaConstraints sdpConstraints = new MediaConstraints();
        sdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "false"));
        sdpConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));

        peerConnection.createOffer(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                Log.d(TAG, "onCreateSuccess - offer created");
                peerConnection.setLocalDescription(new SimpleSdpObserver(), sessionDescription);

                // send offer SDP to firebase
                signalingSender.sendOffer(new SDPMessage("offer", sessionDescription.description));
            }

            @Override public void onSetSuccess() {}
            @Override public void onCreateFailure(String s) { Log.e(TAG, "createOffer fail: " + s); }
            @Override public void onSetFailure(String s) {}
        }, sdpConstraints);
    }

    public void stopCall() {
        Log.d(TAG, "stopCall");
        if (peerConnection != null) {
            peerConnection.close();
            peerConnection = null;
        }
        if (audioSource != null) {
            audioSource.dispose();
            audioSource = null;
        }
        if (localAudioTrack != null) {
            localAudioTrack.dispose();
            localAudioTrack = null;
        }
    }

    private void createPeerConnection() {
        if (peerConnection != null) return;

        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
        RTCConfiguration rtcConfig = new RTCConfiguration(iceServers);

        peerConnection = factory.createPeerConnection(rtcConfig, new PeerConnection.Observer() {
            @Override public void onSignalingChange(PeerConnection.SignalingState signalingState) {}
            @Override public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {}
            @Override public void onIceConnectionReceivingChange(boolean b) {}
            @Override public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {}
            @Override public void onIceCandidate(IceCandidate iceCandidate) {
                // send local ICE to firebase
                IceCandidateMessage msg = new IceCandidateMessage(iceCandidate.sdpMid, iceCandidate.sdpMLineIndex, iceCandidate.sdp);
                signalingSender.sendIce(msg);
            }
            @Override public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {}
            @Override public void onAddStream(org.webrtc.MediaStream mediaStream) {}
            @Override public void onRemoveStream(org.webrtc.MediaStream mediaStream) {}
            @Override public void onDataChannel(org.webrtc.DataChannel dataChannel) {}
            @Override public void onRenegotiationNeeded() {}
            @Override public void onAddTrack(org.webrtc.RtpReceiver rtpReceiver, org.webrtc.MediaStream[] mediaStreams) {}
        });

        // create audio source and track
        MediaConstraints audioConstraints = new MediaConstraints();
        audioSource = factory.createAudioSource(audioConstraints);
        localAudioTrack = factory.createAudioTrack("ARDAMSa0", audioSource);

        // add track to PeerConnection
        RtpSender sender = peerConnection.addTrack(localAudioTrack);
    }

    // minimal SdpObserver impl
    private static class SimpleSdpObserver implements SdpObserver {
        @Override public void onCreateSuccess(SessionDescription sessionDescription) {}
        @Override public void onSetSuccess() {}
        @Override public void onCreateFailure(String s) {}
        @Override public void onSetFailure(String s) {}
    }
}

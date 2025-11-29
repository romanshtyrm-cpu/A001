package com.example.sender.webrtc;

import android.content.Context;

import org.webrtc.PeerConnectionFactory;

public class PeerConnectionFactoryProvider {

    private static PeerConnectionFactory factory;

    public static PeerConnectionFactory get(Context ctx) {
        if (factory == null) {
            PeerConnectionFactory.initialize(
                    PeerConnectionFactory
                            .InitializationOptions
                            .builder(ctx)
                            .createInitializationOptions()
            );

            PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
            factory = PeerConnectionFactory.builder()
                    .setOptions(options)
                    .createPeerConnectionFactory();
        }
        return factory;
    }
}

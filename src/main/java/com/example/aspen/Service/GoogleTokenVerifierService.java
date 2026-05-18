package com.example.aspen.Service;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class GoogleTokenVerifierService {

    @Value("${google.web.client.id}")
    private String googleClientId;



    public GoogleIdToken.Payload verify(
            String idTokenString
    ) throws Exception {

        NetHttpTransport transport =
                new NetHttpTransport();

        GsonFactory jsonFactory =
                GsonFactory.getDefaultInstance();

        GoogleIdTokenVerifier verifier =
                new GoogleIdTokenVerifier.Builder(
                        transport,
                        jsonFactory
                )
                        .setAudience(
                                Collections.singletonList(
                                        googleClientId
                                )
                        )
                        .build();

        GoogleIdToken idToken =
                verifier.verify(idTokenString);




        if (idToken == null) {
            throw new RuntimeException(
                    "Invalid Google token"
            );
        }

        return idToken.getPayload();
    }
}
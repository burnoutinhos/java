package com.burnoutinhos.burnoutinhos_api.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class FirebaseConfig {

    @Value("${firebase.credentials}")
    private String firebaseCredentials;

    @PostConstruct
    public void initializeFirebase() throws IOException {
        try {
            // Decodifica Base64 se necessário
            byte[] credentialsBytes;
            try {
                credentialsBytes = Base64.getDecoder().decode(
                    firebaseCredentials
                );
            } catch (IllegalArgumentException e) {
                credentialsBytes = firebaseCredentials.getBytes();
            }

            InputStream serviceAccount = new ByteArrayInputStream(
                credentialsBytes
            );

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setProjectId("burnoutinhos")
                .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            throw new RuntimeException(
                "Failed to initialize Firebase: " + e.getMessage(),
                e
            );
        }
    }
}

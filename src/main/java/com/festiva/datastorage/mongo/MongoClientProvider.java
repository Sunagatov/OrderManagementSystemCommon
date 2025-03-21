package com.festiva.datastorage.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class MongoClientProvider {

    public MongoClient get() {
        String username = System.getenv("MONGO_USERNAME");
        String password =  System.getenv("MONGO_PASSWORD");
        String host =  System.getenv("MONGO_HOST");

        ConnectionString connString = getConnectionString(username, password, host);

        // Configure settings with TLS and server API version
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();

        return MongoClients.create(settings);
    }

    private static ConnectionString getConnectionString(String username, String password, String host) {
        if (username == null || password == null || host == null) {
            throw new IllegalArgumentException("MongoDB connection parameters (username, password, host) are not set.");
        }

        String connectionString = String.format("mongodb+srv://%s:%s@%s/?retryWrites=true&w=majority&appName=FestivaDatabase&readPreference=primary",
                username, password, host);

        return new ConnectionString(connectionString);
    }
}

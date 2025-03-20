package com.festiva.datastorage.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class MongoClientProvider {

    public MongoClient get() {

        String connectionString = String.format("mongodb+srv://%s:%s@festivedatabase.5tfjn.mongodb.net/?retryWrites=true&w=majority&appName=FestiveDatabasee&readPreference=primary",
                "admin", "qmNZd1JC242tzWS0", "festivadatabase.5tfjn.mongodb.net");

        return MongoClients.create(connectionString);
    }
}

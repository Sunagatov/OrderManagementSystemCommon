package com.festiva.datastorage.mongo;

import com.festiva.datastorage.Friend;
import com.festiva.datastorage.CustomDAO;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MongoDAO implements CustomDAO {

    private final MongoClient mongoClient;
    private final MongoCollection<Document> usersCollection;

    public MongoDAO() {
        String databaseName = "festivadatabase";
        String collectionName = "users";

        this.mongoClient = new MongoClientProvider().get();
        this.usersCollection = mongoClient.getDatabase(databaseName).getCollection(collectionName);
    }

    @Override
    public void addFriend(long telegramUserId, Friend friend) {
        Document friendDoc = new Document("name", friend.getName())
                .append("birthDate", friend.getBirthDate().toString());

        Bson filter = Filters.eq("telegramUserId", telegramUserId);
        Bson update = Updates.push("friends", friendDoc);
        UpdateOptions options = new UpdateOptions().upsert(true);

        usersCollection.updateOne(filter, update, options);
    }

    @Override
    public List<Friend> getFriends(long telegramUserId) {
        List<Friend> friends = new ArrayList<>();

        Bson filter = Filters.eq("telegramUserId", telegramUserId);
        Document userDoc = usersCollection.find(filter).first();

        if (userDoc != null) {
            List<Document> friendDocs = userDoc.getList("friends", Document.class);
            for (Document friendDoc : friendDocs) {
                String name = friendDoc.getString("name");
                LocalDate birthDate = LocalDate.parse(friendDoc.getString("birthDate"));
                friends.add(new Friend(name, birthDate));
            }
        }

        return friends;
    }

    @Override
    public boolean friendExists(long telegramUserId, String name) {
        Bson filter = Filters.eq("telegramUserId", telegramUserId);
        Document userDoc = usersCollection.find(filter).first();

        if (userDoc != null) {
            List<Document> friendDocs = userDoc.getList("friends", Document.class);
            for (Document friendDoc : friendDocs) {
                if (friendDoc.getString("name").equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public void deleteFriend(long telegramUserId, String name) {
        Bson filter = Filters.eq("telegramUserId", telegramUserId);
        Bson update = Updates.pull("friends", Filters.eq("name", name));

        usersCollection.updateOne(filter, update);
    }

    @Override
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("Соединение с MongoDB закрыто.");
        }
    }

    @Override
    public List<Friend> getAllBySortedByDayMonth(long telegramUserId) {
        List<Friend> friends = getFriends(telegramUserId);
        if (friends.isEmpty()) {
            return friends;
        }
        friends.sort(Comparator.comparing(friend -> friend.getBirthDate().withYear(2000)));
        return friends;
    }

    @Override
    public List<Friend> getAllSortedByUpcomingBirthday(long telegramUserId) {
        LocalDate currentDate = LocalDate.now();
        List<Friend> friends = getFriends(telegramUserId);
        if (friends.isEmpty()) {
            return friends;
        }
        friends.sort(Comparator.comparing(friend -> nextBirthday(friend.getBirthDate(), currentDate)));
        return friends;
    }

    @Override
    public List<Long> getAllUserIds() {
        List<Long> userIds = new ArrayList<>();
        usersCollection.distinct("telegramUserId", Long.class).into(userIds);
        return userIds;
    }

    private LocalDate nextBirthday(LocalDate birthDate, LocalDate currentDate) {
        LocalDate nextBirthday = birthDate.withYear(currentDate.getYear());
        if (nextBirthday.isBefore(currentDate)) {
            nextBirthday = nextBirthday.plusYears(1);
        }
        return nextBirthday;
    }
}
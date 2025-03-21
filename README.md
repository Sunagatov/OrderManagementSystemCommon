---

# Festiva Birthday Bot

Festiva Birthday Bot is a Java-based Telegram bot that helps users manage and receive reminders about birthdays. The bot integrates with MongoDB Atlas for data storage and uses the Telegram Bot API to interact with users. Users can add friends with their birth dates, list all birthdays, view upcoming birthdays, and more.

## Features

- **User Management:**  
  Add, remove, and list friends with their birth dates.
- **Birthday Reminders:**  
  Sort birthdays by day/month and upcoming dates.
- **Jubilee Notifications:**  
  Identify friends whose upcoming birthdays mark a milestone (e.g., every 5 years).
- **Telegram Integration:**  
  Interact with the bot using simple commands:
  - `/start` – Welcome and command list.
  - `/list` – List all friends.
  - `/add` – Add a new friend with a name and birth date.
  - `/remove` – Remove an existing friend.
  - `/birthdays` – View birthdays by month.
  - `/upcomingBirthdays` – View birthdays coming up in the next 30 days.
  - `/jubilee` – View upcoming milestone birthdays.
  - `/help` – Display help and command list.

## Prerequisites

- **Java:** JDK 21 (or later) is recommended.
- **Maven:** For dependency management and building the project.
- **MongoDB Atlas:** A MongoDB Atlas cluster is required for data storage.  
- **Telegram Bot Token:** Obtain a bot token from [BotFather](https://core.telegram.org/bots#6-botfather).

## Setup

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/festiva-birthday-bot.git
cd festiva-birthday-bot
```

### 2. Configure MongoDB Credentials

The project reads sensitive data (username, password, host) from environment variables or JVM parameters. Ensure you have a user created in your MongoDB Atlas cluster with proper privileges.

**Set Environment Variables:**

- **Unix/Linux/MacOS:**

  ```bash
  export MONGO_USERNAME=yourMongoUsername
  export MONGO_PASSWORD=yourMongoPassword
  export MONGO_HOST=yourClusterHost  # e.g., festivacluster.q5ws1.mongodb.net
  ```

- **Windows (CMD):**

  ```cmd
  set MONGO_USERNAME=yourMongoUsername
  set MONGO_PASSWORD=yourMongoPassword
  set MONGO_HOST=yourClusterHost
  ```

Alternatively, pass them as JVM parameters when running the application:

```bash
java -DMONGO_USERNAME=yourMongoUsername -DMONGO_PASSWORD=yourMongoPassword -DMONGO_HOST=yourClusterHost -jar target/yourapp.jar
```

### 3. Build the Project

Use Maven to compile and package the project:

```bash
mvn clean package
```

### 4. Running the Application

After building, run the application (make sure to include the environment variables or JVM parameters):

```bash
java -jar target/yourapp.jar
```

The application will initialize the MongoDB connection and start the Telegram bot.

## Code Structure

- **`com.festiva.datastorage`:**  
  Contains the data models (e.g., `Friend`, `User`) and DAO interfaces.

- **`com.festiva.datastorage.mongo`:**  
  Includes classes for MongoDB connection:
  - **`MongoClientProvider`** – Reads sensitive credentials from environment variables or system properties and creates a MongoDB client.
  - **`MongoDAO`** – Implements data access operations (add, remove, list friends).

- **`com.festiva.businessLogic`:**  
  Contains business logic for birthday reminders, message formatting, and Telegram bot command handling:
  - **`BirthdayBot`** – Main Telegram bot class extending `TelegramLongPollingBot`.
  - **`BirthdateInfoProvider`** – Prepares user-friendly birthday information.
  - **`FriendCreator`**, **`FriendRemover`**, **`MonthSelector`**, and **`Callback`** – Handle specific bot commands and interactions.

- **`com.festiva`:**  
  The main entry point of the application (`Main.java`), which initializes the DAO and registers the Telegram bot.

## Troubleshooting

- **Authentication Errors:**  
  If you encounter authentication errors (e.g., `MongoSecurityException`), double-check that the credentials and host values are correct and that your Atlas user is assigned the proper roles. Make sure your authentication source (usually `admin`) is correctly set in the connection string if necessary.

- **SRV Lookup Issues:**  
  Ensure that your `MONGO_HOST` is in the proper format (e.g., `festivacluster.q5ws1.mongodb.net`) and that DNS resolution works for your network.

- **SLF4J Warnings:**  
  The log message `SLF4J: No SLF4J providers were found` is harmless and indicates that no SLF4J binding is present. You can add an SLF4J binding (e.g., `slf4j-simple`) if you wish to see logging output.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

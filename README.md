# Festiva Birthday Bot

Festiva Birthday Bot is a Java-based Telegram bot that helps users manage and receive birthday reminders. It integrates with MongoDB Atlas for data storage and uses the Telegram Bot API (via long polling) to interact with users. With this bot, you can add friends with their birth dates, list birthdays, view upcoming birthdays, and receive milestone (jubilee) notifications.

---

## Features

- **User Management:**
    - Add, remove, and list friends with their birth dates.
- **Birthday Reminders:**
    - Display birthdays sorted by day/month.
    - View upcoming birthdays within a set period.
- **Jubilee Notifications:**
    - Highlight milestone birthdays (e.g., every 5 years).
- **Telegram Integration:**
    - `/start` – Displays a welcome message and lists available commands.
    - `/list` – Lists all registered friends.
    - `/add` – Adds a new friend with a name and birth date.
    - `/remove` – Removes an existing friend.
    - `/birthdays` – Shows birthdays by month.
    - `/upcomingBirthdays` – Displays birthdays coming up in the next 30 days.
    - `/jubilee` – Highlights upcoming milestone birthdays.
    - `/help` – Shows help and command instructions.

---

## Prerequisites

- **Java:** JDK 21 (or later)
- **Maven:** For dependency management and building the project
- **MongoDB Atlas:** A cloud-based MongoDB cluster for data storage
- **Telegram Bot Token:** Obtain one from [BotFather](https://core.telegram.org/bots#6-botfather)

---

## Environment Variables

The application uses environment variables (or JVM system properties) for secure configuration. Set the following variables before launching the app:

### MongoDB Settings
- `MONGO_USERNAME` – Your MongoDB Atlas username.
- `MONGO_PASSWORD` – Your MongoDB Atlas password.
- `MONGO_HOST` – Your MongoDB Atlas host.
- *Optional:* `MONGO_DATABASE_NAME` – Defaults to `FestivaDatabase` if not set.

### Telegram Bot Settings
- `TELEGRAM_BOT_TOKEN` – Your Telegram bot token provided by BotFather.
- `TELEGRAM_BOT_USERNAME` – The username of your Telegram bot.

### Kafka Settings (Optional)
- `KAFKA_BOOTSTRAP_SERVERS` – Kafka bootstrap server addresses.
- `KAFKA_API_KEY` – Your Kafka API key.
- `KAFKA_API_SECRET` – Your Kafka API secret.
- *Optional:* `APP_KAFKA_ENABLED` – Set to `true` to enable Kafka; `false` to disable (defaults to `false` if not set).

---

### Setting Environment Variables

#### Unix/Linux/MacOS
```bash
export MONGO_USERNAME=yourMongoUsername
export MONGO_PASSWORD=yourMongoPassword
export MONGO_HOST=yourMongoHost

export TELEGRAM_BOT_TOKEN=yourTelegramBotToken
export TELEGRAM_BOT_USERNAME=YourBotUsername

export KAFKA_BOOTSTRAP_SERVERS=yourKafkaServer:9092
export KAFKA_API_KEY=yourKafkaApiKey
export KAFKA_API_SECRET=yourKafkaApiSecret
export APP_KAFKA_ENABLED=true
```

#### Windows
```powershell
set MONGO_USERNAME=yourMongoUsername
set MONGO_PASSWORD=yourMongoPassword
set MONGO_HOST=festivacluster.q5ws1.mongodb.net

set TELEGRAM_BOT_TOKEN=yourTelegramBotToken
set TELEGRAM_BOT_USERNAME=YourBotUsername

set KAFKA_BOOTSTRAP_SERVERS=yourKafkaServer:9092
set KAFKA_API_KEY=yourKafkaApiKey
set KAFKA_API_SECRET=yourKafkaApiSecret
set APP_KAFKA_ENABLED=true
```

---

## Building the Project

Use Maven to compile and package the application:

```bash
mvn clean package
```

This will create a JAR file (e.g., `Festiva-1.0-SNAPSHOT.jar`) in the `target` directory.

---

## Running with Docker

A `Dockerfile` is included for containerized deployment.

### Build the Docker Image
```bash
docker build -t festiva-birthday-bot .
```

### Run the Docker Container
Pass the required environment variables using the `-e` flag:

```bash
docker run --rm -p 8080:8080 \
-e MONGO_USERNAME=yourMongoUsername \
-e MONGO_PASSWORD=yourMongoPassword \
-e MONGO_HOST=festivacluster.q5ws1.mongodb.net \
-e TELEGRAM_BOT_TOKEN=yourTelegramBotToken \
-e TELEGRAM_BOT_USERNAME=YourBotUsername \
-e KAFKA_BOOTSTRAP_SERVERS=yourKafkaServer:9092 \
-e KAFKA_API_KEY=yourKafkaApiKey \
-e KAFKA_API_SECRET=yourKafkaApiSecret \
-e APP_KAFKA_ENABLED=true \
festiva-birthday-bot
```

The container exposes port 8080 and runs the bot. You can view the logs using:

```bash
docker logs <container_id>
```

---


## Troubleshooting

- **MongoDB Connection Issues:**
    - Double-check MongoDB credentials and network access.
- **Environment Variables:**
    - Verify with `echo $VAR` (Linux/macOS) or `echo %VAR%` (Windows CMD).
- **Docker:**
    - Use `docker logs <container_id>` for debugging.

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---
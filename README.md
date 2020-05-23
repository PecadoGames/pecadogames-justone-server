# Pecado Games - SoPra 2020

## Introduction
This project is the server-side implementation of the *Pecado Games Just One* Web Application. See [Pecado Games](https://github.com/PecadoGames) for the whole project.

## Setup this Template with your IDE of choice

Download your IDE of choice: (e.g., [Eclipse](http://www.eclipse.org/downloads/), [IntelliJ](https://www.jetbrains.com/idea/download/)) and make sure Java 13 is installed on your system.

1. File -> Open... -> SoPra Server Template
2. Accept to import the project as a `gradle project`

To build right click the `build.gradle` file and choose `Run Build`

## Building with Gradle

You can use the local Gradle Wrapper to build the application.

Plattform-Prefix:

-   MAC OS X: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).

### Build

```bash
./gradlew build
```

### Run

```bash
./gradlew bootRun
```

### Test

```bash
./gradlew test
```

### Development Mode

You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed and you save the file.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

### Technologies
This application was built with:
  - [Spring Boot](https://spring.io/projects/spring-boot) - Backend application
  - [JPA](https://www.oracle.com/java/technologies/persistence-jsp.html)/[Hibernate](https://hibernate.org) - Database management
  
### Main components
One of the main component of this application is the [Game Controller](https://github.com/PecadoGames/pecadogames-justone-server/blob/master/src/main/java/ch/uzh/ifi/seal/soprafs20/controller/GameController.java). It handles all incoming requests from the client during the game, sends data to the [Game Service](https://github.com/PecadoGames/pecadogames-justone-server/blob/master/src/main/java/ch/uzh/ifi/seal/soprafs20/service/GameService.java) which is updated and stored in the database there, and then sends a corresponding response back to the client.

Another high-level component is the [Lobby Controller](https://github.com/PecadoGames/pecadogames-justone-server/blob/master/src/main/java/ch/uzh/ifi/seal/soprafs20/controller/LobbyController.java). It allows for the user to create lobbies and for other users to join a lobby. Moreover, a user is able to add bots or, even better, invite friends of his into a lobby. Without these lobbies, it would not be possible to play the *Pecado Games Just One Game* with your remote friends.

A third, very important component is the chat functionality (i.e. [Chat Service](https://github.com/PecadoGames/pecadogames-justone-server/blob/master/src/main/java/ch/uzh/ifi/seal/soprafs20/service/ChatService.java)). It allows for users to exchange messages in a lobby and also during the game. But be aware: It won't be possible to misuse the chat in order to spoil the game! :wink:

# Pecado Games - SoPra 2020

## Introduction
This project is the server-side implementation of the *Pecado Games Just One* Web Application. See [Pecado Games](https://github.com/PecadoGames) for the whole project.
The goal of this project is to provide an online version of [Just One](https://justone-the-game.com) (Game of the Year 2019) with its core functionality and of course some additional features.

## Technologies
This application is built with:
  - [Spring Boot](https://spring.io/projects/spring-boot) - Backend application
  - [JPA](https://www.oracle.com/java/technologies/persistence-jsp.html) / [Hibernate](https://hibernate.org) - Database management
  
## Main components
One of the main components of this application is the [**Game Controller**](https://github.com/PecadoGames/pecadogames-justone-server/blob/master/src/main/java/ch/uzh/ifi/seal/soprafs20/controller/GameController.java).
It handles all incoming requests from the client during the game, sends data to the [Game Service](https://github.com/PecadoGames/pecadogames-justone-server/blob/master/src/main/java/ch/uzh/ifi/seal/soprafs20/service/GameService.java)
which is updated and stored in the database there, and then sends a corresponding response back to the client.

Another high-level component is the [**Lobby Controller**](https://github.com/PecadoGames/pecadogames-justone-server/blob/master/src/main/java/ch/uzh/ifi/seal/soprafs20/controller/LobbyController.java).
It allows for a user to create lobbies and for other users to join a lobby. Moreover, a user is able to add bots or, even better, invite friends of his into a lobby. Without a lobby, it would not be possible to play the *Pecado Games Just One Game*
with your remote friends.

A third, very important component is the chat functionality (i.e. [**Chat Service**](https://github.com/PecadoGames/pecadogames-justone-server/blob/master/src/main/java/ch/uzh/ifi/seal/soprafs20/service/ChatService.java)).
It allows for users to exchange messages in a lobby as well as during the game. But be aware: It won't be possible to misuse the chat in order to spoil the game! :wink:

Of course, these components are all correlated in a way. First, the Lobby Controller is needed for the lobby functionality, which then redirects the user to the game functionality. The Chat Service is related to both components.

## Launch & Deployment
Download your IDE of choice: (e.g., [Visual Studio Code](https://code.visualstudio.com), [IntelliJ](https://www.jetbrains.com/idea/download/)) and make sure Java 13 is installed on your system.

1. Clone or download this project
2. File -> Open... -> pecadogames-justone-server
2. Accept to import the project as a `gradle project`

To build right click the `build.gradle` file and choose `Run Build`

### Building with Gradle

You can use the local Gradle Wrapper to build the application.

Plattform-Prefix:

-   MAC OS X: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

#### Build

```bash
./gradlew build
```

#### Run

```bash
./gradlew bootRun
```

#### Test

```bash
./gradlew test
```

#### Development Mode

You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed, and you save the file.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

### Deployment
To deploy the project, go to [Heroku](https://dashboard.heroku.com/apps/sopra-fs20-group-04-server) (you can obtain the login credentials by reaching out to one of the developers).
Then, go to **Deploy** --> **Manual deploy**, choose a GitHub branch and then deploy that branch. You could also enable automatic deploys from a GitHub branch.

## Roadmap - a look ahead
The following are the top 3 features we would like to implement in the future:
* Voice Chat
  * We would love to have a voice chat API, in addition to the existing chat functionality. This would make communicating
  during the game a lot more fun!
* Bad words filter
  * For the chat functionality as well as for providing clues in the game, it would be nice to
    have a component to filter out bad/inappropriate words.
  * This could either be done with an internal dictionary or with an external API. Any other suggestions are welcome!
* Non-real clues filter
  * For the functionality of providing clues, we would like to filter out clues that aren't real words
  (i.e. are not found in a dictionary or aren't named entities.)
  * This could also be done via internal dictionary or external API.

## Authors & Acknowledgements
### Backend Developers
 - [Ramon Solo de Zaldivar](https://github.com/solodezaldivar) (aka. Bad Bunny), BSc Computer Science, University of Zurich
 - [Marion Dübendorfer](https://github.com/tsunama) (aka. Märlinde), BSc Computer Science, University of Zurich
 
### Supervision & Guidance
- [Alina Marti](https://github.com/AlinaMarti), MSc Computer Science, University of Zurich

## Licences
### MIT Licence
Copyright (c) [2020] [Ramon Solo, Marion Dübendorfer]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

### Apache OpenNLP
- [Apache License](https://github.com/PecadoGames/pecadogames-justone-server/blob/master/LICENSE)

### DataMuse API
- [Link](http://www.datamuse.com/api/) to DataMuse API

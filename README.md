# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared tests`     | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

### Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

diagram: https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDAEooDmSAzmFMARDQVqhFHXyFiwUgBF+wAIIgQKLl0wATeQCNgXFDA3bMmdlAgBXbDADEaYFQCerDt178kg2wHcAFkjAxRFRSAFoAPnJKGigALhgAbQAFAHkyABUAXRgAegt9KAAdNABvfMp7AFsUABoYXDVvaA06lErgJAQAX0xhGJgIl04ePgEhaNF4qFceSgAKcqgq2vq9LiaoFpg2joQASkw2YfcxvtEByLkwRWVVLnj2FDAAVQKFguWDq5uVNQvDbTxMgAUQAMsC4OkYItljAAGbmSrQgqYb5KX5cAaDI5uUaecYiFTxNAWBAIQ4zE74s4qf5o25qeIgab8FCveYw4DVOoNdbNL7ydF3f5GeIASQAciCWFDOdzVo1mq12p0YJL0ilkbQcSMPIIaQZBvSMUyWYEFBYwL53hUuSgBdchX9BqK1VLgTKtUs7XVgJbfOkIABrdBujUwP1W1GChmY0LYyl4-UTIkR-2BkNoCnHJMEqjneORPqUeKRgPB9C9aKULGRYLoMDxABMAAYW8USmWM+geugNCYzJZrDZoNJHjBQRBOGgfP5Aph62Ei9W4olUhlsjl9Gp8R25SteRsND1i7AEzm9XnJjAEFOkGgbd75Yf+dncZeDXSYyaYI8Xm99wdH5hRdQFyDBCFZQKdo0AMBEICRRZo0dWNayGd9ThTFB4n3N9dUwwlDUub87geJ4kjWI85hfTYgKdONIldEFwUhBU+U2eFES9ZDgOdc8MOpLD4ho-sdSpZNCK-FCf2ZFBWQtK1H0+Hj6JFMDJWlKDbXlLsKxndVNTLFTUMLdD8MEwicI+H00ytbsszE3NP1M08rO0lZdMzKtqBrUzF0bGBW3bUp919dM9N7NB+1McwrFsMwUFDSd2EsZgbD8AIgmQBt-lcxIZAg9JgU3bcuF3exwq809-kcj8hJvKdUrmTz0AOWqCPzWkjRIxlDBQBAnhQBTrRatA6JMxiwIK8Eits8tM04hC5pMY0QP48yJM67CYBJMk8PEq8usGPLdvJaq-OyjBmzbTA+wHWLh2mDQJzcGAAHE7UxdK5yykJmGOld4gSN7gSyXJ2DtYpRu8-p1oOg0yLAD7qi4ZrKta-anKwqTePuGAQEEOEkCgSphrRuy9PGjE1KBCDWNGxakSM1a+MidqLK24lSXJdnNoLYjpNI38njgCwoCgZHVDmKmQMm2mWKhCHqlBV74KRJWUG+GBjOp0zecO7aNZVnhMbqySXMB387WNrBzsGfzruCkojbcSLosHOKbGwMXsAG+AzQMSXZ0yhdLv+5cfNXZI0jBnINah9G0A7DWJTtE8VxqxMzc5-GA8luYU7tPYE4prz9ecgXcaZQnidJ-1yfmjGWYYgF5cgub7MZ5bm7Q8v6tO02Ov5mBm9NOTAnzwvqhl505bdTSreqVPqjDTUNe+HW1rZrOh9TDWxRkQeOYLAHI4eO0D5h3z7bDx3k4vmQ3fuodbEcfrb28GAACkIHvd67VsNoBAoAgyhz+rlS2yRnix3jhVUu6AOz+TgBAW8UA6j70flfM828Ly722gAK1-mgSeD86hIJQdAYucDG4OR3sfLqld6LVzQETEmZNRozxbkxOmUIGZq27j1OMcMsaWR2tzI+fMGEj0EYjEh1QD6cJpuBBWi8UAHy7uveQm9WZmXhvVDBEiDY4yYTAQh945FqJkDUchqDFGgXFO6T0ixV6qI3j3PWdDJHbQHn3c2p8Yhcz2nbOst9Ao3TujFF+NgzDAGcIgOSsBgDYF9oQS8wd5z+QgWffKhViq5CMFgzOuD6HbRAANPA+c2qeKMd1QWvUykJMlqjOxc9pqg2BKozE-DNHXG0UInBAkvGBJ5tUiu0i6l4waXgdkUBml9KUW02aiwulcSQu44R2drw+NGdjWpVd8blKgMNeycz3GtNyR3PSKylrM0Eb3HZojtnFK8WhE64jgnwFCUFW6UVMBAA

package client;

import client.WebSocket.NotificationHandler;
import ui.PrintBoard;
import webSocketMessages.serverMessages.ServerMessage;

import java.util.Scanner;


import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl, this);
    }

    public void run() {
        System.out.println("\uD83D\uDC36 Welcome to Chess. Sign in to start.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    @Override
    public void notify(ServerMessage notification) {
        if (notification.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            System.out.println(RED + notification.getMessage());
            printPrompt();
        }
        if (notification.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            System.out.print("\n");
            PrintBoard.printGameBoard(notification.getGame().getBoard(), null);
            printPrompt();
        }
        if (notification.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_MOVES) {
            System.out.print("\n");
            PrintBoard.printGameBoardHighlghted(notification.getGame().getBoard(), null, notification.getLegalMoves());
            printPrompt();
        }

    }

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> " + GREEN);
    }


}
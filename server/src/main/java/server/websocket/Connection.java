package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String authToken;
    public Session session;
    public boolean isWatcher;

    public boolean isWatcher() {
        return isWatcher;
    }

    public void setWatcher(boolean watcher) {
        isWatcher = watcher;
    }

    public Connection(String authToken, Session session) {
        this.authToken = authToken;
        this.session = session;
        this.isWatcher = false;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}
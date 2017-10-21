import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.util.StringTokenizer;

import static spark.Spark.*;

@WebSocket
public class TritonCodeServer {
    static ServerDriver serverDriver;

    public static void main(String[] args) {
        System.out.println("Hello, World!");

        port(3000);
        staticFileLocation("/public");
        //webSocket("/chat", TritonCodeServer.class);
        webSocket("/code", TritonCodeServer.class);
        init();
    }

    private String sender, msg;

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        String username = "User" + Chat.nextUserNumber++;
        Chat.userUsernameMap.put(user, username);
        Chat.broadcastMessage(sender = "Server", msg = (username + " joined the chat"));
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        String username = Chat.userUsernameMap.get(user);
        Chat.userUsernameMap.remove(user);
        Chat.broadcastMessage(sender = "Server", msg = (username + " left the chat"));
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        //Chat.broadcastMessage(sender = Chat.userUsernameMap.get(user), msg = message);
        if (message.contains("START:")) {
            int newlinePos = message.indexOf('\n');
            String key = message.substring(6, newlinePos);
            String document = message.substring(newlinePos + 1);
            System.out.println(key);
            System.out.println(document);
            serverDriver = new ServerDriver(document, key);
        } else if (message.contains("DOCUMENT")) {
            // TODO: check if file exists
            StringTokenizer tokenizer = new StringTokenizer(message, "\n");
            tokenizer.nextElement();
            String key = (String) tokenizer.nextElement();
            String editKey = (String) tokenizer.nextElement();
            String parentKey = (String) tokenizer.nextElement();
            String edits = (String) tokenizer.nextElement();
            System.out.println(key);
            System.out.println(editKey);
            System.out.println(parentKey);
            System.out.println(edits);
            serverDriver.enqueueClientOperation(new ServerOperation(OperationParser.strToOperation(edits), editKey, parentKey));
            serverDriver.processChange();
            System.out.println(serverDriver.getDocument().getData());
        }
    }
}

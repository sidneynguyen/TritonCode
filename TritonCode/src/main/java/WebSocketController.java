import javax.websocket.*;
import java.net.URI;
import java.util.StringTokenizer;

@ClientEndpoint
public class WebSocketController {
    Session userSession = null;
    private MessageHandler messageHandler;
    ClientDriver clientDriver;
    EditorViewController controller;

    public WebSocketController(URI endpointURI, String filename, String contents, EditorViewController controller) {
        try {
            WebSocketContainer container = ContainerProvider
                    .getWebSocketContainer();
            container.connectToServer(this, endpointURI);
            clientDriver = new ClientDriver(contents, filename);
            this.controller = controller;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @OnOpen
    public void onOpen(Session userSession) {
        this.userSession = userSession;
    }

    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        this.userSession = null;
    }

    @OnMessage
    public void onMessage(String message) {
        if (this.messageHandler != null)
            this.messageHandler.handleMessage(message);
        if (message.contains("START:")) {
            int newlinePos = message.indexOf((char) 0);
            String key = message.substring(6, newlinePos);
            String document = message.substring(newlinePos + 1);
            clientDriver = new ClientDriver(document, key);
            System.out.println(clientDriver.getDocument().getData());
            controller.receivedMessage(clientDriver.getDocument().getData());
        } else if (message.contains("DOCUMENT")) {
            // TODO: check if file exists
            StringTokenizer tokenizer = new StringTokenizer(message, "" + (char) 0);
            tokenizer.nextElement();
            String key = (String) tokenizer.nextElement();
            String editKey = (String) tokenizer.nextElement();
            String parentKey = (String) tokenizer.nextElement();
            String edits = "";
            if (tokenizer.hasMoreElements()) {
                edits = (String) tokenizer.nextElement();
            }

            System.out.println("EDITS:" + edits);
            ServerOperation operation = new ServerOperation(OperationParser.strToOperation(edits), editKey, parentKey);
            clientDriver.enqueueServerOperation(operation);

            String doc = controller.getEditorText();
            if (!doc.equals(clientDriver.getDocument().getData())) {
                clientDriver.applyEdits(doc);
                clientDriver.setHasUnsentEdit(true);
            }
            clientDriver.receiveEdits();
            controller.receivedMessage(clientDriver.getDocument().getData());
        }
    }

    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    public void sendMessage(String message) {
        this.userSession.getAsyncRemote().sendText(message);
    }

    public void sendEdit(String content) {
        if (!clientDriver.canSendEdit()) {
            return;
        }
        if (content.equals(clientDriver.getDocument().getData()) && !clientDriver.hasUnsentEdit()) {
            return;
        }

        ServerOperation operation = clientDriver.sendEdits(content);

        String text = "DOCUMENT" + (char) 0 + clientDriver.getKey() + (char) 0 + operation.getKey() + (char) 0 + operation.getParentKey() + (char) 0 + OperationParser.operationToStr(operation.getOperation());
        this.userSession.getAsyncRemote().sendText(text);
        System.out.println(text);
    }

    public static interface MessageHandler {
        public void handleMessage(String message);
    }

    public static void main(String[] args) throws Exception {
        /*final WebSocketController testClient = new WebSocketController(new URI("ws://localhost:3000/code"));
        testClient.addMessageHandler(new MessageHandler() {
            @Override
            public void handleMessage(String message) {
                System.out.println(message);
            }
        });

        testClient.sendMessage("START:1234\nabc");
        System.out.println("START:1234\nabc");
        //Thread.sleep(1000);
        testClient.sendMessage("DOCUMENT\n1234\n4321\n1234\nIx,R3,\n");
        System.out.println("DOCUMENT\n1234\n4321\n1234\nIx,R3,\n");
*/
    }
}
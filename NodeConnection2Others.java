package diego.middle;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class NodeConnection2Others {
    private final String serverName;
    public final int serverPort;
    private Socket socket;
    private InputStream serverIn;
    private OutputStream serverOut;
    private BufferedReader bufferedIn;

    private String login;
    private NodeOpRegisterController controller;
    private ArrayList<NodeWorker> workerList;

    public void setController(NodeOpRegisterController controller) {
        this.controller = controller;
    }
    public void setWorkerList(ArrayList<NodeWorker> workerList) {
        this.workerList = workerList;
    }

    public NodeConnection2Others(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public boolean connect() {
        try {
            this.socket = new Socket(serverName, serverPort);
            System.out.println("Server port is: " + socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean login(String login, String password) throws IOException {
        String cmd = "login " + login + " " + password+"\n";
        serverOut.write(cmd.getBytes());

        String response = bufferedIn.readLine();
        System.out.println("Response Line: " + response);
        if ("ok login".equalsIgnoreCase(response)) {
            startMessageReader();
            this.login = login;
            return true;
        } else {
            return false;

        }
    }

    public void startMessageReader() {
        Thread t = new Thread() {
            @Override
            public void run() {
                readMessageLoop();
            }
        };
        t.start();
    }

    private void readMessageLoop() {
        try {
            String line;
            while ((line = bufferedIn.readLine()) != null) {
                System.out.println("msg receive: " + line);
                String[] tokens = line.split(" ");
                if (tokens.length > 0) {
                    String cmd = tokens[0];
                    System.out.println(line);
                    switch (cmd) {
                        case "online" -> System.out.println("online: " + tokens[1]);
                        case "offline" -> System.out.println("offline: " + tokens[1]);
                        case "op" -> {
                            String[] operation = line.split(" ",3);
                            System.out.println("op: " + operation[2]);
                            handleOperation(operation);
                        }
                        case "result" -> handleResult(tokens);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            try{
                socket.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void handleResult(String[] tokens) throws IOException {
        //String msg = "node result " + tokens[1] + " " + tokens[2] + "\n";
        String msg = "node result guest " + tokens[2] + "\n";
        serverOut.write(msg.getBytes());

        // TODO: pasarle el mensaje al cliente
        for (NodeWorker worker : workerList) {
            worker.send(msg.split("node ")[1]);
        }

        controller.addOperation2Board("result just bounced");
    }

    private void handleOperation(String[] operation) throws IOException {
        String msg = "node op " + operation[1] + " " + operation[2] + "\n";
        serverOut.write(msg.getBytes());

        controller.addOperation2Board("operation just bounced");

    }
    public void handleBounceMsg(String msg) throws IOException {
        //serverOut.write(msg.getBytes());
        System.out.println(login + " be bouncing");
        controller.addOperation2Board("Msg receive from another node");
        serverOut.write(msg.getBytes());
    }
}
package diego.servidor;

import java.io.*;
import java.net.Socket;

public class ServidorCliente {
    private final String serverName;
    private final int serverPort;
    private Socket socket;
    private InputStream serverIn;
    private OutputStream serverOut;
    private BufferedReader bufferedIn;

    private ControllerOps controller;

    public void setController(ControllerOps controller) {
        this.controller = controller;
    }

    public ServidorCliente(String serverName, int serverPort) {
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

    // op [user] [operation]
    private void handleOperation(String[] operation) throws IOException {
        if (operation.length == 3) {
            int result = EvaluarString.evaluate(operation[2]);
            System.out.println("Result to " + operation[1] + ":" + result);
            String msg = "result " + operation[1] + " " + result + "\n";
            serverOut.write(msg.getBytes());

            //controller.addOperation2List("Result: " + result);
        }
    }
}


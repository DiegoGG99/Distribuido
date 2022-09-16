package diego.calculadora;

import java.io.*;
import java.net.Socket;

public class CalculadoraCliente {
    private final String serverName;
    private final int serverPort;
    private Socket socket;
    private InputStream serverIn;
    private OutputStream serverOut;
    private BufferedReader bufferedIn;

    private String result;

    public CalculadoraCliente(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public boolean connect() {
        try {
            this.socket = new Socket(serverName, serverPort);
            System.out.println("Client port is: " + socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void op(String textOperation) throws IOException {
        String cmd = "op " + textOperation + "\n";
        serverOut.write(cmd.getBytes());
        System.out.println("client just sent an operation");
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
                    switch (cmd) {
                        case "online" -> System.out.println("online: " + tokens[1]);
                        case "offline" -> System.out.println("offline: " + tokens[1]);
                        case "result" -> {
                            System.out.println("result: " + tokens[2]);
                            handleResult(tokens[2]);
                        }
                        case "node" -> {
                            System.out.println();
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

    private void handleResult(String result) {
        this.result = result;
        System.out.println(result);
    }

    public String getResult() {
        return this.result;
    }
}
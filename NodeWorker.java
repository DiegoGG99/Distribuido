package diego.middle;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class NodeWorker extends Thread {
    private NodeOpRegisterController controller;

    private Socket clientSocket;
    private NodeMOM nodeMOM;
    private String login = null;
    private OutputStream outputStream;

    public NodeWorker(NodeMOM nodeMOM, Socket clientSocket) {
        this.nodeMOM = nodeMOM;
        this.clientSocket = clientSocket;
    }

    public void setController(NodeOpRegisterController controller) {
        this.controller = controller;
    }
    private String getLogin() {
        return login;
    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                handleLogoff();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // COMMANDS
    /* logoff / quit
     *  login [user] [password]
     *  op [login] [operation]   - client operation
     *  result [userTo] [result] - server operation
     *
     *  node [op] [login] [operation]
     *  node [result] [userTo] [result]*/
    private void handleClientSocket() throws IOException {
        InputStream inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        // it's always reading clients commands
        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split(" ");
            if (tokens.length > 0) {
                String cmd = tokens[0];
                switch (cmd) {
                    case "logoff", "quit" -> handleLogoff();
                    case "login" -> handleLogin(outputStream, tokens);
                    case "op" -> {
                        String[] tokensOp = line.split(" ", 2); // op [operations]
                        handleOperation(tokensOp[1], false);
                    }
                    case "result" -> handleResult(tokens, false);
                    case "node" -> handleMsgFromNode(line);
                    default -> {
                        String msg = "unknown " + cmd + "\n";
                        outputStream.write(msg.getBytes());
                    }
                }
            }
        }
        clientSocket.close();
    }

    // node [op] [login] [operation]
    // node [result] [userTo] [result]
    private void handleMsgFromNode(String lineNode) throws IOException {
        String[] tokensNode = lineNode.split(" ");
        int intIndex = tokensNode[2].indexOf("node");

        if (intIndex != -1) {

            int n = tokensNode.length - 1;

            String[] tokens = new String[n];
            System.arraycopy(tokensNode, 1, tokens, 0, n);
            tokens[1] = "guest";

            String line = "";
            for (String x : tokens)
                line += x + " ";

            String cmd = tokens[0];
            controller.addOperation2Board("Command receive from a node");
            System.out.println("Node: " + line);

            switch (cmd) {
                case "op" -> {
                    System.out.println("Node receive a operation");
                    String[] tokensOp = line.split(" ", 2); // op [operations]
                    handleOperation(tokensOp[1], true);
                }
                case "result" -> {
                    System.out.println("Node receive a result");
                    handleResult(tokens, true);
                }
            }
        }
    }

    private void handleLogoff() throws IOException {
        nodeMOM.removeWorker(this);
        ArrayList<NodeWorker> workerList = nodeMOM.getWorkerList();

        controller.addOperation2Board("User logged off " + login);
        String msg = "offline " + login + "\n";
        for (NodeWorker worker : workerList) {
            if (!login.equals(worker.getLogin())) {
                worker.send(msg);
            }
        }
    }

    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
        if (tokens.length == 3) {
            String login = tokens[1];
            String password = tokens[2];

            if (login.equals(password)) {
                String msg = "ok login\n";
                outputStream.write(msg.getBytes());

                this.login = login;

                if ((login.split("node").length) > 1) {
                    controller.addNode2List(login, true);
                    controller.addOperation2Board("Node connection acquired");
                } else {
                    controller.addOperation2Board("User logged in: " + login);
                }
                System.out.println("User logged in successfully " + login);

                ArrayList<NodeWorker> workerList = nodeMOM.getWorkerList();
                for (NodeWorker worker : workerList) {
                    if (worker.getLogin() != null) {
                        if (!login.equals(worker.getLogin())) {
                            String msg2 = "online " + worker.getLogin() + "\n";
                            send(msg2);
                        }
                    }
                }

                String onlineMsg = "online " + login + "\n";
                for (NodeWorker worker : workerList) {
                    if (!login.equals(worker.getLogin())) {
                        worker.send(onlineMsg);
                    }
                }
            } else {
                String msg = "error login\n";
                outputStream.write(msg.getBytes());
                System.err.println("Login failed for " + login);
            }
        }
    }

    private void handleOperation(String operation, boolean fromNode) throws IOException {
        boolean isThereAServer = false;
        System.out.println("handleOperation: " + operation);
        String outMsg = "op " + login + " " + operation + "\n";

        ArrayList<NodeWorker> workerList = nodeMOM.getWorkerList();
        for (NodeWorker worker : workerList) {
            if ("server".equalsIgnoreCase(worker.getLogin())) {
                worker.send(outMsg);
                controller.addOperation2Board(login + " just send an operation");
                isThereAServer = true;
            }
        }

        if (!isThereAServer && !fromNode) {
            ArrayList<NodeConnection2Others> nodeList = nodeMOM.getNodeList();
            controller.addOperation2Board("node just bounced an operation");
            for (NodeConnection2Others node : nodeList) {
                System.out.println("Mandando mensjae a nodo");
                node.handleBounceMsg(outMsg);
            }
        }
    }

    // we send the result to all workers
    private void handleResult(String[] tokens, boolean fromNode) throws IOException {
        boolean isThereAClient = false;

        String sendTo = tokens[1];
        String result = tokens[2];
        String outMsg = "result " + sendTo + " " + result + "\n";
        System.out.println("handleResult: " + result);
        controller.addOperation2Board(result);

        ArrayList<NodeWorker> workerList = nodeMOM.getWorkerList();
        for (NodeWorker worker : workerList) {
            /*if (sendTo.equalsIgnoreCase(worker.getLogin())) {*/
            worker.send(outMsg);

            controller.addOperation2Board("A server just answered to " + sendTo);
            System.out.println("A server just answered to " + sendTo);

            isThereAClient = true;
        }

        if (!isThereAClient && !fromNode) {
            ArrayList<NodeConnection2Others> nodeList = nodeMOM.getNodeList();
            controller.addOperation2Board("node just bounced a result");
            for (NodeConnection2Others node : nodeList) {
                System.out.println("Node" + node.serverPort + " just bounced");
                node.handleBounceMsg(outMsg);
            }
        }
    }

    void send(String msg) throws IOException {
        if (login != null) {
            outputStream.write(msg.getBytes());
        }
    }
}

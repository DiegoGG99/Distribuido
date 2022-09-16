package diego.middle;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

//TO-DO, add a socket so we can send
public class NodeMOM extends Thread {
    private final int nodePort;
    // list of servers and clients in this node
    private final ArrayList<NodeWorker> workerList = new ArrayList<>();
    private final ArrayList<NodeConnection2Others> nodeList = new ArrayList<>();

    private NodeOpRegisterController controller;

    // Constructor
    public NodeMOM(int nodePort) {
        this.nodePort = nodePort;
    }

    // setter
    public void setController(NodeOpRegisterController controller) {
        this.controller = controller;
    }

    // getter
    public ArrayList<NodeWorker> getWorkerList() {
        return workerList;
    }

    public ArrayList<NodeConnection2Others> getNodeList() {
        return nodeList;
    }

    @Override
    public void run() {
        if (nodePort > 10000) {
            int x = 0;
            for (int i = 10000; i < nodePort; i++) {
                try {
                    NodeConnection2Others node = new NodeConnection2Others("localhost", (i + x));
                    node.connect();
                    node.setController(controller);
                    node.setWorkerList(workerList);
                    if (node.login("node" + nodePort, "node" + nodePort)) {
                        nodeList.add(node);
                        controller.addNode2List("node" + i,true);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            ServerSocket serverSocket = new ServerSocket(nodePort);

            while (true) {
                controller.addOperation2Board("Node ready to accept client connection..");
                //it stops util it makes a connection
                Socket clientSocket = serverSocket.accept();
                controller.addOperation2Board("Connection accepted from " + clientSocket);
                NodeWorker worker = new NodeWorker(this, clientSocket);
                //it's better if the worker handle the client socket IDK
                workerList.add(worker);
                worker.setController(controller);
                worker.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // we remove the worker from the list
    public void removeWorker(NodeWorker nodeWorker) {
        workerList.remove(nodeWorker);
    }

    public void removeNode(NodeConnection2Others nodeConnection2Others) {
        nodeList.remove(nodeConnection2Others);
    }
}

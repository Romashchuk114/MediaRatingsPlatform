package at.fhtw.swen1.mrp.presentation.httpserver.server;


import at.fhtw.swen1.mrp.presentation.httpserver.utils.RequestHandler;
import at.fhtw.swen1.mrp.presentation.httpserver.utils.Router;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Server {
    private int port;
    private Router router;
    private HttpServer httpServer;

    public Server(int port, Router router) {
        this.port = port;
        this.router = router;
    }

    public void start() throws IOException {

        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.createContext("/", new RequestHandler(router));
        httpServer.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        httpServer.start();

        System.out.println("Start http-server...");
        System.out.println("http-server running at: http://localhost:" + this.port);
    }
}

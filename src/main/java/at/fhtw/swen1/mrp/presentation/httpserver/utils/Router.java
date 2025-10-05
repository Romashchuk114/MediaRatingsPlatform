package at.fhtw.swen1.mrp.presentation.httpserver.utils;


import at.fhtw.swen1.mrp.presentation.controller.Controller;

import java.util.HashMap;
import java.util.Map;

public class Router {
    private Map<String, Controller> controllerRegistry;

    public Router() {
        this.controllerRegistry = new HashMap<>();
    }

    public void addController(String route, Controller controller) {
        this.controllerRegistry.put(route, controller);
    }

    public void removeController(String route) {
        this.controllerRegistry.remove(route);
    }

    public Controller resolve(String route) {
        return this.controllerRegistry.get(route);
    }
}

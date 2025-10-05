package at.fhtw.swen1.mrp.presentation.controller;

import at.fhtw.swen1.mrp.presentation.httpserver.server.Request;
import at.fhtw.swen1.mrp.presentation.httpserver.server.Response;

public interface Controller {
    Response handleRequest(Request request);
}

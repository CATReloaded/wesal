package controllers;

import actors.ClientActor;
import play.*;
import play.mvc.*;
import com.fasterxml.jackson.databind.JsonNode;

public class Application extends Controller {

    public static Result index() {
        return ok("Go Away!");
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static WebSocket<JsonNode> websocket() {
        Logger.info("New WebSocket Connection from:" + request().remoteAddress());
        return WebSocket.withActor(ClientActor::props);
    }

}

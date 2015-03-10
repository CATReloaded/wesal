package utils;

import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Optional;


public class ProtocolCodec {
    public static final String INITIALIZE = "initialize";

    public static ObjectNode formatError(int code, String message) {
        ObjectNode error = Json.newObject();
        error.put("code", code);
        error.put("message", message);
        return error;
    }

    public static ObjectNode initialize(JsonNode request) {
        ObjectNode result = Json.newObject();
        if (request.has("tag")) {
            result.put("evt", "initialized");
            result.put("tag", request.get("tag"));
        } else {
            result.put("evt", "disconnected");
            result.putArray("errors").add(formatError(400, "tag must be specified"));
        }
        return result;
    }

    public static ObjectNode initializeRequiredResponse(Optional<JsonNode> request) {
        ObjectNode result = Json.newObject();
        result.put("evt", "disconnected");
        if (request.isPresent() && request.get().has("tag")) {
            result.put("tag", request.get().get("tag"));
        }
        result.putArray("errors").add(formatError(400, "command must be initialize"));
        return result;
    }

    public static ObjectNode invalidCommand(Optional<JsonNode> request) {
        ObjectNode result = Json.newObject();
        result.put("evt", "error");
        if (request.isPresent() && request.get().has("tag")) {
            result.put("tag", request.get().get("tag"));
        }
        result.putArray("errors").add(formatError(400, "unknown command"));
        return result;
    }

    public static ObjectNode cmdMissing(Optional<JsonNode> request) {
        ObjectNode result = Json.newObject();
        result.put("evt", "disconnected");
        if (request.isPresent() && request.get().has("tag")) {
            result.put("tag", request.get().get("tag"));
        }
        result.putArray("errors").add(formatError(400, "cmd must be present"));
        return result;
    }
}


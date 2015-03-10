package actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Procedure;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.Logger;
import play.libs.Json;
import utils.ProtocolCodec;
import java.util.Optional;
import java.util.Random;

public class ClientActor extends UntypedActor {
    public static Props props(ActorRef out) {
        return Props.create(ClientActor.class, out);
    }
    private Optional<String> userId = Optional.empty();

    private final ActorRef out;
    public ClientActor(ActorRef out) {
        Logger.info("New ClientActor created: " + self().path());
        this.out = out;
    }

    private void replyWith(ObjectNode response) {
        out.tell(response, self());
    }

    //todo refactor use lambda
    Procedure<Object> initializedReceive = new Procedure<Object>() {
        @Override
        public void apply(Object message) {
            if (message instanceof JsonNode) {
                JsonNode jsonRequest = (JsonNode) message;
                if (jsonRequest.has("cmd")) {
                    String command = jsonRequest.get("cmd").textValue();
                    switch (command) {
                        case "me":
                            //todo put real implementation please
                            ObjectNode result = Json.newObject();
                            result.put("me", userId.get());
                            replyWith(result);
                            break;
                        default:
                            replyWith(ProtocolCodec.invalidCommand(Optional.of(jsonRequest)));
                    }
                } else {
                    replyWith(ProtocolCodec.cmdMissing(Optional.of(jsonRequest)));
                }
            }
        }
    };

    //todo refactor use lambda akka api
    public void onReceive(Object message) throws Exception {
        if (message instanceof JsonNode) {
            JsonNode jsonRequest = (JsonNode) message;

            if (jsonRequest.has("cmd")) {
                String command = jsonRequest.get("cmd").textValue();
                switch (command) {
                    case "initialize":
                        replyWith(ProtocolCodec.initialize(jsonRequest));
                        userId = Optional.of("Ahmed-" + (new Random()).nextInt());
                        getContext().become(initializedReceive);
                        break;
                    default:
                        replyWith(ProtocolCodec.initializeRequiredResponse(Optional.of(jsonRequest)));
                }

            } else {
                replyWith(ProtocolCodec.cmdMissing(Optional.of(jsonRequest)));
            }
        }
    }
}

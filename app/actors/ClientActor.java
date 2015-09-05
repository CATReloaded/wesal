package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;;
import akka.japi.pf.ReceiveBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.Logger;
import play.libs.Json;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;
import utils.ProtocolCodec;

import java.util.Optional;
import java.util.Random;

public class ClientActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    private final ActorRef out;
    private Optional<String> userId = Optional.empty();
    // Become  akka actors java lambda api
    public PartialFunction<Object, BoxedUnit> initializedReceive =
            ReceiveBuilder.
                    match(JsonNode.class, jsonRequest -> {
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
                    }).
                    matchAny(o -> log.info("received unknown message")).build();

    public ClientActor(ActorRef out) {
        Logger.info("New ClientActor created: " + self().path());
        this.out = out;
        // receive akka actors java lambda api
        receive(ReceiveBuilder.
                        match(JsonNode.class, jsonRequest -> {
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
                        }).
                        matchAny(o -> log.info("received unknown message")).build()
        );
    }

    public static Props props(ActorRef out) {
        return Props.create(ClientActor.class, out);
    }

    private void replyWith(ObjectNode response) {
        out.tell(response, self());
    }

}

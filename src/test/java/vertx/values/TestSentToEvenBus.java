package vertx.values;

import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import jsonvalues.JsArray;
import jsonvalues.JsInt;
import jsonvalues.JsObj;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import vertx.values.codecs.RegisterJsValuesCodecs;

@ExtendWith(VertxExtension.class)
public class TestSentToEvenBus {


    @Test
    public void test(Vertx vertx, VertxTestContext testContext) {

        Checkpoint codecsVerticleDeployed = testContext.checkpoint();
        Checkpoint bounceConsumerRegistered = testContext.checkpoint();
        Checkpoint sameObjInstanceReceived = testContext.checkpoint();
        Checkpoint sameArrayInstanceReceived = testContext.checkpoint();

        vertx.deployVerticle(new RegisterJsValuesCodecs(), r -> codecsVerticleDeployed.flag());

        vertx.eventBus()
             .consumer("bounce", message -> message.reply(message.body()))
             .completionHandler(it-> bounceConsumerRegistered.flag());

        JsObj objMessage = JsObj.of("a", JsInt.of(1));
        vertx.eventBus().<JsObj>request("bounce", objMessage, resp-> {
            JsObj objResp = resp.result().body();
            //same instance, the message is not copied before sending to EB
            testContext.verify(()->Assertions.assertSame(objResp,objMessage));
            sameObjInstanceReceived.flag();
        });

        JsArray arrayMessage = JsArray.of(1,2,3);
        vertx.eventBus().<JsArray>request("bounce", arrayMessage, resp-> {
            JsArray arrResp = resp.result().body();
            //same instance, the message is not copied before sending to EB
            testContext.verify(()->Assertions.assertSame(arrResp,arrayMessage));
            sameArrayInstanceReceived.flag();
        });


    }
}

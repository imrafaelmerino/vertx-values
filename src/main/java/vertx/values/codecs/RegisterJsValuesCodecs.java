package vertx.values.codecs;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import jsonvalues.JsArray;
import jsonvalues.JsObj;


/**
 * Verticle to register the codecs to be able to send json values ({@link JsObj} and {@link JsArray}) to the even bus.
 * If this verticle is deployed more than once, you'll receive a failure saying that
 * the codecs has already been registered
 */
public class RegisterJsValuesCodecs extends AbstractVerticle {

    @Override
    public void start(final Promise<Void> promise) {
        try {
            vertx.eventBus()
                    .registerDefaultCodec(JsObj.class, JsObjMessageCodec.INSTANCE);

            vertx.eventBus()
                    .registerDefaultCodec(JsArray.class, JsArrayMessageCodec.INSTANCE);

            promise.complete();

        } catch (Exception e) {
            promise.fail(e);
        }
    }
}

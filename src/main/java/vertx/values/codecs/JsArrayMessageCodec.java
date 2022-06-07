package vertx.values.codecs;

import io.vertx.core.eventbus.MessageCodec;
import jsonvalues.JsArray;

/**
 * Codec that allows to send {@link JsArray} as messages
 */
public class JsArrayMessageCodec extends JsMessageCodec<JsArray> implements MessageCodec<JsArray, JsArray> {

    public static final JsArrayMessageCodec INSTANCE = new JsArrayMessageCodec();

    private JsArrayMessageCodec() {}

    @Override
    public JsArray parse(final String json) {
        return JsArray.parse(json);
    }

    @Override
    public String name() {
        return "json-array-value";
    }

}

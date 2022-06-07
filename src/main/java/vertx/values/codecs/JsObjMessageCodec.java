package vertx.values.codecs;

import io.vertx.core.eventbus.MessageCodec;
import jsonvalues.JsObj;

/**
 * Codec that allows to send {@link JsObj} as messages
 */
public class JsObjMessageCodec extends JsMessageCodec<JsObj> implements MessageCodec<JsObj, JsObj> {

    public static final JsObjMessageCodec INSTANCE = new JsObjMessageCodec();

    private JsObjMessageCodec() {
    }

    @Override
    protected JsObj parse(String json) {
        return JsObj.parse(json);
    }

    @Override
    public String name() {
        return "json-obj-value";
    }

}

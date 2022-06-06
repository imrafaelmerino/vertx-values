package vertx.values.codecs;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.DecodeException;
import jsonvalues.JsObj;
import jsonvalues.MalformedJson;

/**
 Codec that allows to send {@link JsObj} as messages
 */
public class JsObjMessageCodec implements MessageCodec<JsObj, JsObj> {

  public static final JsObjMessageCodec INSTANCE = new JsObjMessageCodec();

  private JsObjMessageCodec() {}

  @Override
  public JsObj decodeFromWire(int pos,
                              final Buffer buffer
                             ) {
    try {
      int length = buffer.getInt(pos);
      pos += 4;
      return JsObj.parse(buffer.getString(pos,
                                          pos + length
                                         )
                        );
    } catch (MalformedJson exp) {
      throw new DecodeException("error decoding jsonvalues.JsObj",exp);
    }
  }

  @Override
  public void encodeToWire(final Buffer buffer,
                           final JsObj obj
                          ) {
    byte[] encoded = obj.serialize();
    buffer.appendInt(encoded.length);
    buffer.appendBytes(encoded);
  }

  @Override
  public String name() {
    return "json-obj-value";
  }

  @Override
  public byte systemCodecID() {
    return -1;
  }

  @Override
  public JsObj transform(final JsObj obj) {
    return obj;
  }
}

package vertx.values.codecs;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.DecodeException;
import jsonvalues.spec.JsParserException;
import jsonvalues.Json;

import static java.util.Objects.requireNonNull;

abstract class JsMessageCodec<J extends Json<J>> {

    public void encodeToWire(final Buffer buffer,
                             final J json
    ) {
        byte[] encoded = requireNonNull(json).serialize();
        requireNonNull(buffer).appendInt(encoded.length);
        buffer.appendBytes(encoded);
    }


    public J decodeFromWire(int pos,
                            final Buffer buffer
    ) {
        try {
            int length = requireNonNull(buffer).getInt(pos);
            pos += 4;
            return parse(buffer.getString(pos, pos + length));
        } catch (JsParserException exp) {
            throw new DecodeException("error decoding json", exp);
        }
    }

    protected abstract J parse(final String json);

    public J transform(final J json) {
        return json;
    }

    public byte systemCodecID() {
        return -1;
    }
}

package vertx.values.codecs;

import fun.gen.Gen;
import io.vertx.core.buffer.Buffer;
import jsonvalues.JsArray;
import jsonvalues.JsNull;
import jsonvalues.JsObj;
import jsonvalues.gen.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

public class TestJsArrayCodec {

    @Test
    public void testEncodeAndDecode() {

        JsObjGen xs =
                JsObjGen.of(
                                "a", JsStrGen.biased(0, 10),
                                "b", JsIntGen.biased(),
                                "c", JsLongGen.biased(),
                                "d", JsBoolGen.arbitrary(),
                                "e", JsInstantGen.arbitrary(0, Integer.MAX_VALUE),
                                "f", JsArrayGen.arbitrary(JsIntGen.arbitrary(0, 10), 10,10),
                                "g", JsBinaryGen.arbitrary(0, 100),
                                "h", JsBigDecGen.arbitrary(),
                                "i", JsBigIntGen.biased(25),
                                "j", Gen.cons(JsArray.empty()
                                )
                        )
                        .withAllOptKeys()
                        .withAllNullValues();

        Gen<JsArray> ys = JsTupleGen.of(
                JsStrGen.biased(0, 10),
                JsIntGen.biased(),
                JsLongGen.biased(),
                JsBigDecGen.biased(),
                JsBigIntGen.biased(25),
                JsBoolGen.arbitrary(),
                Gen.cons(JsObj.empty()),
                Gen.cons(JsNull.NULL),
                xs,
                xs
        );


        Predicate<JsArray> encodeThenDecodeReturnsSameObj = o -> {
            Buffer buffer = Buffer.buffer();

            JsArrayMessageCodec.INSTANCE.encodeToWire(buffer, o);

            JsArray obj = JsArrayMessageCodec.INSTANCE.decodeFromWire(0, buffer);


            return o.equals(obj);
        };

        Assertions.assertTrue(
                ys.sample(100_000)
                        .allMatch(encodeThenDecodeReturnsSameObj)
        );
    }
}

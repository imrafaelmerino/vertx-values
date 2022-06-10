package vertxvalues.benchmark;


import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jsonvalues.JsArray;
import jsonvalues.JsObj;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.infra.Blackhole;
import vertx.values.codecs.RegisterJsValuesCodecs;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static vertxvalues.benchmark.JsonExample.PERSON;


@BenchmarkMode(Mode.Throughput)
public class SendThreeMessageToEventBus {
    private static final JsonObject mutableJsonObj =
            new JsonObject(PERSON);
    private static final JsonArray mutableJsonArray =
            new JsonArray(Arrays.asList(mutableJsonObj, mutableJsonObj, mutableJsonObj, mutableJsonObj));
    private static final JsObj immutableJsonObj =
            JsObj.parse(PERSON);
    private static final JsArray immutableJsonArray =
            JsArray.of(immutableJsonObj, immutableJsonObj, immutableJsonObj, immutableJsonObj);
    static Vertx vertx;

    {
        init();
    }


    public static void init() {
        vertx = Vertx.vertx();

        String id = vertx
                .deployVerticle(new RegisterJsValuesCodecs())
                .toCompletionStage()
                .toCompletableFuture()
                .join();

        System.out.println("Deployed verticle" + id);

        await(vertx.eventBus().consumer("bounce", m -> m.reply(m.body())),
                1,
                TimeUnit.SECONDS);
    }

    public static void await(MessageConsumer<?> consumer,
                             int time,
                             TimeUnit unit) {
        try {
            CountDownLatch latch = new CountDownLatch(1);
            consumer.completionHandler(it -> {
                latch.countDown();
                System.out.println("Registered consumer");
            });
            boolean ended = latch.await(time, unit);
            if (!ended) throw new RuntimeException("CountDownLatch didn't count to zero");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @Benchmark
    public void vertx_values_obj(Blackhole blackHole) {
        blackHole.consume(
                vertx.eventBus().request("bounce", immutableJsonObj)
                        .flatMap(message -> vertx.eventBus()
                                .request("bounce", message.body()))
                        .flatMap(message -> vertx.eventBus()
                                .request("bounce", message.body()))
                        .toCompletionStage()
                        .toCompletableFuture()
                        .join()
        );
    }

    @Benchmark
    public void vertx_values_array(Blackhole blackHole) {
        blackHole.consume(
                vertx.eventBus()
                        .request("bounce", immutableJsonArray)
                        .flatMap(message -> vertx.eventBus()
                                .request("bounce", message.body()))
                        .flatMap(message -> vertx.eventBus()
                                .request("bounce", message.body()))
                        .toCompletionStage()
                        .toCompletableFuture()
                        .join()
        );
    }


    @Benchmark
    public void vertx_jackson_obj(Blackhole blackHole) {
        blackHole.consume(vertx.eventBus().request("bounce", mutableJsonObj)
                .flatMap(message -> vertx.eventBus()
                        .request("bounce", message.body()))
                .flatMap(message -> vertx.eventBus()
                        .request("bounce", message.body()))
                .toCompletionStage()
                .toCompletableFuture()
                .join());

    }

    @Benchmark
    public void vertx_jackson_array(Blackhole blackHole) {
        blackHole.consume(vertx.eventBus()
                .request("bounce", mutableJsonArray)
                .flatMap(message -> vertx.eventBus()
                        .request("bounce", message.body()))
                .flatMap(message -> vertx.eventBus()
                        .request("bounce", message.body()))
                .toCompletionStage()
                .toCompletableFuture()
                .join()
        );

    }


}
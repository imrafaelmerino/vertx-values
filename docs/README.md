
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=imrafaelmerino_vertx-values&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=imrafaelmerino_vertx-values)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=imrafaelmerino_vertx-values&metric=alert_status)](https://sonarcloud.io/dashboard?id=imrafaelmerino_vertx-values)
[![SonarCloud Coverage](https://sonarcloud.io/api/project_badges/measure?project=imrafaelmerino_vertx-values&metric=coverage)](https://sonarcloud.io/component_measures/metric/coverage/list?id=imrafaelmerino_vertx-values)
[![Maven](https://img.shields.io/maven-central/v/com.github.imrafaelmerino/vertx-json-values/0.1)](https://search.maven.org/artifact/com.github.imrafaelmerino/vertx-json-values/0.1/jar)


- [Goal](#goal)
- [Explanation](#exp)
- [How-to](#howto)
- [Installation](#inst)

## <a name="goal"><a/> Goal
It’s a convention and common practice in Vert.x to send messages as JSON.
JSON is very easy to create, read and parse in all the languages that
Vert.x supports, so it has become a kind of lingua franca for Vert.x.

The problem is that, every time a message of type [JsonObject or JsonArray](https://vertx.io/docs/apidocs/io/vertx/core/json/package-summary.html) 
is sent across the Event Bus, Vertx has to make a copy of the message. The bigger the JSON, 
the worse the impact on performance. Moreover, it puts a lot of pressure on the Garbage Collector.
**vertx-values solves this adding support to be able to send the immutable JSON from
[json-values](https://github.com/imrafaelmerino/json-values)**. json-values is a truly 
immutable JSON implemented with persistent data structures. json-values also has a richer 
API than Vertx implementation.

## <a name="exp"><a/> Explanation

Every type (_Integer_, _String_, _JsonObject_, _JsonArray_, _Buffer_ etc.) that can be sent 
across the Event Bus has an associated [MessageCodec](https://vertx.io/docs/apidocs/io/vertx/core/eventbus/MessageCodec.html) where it's defined how to serialize 
and deserialize messages of that type. A third method called _transform_ is also 
implemented. When a verticle sends a message to the event bus, Vertx intercepts 
that message and calls its codecs transform method.

Go to the source package [io.vertx.core.eventbus.impl.codecs](https://vertx.io/docs/apidocs/io/vertx/core/eventbus/impl/codecs/package-frame.html) 
to check out what types Vertx supports. The good thing is that you can define your own codecs 
to send messages of new types to the EB.

The default JSONs implemented in Vertx with **Jackson**, [JsonObject](https://vertx.io/docs/apidocs/io/vertx/core/json/JsonObject.html) and 
[JsonArray](https://vertx.io/docs/apidocs/io/vertx/core/json/JsonArray.html), have the codecs [JsonObjectMessageCodec](https://vertx.io/docs/apidocs/io/vertx/core/eventbus/impl/codecs/JsonObjectMessageCodec.html) 
and [JsonArrayMessageCodec](https://vertx.io/docs/apidocs/io/vertx/core/eventbus/impl/codecs/JsonArrayMessageCodec.html). Let's
take a look at their _transform_ method:


```java

// Vertx impl 
public JsonObject transform(JsonObject message) {
    return message.copy();
}

```


Since **Jackson** is not immutable at all, the _transform_ method of the JSON codecs 
has to make a copy of the message before sending it to the EB. Otherwise, we'd have 
a shared reference to an object among independent Verticles, which would be 
a nightmare and violates some of the most basis principles of the message-passing 
architectures.

As I pointed out before, making a copy every time a message is sent is inefficient and put more pressure to 
the Garbage Collector, especially if you have a large number of Verticles communicating one to
each other.

vertx-values provides a codec to send [json-values](https://github.com/imrafaelmerino/json-values) across the EB. 
Take a look at the transform method of its codecs:

```java
// vertx-effect impl
public JsObj transform(final JsObj message) {
   return message;
}
```

**As you can see, it returns the same message without making any copy**.
And if that was not enough, immutable data structures have a lot of benefits, 
especially in concurrent programs and architectures based on the actor model like Vertx.


## <a name="howto"><a/> How to


## <a name="inst"><a/> Installation

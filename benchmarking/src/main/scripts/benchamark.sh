#!/usr/bin/env bash
VERSION=0.5
NOW=$(date +"%m-%d-%Y-%T" | tr ':' '_')

java -Xms256m \
-jar releases/vertx-json-values-benchmark-${VERSION}.jar \
-rff results/one-message-output-${VERSION}-${NOW}.json \
-rf json \
vertxvalues\.benchmark\.SendMessageToEventBus


java -Xms256m \
-jar releases/vertx-json-values-benchmark-${VERSION}.jar \
-rff results/two-message-output-${VERSION}-${NOW}.json \
-rf json \
vertxvalues\.benchmark\.SendTwoMessageToEventBus

java -Xms256m \
-jar releases/vertx-json-values-benchmark-${VERSION}.jar \
-rff results/three-message-output-${VERSION}-${NOW}.json \
-rf json \
vertxvalues\.benchmark\.SendThreeMessageToEventBus


mv /cargamasiva/script_delta/load-sat-customers-3.4.0.0.jar /cargamasiva/script_delta/old/oldjars/
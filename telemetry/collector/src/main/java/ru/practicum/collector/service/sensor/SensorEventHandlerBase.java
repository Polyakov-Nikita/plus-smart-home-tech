package ru.practicum.collector.service.sensor;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import ru.practicum.collector.kafka.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Instant;

@RequiredArgsConstructor
public abstract class SensorEventHandlerBase<P extends SpecificRecordBase> implements SensorEventHandler {
    private static final String TOPIC = "telemetry.sensors.v1";

    private final KafkaEventProducer producer;

    protected abstract P getPayload(SensorEventProto event);

    @Override
    public void handle(SensorEventProto event) {
        String hubId = event.getHubId();
        Timestamp timestamp = event.getTimestamp();
        Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
        SensorEventAvro eventAvro = SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(instant)
                .setPayload(getPayload(event))
                .build();
        producer.send(TOPIC, instant, hubId, eventAvro);
    }
}

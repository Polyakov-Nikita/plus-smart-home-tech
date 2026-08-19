package ru.practicum.collector.service.sensor;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.practicum.collector.dto.sensor.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@RequiredArgsConstructor
public abstract class SensorEventHandlerBase<P extends SpecificRecordBase> implements SensorEventHandler {
    private static final String TOPIC = "telemetry.sensors.v1";

    protected final Producer<String, SpecificRecordBase> producer;

    protected abstract P getPayload(SensorEvent event);

    @Override
    public void handle(SensorEvent event) {
        SensorEventAvro eventAvro = SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(getPayload(event))
                .build();
        producer.send(new ProducerRecord<>(TOPIC, eventAvro));
    }
}

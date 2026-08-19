package ru.practicum.collector.service.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.collector.dto.sensor.LightSensorEvent;
import ru.practicum.collector.dto.sensor.SensorEvent;
import ru.practicum.collector.dto.sensor.SensorTypeNames;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;

@Component(value = SensorTypeNames.LIGHT_SENSOR_EVENT)
@SuppressWarnings("unused")
public class LightSensorEventHandler extends SensorEventHandlerBase<LightSensorAvro> {
    @Autowired
    public LightSensorEventHandler(Producer<String, SpecificRecordBase> producer) {
        super(producer);
    }

    @Override
    protected LightSensorAvro getPayload(SensorEvent event) {
        LightSensorEvent lightSensorEvent = (LightSensorEvent) event;
        return LightSensorAvro.newBuilder()
                .setLinkQuality(lightSensorEvent.getLinkQuality())
                .setLuminosity(lightSensorEvent.getLuminosity())
                .build();
    }
}

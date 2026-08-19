package ru.practicum.collector.service.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.collector.dto.sensor.SensorEvent;
import ru.practicum.collector.dto.sensor.SensorTypeNames;
import ru.practicum.collector.dto.sensor.SwitchSensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

@Component(value = SensorTypeNames.SWITCH_SENSOR_EVENT)
@SuppressWarnings("unused")
public class SwitchSensorEventHandler extends SensorEventHandlerBase<SwitchSensorAvro> {
    @Autowired
    public SwitchSensorEventHandler(Producer<String, SpecificRecordBase> producer) {
        super(producer);
    }

    @Override
    protected SwitchSensorAvro getPayload(SensorEvent event) {
        SwitchSensorEvent switchSensorEvent = (SwitchSensorEvent) event;
        return SwitchSensorAvro.newBuilder()
                .setState(switchSensorEvent.isState())
                .build();
    }
}

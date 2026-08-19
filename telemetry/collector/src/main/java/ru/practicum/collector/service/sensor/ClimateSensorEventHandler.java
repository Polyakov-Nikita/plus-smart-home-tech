package ru.practicum.collector.service.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.collector.dto.sensor.ClimateSensorEvent;
import ru.practicum.collector.dto.sensor.SensorEvent;
import ru.practicum.collector.dto.sensor.SensorTypeNames;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;

@Component(value = SensorTypeNames.CLIMATE_SENSOR_EVENT)
@SuppressWarnings("unused")
public class ClimateSensorEventHandler extends SensorEventHandlerBase<ClimateSensorAvro> {
    @Autowired
    public ClimateSensorEventHandler(Producer<String, SpecificRecordBase> producer) {
        super(producer);
    }

    @Override
    protected ClimateSensorAvro getPayload(SensorEvent event) {
        ClimateSensorEvent climateSensorEvent = (ClimateSensorEvent) event;
        return ClimateSensorAvro.newBuilder()
                .setTemperatureC(climateSensorEvent.getTemperatureC())
                .setHumidity(climateSensorEvent.getHumidity())
                .setCo2Level(climateSensorEvent.getCo2Level())
                .build();
    }
}

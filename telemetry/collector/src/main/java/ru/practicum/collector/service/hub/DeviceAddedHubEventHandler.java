package ru.practicum.collector.service.hub;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.collector.dto.hub.HubEvent;
import ru.practicum.collector.dto.hub.HubTypeNames;
import ru.practicum.collector.dto.hub.device.added.DeviceAddedHubEvent;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;

@Component(value = HubTypeNames.DEVICE_ADDED_EVENT)
@SuppressWarnings("unused")
public class DeviceAddedHubEventHandler extends HubEventHandlerBase<DeviceAddedEventAvro> {
    @Autowired
    public DeviceAddedHubEventHandler(Producer<String, SpecificRecordBase> producer) {
        super(producer);
    }

    @Override
    protected DeviceAddedEventAvro getPayload(HubEvent event) {
        DeviceAddedHubEvent deviceAddedHubEvent = (DeviceAddedHubEvent) event;
        return DeviceAddedEventAvro.newBuilder()
                .setId(deviceAddedHubEvent.getId())
                .setType(deviceAddedHubEvent.getDeviceType().getAvro())
                .build();
    }
}

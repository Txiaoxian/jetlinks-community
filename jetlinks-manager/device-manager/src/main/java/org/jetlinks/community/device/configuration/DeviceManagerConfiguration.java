package org.jetlinks.community.device.configuration;

import com.rabbitmq.client.ConnectionFactory;
import org.apache.kafka.clients.KafkaClient;
import org.hswebframework.ezorm.rdb.mapping.ReactiveRepository;
import org.hswebframework.ezorm.rdb.operator.DatabaseOperator;
import org.jetlinks.community.buffer.BufferProperties;
import org.jetlinks.community.device.entity.DeviceInstanceEntity;
import org.jetlinks.community.device.function.ReactorQLDeviceSelectorBuilder;
import org.jetlinks.community.device.function.RelationDeviceSelectorProvider;
import org.jetlinks.community.device.message.DeviceMessageConnector;
import org.jetlinks.community.device.message.writer.KafkaMessageWriterConnector;
import org.jetlinks.community.device.message.writer.RabbitMQMessageWriterConnector;
import org.jetlinks.community.device.message.writer.TimeSeriesMessageWriterConnector;
import org.jetlinks.community.device.service.data.*;
import org.jetlinks.community.rule.engine.executor.DeviceSelectorBuilder;
import org.jetlinks.community.rule.engine.executor.device.DeviceSelectorProvider;
import org.jetlinks.core.device.DeviceRegistry;
import org.jetlinks.core.device.session.DeviceSessionManager;
import org.jetlinks.core.event.EventBus;
import org.jetlinks.core.server.MessageHandler;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(DeviceDataStorageProperties.class)
public class DeviceManagerConfiguration {


    @Bean
    public DeviceSelectorProvider relationSelectorProvider() {
        return new RelationDeviceSelectorProvider();
    }

    @Bean
    public DeviceSelectorBuilder deviceSelectorBuilder(ReactiveRepository<DeviceInstanceEntity, String> deviceRepository,
                                                       DeviceRegistry deviceRegistry) {
        return new ReactorQLDeviceSelectorBuilder(deviceRegistry, deviceRepository);
    }

    @Bean
    public DeviceMessageConnector deviceMessageConnector(EventBus eventBus,
                                                         MessageHandler messageHandler,
                                                         DeviceSessionManager sessionManager,
                                                         DeviceRegistry registry) {
        return new DeviceMessageConnector(eventBus, registry, messageHandler, sessionManager);
    }

    @Bean
    @ConditionalOnProperty(prefix = "device.message.writer.time-series", name = "enabled", havingValue = "true", matchIfMissing = true)
    public TimeSeriesMessageWriterConnector timeSeriesMessageWriterConnector(DeviceDataService dataService) {
        return new TimeSeriesMessageWriterConnector(dataService);
    }


    @Configuration
    @ConditionalOnProperty(prefix = "jetlinks.device.storage", name = "enable-last-data-in-db", havingValue = "true")
    static class DeviceLatestDataServiceConfiguration {

        @Bean
        @ConfigurationProperties(prefix = "jetlinks.device.storage.latest.buffer")
        public BufferProperties deviceLatestDataServiceBufferProperties() {
            BufferProperties bufferProperties = new BufferProperties();
            bufferProperties.setFilePath("./data/device-latest-data-buffer");
            bufferProperties.setSize(1000);
            bufferProperties.setParallelism(1);
            bufferProperties.setTimeout(Duration.ofSeconds(1));
            return bufferProperties;
        }

        @Bean(destroyMethod = "destroy")
        public DatabaseDeviceLatestDataService deviceLatestDataService(DatabaseOperator databaseOperator) {
            return new DatabaseDeviceLatestDataService(databaseOperator,
                                                       deviceLatestDataServiceBufferProperties());
        }

    }

    @Bean
    @ConditionalOnProperty(
        prefix = "jetlinks.device.storage",
        name = "enable-last-data-in-db",
        havingValue = "false",
        matchIfMissing = true)
    @ConditionalOnMissingBean(DeviceLatestDataService.class)
    public DeviceLatestDataService deviceLatestDataService() {
        return new NonDeviceLatestDataService();
    }

    @Configuration
    @ConditionalOnClass(KafkaClient.class)
    @EnableConfigurationProperties(KafkaProperties.class)
    @ConditionalOnProperty(prefix = "device.message.writer.kafka", name = "enabled", havingValue = "true")
    static class KafkaMessageWriterConnectorConfiguration{

        @Bean
        @ConfigurationProperties(prefix = "device.message.writer.kafka")
        public KafkaMessageWriterConnector kafkaMessageWriterConnector(DeviceDataService dataService,
                                                                       KafkaProperties kafkaProperties) {
            return new KafkaMessageWriterConnector(dataService, kafkaProperties);
        }

    }

    @Configuration
    @ConditionalOnClass(ConnectionFactory.class)
    @EnableConfigurationProperties(RabbitProperties.class)
    @ConditionalOnProperty(prefix = "device.message.writer.rabbitmq", name = "enabled", havingValue = "true")
    static class RabbitMQMessageWriterConnectorConfiguration{

        @Bean
        @ConfigurationProperties(prefix = "device.message.writer.rabbitmq")
        public RabbitMQMessageWriterConnector rabbitMQMessageWriterConnector(DeviceDataService dataService,
                                                                             Scheduler scheduler,
                                                                             RabbitProperties rabbitProperties) {
            RabbitMQMessageWriterConnector writerConnector = new RabbitMQMessageWriterConnector(dataService, rabbitProperties);
            writerConnector.setScheduler(scheduler);
            return writerConnector;
        }

    }

}

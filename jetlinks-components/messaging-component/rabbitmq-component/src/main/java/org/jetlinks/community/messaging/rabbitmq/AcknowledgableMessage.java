package org.jetlinks.community.messaging.rabbitmq;

public interface AcknowledgableMessage extends AmqpMessage {

    void ack();

    void nack(boolean requeue);
}

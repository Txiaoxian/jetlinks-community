package org.jetlinks.community.messaging.kafka;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;


public interface KafkaProducer {

    Mono<Void> send(Publisher<Message> publisher);

    void shutdown();


}

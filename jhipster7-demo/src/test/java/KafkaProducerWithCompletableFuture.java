import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KafkaProducerWithCompletableFuture {
    private KafkaProducer<String, String> producer;

    public KafkaProducerWithCompletableFuture() {
        // Kafka Producer configuration properties
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Replace with your broker address
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");

        // Initialize the Kafka producer
        this.producer = new KafkaProducer<>(props);
    }

    public CompletableFuture<RecordMetadata> sendMessageAsync(String topic, String key, String value) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);

        // Create a CompletableFuture to handle the async response
        CompletableFuture<RecordMetadata> completableFuture = new CompletableFuture<>();

        // Send the message asynchronously and handle callback
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                completableFuture.completeExceptionally(exception); // Complete exceptionally on failure
            } else {
                completableFuture.complete(metadata); // Complete successfully on success
            }
        });

        return completableFuture;
    }

    public static void main(String[] args) {
        KafkaProducerWithCompletableFuture kafkaProducer = new KafkaProducerWithCompletableFuture();

        // Send a message and use CompletableFuture to handle the response
        CompletableFuture<RecordMetadata> future = kafkaProducer.sendMessageAsync("my-topic", "key1", "Hello with CompletableFuture!");

        future.thenAccept(metadata -> {
            System.out.printf("Message sent to topic %s with offset %d%n", metadata.topic(), metadata.offset());
        }).exceptionally(e -> {
            System.err.println("Failed to send message: " + e.getMessage());
            return null;
        });

        ExecutorService executorService = Executors.newFixedThreadPool(8);

        // Ensure the application runs until the future completes
        try {
            future.get(); // Wait for the CompletableFuture to complete
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error waiting for message send completion: " + e.getMessage());
        } finally {
            kafkaProducer.producer.close(); // Close producer after all messages are sent
        }
    }
}

package poc.ignite.properties;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "kafka")
@Setter
@Getter
public class KafkaProperties {
	private Map<String, String> kafkaConsumer;
	private Map<String, String> metaData;
}

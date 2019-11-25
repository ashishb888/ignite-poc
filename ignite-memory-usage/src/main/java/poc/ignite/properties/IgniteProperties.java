package poc.ignite.properties;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "ignite")
@Setter
@Getter
public class IgniteProperties {
	private Map<String, String> cache;
	private Map<String, String> other;
	private Map<String, Integer> threads;
}

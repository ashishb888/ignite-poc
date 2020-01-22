package poc.ignite.properties;

import java.util.List;
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
	private Map<String, Integer> tcpDiscoverySpi;
	private Map<String, Integer> tcpCommunicationSpi;
	private List<String> ips;
	private Map<String, String> defaultRegion;
	private Map<String, String> caches;
}

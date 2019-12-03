package poc.ignite.serialization;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.util.SerializationUtils;

import poc.ignite.domain.TestDomain1;

public class TestDomain1Deserializer implements Deserializer<TestDomain1> {

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {

	}

	@Override
	public TestDomain1 deserialize(String topic, byte[] data) {
		return (TestDomain1) SerializationUtils.deserialize(data);
	}

	@Override
	public void close() {

	}

}

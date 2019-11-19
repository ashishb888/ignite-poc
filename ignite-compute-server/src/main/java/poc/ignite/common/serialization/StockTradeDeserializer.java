package poc.ignite.common.serialization;

import java.io.IOException;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import poc.ignite.domain.StockTrade;

public class StockTradeDeserializer implements Deserializer<StockTrade> {

	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public StockTrade deserialize(String topic, byte[] data) {
		if (data == null)
			return null;

		try {
			return objectMapper.readValue(data, StockTrade.class);
		} catch (IOException e) {
			throw new SerializationException("Error while deserializing object from JSON", e);
		}
	}

}

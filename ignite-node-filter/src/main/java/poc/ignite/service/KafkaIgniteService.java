package poc.ignite.service;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import poc.ignite.domain.StockTrade;
import poc.ignite.domain.StockTradeKey;
import poc.ignite.filters.FirstNodeFilter;
import poc.ignite.filters.SecondNodeFilter;
import poc.ignite.properties.IgniteProperties;
import poc.ignite.properties.KafkaProperties;

@Service
@Slf4j
public class KafkaIgniteService {

	@Autowired
	private Ignite ignite;
	private IgniteCache<StockTradeKey, StockTrade> stockTradeCache;
	@Autowired
	private IgniteProperties ip;
	@Autowired
	private KafkaProperties kp;

	private void consume() {
		log.debug("consume service");

		IgniteDataStreamer<StockTradeKey, StockTrade> stockTradeStreamer = ignite
				.dataStreamer(stockTradeCache.getName());
		stockTradeStreamer.autoFlushFrequency(10);
		stockTradeStreamer.allowOverwrite(true);

		String topic = kp.getMetaData().get("topic");
		long poll = Long.valueOf(kp.getMetaData().get("poll"));

		Consumer<String, StockTrade> consumer = consumer();
		TopicPartition tp = new TopicPartition(topic, 0);
		List<TopicPartition> partitions = Arrays.asList(tp);

		consumer.assign(partitions);
		consumer.seekToBeginning(partitions);

		while (true) {
			ConsumerRecords<String, StockTrade> records = consumer.poll(Duration.ofMillis(poll));

			Map<StockTradeKey, StockTrade> entries = new HashMap<>();
			records.forEach(r -> {
				StockTrade st = r.value();
				StockTradeKey stk = new StockTradeKey(st.getSymbol(), st.getSeries(), st.getTimestamp());

				entries.put(stk, st);
			});

			if (!entries.isEmpty()) {
				stockTradeStreamer.addData(entries);
				stockTradeStreamer.flush();
			}
		}

	}

	private Properties configs() {
		log.debug("configs service");

		Properties configs = new Properties();

		kp.getKafkaConsumer().forEach((k, v) -> {
			configs.put(k, v);
		});

		return configs;
	}

	private Consumer<String, StockTrade> consumer() {
		log.debug("consumer service");

		return new KafkaConsumer<>(configs());
	}

	private void initCache() {
		log.debug("initCache service");

		CacheConfiguration<StockTradeKey, StockTrade> cacheConfig = new CacheConfiguration<>("stock-trade-cache");
		cacheConfig.setIndexedTypes(StockTradeKey.class, StockTrade.class);
		cacheConfig.setCacheMode(CacheMode.PARTITIONED);
		cacheConfig.setSqlSchema("sts");

		switch (ip.getOther().get("nodeName")) {
		case "FISRT":
			cacheConfig.setNodeFilter(new FirstNodeFilter());
			break;
		case "SECOND":
			cacheConfig.setNodeFilter(new SecondNodeFilter());
			break;
		}

		int backups = Integer.valueOf(ip.getCache().get("backups"));
		cacheConfig.setBackups(backups);

		ignite.addCacheConfiguration(cacheConfig);
		stockTradeCache = ignite.getOrCreateCache(cacheConfig.getName());
	}

	private void init() {
		log.debug("init service");

		initCache();
	}

	private void start() {
		log.debug("start service");

		init();
		consume();
	}

	public void main() {
		log.debug("main service");

		start();
	}
}

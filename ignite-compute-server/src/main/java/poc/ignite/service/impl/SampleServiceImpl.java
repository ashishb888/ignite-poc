package poc.ignite.service.impl;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.cache.Cache.Entry;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.services.ServiceContext;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import poc.ignite.domain.StockTrade;
import poc.ignite.domain.StockTradeKey;
import poc.ignite.properties.IgniteProperties;
import poc.ignite.properties.KafkaProperties;
import poc.ignite.service.SampleService;

@Service
@Slf4j
public class SampleServiceImpl implements SampleService {

	private static final long serialVersionUID = 8376376347919256160L;

	@Autowired
	private Ignite ignite;
	private IgniteCache<StockTradeKey, StockTrade> stockTradeCache;
	@Autowired
	private IgniteProperties ip;
	@Autowired
	private KafkaProperties kp;

	@Override
	public void cancel(ServiceContext ctx) {
		log.debug("cancel service");
	}

	@Override
	public void init(ServiceContext ctx) throws Exception {
		log.debug("init service");

		initCache();
	}

	@Override
	public void execute(ServiceContext ctx) throws Exception {
		log.debug("execute service");

		consume();
	}

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

		int backups = Integer.valueOf(ip.getCache().get("backups"));
		cacheConfig.setBackups(backups);

		ignite.addCacheConfiguration(cacheConfig);
		stockTradeCache = ignite.getOrCreateCache(cacheConfig.getName());
	}

	@Override
	public long call(String cacheName, String affKey) {
		log.info("k: " + affKey + ", cacheName: " + cacheName);

		IgniteCache<StockTradeKey, StockTrade> cache = ignite.cache(cacheName);
		SqlQuery<StockTradeKey, StockTrade> sqlQuery = new SqlQuery<>(StockTrade.class,
				"timestamp = ? and isin != ? limit ?");
		sqlQuery.setArgs(affKey, "From compute", 10);

		long entries = 0;

		try (QueryCursor<Entry<StockTradeKey, StockTrade>> queryCursor = cache.query(sqlQuery)) {
			for (Entry<StockTradeKey, StockTrade> entry : queryCursor) {
				StockTrade st = entry.getValue();
				st.setIsin("From compute");
				entries += 1;

				cache.put(entry.getKey(), st);
			}
		}

		log.info("entries: " + entries);

		return entries;
	}

	@Override
	public void run(String cacheName, String affKey) {
		log.debug("run service");

		IgniteCache<StockTradeKey, StockTrade> cache = ignite.cache(cacheName);// .withKeepBinary();
		SqlQuery<StockTradeKey, StockTrade> sqlQuery = new SqlQuery<>(StockTrade.class,
				"timestamp = ? and isin != ? limit ?");
		sqlQuery.setArgs(affKey, "From compute", 10);

		long entries = 0;

		try (QueryCursor<Entry<StockTradeKey, StockTrade>> queryCursor = cache.query(sqlQuery)) {
			for (Entry<StockTradeKey, StockTrade> entry : queryCursor) {
				StockTrade st = entry.getValue();

				st.setIsin("From compute");
				entries += 1;

				cache.put(entry.getKey(), st);
			}
		}

		log.debug("entries: " + entries);

	}

}

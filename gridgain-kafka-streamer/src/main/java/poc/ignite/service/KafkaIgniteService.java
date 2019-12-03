package poc.ignite.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map.Entry;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.stream.StreamMultipleTupleExtractor;
import org.apache.ignite.stream.StreamSingleTupleExtractor;
import org.apache.ignite.stream.kafka.KafkaStreamer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import poc.ignite.domain.TestDomain1;
import poc.ignite.domain.TestDomain2;
import poc.ignite.filters.DataNodeFilter;
import poc.ignite.filters.WorkerNodeFilter;
import poc.ignite.properties.IgniteProperties;
import poc.ignite.properties.KafkaProperties;

@Service
@Slf4j
public class KafkaIgniteService {

	@Autowired
	private Ignite ignite;
	private IgniteCache<Integer, String> cache;
	private IgniteCache<Integer, TestDomain1> td1Cache;
	private IgniteCache<Integer, TestDomain2> td2Cache;
	@Autowired
	private IgniteProperties ip;
	@Autowired
	private KafkaProperties kp;
	private AtomicInteger ai = new AtomicInteger(1);

	private void consume() {
		log.debug("consume service");

		KafkaStreamer<Integer, String> kStreamer = new KafkaStreamer<>();
		IgniteDataStreamer<Integer, String> streamer = ignite.dataStreamer(cache.getName());
		streamer.autoFlushFrequency(10);
		streamer.allowOverwrite(true);

		kStreamer.setIgnite(ignite);
		kStreamer.setStreamer(streamer);
		kStreamer.setTopic(Arrays.asList(kp.getMetaData().get("topic")));
		kStreamer.setThreads(4);
		kStreamer.setConsumerConfig(configs());

//		td1KStreamer.setSingleTupleExtractor(new StreamSingleTupleExtractor<ConsumerRecord, Integer, TestDomain1>() {
//
//			@Override
//			public Entry<Integer, TestDomain1> extract(ConsumerRecord record) {
//
//				return null;
//			}
//		});
//
		kStreamer.setMultipleTupleExtractor(new StreamMultipleTupleExtractor<ConsumerRecord, Integer, String>() {
			@Override
			public Map<Integer, String> extract(ConsumerRecord record) {
				Map<Integer, String> entries = new HashMap<Integer, String>();
				entries.put(ai.getAndIncrement(), (String) record.value());

				return entries;
			}
		});

		kStreamer.start();
//		td1KStreamer.stop();

	}

	private Properties configs() {
		log.debug("configs service");

		Properties configs = new Properties();

		kp.getKafkaConsumer().forEach((k, v) -> {
			configs.put(k, v);
		});

		return configs;
	}

	private void initCache() {
		log.debug("initCache service");

		int backups = Integer.valueOf(ip.getCache().get("backups"));

//		CacheConfiguration<Integer, TestDomain1> td1CacheConfig = new CacheConfiguration<>("td1-cache");
//		td1CacheConfig.setIndexedTypes(Integer.class, TestDomain1.class);
//		td1CacheConfig.setCacheMode(CacheMode.PARTITIONED);
//		td1CacheConfig.setSqlSchema("sts");
//		td1CacheConfig.setBackups(backups);
//		// td1CacheConfig.setNodeFilter(new DataNodeFilter());
//		td1CacheConfig.setNodeFilter(new WorkerNodeFilter());
//
//		ignite.addCacheConfiguration(td1CacheConfig);
//		td1Cache = ignite.getOrCreateCache(td1CacheConfig.getName());
//
//		CacheConfiguration<Integer, TestDomain2> td2CacheConfig = new CacheConfiguration<>("td2-cache");
//		td2CacheConfig.setIndexedTypes(Integer.class, TestDomain2.class);
//		td2CacheConfig.setCacheMode(CacheMode.PARTITIONED);
//		td2CacheConfig.setSqlSchema("sts");
//		td2CacheConfig.setBackups(backups);
//		td2CacheConfig.setNodeFilter(new WorkerNodeFilter());
//
//		ignite.addCacheConfiguration(td2CacheConfig);
//		td2Cache = ignite.getOrCreateCache(td2CacheConfig.getName());

		CacheConfiguration<Integer, String> cacheConfig = new CacheConfiguration<>("cache");
		cacheConfig.setIndexedTypes(Integer.class, String.class);
		cacheConfig.setCacheMode(CacheMode.PARTITIONED);
		cacheConfig.setSqlSchema("sts");
		cacheConfig.setBackups(backups);
		cacheConfig.setNodeFilter(new WorkerNodeFilter());

		ignite.addCacheConfiguration(cacheConfig);
		cache = ignite.getOrCreateCache(cacheConfig.getName());
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

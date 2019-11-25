package poc.ignite.service;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import poc.ignite.domain.TestDomain;
import poc.ignite.properties.IgniteProperties;

@Service
@Slf4j
public class KafkaIgniteService {

	@Autowired
	private Ignite ignite;
	private IgniteCache<Integer, TestDomain> testCache;
	@Autowired
	private IgniteProperties ip;

	private void consume() {
		log.debug("consume service");

		IgniteDataStreamer<Integer, TestDomain> testStreamer = ignite.dataStreamer(testCache.getName());
		testStreamer.autoFlushFrequency(10);
		testStreamer.allowOverwrite(true);

		int records = Integer.valueOf(ip.getOther().get("records"));

		log.debug("records: " + records);

		for (int i = 0; i < records; i++) {
			TestDomain td = new TestDomain(
					"s1: 2019-11-22 19:34:22.301  INFO 167392 --- [eout-worker-#71] org.apache.ignite.internal.IgniteKernal2019-11-22 19:34:22.301  INFO 167392 --- [eout-worker-#71] org.apache.ignite.internal.IgniteKernal",
					"s2: 2019-11-22 19:34:22.301  INFO 167392 --- [eout-worker-#71] org.apache.ignite.internal.IgniteKernal2019-11-22 19:34:22.301  INFO 167392 --- [eout-worker-#71] org.apache.ignite.internal.IgniteKernal1234");

			testCache.put(i, td);
		}

	}

	private void initCache() {
		log.debug("initCache service");

		CacheConfiguration<Integer, TestDomain> cacheConfig = new CacheConfiguration<>("test-cache");
		cacheConfig.setIndexedTypes(Integer.class, TestDomain.class);
		cacheConfig.setCacheMode(CacheMode.PARTITIONED);
		cacheConfig.setSqlSchema("sts");

		int backups = Integer.valueOf(ip.getCache().get("backups"));
		cacheConfig.setBackups(backups);

		ignite.addCacheConfiguration(cacheConfig);
		testCache = ignite.getOrCreateCache(cacheConfig.getName());
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

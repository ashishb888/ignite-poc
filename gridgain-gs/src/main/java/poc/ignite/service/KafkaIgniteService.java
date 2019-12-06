package poc.ignite.service;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import poc.ignite.domain.TestDomain1;
import poc.ignite.domain.TestDomain2;
import poc.ignite.filters.DataNodeFilter;
import poc.ignite.filters.WorkerNodeFilter;
import poc.ignite.properties.IgniteProperties;

@Service
@Slf4j
public class KafkaIgniteService {

	@Autowired
	private Ignite ignite;
	private IgniteCache<Integer, TestDomain1> td1Cache;
	private IgniteCache<Integer, TestDomain2> td2Cache;
	@Autowired
	private IgniteProperties ip;

	private void consume() {
		log.debug("consume service");

		IgniteDataStreamer<Integer, TestDomain1> testStreamer = ignite.dataStreamer(td1Cache.getName());
		testStreamer.autoFlushFrequency(10);
		testStreamer.allowOverwrite(true);

		int records = Integer.valueOf(ip.getOther().get("records"));

		log.debug("records: " + records);

		for (int i = 0; i < records; i++) {
			TestDomain1 td1 = new TestDomain1(
					"s1: 2019-11-22 19:34:22.301  INFO 167392 --- [eout-worker-#71] org.apache.ignite.internal.IgniteKernal2019-11-22 19:34:22.301  INFO 167392 --- [eout-worker-#71] org.apache.ignite.internal.IgniteKernal",
					"s2: 2019-11-22 19:34:22.301  INFO 167392 --- [eout-worker-#71] org.apache.ignite.internal.IgniteKernal2019-11-22 19:34:22.301  INFO 167392 --- [eout-worker-#71] org.apache.ignite.internal.IgniteKernal1234");

			TestDomain2 td2 = new TestDomain2(
					"s1: 2019-11-22 19:34:22.301  INFO 167392 --- [eout-worker-#71] org.apache.ignite.internal.IgniteKernal2019-11-22 19:34:22.301  INFO 167392 --- [eout-worker-#71] org.apache.ignite.internal.IgniteKernal",
					"s2: 2019-11-22 19:34:22.301  INFO 167392 --- [eout-worker-#71] org.apache.ignite.internal.IgniteKernal2019-11-22 19:34:22.301  INFO 167392 --- [eout-worker-#71] org.apache.ignite.internal.IgniteKernal1234");

			td1Cache.put(i, td1);
			td2Cache.put(i, td2);
		}

	}

	private void initCache() {
		log.debug("initCache service");

		int backups = Integer.valueOf(ip.getCache().get("backups"));

		CacheConfiguration<Integer, TestDomain1> td1CacheConfig = new CacheConfiguration<>("td1-cache");
		td1CacheConfig.setIndexedTypes(Integer.class, TestDomain1.class);
		td1CacheConfig.setCacheMode(CacheMode.PARTITIONED);
		td1CacheConfig.setSqlSchema("sts");
		td1CacheConfig.setBackups(backups);
		td1CacheConfig.setNodeFilter(new DataNodeFilter());

		ignite.addCacheConfiguration(td1CacheConfig);
		td1Cache = ignite.getOrCreateCache(td1CacheConfig.getName());

		CacheConfiguration<Integer, TestDomain2> td2CacheConfig = new CacheConfiguration<>("td2-cache");
		td2CacheConfig.setIndexedTypes(Integer.class, TestDomain2.class);
		td2CacheConfig.setCacheMode(CacheMode.PARTITIONED);
		td2CacheConfig.setSqlSchema("sts");
		td2CacheConfig.setBackups(backups);
		td2CacheConfig.setNodeFilter(new WorkerNodeFilter());

		ignite.addCacheConfiguration(td2CacheConfig);
		td2Cache = ignite.getOrCreateCache(td2CacheConfig.getName());
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

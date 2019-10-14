package poc.ignite.service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataStreamerService {

	@Autowired
	private Ignite ignite;
	private IgniteCache<Integer, Integer> cache;

	private void streamer() {
		log.debug("streamer service");

		try (IgniteDataStreamer<Integer, Integer> streamer = ignite.dataStreamer(cache.getName())) {
			streamer.autoFlushFrequency(10);
			streamer.allowOverwrite(true);

			for (int j = 0; j < 20; j++) {
				Map<Integer, Integer> entries = new HashMap<>();

				IntStream.range(0, 10000).forEach(i -> {
					entries.put(i, i);
				});

				streamer.addData(entries);
				// streamer.flush();

				IntStream.range(0, 10000).forEach(i -> {
					entries.put(i, i + 1);
				});

				streamer.addData(entries);
				// streamer.flush();

				IntStream.range(0, 10000).forEach(i -> {
					entries.put(i, i + 2);
				});

				streamer.addData(entries);
				// streamer.flush();
			}
		}

		while (true) {
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void initCache() {
		log.debug("initCache service");

		CacheConfiguration<Integer, Integer> cacheConfig = new CacheConfiguration<>("cache");
		cacheConfig.setIndexedTypes(Integer.class, Integer.class);
		cacheConfig.setCacheMode(CacheMode.PARTITIONED);

		cacheConfig.setSqlSchema("test");

		ignite.addCacheConfiguration(cacheConfig);
		cache = ignite.getOrCreateCache(cacheConfig.getName());
	}

	private void init() {
		log.debug("init service");

		initCache();
	}

	public void main() {
		log.debug("main service");

		init();
		streamer();
	}
}

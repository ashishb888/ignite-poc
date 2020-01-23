package poc.ignite.service;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.query.ContinuousQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import poc.ignite.domain.CacheEntryUpdatedListenerImpl;
import poc.ignite.domain.FactoryImpl;
import poc.ignite.domain.Person;

@Service
@Slf4j
public class ContinuousQueriesService {

	@Autowired
	private Ignite ignite;

	private void start() {
		log.debug("start service");

		String cacheName = "person-cache2";
		String schema = "sts2";
		CacheConfiguration<Integer, Person> personCacheConfig = new CacheConfiguration<>(cacheName);
		personCacheConfig.setIndexedTypes(Integer.class, Person.class);
		personCacheConfig.setCacheMode(CacheMode.PARTITIONED);
		personCacheConfig.setSqlSchema(schema);

		ignite.addCacheConfiguration(personCacheConfig);

		IgniteCache<Integer, Person> personCache2 = ignite.getOrCreateCache(cacheName);

		ContinuousQuery<Integer, Person> cQuery = new ContinuousQuery<>();

		cQuery.setLocalListener(new CacheEntryUpdatedListenerImpl());
		cQuery.setRemoteFilterFactory(new FactoryImpl());

		personCache2.query(cQuery);
	}

	private void init() {
		log.debug("init service");

		start();
	}

	public void main() {
		log.debug("main service");
		init();
	}
}

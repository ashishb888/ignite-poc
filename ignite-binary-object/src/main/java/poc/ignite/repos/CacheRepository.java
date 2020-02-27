package poc.ignite.repos;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;

import poc.ignite.config.BeansConfig;
import poc.ignite.domain.Person;

public class CacheRepository {

	public static IgniteCache<Integer, Person> personCache(String cacheName, String schema, String regionName) {
		// System.out.println("personCache repo");

		Ignite ignite = BeansConfig.ignite();

		CacheConfiguration<Integer, Person> personCacheConfig = new CacheConfiguration<>(cacheName);
		personCacheConfig.setIndexedTypes(Integer.class, Person.class);
		personCacheConfig.setCacheMode(CacheMode.PARTITIONED);
		personCacheConfig.setSqlSchema(schema);
		personCacheConfig.setBackups(1);
		personCacheConfig.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);

		ignite.addCacheConfiguration(personCacheConfig);
		return ignite.getOrCreateCache(cacheName);
	}

}

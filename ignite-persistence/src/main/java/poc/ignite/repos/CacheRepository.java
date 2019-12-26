package poc.ignite.repos;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import poc.ignite.domain.Person;
import poc.ignite.filters.DataNodeFilter;

@Slf4j
@Repository
public class CacheRepository {
	@Autowired
	private Ignite ignite;

	public IgniteCache<Integer, Person> personCache(String cacheName, String schema, String regionName) {
		log.debug("personCache repo");

		CacheConfiguration<Integer, Person> personCacheConfig = new CacheConfiguration<>(cacheName);
		personCacheConfig.setIndexedTypes(Integer.class, Person.class);
		personCacheConfig.setCacheMode(CacheMode.PARTITIONED);
		personCacheConfig.setSqlSchema(schema);
		personCacheConfig.setNodeFilter(new DataNodeFilter());

		// if (regionName != null)
		personCacheConfig.setDataRegionName(regionName);

		log.debug("getCacheConfiguration: " + ignite.configuration().getCacheConfiguration());

		ignite.configuration().setCacheConfiguration(personCacheConfig);
		// ignite.addCacheConfiguration(personCacheConfig);

		return ignite.getOrCreateCache(cacheName);
	}
}

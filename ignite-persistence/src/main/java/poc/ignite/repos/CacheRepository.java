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
import poc.ignite.filters.WorkerNodeFilter;
import poc.ignite.properties.IgniteProperties;

@Slf4j
@Repository
public class CacheRepository {
	@Autowired
	private Ignite ignite;
	@Autowired
	private IgniteProperties ip;

	public IgniteCache<Integer, Person> personCacheDataNode(String cacheName, String schema, String regionName) {
		log.debug("personCache repo");

		CacheConfiguration<Integer, Person> personCacheConfig = new CacheConfiguration<>(cacheName);
		personCacheConfig.setIndexedTypes(Integer.class, Person.class);
		personCacheConfig.setCacheMode(CacheMode.PARTITIONED);
		personCacheConfig.setSqlSchema(schema);
		personCacheConfig.setNodeFilter(new DataNodeFilter());
		personCacheConfig.setDataRegionName(regionName);
		personCacheConfig.setBackups(Integer.valueOf(ip.getCaches().get("backups")));

		ignite.addCacheConfiguration(personCacheConfig);
		return ignite.getOrCreateCache(cacheName);
	}

	public IgniteCache<Integer, Person> personCacheWorkeNode(String cacheName, String schema, String regionName) {
		log.debug("personCacheWorkeNode repo");

		CacheConfiguration<Integer, Person> personCacheConfig = new CacheConfiguration<>(cacheName);
		personCacheConfig.setIndexedTypes(Integer.class, Person.class);
		personCacheConfig.setCacheMode(CacheMode.PARTITIONED);
		personCacheConfig.setSqlSchema(schema);
		personCacheConfig.setNodeFilter(new WorkerNodeFilter());
		personCacheConfig.setDataRegionName(regionName);
		personCacheConfig.setBackups(Integer.valueOf(ip.getCaches().get("backups")));

		ignite.addCacheConfiguration(personCacheConfig);
		return ignite.getOrCreateCache(cacheName);
	}
}

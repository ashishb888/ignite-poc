package poc.ignite.repos;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import poc.ignite.domain.Person;
import poc.ignite.domain.PersonKey;
import poc.ignite.properties.IgniteProperties;

@Slf4j
@Repository
public class CacheRepository {
	@Autowired
	private Ignite ignite;
	@Autowired
	private IgniteProperties ip;

	public IgniteCache<PersonKey, Person> personCacheDataNode(String cacheName, String schema) {
		log.debug("personCache repo");

		CacheConfiguration<PersonKey, Person> personCacheConfig = new CacheConfiguration<>(cacheName);
		personCacheConfig.setIndexedTypes(PersonKey.class, Person.class);
		personCacheConfig.setCacheMode(CacheMode.PARTITIONED);
		personCacheConfig.setSqlSchema(schema);
		personCacheConfig.setBackups(Integer.valueOf(ip.getCaches().get("backups")));

		ignite.addCacheConfiguration(personCacheConfig);
		return ignite.getOrCreateCache(cacheName);
	}
}

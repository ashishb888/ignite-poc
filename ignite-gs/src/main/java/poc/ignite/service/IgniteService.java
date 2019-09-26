package poc.ignite.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.cache.QueryIndex;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import poc.ignite.domain.Person;

@Service
@Slf4j
@SuppressWarnings("unused")
public class IgniteService {

	@Autowired
	private Ignite ignite;
	private IgniteCache<Long, Person> personCache;

	private void start() {
		log.debug("start service");

		personCache.put(1L, new Person(1L, "Test user"));

		personCache.forEach(r -> log.info("r: " + r.toString()));
	}

	private void initCache() {
		log.debug("initCache service");

		CacheConfiguration<Long, Person> personCacheConfig = new CacheConfiguration<>("person-cache");
		// personCacheConfig.setIndexedTypes(Long.class, Person.class);
		// personCacheConfig.setCacheMode(CacheMode.PARTITIONED);

		QueryEntity queryEntity = new QueryEntity();

		queryEntity.setKeyType(Long.class.getName());
		queryEntity.setValueType(Person.class.getName());

		LinkedHashMap<String, String> fields = new LinkedHashMap<String, String>();

//		fields.put("id", Long.class.getName());
//		fields.put("orgId", Long.class.getName());
//		fields.put("firstName", String.class.getName());
//		fields.put("lastName", String.class.getName());
//		fields.put("resume", String.class.getName());
		// fields.put("salary", Double.class.getName());

		List<Field> fieldsLs = Arrays.stream(Person.class.getDeclaredFields())
				.filter(f -> !f.getName().toLowerCase().equals("serialversionuid")).collect(Collectors.toList());

		fieldsLs.forEach(f -> fields.put(f.getName(), f.getType().getName()));

		queryEntity.setFields(fields);

		Collection<QueryIndex> indexes = new ArrayList<>(3);

		indexes.add(new QueryIndex("id"));
		indexes.add(new QueryIndex("orgId"));
		indexes.add(new QueryIndex("salary"));

		queryEntity.setIndexes(indexes);

		personCacheConfig.setQueryEntities(Arrays.asList(queryEntity));
		personCacheConfig.setSqlSchema("test");

		ignite.addCacheConfiguration(personCacheConfig);
		personCache = ignite.getOrCreateCache(personCacheConfig.getName());
	}

	private void init() {
		log.debug("init service");

		initCache();
	}

	public void main() {
		log.debug("main service");
		log.debug("name: " + ignite.name());

		init();
		start();
	}
}

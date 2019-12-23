package poc.ignite.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.cache.QueryIndex;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import poc.ignite.domain.Person;
import poc.ignite.properties.IgniteProperties;
import poc.ignite.repos.CacheRepository;

@Service
@Slf4j
@SuppressWarnings("unused")
public class SimplePersistenceService {

	@Autowired
	private Ignite ignite;
	@Autowired
	private CacheRepository cr;
	@Autowired
	private IgniteProperties ip;

	private void start() {
		log.debug("start service");

		IgniteCache<Integer, Person> personCache = cr.personCache();
		int records = Integer.valueOf(ip.getOther().get("records"));

		try (IgniteDataStreamer<Integer, Person> streamer = ignite.dataStreamer(personCache.getName())) {
			for (int i = 0; i < records; i++) {
				streamer.addData(i, new Person(i, i, "p" + i));
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private void init() {
		log.debug("start service");
	}

	public void main() {
		log.debug("main service");

		start();
	}
}

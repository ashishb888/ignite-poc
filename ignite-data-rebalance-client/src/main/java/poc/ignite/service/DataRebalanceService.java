package poc.ignite.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import poc.ignite.domain.Person;
import poc.ignite.domain.PersonKey;
import poc.ignite.properties.IgniteProperties;

@Service
@Slf4j
public class DataRebalanceService {

	@Autowired
	private Ignite ignite;
	@Autowired
	private IgniteProperties ip;

	private void start() {
		log.debug("start service");

		String cacheName = "person-cache1";
		IgniteCache<PersonKey, Person> personCache1 = ignite.getOrCreateCache(cacheName);
		int offset = Integer.valueOf(ip.getOther().get("offset"));
		int limit = Integer.valueOf(ip.getOther().get("limit"));
		List<Integer> cityIds;
		String cityIdsString = ip.getOther().get("cityIds");

		if (cityIdsString == null || cityIdsString.isEmpty())
			cityIds = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20);
		else
			cityIds = Arrays.stream(cityIdsString.split(",")).map(r -> Integer.parseInt(r.trim()))
					.collect(Collectors.toList());

		log.debug("cityIds: " + cityIds);
		log.debug("offset: " + offset);
		log.debug("limit: " + limit);

		try (IgniteDataStreamer<PersonKey, Person> streamer = ignite.dataStreamer(personCache1.getName())) {

			limit += offset;

			for (int i = offset; i < limit; i++) {
				Collections.shuffle(cityIds);
				streamer.addData(new PersonKey(i, cityIds.get(0)), new Person(i, cityIds.get(0), "p" + i));
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
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

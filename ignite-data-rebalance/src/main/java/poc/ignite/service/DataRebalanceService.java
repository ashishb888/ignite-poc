package poc.ignite.service;

import java.util.Arrays;
import java.util.List;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.events.Event;
import org.apache.ignite.events.EventType;
import org.apache.ignite.lang.IgnitePredicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import poc.ignite.domain.Person;
import poc.ignite.domain.PersonKey;
import poc.ignite.repos.CacheRepository;

@Service
@Slf4j
public class DataRebalanceService {

	@Autowired
	private Ignite ignite;
	@Autowired
	private CacheRepository cr;

	private void start() {
		log.debug("start service");

		String cacheName = "person-cache1";
		String cacheSchema = "sts";

		IgniteCache<PersonKey, Person> personCache1 = cr.personCacheDataNode(cacheName, cacheSchema);

		int partitions = ignite.affinity(personCache1.getName()).partitions();
		int allPartitions[] = ignite.affinity(personCache1.getName()).allPartitions(ignite.cluster().localNode());
		int primaryPartitions[] = ignite.affinity(personCache1.getName())
				.primaryPartitions(ignite.cluster().localNode());

		List<Integer> cityIds = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20);

		log.debug("partitions: " + partitions);
		log.debug("allPartitions: " + Arrays.toString(allPartitions));
		log.debug("primaryPartitions: " + Arrays.toString(primaryPartitions));

		cityIds.forEach(r -> {
			int p = ignite.affinity(personCache1.getName()).partition(r);
			log.debug("r: " + r + ", p: " + p);
		});

	}

	private void eventsListener() {
		log.debug("eventsListener service");

		IgnitePredicate<Event> eventListener = event -> {
			String cacheName = "person-cache1";
			int partitions = ignite.affinity(cacheName).partitions();
			int allPartitions[] = ignite.affinity(cacheName).allPartitions(ignite.cluster().localNode());
			int primaryPartitions[] = ignite.affinity(cacheName).primaryPartitions(ignite.cluster().localNode());
			List<Integer> cityIds = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
					20);

			log.debug("partitions: " + partitions);
			log.debug("allPartitions: " + Arrays.toString(allPartitions));
			log.debug("primaryPartitions: " + Arrays.toString(primaryPartitions));

			cityIds.forEach(r -> {
				int p = ignite.affinity(cacheName).partition(r);
				log.debug("r: " + r + ", p: " + p);
			});

			return true;
		};

		ignite.events().localListen(eventListener, EventType.EVT_NODE_LEFT, EventType.EVT_NODE_JOINED,
				EventType.EVT_NODE_FAILED);
	}

	private void init() {
		log.debug("init service");

		eventsListener();
		start();
	}

	public void main() {
		log.debug("main service");

		init();
	}
}

package poc.ignite.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.events.Event;
import org.apache.ignite.events.EventType;
import org.apache.ignite.lang.IgnitePredicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import poc.ignite.domain.Person;
import poc.ignite.properties.IgniteProperties;
import poc.ignite.repos.CacheRepository;

@Service
@Slf4j
@SuppressWarnings("unused")
public class TransactionService {

	@Autowired
	private Ignite ignite;
	@Autowired
	private CacheRepository cr;
	@Autowired
	private IgniteProperties ip;
	private AtomicBoolean baseTopology = new AtomicBoolean(false);

	private void start() {
		log.debug("start service");

		int records = Integer.valueOf(ip.getOther().get("records"));
		IgniteCache<Integer, Person> personCache = cr.personCache("person-cache2", "sts2", "Default_Region");

		IntStream.iterate(0, i -> i + 1).limit(records).forEach(i -> {
			personCache.put(i, new Person(i, "s1" + i, "s2" + i));
		});

		Set<Integer> keys = IntStream.iterate(0, i -> i + 1).limit(records).boxed().collect(Collectors.toSet());
		// Set<Integer> keys = new HashSet<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8,
		// 9));

		Thread t1 = new Thread(() -> {
			log.debug("t1 starts");

			Map<Integer, Person> personMp = personCache.getAll(keys);

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}

			personMp.forEach((k, v) -> {
				log.info("v: " + v);

				// v.setT1("t1");
				v.setT2("t1");
			});
		});

		Thread t2 = new Thread(() -> {
			log.debug("t2 starts");

			Map<Integer, Person> personMp = personCache.getAll(keys);

//			try {
//				Thread.sleep(5000);
//			} catch (InterruptedException e) {
//				log.error(e.getMessage(), e);
//			}

			personMp.forEach((k, v) -> {
				log.info("v: " + v);

				v.setT1("t2");
				// v.setT2("t2");
			});
		});

		t1.start();
		t2.start();
	}

	public void main() {
		log.debug("main service");

		start();
	}
}

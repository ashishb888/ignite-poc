package poc.ignite.service;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import poc.ignite.domain.Person;
import poc.ignite.properties.IgniteProperties;

@Service
@Slf4j
public class ContinuousQueriesService {

	@Autowired
	private Ignite ignite;
	@Autowired
	private IgniteProperties ip;

	private void start() {
		log.debug("start service");

		int records = Integer.valueOf(ip.getOther().get("records"));

		IgniteCache<Integer, Person> personCache2 = ignite.cache("person-cache2");
		try (IgniteDataStreamer<Integer, Person> streamer = ignite.dataStreamer(personCache2.getName())) {
			for (int i = 0; i < records; i++) {
				streamer.addData(i, new Person(i, i, "p" + i));

				Thread.sleep(5000);
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

package poc.ignite.service;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

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
public class SimplePersistenceService {

	@Autowired
	private Ignite ignite;
	@Autowired
	private CacheRepository cr;
	@Autowired
	private IgniteProperties ip;
	private AtomicBoolean baseTopology = new AtomicBoolean(false);

	private void start() {
		log.debug("start service");

		// IgniteCache<Integer, Person> personCache1 = cr.personCache("person-cache1",
		// "sts1", "Default_Region");
		IgniteCache<Integer, Person> personCache1 = cr.personCacheDataNode("person-cache1", "sts1", "Data_Region");
		int records = Integer.valueOf(ip.getOther().get("records"));

		try (IgniteDataStreamer<Integer, Person> streamer = ignite.dataStreamer(personCache1.getName())) {
			for (int i = 0; i < records; i++) {
				streamer.addData(i, new Person(i, i, "p" + i));
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		// IgniteCache<Integer, Person> personCache2 = cr.personCache("person-cache2",
		// "sts2", "Data_Region");
		IgniteCache<Integer, Person> personCache2 = cr.personCacheWorkeNode("person-cache2", "sts2", "Default_Region");

		try (IgniteDataStreamer<Integer, Person> streamer = ignite.dataStreamer(personCache2.getName())) {
			for (int i = 0; i < records; i++) {
				streamer.addData(i, new Person(i, i, "p" + i));
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private void eventsListener() {
		log.debug("eventsListener service");

		IgnitePredicate<Event> eventListener = event -> {
			String message = event.message();
			String name = event.name();
			int nodes = ignite.cluster().forAttribute("nodeName", "data-node").nodes().size();
			int expectedNodes = Integer.valueOf(ip.getOther().get("nodes"));
			boolean clusterActive = ignite.cluster().active();

			log.debug("name: " + name);
			log.debug("message: " + message);
			log.debug("nodes: " + nodes);
			log.debug("clusterActive: " + clusterActive);

			if (expectedNodes == nodes) {
				// ignite.cluster().active(Boolean.valueOf(ip.getDataRegion().get("active")));
				// Collection<ClusterNode> dataNodes = ignite.cluster().forAttribute("nodeName",
				// "data-node").nodes();

				ignite.cluster().active(Boolean.valueOf(ip.getDataRegion().get("active")));

				Collection<ClusterNode> dataNodes = ignite.cluster().forServers().nodes();
				ignite.cluster().setBaselineTopology(dataNodes);

				baseTopology.set(true);
			}

			return true;
		};

		ignite.events().localListen(eventListener, EventType.EVT_NODE_JOINED);
	}

	private void init() {
		log.debug("init service");

		// eventsListener();

		boolean clusterActive = ignite.cluster().active();
		log.debug("clusterActive: " + clusterActive);

		ignite.cluster().active(Boolean.valueOf(ip.getDataRegion().get("active")));

		Collection<ClusterNode> dataNodes = ignite.cluster().forAttribute("nodeName", "data-node").nodes();
		ignite.cluster().setBaselineTopology(dataNodes);
		//ignite.cluster().setBaselineTopology(Long.valueOf(ip.getOther().get("topVer")));
	}

	public void main() {
		log.debug("main service");

		init();

//		if (baseTopology.get()) {
//			start();
//		}

		if (Boolean.valueOf(ip.getOther().get("start")))
			start();
	}
}

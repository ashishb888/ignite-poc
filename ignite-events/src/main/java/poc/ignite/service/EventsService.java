package poc.ignite.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.ignite.Ignite;
import org.apache.ignite.events.CacheEvent;
import org.apache.ignite.events.Event;
import org.apache.ignite.events.EventType;
import org.apache.ignite.lang.IgnitePredicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import poc.ignite.properties.IgniteProperties;

@Service
@Slf4j
public class EventsService {

	@Autowired
	private Ignite ignite;
	@Autowired
	private IgniteProperties ip;

	private void remoteEventsListener() {
		log.debug("remoteEventsListener service");

		IgnitePredicate<Event> eventListener = event -> {
			String message = event.message();
			String name = event.name();
			int nNodes = ignite.cluster().nodes().size();

			log.debug("name: " + name);
			log.debug("message: " + message);
			log.debug("nNodes: " + nNodes);

			if (nNodes != Integer.valueOf(ip.getOther().get("nNodes")))
				ignite.atomicLong("isAllUp", 0, false).getAndSet(0);
			else
				ignite.atomicLong("isAllUp", 0, false).getAndSet(1);

			return true;
		};

		ignite.events(ignite.cluster().forServers()).remoteListen(null, eventListener, EventType.EVT_NODE_LEFT,
				EventType.EVT_NODE_JOINED, EventType.EVT_NODE_FAILED);
	}

	private void eventsListener() {
		log.debug("eventsListener service");

		IgnitePredicate<Event> eventListener = event -> {
			String message = event.message();
			String name = event.name();
			int nNodes = ignite.cluster().nodes().size();

			log.debug("name: " + name);
			log.debug("message: " + message);
			log.debug("nNodes: " + nNodes);

			if (nNodes != Integer.valueOf(ip.getOther().get("nNodes")))
				ignite.atomicLong("isAllUp", 0, false).getAndSet(0);
			else
				ignite.atomicLong("isAllUp", 0, false).getAndSet(1);

			return true;
		};

		IgnitePredicate<CacheEvent> cacheEventListener = event -> {
			String message = event.message();
			String name = event.name();
			String cacheName = event.cacheName();

			int nNodes = ignite.cluster().nodes().size();

			log.debug("name: " + name);
			log.debug("cacheName: " + cacheName);
			log.debug("message: " + message);
			log.debug("nNodes: " + nNodes);

			return true;
		};

		ignite.events().localListen(eventListener, EventType.EVT_NODE_LEFT, EventType.EVT_NODE_JOINED,
				EventType.EVT_NODE_FAILED);

		// ignite.events(ignite.cluster().forServers()).localListen(eventListener,
		// EventType.EVT_NODE_LEFT,
		// EventType.EVT_NODE_JOINED, EventType.EVT_NODE_FAILED);

		// ignite.events().localListen(cacheEventListener, EventType.EVT_NODE_LEFT,
		// EventType.EVT_CACHE_NODES_LEFT);
		// 2019-11-19 15:22:03.964 WARN 156008 --- [ main]
		// o.a.i.i.m.e.GridEventStorageManager : Added listener for disabled event type:
		// CACHE_NODES_LEFT

	}

	private void someProcess() {
		log.debug("someProcess service");

		ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
		ses.scheduleAtFixedRate(() -> {
			long isAllUp = ignite.atomicLong("isAllUp", 0, false).get();

			log.debug("isAllUp: " + isAllUp);

		}, 1, 1, TimeUnit.SECONDS);

	}

	private void start() {
		log.debug("start service");

		if (Boolean.valueOf(ip.getOther().get("listener")))
			remoteEventsListener();

		// eventsListener();
		someProcess();
	}

	public void main() {
		log.debug("main service");

		start();
	}
}

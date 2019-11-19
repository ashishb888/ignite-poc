package poc.ignite.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.cache.QueryIndex;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.events.CacheEvent;
import org.apache.ignite.events.Event;
import org.apache.ignite.events.EventType;
import org.apache.ignite.lang.IgnitePredicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import poc.ignite.domain.StockTrade;
import poc.ignite.domain.StockTradeKey;

@Service
@Slf4j
@SuppressWarnings("unused")
public class EventsService {

	@Autowired
	private Ignite ignite;

	private void eventsListener() {
		log.debug("eventsListener service");

		IgnitePredicate<Event> eventListener = event -> {
			String message = event.message();
			String name = event.name();
			int nNodes = ignite.cluster().nodes().size();

			log.error("name: " + name);
			log.error("message: " + message);
			log.error("nNodes: " + nNodes);

			return true;
		};

		IgnitePredicate<CacheEvent> cacheEventListener = event -> {
			String message = event.message();
			String name = event.name();
			String cacheName = event.cacheName();

			int nNodes = ignite.cluster().nodes().size();

			log.error("name: " + name);
			log.error("message: " + message);
			log.error("nNodes: " + nNodes);

			return true;
		};

		ignite.events().localListen(eventListener, EventType.EVT_NODE_LEFT, EventType.EVT_CACHE_NODES_LEFT);
		ignite.events().localListen(cacheEventListener, EventType.EVT_NODE_LEFT, EventType.EVT_CACHE_NODES_LEFT);
		// 2019-11-19 15:22:03.964 WARN 156008 --- [ main]
		// o.a.i.i.m.e.GridEventStorageManager : Added listener for disabled event type:
		// CACHE_NODES_LEFT

	}

	private void start() {
		log.debug("start service");

		eventsListener();
	}

	public void main() {
		log.debug("main service");

		start();
	}
}

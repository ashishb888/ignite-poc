package poc.ignite.service;

import org.apache.ignite.Ignite;
import org.apache.ignite.events.Event;
import org.apache.ignite.events.EventType;
import org.apache.ignite.lang.IgnitePredicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import poc.ignite.properties.IgniteProperties;

@Service
@Slf4j
public class NodeEventsService {

	@Autowired
	private Ignite ignite;
	@Autowired
	private IgniteProperties ip;

	private void eventsListener() {
		log.debug("eventsListener service");

		IgnitePredicate<Event> eventListener = event -> {
			String message = event.message();
			String name = event.name();
			int nodes = ignite.cluster().forAttribute("nodeName", ip.getOther().get("nodeName")).nodes().size();

			log.debug("name: " + name);
			log.debug("message: " + message);
			log.debug("nodes: " + nodes);

			return true;
		};

		ignite.events().localListen(eventListener, EventType.EVT_NODE_LEFT, EventType.EVT_NODE_FAILED);
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

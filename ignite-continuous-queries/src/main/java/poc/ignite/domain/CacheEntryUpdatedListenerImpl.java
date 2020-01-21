package poc.ignite.domain;

import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.event.CacheEntryUpdatedListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheEntryUpdatedListenerImpl implements CacheEntryUpdatedListener<Integer, Person> {

	@Override
	public void onUpdated(Iterable<CacheEntryEvent<? extends Integer, ? extends Person>> events)
			throws CacheEntryListenerException {
		for (CacheEntryEvent<? extends Integer, ? extends Person> cacheEntryEvent : events) {
			log.debug("cacheEntryEvent: " + cacheEntryEvent.getValue());
		}
	}
}

package poc.ignite.domain;

import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryEventFilter;
import javax.cache.event.CacheEntryListenerException;

public class CacheEntryEventFilterImpl implements CacheEntryEventFilter<Integer, Person> {

	@Override
	public boolean evaluate(CacheEntryEvent<? extends Integer, ? extends Person> event)
			throws CacheEntryListenerException {
		return true;
	}
}

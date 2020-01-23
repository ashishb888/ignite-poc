package poc.ignite.domain;

import javax.cache.configuration.Factory;
import javax.cache.event.CacheEntryEventFilter;

public class FactoryImpl implements Factory<CacheEntryEventFilter<Integer, Person>> {

	private static final long serialVersionUID = 881669537085864136L;

	@Override
	public CacheEntryEventFilter<Integer, Person> create() {
		return new CacheEntryEventFilterImpl();
	}

}

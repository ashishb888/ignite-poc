package poc.ignite.service;

import java.util.ArrayList;
import java.util.List;

import javax.cache.Cache.Entry;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import poc.ignite.domain.StockTrade;
import poc.ignite.domain.StockTradeKey;
import poc.ignite.properties.IgniteProperties;

@Service
@Slf4j
public class ComputeService {
	@Autowired
	private Ignite ignite;
	@Autowired
	private IgniteProperties ip;

	private void affinityRun() {
		log.debug("affinityRun service");

		List<String> affKeys = new ArrayList<>();
		affKeys.add("07-JAN-2019");
		affKeys.add("08-JAN-2019");
		affKeys.add("09-JAN-2019");

		new Thread(() -> {

			while (true) {
				affKeys.forEach(affKey -> {
					ignite.compute().affinityRun("stock-trade-cache", affKey, new IR(affKey, "stock-trade-cache"));
				});

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					log.error(e.getMessage(), e);
				}
			}

		}, "affinity-run").start();
	}

	private void compute() {
		log.debug("compute service");

		affinityRun();
	}

	static class IR implements IgniteRunnable {
		Logger logger = LoggerFactory.getLogger(this.getClass());
		@IgniteInstanceResource
		private Ignite ignite;

		String key;
		String cacheName;

		private static final long serialVersionUID = -7262215173125724166L;

		IR() {
		}

		IR(String key, String cacheName) {
			this.key = key;
			this.cacheName = cacheName;
		}

		@Override
		public void run() {
			logger.debug("k: " + key + ", cacheName: " + cacheName);

			IgniteCache<StockTradeKey, StockTrade> cache = ignite.cache(cacheName);// .withKeepBinary();
			SqlQuery<StockTradeKey, StockTrade> sqlQuery = new SqlQuery<>(StockTrade.class,
					"timestamp = ? and isin != ? limit ?");
			sqlQuery.setArgs(key, "From compute", 10);

			long entries = 0;

			try (QueryCursor<Entry<StockTradeKey, StockTrade>> queryCursor = cache.query(sqlQuery)) {
				for (Entry<StockTradeKey, StockTrade> entry : queryCursor) {
					StockTrade st = entry.getValue();

					st.setIsin("From compute");
					entries += 1;

					cache.put(entry.getKey(), st);
				}
			}

			logger.debug("entries: " + entries);
		}

	}

	static class IC implements IgniteCallable<Long> {
		Logger logger = LoggerFactory.getLogger(this.getClass());

		@IgniteInstanceResource
		private Ignite ignite;

		String key;
		String cacheName;

		IC() {
		}

		IC(String key, String cacheName) {
			this.key = key;
			this.cacheName = cacheName;
		}

		private static final long serialVersionUID = -1290099421708832298L;

		@Override
		public Long call() throws Exception {
			logger.info("k: " + key + ", cacheName: " + cacheName);

			IgniteCache<StockTradeKey, StockTrade> cache = ignite.cache(cacheName);
			SqlQuery<StockTradeKey, StockTrade> sqlQuery = new SqlQuery<>(StockTrade.class,
					"timestamp = ? and isin != ? limit ?");
			sqlQuery.setArgs(key, "From compute", 10);

			long entries = 0;

			try (QueryCursor<Entry<StockTradeKey, StockTrade>> queryCursor = cache.query(sqlQuery)) {
				for (Entry<StockTradeKey, StockTrade> entry : queryCursor) {
					StockTrade st = entry.getValue();
					st.setIsin("From compute");
					entries += 1;
				}
			}

			logger.info("entries: " + entries);

			return entries;
		}

	}

	private void start() {
		log.debug("start service");

		compute();
	}

	public void main() {
		log.debug("main service");

		start();
	}
}

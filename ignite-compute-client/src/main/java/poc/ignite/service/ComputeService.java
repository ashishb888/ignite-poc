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
import org.apache.ignite.resources.ServiceResource;
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

	private void affinityCall() {
		log.debug("affinityCall service");

		List<String> affKeys = new ArrayList<>();
		affKeys.add("07-JAN-2019");
		affKeys.add("08-JAN-2019");
		affKeys.add("09-JAN-2019");

		new Thread(() -> {

			while (true) {
				affKeys.forEach(affKey -> {
					long entries = ignite.compute().affinityCall("stock-trade-cache", affKey,
							new IC(affKey, "stock-trade-cache"));
					log.debug("entries: " + entries);
				});

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					log.error(e.getMessage(), e);
				}
			}

		}, "affinity-call").start();
	}

	private void affinityRunWithService() {
		log.debug("affinityRunWithService service");

		List<String> affKeys = new ArrayList<>();
		affKeys.add("07-JAN-2019");
		affKeys.add("08-JAN-2019");
		affKeys.add("09-JAN-2019");

		new Thread(() -> {

			while (true) {
				affKeys.forEach(affKey -> {
					ignite.compute().affinityRun("stock-trade-cache", affKey,
							new IRWithService(affKey, "stock-trade-cache"));
				});

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					log.error(e.getMessage(), e);
				}
			}

		}, "affinity-run-with-service").start();
	}

	private void affinityCallWithService() {
		log.debug("affinityCallWithService service");

		List<String> affKeys = new ArrayList<>();
		affKeys.add("07-JAN-2019");
		affKeys.add("08-JAN-2019");
		affKeys.add("09-JAN-2019");

		new Thread(() -> {

			while (true) {
				affKeys.forEach(affKey -> {
					long entries = ignite.compute().affinityCall("stock-trade-cache", affKey,
							new ICWithService(affKey, "stock-trade-cache"));
					log.debug("entries: " + entries);
				});

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					log.error(e.getMessage(), e);
				}
			}

		}, "affinity-call-with-service").start();
	}

	private void compute() {
		log.debug("compute service");

		// affinityRun();
		// affinityCall();
		// affinityRunWithService();
		affinityCallWithService();
	}

	static class IR implements IgniteRunnable {
		Logger logger = LoggerFactory.getLogger(this.getClass());
		@IgniteInstanceResource
		private Ignite ignite;

		String affKey;
		String cacheName;

		private static final long serialVersionUID = -7262215173125724166L;

		IR() {
		}

		IR(String affKey, String cacheName) {
			this.affKey = affKey;
			this.cacheName = cacheName;
		}

		@Override
		public void run() {
			logger.debug("k: " + affKey + ", cacheName: " + cacheName);

			IgniteCache<StockTradeKey, StockTrade> cache = ignite.cache(cacheName);// .withKeepBinary();
			SqlQuery<StockTradeKey, StockTrade> sqlQuery = new SqlQuery<>(StockTrade.class,
					"timestamp = ? and isin != ? limit ?");
			sqlQuery.setArgs(affKey, "From compute", 10);

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

		String affKey;
		String cacheName;

		IC() {
		}

		IC(String affKey, String cacheName) {
			this.affKey = affKey;
			this.cacheName = cacheName;
		}

		private static final long serialVersionUID = -1290099421708832298L;

		@Override
		public Long call() throws Exception {
			logger.info("k: " + affKey + ", cacheName: " + cacheName);

			IgniteCache<StockTradeKey, StockTrade> cache = ignite.cache(cacheName);
			SqlQuery<StockTradeKey, StockTrade> sqlQuery = new SqlQuery<>(StockTrade.class,
					"timestamp = ? and isin != ? limit ?");
			sqlQuery.setArgs(affKey, "From compute", 10);

			long entries = 0;

			try (QueryCursor<Entry<StockTradeKey, StockTrade>> queryCursor = cache.query(sqlQuery)) {
				for (Entry<StockTradeKey, StockTrade> entry : queryCursor) {
					StockTrade st = entry.getValue();
					st.setIsin("From compute");
					entries += 1;

					cache.put(entry.getKey(), st);
				}
			}

			logger.info("entries: " + entries);

			return entries;
		}

	}

	static class IRWithService implements IgniteRunnable {
		Logger logger = LoggerFactory.getLogger(this.getClass());
		@IgniteInstanceResource
		private Ignite ignite;
		@ServiceResource(serviceName = "sampleService", proxyInterface = SampleService.class)
		private SampleService sampleService;

		String affKey;
		String cacheName;

		private static final long serialVersionUID = -7262215173125724166L;

		IRWithService() {
		}

		IRWithService(String affKey, String cacheName) {
			this.affKey = affKey;
			this.cacheName = cacheName;
		}

		@Override
		public void run() {
			logger.debug("k: " + affKey + ", cacheName: " + cacheName);

			sampleService.run(cacheName, affKey);
		}

	}

	static class ICWithService implements IgniteCallable<Long> {
		Logger logger = LoggerFactory.getLogger(this.getClass());

		@IgniteInstanceResource
		private Ignite ignite;
		@ServiceResource(serviceName = "sampleService", proxyInterface = SampleService.class)
		private SampleService sampleService;

		String affKey;
		String cacheName;

		ICWithService() {
		}

		ICWithService(String affKey, String cacheName) {
			this.affKey = affKey;
			this.cacheName = cacheName;
		}

		private static final long serialVersionUID = -1290099421708832298L;

		@Override
		public Long call() throws Exception {
			logger.info("k: " + affKey + ", cacheName: " + cacheName);

			long entries = sampleService.call(cacheName, affKey);

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

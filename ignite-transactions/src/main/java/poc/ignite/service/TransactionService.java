package poc.ignite.service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteTransactions;
import org.apache.ignite.Ignition;
import org.apache.ignite.transactions.Transaction;
import org.apache.ignite.transactions.TransactionConcurrency;
import org.apache.ignite.transactions.TransactionIsolation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import poc.ignite.domain.Person;
import poc.ignite.properties.IgniteProperties;
import poc.ignite.repos.CacheRepository;

@Service
@Slf4j
@SuppressWarnings("unused")
public class TransactionService {

	@Autowired
	private Ignite ignite;
	@Autowired
	private CacheRepository cr;
	@Autowired
	private IgniteProperties ip;
	private AtomicBoolean baseTopology = new AtomicBoolean(false);
	private String schema = "sts2";

	private void getAllAndPutAll() {
		log.debug("getAllAndPutAll service");

		int records = Integer.valueOf(ip.getOther().get("records"));
		IgniteCache<Integer, Person> personCache = cr.personCache("person-cache2", schema, "Default_Region");

		IntStream.iterate(0, i -> i + 1).limit(records).forEach(i -> {
			personCache.put(i, new Person(i, "s1" + i, "s2" + i));
		});

		Set<Integer> keys = IntStream.iterate(0, i -> i + 1).limit(records).boxed().collect(Collectors.toSet());
		// Set<Integer> keys = new HashSet<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8,
		// 9));

		Thread t1 = new Thread(() -> {
			log.debug("t1 starts");

			Map<Integer, Person> personMp = personCache.getAll(keys);

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}

			personMp.forEach((k, v) -> {
				log.debug("v: " + v);

				// v.setT1("t1");
				v.setT2("t1");

				personCache.put(k, v);
			});

		}, "t1");

		Thread t2 = new Thread(() -> {
			log.debug("t2 starts");

			Map<Integer, Person> personMp = personCache.getAll(keys);

//			try {
//				Thread.sleep(5000);
//			} catch (InterruptedException e) {
//				log.error(e.getMessage(), e);
//			}

			personMp.forEach((k, v) -> {
				log.debug("v: " + v);

				v.setT1("t2");
				// v.setT2("t2");

				personCache.put(k, v);
			});
		}, "t2");

		Thread t3 = new Thread(() -> {
			log.debug("t3 starts");

			while (true) {
				personCache.getAll(keys).forEach((k, v) -> {
					log.debug("v: " + v);
				});

				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					log.error(e.getMessage(), e);
				}
			}
		}, "t3");

		t1.start();
		t2.start();
		t3.start();
	}

	private void getAndPut() {
		log.debug("getAndPut service");

		int records = Integer.valueOf(ip.getOther().get("records"));
		IgniteCache<Integer, Person> personCache = cr.personCache("person-cache2", schema, "Default_Region");

		IntStream.iterate(0, i -> i + 1).limit(records).forEach(i -> {
			personCache.put(i, new Person(i, "s1" + i, "s2" + i));
		});

		int key = 0;

		Thread t1 = new Thread(() -> {
			log.debug("t1 starts");

			Person val = personCache.get(key);

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}

			log.debug("val: " + val);

			val.setT2("t1");
			personCache.put(key, val);

		}, "t1");

		Thread t2 = new Thread(() -> {
			log.debug("t2 starts");

			Person val = personCache.get(key);

			log.debug("val: " + val);

			val.setT1("t2");
			personCache.put(key, val);
		}, "t2");

		Thread t3 = new Thread(() -> {
			log.debug("t3 starts");

			while (true) {
				log.debug("val: " + personCache.get(key));

				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					log.error(e.getMessage(), e);
				}
			}
		}, "t3");

		t1.start();
		t2.start();
		t3.start();
	}

	private void getAndPutWithTransaction() {
		log.debug("getAndPutWithTransaction service");

		int records = Integer.valueOf(ip.getOther().get("records"));
		IgniteCache<Integer, Person> personCache = cr.personCache("person-cache2", schema, "Default_Region");

		IntStream.iterate(0, i -> i + 1).limit(records).forEach(i -> {
			personCache.put(i, new Person(i, "s1" + i, "s2" + i));
		});

		int key = 0;

		Thread t1 = new Thread(() -> {
			log.debug("t1 starts");
			IgniteTransactions transactions = ignite.transactions();

			try (Transaction tx = transactions.txStart()) {
				Person val = personCache.get(key);

				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					log.error(e.getMessage(), e);
				}

				log.debug("val: " + val);

				val.setT2("t1");
				val.setT2("t1");
				personCache.put(key, val);

				tx.commit();
			}
		}, "t1");

		Thread t2 = new Thread(() -> {
			log.debug("t2 starts");

			IgniteTransactions transactions = ignite.transactions();

			try (Transaction tx = transactions.txStart()) {
				Person val = personCache.get(key);

				log.debug("val: " + val);

				val.setT1("t2");
				personCache.put(key, val);
				tx.commit();
			}
		}, "t2");

		Thread t3 = new Thread(() -> {
			log.debug("t3 starts");

			while (true) {
				log.debug("val: " + personCache.get(key));

				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					log.error(e.getMessage(), e);
				}
			}
		}, "t3");

		t1.start();
		t2.start();
		t3.start();
	}

	private void getAllAndPutAllWithTransaction() {
		log.debug("getAllAndPutAllWithTransaction service");

		int records = Integer.valueOf(ip.getOther().get("records"));
		IgniteCache<Integer, Person> personCache = cr.personCache("person-cache2", schema, "Default_Region");

		IntStream.iterate(0, i -> i + 1).limit(records).forEach(i -> {
			personCache.put(i, new Person(i, "s1" + i, "s2" + i));
		});

		Set<Integer> keys = IntStream.iterate(0, i -> i + 1).limit(records).boxed().collect(Collectors.toSet());
		// Set<Integer> keys = new HashSet<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8,
		// 9));

		Thread t1 = new Thread(() -> {
			log.debug("t1 starts");

			try (Transaction tx = Ignition.ignite().transactions().txStart()) {
				Map<Integer, Person> personMp = personCache.getAll(keys);
				log.debug("Original personMp: " + personMp);

				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					log.error(e.getMessage(), e);
				}

				personMp.forEach((k, v) -> {
					v.setT2("t1");
				});

				log.debug("Updated personMp: " + personMp);
				personCache.putAll(personMp);

				tx.commit();
			}
		}, "t1");

		Thread t2 = new Thread(() -> {
			log.debug("t2 starts");

			try (Transaction tx = Ignition.ignite().transactions().txStart(TransactionConcurrency.PESSIMISTIC,
					TransactionIsolation.READ_COMMITTED)) {
				Map<Integer, Person> personMp = personCache.getAll(keys);
				log.debug("Original personMp: " + personMp);

//			try {
//				Thread.sleep(5000);
//			} catch (InterruptedException e) {
//				log.error(e.getMessage(), e);
//			}

				personMp.forEach((k, v) -> {
					v.setT1("t2");
				});

				log.debug("Updated personMp: " + personMp);
				personCache.putAll(personMp);

				tx.commit();
			}
		}, "t2");

		Thread t3 = new Thread(() -> {
			log.debug("t3 starts");

			while (true) {
				log.debug("personMp: " + personCache.getAll(keys));
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					log.error(e.getMessage(), e);
				}
			}
		}, "t3");

		t1.start();
		t2.start();
		t3.start();
	}

	public void main() {
		log.debug("main service");

		// start();
		// getAndPut();
		// getAndPutWithTransaction();
		getAllAndPutAllWithTransaction();
	}
}

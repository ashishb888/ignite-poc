package poc.ignite.service;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.binary.BinaryObjectBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import poc.ignite.domain.Person;
import poc.ignite.properties.IgniteProperties;
import poc.ignite.repos.CacheRepository;

@Service
@Slf4j
public class BinaryObjectService {

	@Autowired
	private CacheRepository cr;
	@Autowired
	private IgniteProperties ip;
	private String schema = "sts2";
	IgniteCache<Integer, Person> personCache;
	private int records;

	private void bo() {
		log.debug("bo service");

		IgniteCache<Integer, BinaryObject> personCacheBinary = personCache.withKeepBinary();

		Set<Integer> keys = IntStream.iterate(0, i -> i + 1).limit(records).boxed().collect(Collectors.toSet());

		personCacheBinary.getAll(keys).forEach((k, v) -> {
			BinaryObjectBuilder bob = v.toBuilder();
			bob.setField("t1", "bo1" + k);
			bob.setField("t2", "bo2" + k);
			personCacheBinary.put(k, bob.build());
		});
	}

	private void so() {
		log.debug("so service");

		Set<Integer> keys = IntStream.iterate(0, i -> i + 1).limit(records).boxed().collect(Collectors.toSet());

		personCache.getAll(keys).forEach((k, v) -> {
			v.setT1("so1" + k);
			v.setT2("so2" + k);

			personCache.put(k, v);
		});
	}

	private void printAll() {
		personCache.forEach(r -> {
			log.debug("r: " + r.getValue());
		});
	}

	private void init() {
		log.debug("init service");

		records = Integer.valueOf(ip.getOther().get("records"));
		personCache = cr.personCache("person-cache2", schema, "Default_Region");

		IntStream.iterate(0, i -> i + 1).limit(records).forEach(i -> {
			personCache.put(i, new Person(i, "s1" + i, "s2" + i));
		});
	}

	public void main() {
		log.debug("main service");

		init();
		bo();
		printAll();
		so();
		printAll();
	}
}

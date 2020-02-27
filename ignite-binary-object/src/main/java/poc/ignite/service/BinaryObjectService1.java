package poc.ignite.service;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.binary.BinaryObjectBuilder;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import poc.ignite.domain.Person;
import poc.ignite.repos.CacheRepository;

@State(Scope.Thread)
@BenchmarkMode(Mode.All)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
public class BinaryObjectService1 {
	IgniteCache<Integer, Person> personCache;
	IgniteCache<Integer, BinaryObject> personCacheBinary;
	int records;

	@Setup
	public void buildMeCounterHearty() {
		personCache = CacheRepository.personCache("person-cache2", "sts1", "Default_Region");
		personCacheBinary = personCache.withKeepBinary();
		records = 10;
	}

	@Benchmark
	public void bo() {
		// System.out.println("bo service");

		Set<Integer> keys = IntStream.iterate(0, i -> i + 1).limit(records).boxed().collect(Collectors.toSet());

		personCacheBinary.getAll(keys).forEach((k, v) -> {
			BinaryObjectBuilder bob = v.toBuilder();
			bob.setField("t1", "bo1" + k);
			bob.setField("t2", "bo2" + k);
			personCacheBinary.put(k, bob.build());
		});
	}

	@Benchmark
	public void so() {
		// System.out.println("so service");

		Set<Integer> keys = IntStream.iterate(0, i -> i + 1).limit(records).boxed().collect(Collectors.toSet());

		personCache.getAll(keys).forEach((k, v) -> {
			v.setT1("so1" + k);
			v.setT2("so2" + k);

			personCache.put(k, v);
		});
	}

	private void init() {
		System.out.println("init service");
	}

	public void main() {
		System.out.println("main service");

		init();
	}
}

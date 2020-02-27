package poc.ignite.service;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.binary.BinaryObjectBuilder;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import poc.ignite.domain.Person;
import poc.ignite.repos.CacheRepository;

public class BinaryObjectService {

	@State(Scope.Benchmark)
	public static class BenchmarkState {
		int records;
		IgniteCache<Integer, Person> personCache;

		@Setup
		public void setup() {
			records = Integer.valueOf(System.getProperty("records"));
			personCache = CacheRepository.personCache("person-cache2", "sts1", "Default_Region");

			IntStream.iterate(0, i -> i + 1).limit(records).forEach(i -> {
				personCache.put(i, new Person(i, "s1" + i, "s2" + i));
			});
		}
	}

	@BenchmarkMode(Mode.All)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void bo(BenchmarkState state) {
		System.out.println("bo service");

		IgniteCache<Integer, BinaryObject> personCacheBinary = state.personCache.withKeepBinary();

		Set<Integer> keys = IntStream.iterate(0, i -> i + 1).limit(state.records).boxed().collect(Collectors.toSet());

		personCacheBinary.getAll(keys).forEach((k, v) -> {
			BinaryObjectBuilder bob = v.toBuilder();
			bob.setField("t1", "bo1" + k);
			bob.setField("t2", "bo2" + k);
			personCacheBinary.put(k, bob.build());
		});
	}

	@BenchmarkMode(Mode.All)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void so(BenchmarkState state) {
		System.out.println("so service");

		Set<Integer> keys = IntStream.iterate(0, i -> i + 1).limit(state.records).boxed().collect(Collectors.toSet());

		state.personCache.getAll(keys).forEach((k, v) -> {
			v.setT1("so1" + k);
			v.setT2("so2" + k);

			state.personCache.put(k, v);
		});
	}

	private void printAll(BenchmarkState state) {
		state.personCache.forEach(r -> {
			System.out.println("r: " + r.getValue());
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

package poc.ignite;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import poc.ignite.service.BinaryObjectService1;

public class IgniteApp {

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder().include(BinaryObjectService1.class.getSimpleName()).forks(1).build();

		new Runner(opt).run();
	}
}

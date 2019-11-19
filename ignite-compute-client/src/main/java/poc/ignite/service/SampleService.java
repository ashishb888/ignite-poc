package poc.ignite.service;

import org.apache.ignite.services.Service;

public interface SampleService extends Service {
	void call();

	void run();
}

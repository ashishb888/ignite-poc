package poc.ignite.service;

import org.apache.ignite.services.Service;

public interface SampleService extends Service {
	long call(String cacheName, String affKey);

	void run(String cacheName, String affKey);
}

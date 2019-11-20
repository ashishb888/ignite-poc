package poc.ignite.service;

import org.apache.ignite.Ignite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import poc.ignite.service.impl.SampleServiceImpl;

@Service
@Slf4j
public class ComputeService {

	@Autowired
	private Ignite ignite;
	@Autowired
	private SampleServiceImpl sampleService;

	private void init() {
		log.debug("init service");

		ignite.services().deployNodeSingleton("sampleService", sampleService);
	}

	private void start() {
		log.debug("start service");

		init();
	}

	public void main() {
		log.debug("main service");

		start();
	}
}

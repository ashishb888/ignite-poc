package poc.ignite.service;

import org.apache.ignite.Ignite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IgniteService {

	@Autowired
	private Ignite ignite;

	@Autowired
	private KafkaIgniteService kis;

	public void main() {
		log.debug("main service");
		log.debug("name: " + ignite.name());

		kis.main();
	}
}

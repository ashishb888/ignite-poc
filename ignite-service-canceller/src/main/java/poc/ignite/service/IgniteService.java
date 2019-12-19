package poc.ignite.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.cache.QueryIndex;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@SuppressWarnings("unused")
public class IgniteService {

	@Autowired
	private Ignite ignite;

	private void start() {
		log.debug("start service");

		ignite.services().serviceDescriptors().forEach(r -> {
			log.debug("r: " + r.name());
		});

		ignite.services().cancelAll();

		log.debug("Cancelled all services");
	}

	public void main() {
		log.debug("main service");

		start();
	}
}

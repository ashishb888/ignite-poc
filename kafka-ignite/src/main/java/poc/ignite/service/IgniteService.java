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
import poc.ignite.domain.StockTrade;
import poc.ignite.domain.StockTradeKey;

@Service
@Slf4j
@SuppressWarnings("unused")
public class IgniteService {

	@Autowired
	private Ignite ignite;
	private IgniteCache<StockTradeKey, StockTrade> personCache;

	@Autowired
	private KafkaIgniteService kis;

	public void main() {
		log.debug("main service");
		log.debug("name: " + ignite.name());

		kis.main();
	}
}

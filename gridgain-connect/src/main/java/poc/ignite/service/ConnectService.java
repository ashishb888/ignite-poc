package poc.ignite.service;

import java.util.Arrays;
import java.util.Properties;

import javax.cache.CacheException;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.binary.BinaryObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import poc.ignite.domain.SQL_PUBLIC_PERSON_9af4b0f8_1335_4e0d_931f_19fb389e119e;
import poc.ignite.properties.IgniteProperties;
import poc.ignite.properties.KafkaProperties;

@Service
@Slf4j
public class ConnectService {

	@Autowired
	private Ignite ignite;
	@Autowired
	private IgniteProperties ip;
	@Autowired
	private KafkaProperties kp;

	private void consume() {
		log.debug("consume service");

		while (true) {

			try {
//				IgniteCache<Integer, SQL_PUBLIC_PERSON_9af4b0f8_1335_4e0d_931f_19fb389e119e> cache = ignite
//						.cache("SQL_PUBLIC_PERSON");
//				cache.forEach(r -> {
//					log.info("k: " + r.getKey() + ",v: " + r.getValue());
//				});

				ignite.cache(ip.getOther().get("cacheName")).withKeepBinary().forEach(r -> {
					log.info("k: " + r.getKey() + ",v: " + r.getValue());
//					log.info("k: " + Arrays.toString((byte[]) r.getKey()) + ",v: "
//							+ Arrays.toString((byte[]) r.getValue()));
					BinaryObject key = (BinaryObject) r.getKey();
					log.info("key: " + key.field("id") + ", " + key.field("cityId"));

					BinaryObject value = (BinaryObject) r.getValue();
					log.info("value: " + value.field("id") + ", " + value.field("cityId") + ", " + value.field("name"));

				});
			} catch (CacheException e) {
				log.error(e.getMessage(), e);
			}

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	private Properties configs() {
		log.debug("configs service");

		Properties configs = new Properties();

		kp.getKafkaConsumer().forEach((k, v) -> {
			configs.put(k, v);
		});

		return configs;
	}

	private void init() {
		log.debug("init service");

	}

	private void start() {
		log.debug("start service");

		init();
		consume();
	}

	public void main() {
		log.debug("main service");

		start();
	}
}

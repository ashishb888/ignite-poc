package poc.ignite.config;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.configuration.TransactionConfiguration;
import org.apache.ignite.logger.slf4j.Slf4jLogger;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import poc.ignite.properties.IgniteProperties;

@Configuration
@Slf4j
public class BeansConfig {

	@Autowired
	private IgniteProperties ip;

	@Bean
	public Ignite ignite() {
		log.info("ignite bean service");

		Ignite ignite = null;

		try {

			TcpDiscoverySpi spi = new TcpDiscoverySpi();

			spi.setLocalPort(ip.getTcpDiscoverySpi().get("localPort"));
			spi.setLocalPortRange(100);

			TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
			ipFinder.setAddresses(ip.getIps());
			spi.setIpFinder(ipFinder);
			IgniteConfiguration igniteConfiguration = new IgniteConfiguration();

			DataStorageConfiguration storageCfg = new DataStorageConfiguration();
			storageCfg.getDefaultDataRegionConfiguration()
					.setInitialSize(Long.valueOf(ip.getDefaultRegion().get("initial")));
			storageCfg.getDefaultDataRegionConfiguration().setMaxSize(Long.valueOf(ip.getDefaultRegion().get("max")));
			igniteConfiguration.setDataStorageConfiguration(storageCfg);

			TcpCommunicationSpi commSpi = new TcpCommunicationSpi();
			commSpi.setLocalPort(ip.getTcpCommunicationSpi().get("localPort"));
			commSpi.setMessageQueueLimit(1024);
			commSpi.setSocketWriteTimeout(10000L);

			igniteConfiguration.setDiscoverySpi(spi);
			igniteConfiguration.setCommunicationSpi(commSpi);

			igniteConfiguration.setFailureDetectionTimeout(90000);
			igniteConfiguration.setDiscoverySpi(spi);
			igniteConfiguration.setIncludeEventTypes();
			igniteConfiguration.setPublicThreadPoolSize(16);
			igniteConfiguration.setSystemThreadPoolSize(16);
			igniteConfiguration.setPeerClassLoadingEnabled(true);
			igniteConfiguration.setGridLogger(new Slf4jLogger());
			igniteConfiguration.setClientMode(true);

			TransactionConfiguration txCfg = new TransactionConfiguration();
			txCfg.setDefaultTxTimeout(ip.getTransactions().get("defaultTxTimeout"));
			txCfg.setTxTimeoutOnPartitionMapExchange(ip.getTransactions().get("txTimeoutOnPME"));
			igniteConfiguration.setTransactionConfiguration(txCfg);

			ignite = Ignition.getOrStart(igniteConfiguration);
		} catch (IgniteException e) {
			log.error("Exception", e);
		}

		return ignite;
	}
}

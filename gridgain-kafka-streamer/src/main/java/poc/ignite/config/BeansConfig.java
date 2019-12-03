package poc.ignite.config;

import java.util.Collections;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.logger.slf4j.Slf4jLogger;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.gridgain.grid.configuration.GridGainConfiguration;
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

			spi.setLocalPort(42500);
			spi.setLocalPortRange(100);

			TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();

			// ipFinder.setAddresses(Arrays.asList("172.17.104.233:42500..42700"));
			ipFinder.setAddresses(ip.getIps());

			spi.setIpFinder(ipFinder);
			IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
			igniteConfiguration.setUserAttributes(Collections.singletonMap("nodeName", ip.getOther().get("nodeName")));

			DataStorageConfiguration storageCfg = new DataStorageConfiguration();

			DataRegionConfiguration defaultRegion = new DataRegionConfiguration();
			defaultRegion.setName("Default_Region");
			defaultRegion.setInitialSize(ip.getDataRegion().get("initial") * 1024 * 1024);
			defaultRegion.setMaxSize(ip.getDataRegion().get("max") * 1024 * 1024);

			storageCfg.setDefaultDataRegionConfiguration(defaultRegion);

			igniteConfiguration.setDataStorageConfiguration(storageCfg);

			igniteConfiguration.setFailureDetectionTimeout(90000);

			TcpCommunicationSpi commSpi = new TcpCommunicationSpi();
			commSpi.setLocalPort(42100);

			commSpi.setMessageQueueLimit(1024);
			commSpi.setSocketWriteTimeout(10000L);

			igniteConfiguration.setCommunicationSpi(commSpi);

			// All properties should be in YAML
			igniteConfiguration.setDiscoverySpi(spi);
			igniteConfiguration.setIncludeEventTypes();

			if (ip.getThreads().get("enabled") == 1) {
				igniteConfiguration.setPublicThreadPoolSize(ip.getThreads().get("public"));
				igniteConfiguration.setSystemThreadPoolSize(ip.getThreads().get("system"));
				igniteConfiguration.setDataStreamerThreadPoolSize(ip.getThreads().get("dataStreamer"));
				igniteConfiguration.setQueryThreadPoolSize(ip.getThreads().get("queries"));
				igniteConfiguration.setServiceThreadPoolSize(ip.getThreads().get("service"));
				igniteConfiguration.setStripedPoolSize(ip.getThreads().get("striped"));

				igniteConfiguration.setIgfsThreadPoolSize(ip.getThreads().get("igfs"));
				igniteConfiguration.setManagementThreadPoolSize(ip.getThreads().get("management"));
				igniteConfiguration.setPeerClassLoadingThreadPoolSize(ip.getThreads().get("peerClass"));
				igniteConfiguration.setRebalanceThreadPoolSize(ip.getThreads().get("rebalance"));
				igniteConfiguration.setUtilityCachePoolSize(ip.getThreads().get("utilityCache"));
			}

			log.info("public: " + igniteConfiguration.getPublicThreadPoolSize());
			log.info("system: " + igniteConfiguration.getSystemThreadPoolSize());
			log.info("dataStreamer: " + igniteConfiguration.getDataStreamerThreadPoolSize());
			log.info("queries: " + igniteConfiguration.getQueryThreadPoolSize());
			log.info("service: " + igniteConfiguration.getServiceThreadPoolSize());
			log.info("striped: " + igniteConfiguration.getStripedPoolSize());
			log.info("getIgfsThreadPoolSize: " + igniteConfiguration.getIgfsThreadPoolSize());
			log.info("getManagementThreadPoolSize: " + igniteConfiguration.getManagementThreadPoolSize());
			log.info("getPeerClassLoadingThreadPoolSize: " + igniteConfiguration.getPeerClassLoadingThreadPoolSize());
			log.info("getRebalanceThreadPoolSize: " + igniteConfiguration.getRebalanceThreadPoolSize());
			log.info("getUtilityCacheThreadPoolSize: " + igniteConfiguration.getUtilityCacheThreadPoolSize());
			igniteConfiguration.getIgfsThreadPoolSize();

			igniteConfiguration.setPeerClassLoadingEnabled(true);
			// igniteConfiguration.setIgniteInstanceName("test";)

			igniteConfiguration.setGridLogger(new Slf4jLogger());

//			GridGainConfiguration ggc = new GridGainConfiguration();
//			ggc.setLicenseUrl("");

			ignite = Ignition.getOrStart(igniteConfiguration);

			log.info("nodeName: " + ignite.cluster().localNode().attribute("nodeName"));

		} catch (IgniteException e) {
			log.error("Exception", e);
		}

		return ignite;
	}
}

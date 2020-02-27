package poc.ignite.config;

import java.util.Arrays;

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

public class BeansConfig {

	public static Ignite ignite() {
		System.out.println("ignite bean service");

		Ignite ignite = null;

		try {

			TcpDiscoverySpi spi = new TcpDiscoverySpi();

			spi.setLocalPort(42500);
			spi.setLocalPortRange(100);

			TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
			ipFinder.setAddresses(Arrays.asList("172.17.5.36:42500..42700"));
			spi.setIpFinder(ipFinder);
			IgniteConfiguration igniteConfiguration = new IgniteConfiguration();

			// Changing total RAM size to be used by Ignite Node.
			DataStorageConfiguration storageCfg = new DataStorageConfiguration();
			DataRegionConfiguration defaultRegion = new DataRegionConfiguration();
			defaultRegion.setName("Default_Region");
			defaultRegion.setInitialSize(10435456);
			defaultRegion.setMaxSize(10435456);
			storageCfg.setDefaultDataRegionConfiguration(defaultRegion);

			TcpCommunicationSpi commSpi = new TcpCommunicationSpi();
			commSpi.setLocalPort(42100);
			commSpi.setMessageQueueLimit(1024);
			commSpi.setSocketWriteTimeout(10000L);

			igniteConfiguration.setDiscoverySpi(spi);
			igniteConfiguration.setCommunicationSpi(commSpi);

			igniteConfiguration.setFailureDetectionTimeout(90000);
			// All properties should be in YAML
			igniteConfiguration.setDiscoverySpi(spi);
			igniteConfiguration.setIncludeEventTypes();
			igniteConfiguration.setPublicThreadPoolSize(16);
			igniteConfiguration.setSystemThreadPoolSize(16);
			igniteConfiguration.setPeerClassLoadingEnabled(true);
			// igniteConfiguration.setIgniteInstanceName("test";)

			igniteConfiguration.setGridLogger(new Slf4jLogger());

			ignite = Ignition.getOrStart(igniteConfiguration);
		} catch (IgniteException e) {
			e.printStackTrace();
		}

		return ignite;
	}
}

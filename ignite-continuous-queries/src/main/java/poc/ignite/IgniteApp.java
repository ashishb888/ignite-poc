package poc.ignite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;
import poc.ignite.service.IgniteService;

@SpringBootApplication
@Slf4j
public class IgniteApp implements CommandLineRunner {

	@Autowired
	private IgniteService is;

	public static void main(String[] args) {
		SpringApplication.run(IgniteApp.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("run service");

		is.main();
	}
}

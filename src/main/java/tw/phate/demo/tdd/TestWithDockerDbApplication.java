package tw.phate.demo.tdd;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestWithDockerDbApplication implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(TestWithDockerDbApplication.class);

  @Autowired
  private CustomerDao customerDao;

  public static void main(String[] args) {
    SpringApplication.run(TestWithDockerDbApplication.class, args);
  }

  @Override
  public void run(String... strings) throws Exception {
    log.info("Creating tables");

    customerDao.dropTableIfExists();
    customerDao.createTable();
    customerDao.init();

    List<Customer> customers = customerDao.query("Josh");
    customers.forEach(customer -> log.info(customer.toString()));

    customerDao.dropTableIfExists();
  }
}

package tw.phate.demo.tdd.test;

import static org.assertj.db.api.Assertions.assertThat;
import static org.assertj.db.output.Outputs.output;

import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.assertj.db.type.Table;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import tw.phate.demo.tdd.Customer;
import tw.phate.demo.tdd.CustomerDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CustomerDaoTests {

  private static final Logger log = LoggerFactory.getLogger(CustomerDaoTests.class);

  @Autowired
  private DataSource dataSource;

  @Autowired
  private CustomerDao customerDao;

  @Before
  public void setup() throws SQLException {
    customerDao.createTable();
  }

  @After
  public void teardown() throws SQLException {
    customerDao.dropTableIfExists();
  }

  @Test
  public void validTable() {
    Table table = new Table(dataSource, "customers");
//    output(table).toConsole();
    assertThat(table)
        .column().hasColumnName("ID")
        .column().hasColumnName("first_name")
        .column().hasColumnName("last_name")
        .isEmpty();
  }

  @Test
  public void init() {
    customerDao.init();

    Table table = new Table(dataSource, "customers");
//    output(table).toConsole();
    assertThat(table).column("first_name")
        .hasValues("John", "Jeff", "Josh", "Josh");
  }

  @Test
  public void query() {
    customerDao.init();

    List<Customer> customers = customerDao.query("Josh");
    Table table = new Table(dataSource, "customers");
    output(table).toConsole();
    customers.forEach(customer -> {
      log.info(customer.toString());
      assert(customer.getFirstName().equals("Josh"));
      assertThat(table).row(Math.toIntExact(customer.getId()) - 1)
          .hasValues(Math.toIntExact(customer.getId()), customer.getFirstName(), customer.getLastName());
    });
  }
}

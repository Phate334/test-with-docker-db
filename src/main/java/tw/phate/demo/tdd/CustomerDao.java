package tw.phate.demo.tdd;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

@Component
public class CustomerDao {

  private static final Logger log = LoggerFactory.getLogger(CustomerDao.class);
  @Autowired
  private JdbcTemplate jdbcTemplate;


  public void dropTableIfExists() throws SQLException {
    log.info("Dropping tables");
    Connection connection = jdbcTemplate.getDataSource().getConnection();
    ScriptUtils.executeSqlScript(connection, new ClassPathResource("database/drop.sql"));
  }

  public void createTable() throws SQLException {
    log.info("Creating tables");
    Connection connection = jdbcTemplate.getDataSource().getConnection();
    ScriptUtils.executeSqlScript(connection, new ClassPathResource("database/create.sql"));
  }

  public void init() {
    // Split up the array of whole names into an array of first/last names
    List<Object[]> splitUpNames = Arrays.asList("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long")
        .stream()
        .map(name -> name.split(" "))
        .collect(Collectors.toList());

    // Use a Java 8 stream to print out each tuple of the list
    splitUpNames.forEach(
        name -> log.info(String.format("Inserting customer record for %s %s", name[0], name[1])));

    // Uses JdbcTemplate's batchUpdate operation to bulk load data
    jdbcTemplate
        .batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?,?)", splitUpNames);
  }

  public List<Customer> query(String firstName) {
    log.info("Querying for customer records where first_name = '" + firstName + "':");
    return jdbcTemplate.query(
        "SELECT id, first_name, last_name FROM customers WHERE first_name = ?",
        new Object[]{firstName},
        (rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("first_name"),
            rs.getString("last_name"))
    );
  }
}

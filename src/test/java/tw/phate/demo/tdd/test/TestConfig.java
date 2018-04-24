package tw.phate.demo.tdd.test;

import com.mysql.cj.jdbc.MysqlDataSource;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import tw.phate.demo.tdd.CustomerDao;

@Configuration
public class TestConfig {

  @Bean
  public JdbcTemplate jdbcTemplate() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate();
    jdbcTemplate.setDataSource(dataSource());
    return jdbcTemplate;
  }

  @Bean
  public DataSource dataSource() {
    MysqlDataSource dataSource = new MysqlDataSource();
    dataSource.setURL("jdbc:mysql://localhost/tdd_test");
    dataSource.setUser("root");
    dataSource.setPassword("root");
    return dataSource;
  }

  @Bean
  public CustomerDao customerDao() {
    return new CustomerDao();
  }
}

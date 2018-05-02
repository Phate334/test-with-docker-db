package tw.phate.demo.tdd.test.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.Ports.Binding;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.netty.NettyDockerCmdExecFactory;
import com.mysql.cj.jdbc.MysqlDataSource;
import java.io.IOException;
import java.net.ServerSocket;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import tw.phate.demo.tdd.CustomerDao;

@Configuration
public class DockerTestConfig {

  public static final String DOCKER_HOST = "tcp://localhost:2375";

  private int dbPort;

  public DockerTestConfig() throws IOException {
    this.dbPort = freePort();
  }

  @Bean
  public DockerClient dockerClient() {
    DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
        .withDockerTlsVerify(false)
        .withDockerHost(DOCKER_HOST)
        .build();
    return DockerClientBuilder.getInstance(config)
        .withDockerCmdExecFactory(new NettyDockerCmdExecFactory())
        .build();
  }

  @Bean
  public CreateContainerResponse container() {
    ExposedPort exposedPort = ExposedPort.tcp(3306);
    Ports portBindings = new Ports();
    portBindings.bind(exposedPort, Binding.bindPort(dbPort));
    CreateContainerResponse container = dockerClient()
        .createContainerCmd("mariadb")
        .withExposedPorts(exposedPort)
        .withPortBindings(portBindings)
        .exec();
    return container;
  }

  @Bean
  public JdbcTemplate jdbcTemplate() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate();
    jdbcTemplate.setDataSource(dataSource());
    return jdbcTemplate;
  }

  @Bean
  public DataSource dataSource() {
    MysqlDataSource dataSource = new MysqlDataSource();
    dataSource.setURL(
        String.format("jdbc:mysql://localhost:%d/tdd_test", dbPort)
    );
    dataSource.setUser("root");
    dataSource.setPassword("root");
    return dataSource;
  }

  @Bean
  public CustomerDao customerDao() {
    return new CustomerDao();
  }

  private int freePort() throws IOException {
    ServerSocket socket = new ServerSocket(0);
    int freePort = socket.getLocalPort();
    socket.close();
    return freePort;
  }
}

package tw.phate.demo.tdd.test.docker;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Info;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DockerTestConfig.class)
public class CustomerDaoDockerTest {

  private static final Logger log = LoggerFactory.getLogger(CustomerDaoDockerTest.class);

  @Autowired
  private DockerClient dockerClient;

  @Autowired
  private CreateContainerResponse container;

//  @Autowired
//  private DataSource dataSource;

  @Test
  public void test() {
    Info info = dockerClient.infoCmd().exec();
    log.info(info.getOsType());
    log.info(info.getServerVersion());
  }

  @Test
  public void containerTest() {
    log.info(container.getId());
    log.info(container.toString());
    dockerClient.removeContainerCmd(container.getId());
  }
}

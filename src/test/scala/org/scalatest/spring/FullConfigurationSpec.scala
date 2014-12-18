package org.scalatest.spring

import org.springframework.test.context.{ActiveProfiles, ContextConfiguration}
import org.springframework.transaction.annotation.{Isolation, Propagation, Transactional}
import org.scalatest.{BeforeAndAfterEach, BeforeAndAfter, FunSpec}
import org.springframework.test.context.transaction.TransactionConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.context.annotation.{Bean, Profile, Configuration}
import org.scalatest.spring.config.DataConfig

//for xml based configuration: @ContextConfiguration(locations = Array("/app-config.xml", "/data-config.xml"))
@ContextConfiguration(classes = Array(classOf[DevConfig], classOf[ProdConfig], classOf[DataConfig]))
@ActiveProfiles(Array("dev", "integration"))
@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, readOnly = true)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
class FullConfigurationSpec extends FunSpec with SpringSupport with BeforeAndAfterEach {

  @Autowired var profileConfig: ProfileConfig = _

  override def beforeEach: Unit = {
    println(s"beforeEach should run without transaction support")
    assert(profileConfig != null)
    assert(TransactionSynchronizationManager.isActualTransactionActive === false)
  }

  describe("full configuration spec") {
    it("should run in transaction with dependencies injected") {
      assert(profileConfig.profile === "dev")
      assert(TransactionSynchronizationManager.isActualTransactionActive)
      assert(TransactionSynchronizationManager.getCurrentTransactionIsolationLevel === TransactionDefinition.ISOLATION_READ_COMMITTED)
      assert(TransactionSynchronizationManager.isCurrentTransactionReadOnly === true)
    }
  }

  override def afterEach: Unit = {
    println(s"afterEach should run without transaction support")
    assert(TransactionSynchronizationManager.isActualTransactionActive === false)
  }
}

trait ProfileConfig {
  val profile: String
}

@Configuration
@Profile(Array("dev"))
class DevConfig{

  @Bean def profileConfig(): ProfileConfig = {
    new ProfileConfig {
      val profile = "dev"
    }
  }

}

@Configuration
@Profile(Array("prod"))
class ProdConfig{

  @Bean def profileConfig(): ProfileConfig = {
    new ProfileConfig {
      val profile = "prod"
    }
  }

}

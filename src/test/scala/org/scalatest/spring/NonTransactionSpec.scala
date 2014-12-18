package org.scalatest.spring

import org.scalatest.FunSpec
import org.springframework.test.context.ContextConfiguration
import org.scalatest.spring.config.{AppConfig, DataConfig}
import org.springframework.beans.factory.annotation.Autowired
import org.scalatest.spring.service.DemoService
import org.springframework.transaction.support.TransactionSynchronizationManager

@ContextConfiguration(classes = Array(classOf[DataConfig], classOf[AppConfig]))
//not annotated with @Transactional
class NonTransactionSpec extends FunSpec with SpringSupport {

  @Autowired var demoService: DemoService = _

  describe("non transaction spec") {
    it("should not run in transaction without @Transactional") {
      assert(demoService != null)
      assert(TransactionSynchronizationManager.isActualTransactionActive === false)
    }
  }

}

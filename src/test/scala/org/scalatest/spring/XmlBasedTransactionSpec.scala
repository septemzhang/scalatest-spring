package org.scalatest.spring

import org.springframework.test.context.ContextConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.scalatest.spring.service.DemoService
import org.springframework.transaction.support.TransactionSynchronizationManager

//override @ContextConfiguration with xml base configurations
@ContextConfiguration(locations = Array("/app-config.xml", "/data-config.xml"))
class XmlBasedTransactionSpec extends AbstractSpringSupportSpec {

  @Autowired var demoService: DemoService = _

  describe("xml based spring transaction spec") {
    it("should run in transaction with dependencies injected") {
      assert(demoService != null)
      assert(TransactionSynchronizationManager.isActualTransactionActive)
      assert(TransactionSynchronizationManager.getCurrentTransactionIsolationLevel === null)
      assert(TransactionSynchronizationManager.isCurrentTransactionReadOnly === false)
    }
  }
}


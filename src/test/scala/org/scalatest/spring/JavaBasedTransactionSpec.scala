package org.scalatest.spring

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.scalatest.spring.service.DemoService

class JavaBasedTransactionSpec extends AbstractSpringSupportSpec {

  @Autowired var demoService: DemoService = _

  describe("java based spring transaction spec") {
    it("should run in transaction with dependencies injected") {
      assert(demoService != null)
      assert(TransactionSynchronizationManager.isActualTransactionActive)
      assert(TransactionSynchronizationManager.getCurrentTransactionIsolationLevel === null)
      assert(TransactionSynchronizationManager.isCurrentTransactionReadOnly === false)
    }
  }

}

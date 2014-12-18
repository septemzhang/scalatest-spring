package org.scalatest.spring

import org.scalatest.FunSpec
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import org.scalatest.spring.config.AppConfig

class TransactionConfigurationSpec extends FunSpec {

  describe("spring transaction configuration") {

    it("should permit only one declaration of resource for @ContextConfiguration, either classes or locations") {
      intercept[IllegalStateException]{
        (new ContextConfigurationWithoutResourceSpec).execute()
      }

      intercept[IllegalStateException]{
        (new ContextConfigurationWithBothResourcesSpec).execute()
      }
    }

    it("should throw exception when @ContextConfiguration not found") {
      intercept[IllegalArgumentException]{
        (new WithoutContextConfigurationSpec).execute()
      }
    }
  }

  @ContextConfiguration
  class ContextConfigurationWithoutResourceSpec extends FunSpec with SpringSupport

  @ContextConfiguration(locations = Array("/app-config.xml"), classes = Array(classOf[AppConfig]))
  class ContextConfigurationWithBothResourcesSpec extends FunSpec with SpringSupport

  @Transactional
  class WithoutContextConfigurationSpec extends FunSpec with SpringSupport

}

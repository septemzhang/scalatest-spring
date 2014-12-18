package org.scalatest.spring

import org.scalatest._
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.context.ApplicationContext
import org.springframework.test.context.support.DelegatingSmartContextLoader
import org.springframework.test.context.transaction.TransactionConfiguration
import org.springframework.test.context.{ActiveProfiles, ContextConfiguration, MergedContextConfiguration}
import org.springframework.transaction.annotation.SpringTransactionAnnotationParser
import org.springframework.transaction.support.DefaultTransactionDefinition
import org.springframework.transaction.{PlatformTransactionManager, TransactionStatus}

/**
 * auto wire dependencies and begin/rollback transaction around every test in suite
 */
trait SpringSupport extends SuiteMixin { self: Suite =>

  private val suiteClass = this.getClass
  val applicationContext = loadApplicationContext()
  //attributes from @TransactionConfiguration
  val (transactionManagerName, defaultRollback) = parseTransactionConfiguration()
  //attributes from @Transactional
  private val transactionalAttribute = new SpringTransactionAnnotationParser().parseTransactionAnnotation(suiteClass)

  abstract override def run(testName: Option[String], args: Args): Status = {
    injectDependencies()
    super.run(testName, args)
  }

  abstract override def withFixture(test: NoArgTest): Outcome = {
    if (transactionalAttribute == null) {
      println(s"@Transactional not found on suite: $suiteClass, tests will be run without transaction support")
      super.withFixture(test)
    } else {
      println(s"explicit transaction attribute [$transactionalAttribute] found for test suite $suiteClass")
      val td = new DefaultTransactionDefinition(transactionalAttribute)
      val tm = applicationContext.getBean(transactionManagerName, classOf[PlatformTransactionManager])
      println(s"Began transaction for test [${test.name}]: transaction manager [$tm]; rollback [${defaultRollback}]")
      val transactionStatus = tm.getTransaction(td)
      try super.withFixture(test)
      finally {
        if (!transactionStatus.isCompleted) endTransaction(transactionStatus)
      }
    }
  }

  private def loadApplicationContext() : ApplicationContext = {
    val contextConfiguration = suiteClass.getAnnotation(classOf[ContextConfiguration])
    require(contextConfiguration != null, s"@ContextConfiguration not found for class $suiteClass")
    new DelegatingSmartContextLoader().loadContext(merge(contextConfiguration))
  }

  private def parseTransactionConfiguration() = {
    val tc = suiteClass.getAnnotation(classOf[TransactionConfiguration])
    val transactionManagerName = if (tc == null) "transactionManager" else tc.transactionManager
    val defaultRollback = if (tc == null) true else tc.defaultRollback
    (transactionManagerName, defaultRollback)
  }

  private def merge(cc: ContextConfiguration): MergedContextConfiguration = {
    val profiles = parseActiveProfiles
    new MergedContextConfiguration(suiteClass, cc.locations(), cc.classes(), profiles, new DelegatingSmartContextLoader)
  }

  private def parseActiveProfiles: Array[String] = {
    val activeProfiles = suiteClass.getAnnotation(classOf[ActiveProfiles])
    if (activeProfiles != null) {
      println(s"Retrieved @ActiveProfiles [$activeProfiles] for test class [$suiteClass].")

      val profiles = activeProfiles.profiles
      val value = activeProfiles.value

      if (profiles != null && profiles.length > 0 && value != null && value.length > 0) {
        val msg = s"Test class [$suiteClass] has been configured with @ActiveProfiles 'value' [$value] and 'profiles' [$profiles] attributes. " +
          s"Only one declaration of active bean definition profiles is permitted per @ActiveProfiles annotation."
        throw new IllegalStateException(msg)
      }

      if (profiles != null && profiles.length > 0) profiles else value
    } else Array[String]()
  }

  private def endTransaction(transactionStatus: TransactionStatus): Unit = {
    println((if (defaultRollback) "Rolled back" else "Committed") + " transaction after test execution")
    val transactionManager = applicationContext.getBean(transactionManagerName, classOf[PlatformTransactionManager])
    if (defaultRollback) {
      transactionManager.rollback(transactionStatus)
    } else {
      transactionManager.commit(transactionStatus)
    }
  }

  private def injectDependencies(): Unit = {
    val beanFactory = applicationContext.getAutowireCapableBeanFactory
    beanFactory.autowireBeanProperties(self, AutowireCapableBeanFactory.AUTOWIRE_NO, false)
    beanFactory.initializeBean(self, suiteClass.getName)
  }
  
}


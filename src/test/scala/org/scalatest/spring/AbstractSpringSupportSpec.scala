package org.scalatest.spring

import org.scalatest.FunSpec
import org.springframework.transaction.annotation.Transactional
import org.scalatest.spring.config.{DataConfig, AppConfig}
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = Array(classOf[DataConfig], classOf[AppConfig]))
@Transactional
abstract class AbstractSpringSupportSpec extends FunSpec with SpringSupport


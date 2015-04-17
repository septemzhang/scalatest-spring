`scalatest-spring` is a scalatest extension which provides dependencies injection and transaction managerment for scalatest

here is a full example:

```scala
    //spring bean definitions
    @ContextConfiguration(classes = Array(classOf[DevConfig], classOf[ProdConfig], classOf[DataConfig]))
    @ActiveProfiles(Array("dev", "integration"))
    //transaction attributes
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, readOnly = true)
    @TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
    //just mixin SpringSupport in your Spec
    class FullConfigurationSpec extends FunSpec with SpringSupport with BeforeAndAfterEach {

        //dependencies will be injected
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
```

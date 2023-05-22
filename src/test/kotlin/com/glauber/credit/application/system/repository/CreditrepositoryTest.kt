package com.glauber.credit.application.system.repository

import com.glauber.credit.application.system.model.Address
import com.glauber.credit.application.system.model.Credit
import com.glauber.credit.application.system.model.Customer
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month
import java.util.*

@ActiveProfiles("test")
@DataJpaTest // Usada para testar interações com o banco de dados em um ambiente Spring Boot
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Desativar a configuração automática do banco de dados durante os testes
// Indicando explicitamente que deseja usar o banco de dados configurado no ambiente de teste, em vez de substituí-lo
class CreditrepositoryTest {

    @Autowired
    lateinit var creditRepository: CreditRepository

    @Autowired
    lateinit var testEntityManager: TestEntityManager
    // Classe do spring para os testes de integração, gerenciamento de entitys pode ser feita durante os testes

    private lateinit var customer: Customer
    private lateinit var credit1: Credit
    private lateinit var credit2: Credit
    // Indica que a inicialização da variável será feita posterior, em vez de exigir uma atribuição imediata

    @BeforeEach
    fun setup() { // Anotação indica que esse método deve ser executado antes de cada teste.
        customer = testEntityManager.persist(buildCustomer()) // Persiste um objeto customer no banco de dados.
        credit1 = testEntityManager.persist(buildCredit(customer = customer))
        credit2 = testEntityManager.persist(buildCredit(customer = customer))
    }

    @Test
    fun `should find credit by credit code`() {
        //GIVEN
        val creditCode1 = UUID.fromString("aa547c0f-9a6a-451f-8c89-afddce916a29")
        val creditCode2 = UUID.fromString("49f740be-46a7-449b-84e7-ff5b7986d7ef")
        credit1.creditCode = creditCode1
        credit2.creditCode = creditCode2

        //WHEN
        val fakeCredit1: Credit = creditRepository.findByCreditCode(creditCode1)!!
        val fakeCredit2: Credit = creditRepository.findByCreditCode(creditCode2)!!

        //THEN
        Assertions.assertThat(fakeCredit1).isNotNull
        Assertions.assertThat(fakeCredit2).isNotNull
        Assertions.assertThat(fakeCredit1).isSameAs(credit1)
        Assertions.assertThat(fakeCredit2).isSameAs(credit2)
    }

    @Test
    fun `should find all credits by customer id`(){
        //GIVEN
        val customerId: Long = 1L
        //WHEN
        val creditList: List<Credit> = creditRepository.findAllByCustomerId(customerId)
        //THEN
        Assertions.assertThat(creditList).isNotEmpty
        Assertions.assertThat(creditList.size).isEqualTo(2)
        Assertions.assertThat(creditList).contains(credit1, credit2)
    }


    private fun buildCredit(
        creditValue: BigDecimal = BigDecimal.valueOf(500.0),
        dayFirstInstallment: LocalDate = LocalDate.of(2023, Month.APRIL, 22),
        numberOfInstallments: Int = 5,
        customer: Customer
    ): Credit = Credit(
        creditValue = creditValue,
        dayFirstInstallment = dayFirstInstallment,
        numberOfInstallment = numberOfInstallments,
        customer = customer
    )

    private fun buildCustomer(
        firstName: String = "Gabriel",
        lastName: String = "Souza",
        cpf: String = "28475934625",
        email: String = "gabriel@gmail.com",
        password: String = "12345",
        zipCode: String = "12345",
        street: String = "Rua do Gabriel",
        income: BigDecimal = BigDecimal.valueOf(1000.0),
    ) = Customer(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        password = password,
        address = Address(
            zipCode = zipCode,
            street = street,
        ),
        income = income,
    )
}
package com.glauber.credit.application.system.service

import com.glauber.credit.application.system.exception.BusinessException
import com.glauber.credit.application.system.model.Address
import com.glauber.credit.application.system.model.Customer
import com.glauber.credit.application.system.repository.CustomerRepository
import com.glauber.credit.application.system.service.impl.CustomerService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.util.*

// Boa prática repetir o nome da classe que vai testar e colocar test no final
// Deve-se definir um banco de dados especifico para fazer os testes.
// Posso duplicar o aplication.yml e deixar um como profile de testes.
@ActiveProfiles("test") // É usada para ativar perfis específicos durante a execução dos testes. Não precisa aqui, pq n sobe o contexto do spring
/*Dessa forma, você pode definir facilmente configurações específicas para o ambiente de teste e garantir
que os testes sejam executados com os componentes e propriedades corretos ativados pelo perfil "test".*/
@ExtendWith(MockKExtension::class) //Diz ao JUnit para aplicar a extensão do Mockito-Kotlin em testes na classe anotada
class CustomerServiceTest {
    @MockK
    lateinit var customerRepository: CustomerRepository // Variável simulada para teste, inicialização posterior.

    @InjectMockKs
    lateinit var customerService: CustomerService // Variável injetada com dependências simuladas.

    @Test
    fun `should create customer`() {
        // GIVEN - dados que preciso receber
        val fakeCustomer: Customer = buildCustomer()
        every { customerRepository.save(any()) } returns fakeCustomer //Simulando save de um customer, retornando o fake

        // WHEN - Onde terei o método que vou testar
        val actual: Customer = customerService.save(fakeCustomer)

        // THEN - Asserts, onde verifico se tenho o retorno que deveria
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(fakeCustomer)
        verify(exactly = 1) { customerRepository.save(fakeCustomer) }
    }

    @Test
    fun `should find customer by id`() {
        //GIVEN
        val fakeId: Long = Random().nextLong() //Gerar um id para testar o método
        val fakeCustomer: Customer = buildCustomer(id = fakeId)
        every { customerRepository.findById(fakeId) } returns Optional.of(fakeCustomer)
        //WHEN
        val actual: Customer = customerService.findById(fakeId)
        //THEN
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual)
            .isExactlyInstanceOf(Customer::class.java) // Verificar se estar retornando um customer
        Assertions.assertThat(actual).isSameAs(fakeCustomer)
        verify(exactly = 1) { customerRepository.findById(fakeId) }
    }

    @Test
    fun `should not find customer by invalid id and throw BussinesException`() {
        //GIVEN
        val fakeId: Long = Random().nextLong()
        every { customerRepository.findById(fakeId) } returns Optional.empty() // Retornarndo vazio porque o customer n existe aqui
        //  Optional vazio faz que ocorra a exception.
        //WHEN
        //THEN
        Assertions.assertThatExceptionOfType(BusinessException::class.java) // Confirmar se a exception de retorno é a bussinesException
            .isThrownBy { customerService.findById(fakeId) }
            .withMessage("Id $fakeId not found!") //Verifiquei se a mensagem é a mesma mensagem do método testado
        verify(exactly = 1) { customerRepository.findById(fakeId) }
    }

    @Test
    fun `should delete customer by id`() {
        //GIVEN
        val fakeId: Long = Random().nextLong()
        val fakeCustomer: Customer = buildCustomer(id = fakeId)
        every { customerRepository.findById(fakeId) } returns Optional.of(fakeCustomer)
        every { customerRepository.delete(fakeCustomer) } just runs
        //WHEN
        customerService.delete(fakeId)
        //THEN
        verify(exactly = 1) { customerRepository.findById(fakeId) }
        verify(exactly = 1) { customerRepository.delete(fakeCustomer) }
    }


    // Criei um buildCustomer para realizar os testes com todos os atributos de um customer poreḿ fake.
    private fun buildCustomer(
        firstName: String = "Glauber",
        lastName: String = "Souza",
        cpf: String = "28475934625",
        email: String = "glauber@gmail.com",
        password: String = "12345",
        zipCode: String = "12345",
        street: String = "Rua do glauber",
        income: BigDecimal = BigDecimal.valueOf(1000.0),
        id: Long = 1L
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
        id = id
    )
}

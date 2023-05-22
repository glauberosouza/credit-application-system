package com.glauber.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.glauber.credit.application.system.dto.CustomerDto
import com.glauber.credit.application.system.dto.CustomerUpdateDto
import com.glauber.credit.application.system.model.Customer
import com.glauber.credit.application.system.repository.CustomerRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.util.*

@SpringBootTest
/*Isso permite que você use recursos do Spring, como a injeção de dependências, e execute os testes
em um ambiente próximo ao ambiente de execução real da aplicação.*/
@ActiveProfiles("test")
/*Essa Anotação permite separar as configurações de teste das configurações de produção, garantindo que os
testes sejam executados de forma consistente e controlada.*/
@AutoConfigureMockMvc
/*pode enviar solicitações HTTP simuladas para os controladores da aplicação e verificar as respostas recebidas.
Isso ajuda a testar a camada de controle do aplicativo de forma isolada, sem a necessidade de iniciar um servidor HTTP real.*/
@ContextConfiguration
/*la permite configurar o contexto de teste personalizado, fornecendo informações sobre as classes de configuração,
arquivos XML de configuração ou outros recursos necessários para a execução dos testes.*/
class CustomerControllerTest {
    /*O uso de lateinit var é útil quando se sabe que uma variável será inicializada em algum momento antes
    de ser usada e queremos evitar a necessidade de atribuir um valor inicial imediatamente na declaração*/

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var mockMvc: MockMvc // Injetando automaticamente um objeto chamado mockMvc na classe em que ela está sendo utilizada

    @Autowired
    private lateinit var objectMapper: ObjectMapper //é uma biblioteca usada para converter objetos Java ou Kotlin em formatos de dados, como JSON, e vice-versa.

    companion object {
        const val URL: String = "/api/customers"
        /* está definindo um membro estático dentro de uma classe. Esse membro é uma constante chamada URL,
        que armazena a sequência "/api/customers". Essa constante pode ser acessada usando o nome da classe seguido de URL,
        como por exemplo NomeDaClasse.URL, para obter o valor "/api/customers" em qualquer parte do programa.*/
    }

    @BeforeEach
    fun setup() = customerRepository.deleteAll()

    @AfterEach
    fun tearDown() = customerRepository.deleteAll()
    /*Elas são responsáveis por garantir que o repositório de clientes esteja vazio antes de
    cada teste e seja esvaziado após a execução de cada teste, garantindo a consistência e a independência dos testes.*/

    @Test
    fun `should creat a customer and return 201 status`() {
        //GIVEN
        val customerDto: CustomerDto = builderCustomerDto()
        val valueAsString: String = objectMapper.writeValueAsString(customerDto)
        // Estou convertendo o Objeto CustomerDto em uma string JSON.
        //WHEN
        //THEN
        /*Essa linha de código está simulando o envio de uma requisição POST para uma determinada
        URL("/api/customers") usando o objeto mockMvc. Nessa requisição, estamos enviando um conteúdo no formato JSON.
        Em seguida, estamos verificando se o status de resposta retornado é "Created"
        (indicando que a requisição foi bem-sucedida).*/
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Glauber"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Souza"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("28475934625"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("glauber@email.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.income").value("1000.0"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("000000"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Rua do glauber, 123"))
            .andDo(MockMvcResultHandlers.print())
        /*Resumindo, esse código está testando se a resposta de uma requisição POST possui os
        valores esperados para cada campo do JSON, e também exibe informações da resposta no console.*/
    }

    @Test
    fun `should not save a customer with same CPF and return 409 status`() {
        //GIVEN
        customerRepository.save(builderCustomerDto().toEntity())
        val customerDto: CustomerDto = builderCustomerDto() // Criei outro Customer repetindo o cpf para testar
        val valueAsString: String = objectMapper.writeValueAsString(customerDto)
        //WHEN
        //THEN
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isConflict) // status 409 esperado
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(409))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.springframework.dao.DataIntegrityViolationException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save a customer with firstName empty and return 400 status`() {
        //GIVEN
        val customerDto: CustomerDto = builderCustomerDto(firstName = "")
        val valueAsString: String = objectMapper.writeValueAsString(customerDto)
        //WHEN
        //THEN
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL).content(valueAsString)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.springframework.web.bind.MethodArgumentNotValidException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should find customer by id and return status 200`() {
        //GIVEN
        val customer: Customer = customerRepository.save(builderCustomerDto().toEntity())
        //WHEN
        //THEN
        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/${customer.id}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Glauber"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Souza"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("28475934625"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("glauber@email.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.income").value("1000.0"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("000000"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Rua do glauber, 123"))
            .andDo(MockMvcResultHandlers.print())

    }

    @Test
    fun `should not find customer with invalid id and return 400 status`() {
        //GIVEN
        val invalidId: Long = 2L
        //WHEN
        //THEN
        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/$invalidId")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class com.glauber.credit.application.system.exception.BusinessException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should delete a customer by id and return 204 status`() {
        //GIVEN
        val customer: Customer = customerRepository.save(builderCustomerDto().toEntity())
        //WHEN
        //THEN
        mockMvc.perform(
            MockMvcRequestBuilders.delete("$URL/${customer.id}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not delete a customer by id and return 400 status`() {
        //GIVEN
        val invalidId: Long = 2
        //WHEN
        //THEN
        mockMvc.perform(
            MockMvcRequestBuilders.delete("$URL/${invalidId}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class com.glauber.credit.application.system.exception.BusinessException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }
    @Test
    fun `should update a costumer and return status 200`(){
        //GIVEN
        val customer: Customer = customerRepository.save(builderCustomerDto().toEntity())
        val customerUpdateDto: CustomerUpdateDto = builderCustomerUpdateDto()
        val valueAsString: String = objectMapper.writeValueAsString(customerUpdateDto)
        //WHEN
        //THEN
        mockMvc.perform(
            /*o uso do @RequestParam está relacionado ao trecho ?customerId=${customer.id} na URL,
            onde o valor do parâmetro de consulta é passado e vinculado ao parâmetro do método usando
            a anotação @RequestParam.*/
            MockMvcRequestBuilders.patch("$URL?customerId=${customer.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("GlauberUpdate"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("SouzaUpdate"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("28475934625"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("glauber@email.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.income").value("5000.0"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("45656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Rua Updated"))
            .andDo(MockMvcResultHandlers.print())
    }
    @Test
    fun `should not update a customer with invalid id and return 400 status`(){
        //GIVEN
        val invalidId: Long = Random().nextLong()
        val customerUpdateDto: CustomerUpdateDto = builderCustomerUpdateDto()
        val valueAsString: String = objectMapper.writeValueAsString(customerUpdateDto)
        //WHEN
        //THEN
        mockMvc.perform(
        MockMvcRequestBuilders.patch("$URL?customerId=$invalidId")
            .contentType(MediaType.APPLICATION_JSON)
            .content(valueAsString))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class com.glauber.credit.application.system.exception.BusinessException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    private fun builderCustomerDto(
        firstName: String = "Glauber",
        lastName: String = "Souza",
        cpf: String = "28475934625",
        email: String = "glauber@email.com",
        income: BigDecimal = BigDecimal.valueOf(1000.0),
        password: String = "1234",
        zipCode: String = "000000",
        street: String = "Rua do glauber, 123",
    ) = CustomerDto(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        income = income,
        password = password,
        zipCode = zipCode,
        street = street
    )
    private fun builderCustomerUpdateDto(
        firstName: String = "GlauberUpdate",
        lastName: String = "SouzaUpdate",
        income: BigDecimal = BigDecimal.valueOf(5000.0),
        zipCode: String = "45656",
        street: String = "Rua Updated"
    ): CustomerUpdateDto = CustomerUpdateDto(
        firstName = firstName,
        lastName = lastName,
        income = income,
        zipCode = zipCode,
        street = street
    )
}
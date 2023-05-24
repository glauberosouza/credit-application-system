package com.glauber.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.glauber.credit.application.system.dto.CreditDto
import com.glauber.credit.application.system.dto.CustomerDto
import com.glauber.credit.application.system.dto.response.CreditView
import com.glauber.credit.application.system.enummeration.Status
import com.glauber.credit.application.system.model.Address
import com.glauber.credit.application.system.model.Credit
import com.glauber.credit.application.system.model.Customer
import com.glauber.credit.application.system.repository.CreditRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CreditControllerTest {

    @Autowired
    lateinit var creditRepository: CreditRepository // Será inicializada só depois, por isso usei lateinit var

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun shouldCreateCreditAndReturnStatus201() {
        // GIVEN
        val customerDto = CustomerDto(
            firstName = "Glauber",
            lastName = "Souza",
            cpf = "28475934625",
            email = "glauber@email.com",
            income = BigDecimal.valueOf(1000.0),
            password = "1234",
            zipCode = "000000",
            street = "Rua do glauber, 123",
        )
        val creditDto = CreditDto(
            creditValue = BigDecimal.valueOf(500.0),
            dayFirstInstallment = LocalDate.of(2026, Month.JANUARY, 10),
            numberOfInstallment = 5,
            customerId = 1
        )

        val customerAsString = objectMapper.writeValueAsString(customerDto)
        val creditAsString = objectMapper.writeValueAsString(creditDto)

        // WHEN
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/credits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(creditAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
    }

    @Test
    fun `should find all by customerId`() {
        //GIVEN
        val customerId: Long = 1L
        //WHEN
        val creditList: List<Credit> = creditRepository.findAllByCustomerId(customerId)
        val creditAsString = objectMapper.writeValueAsString(creditList)
        //THEN
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/credits?customerId=1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(creditAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
    }
}

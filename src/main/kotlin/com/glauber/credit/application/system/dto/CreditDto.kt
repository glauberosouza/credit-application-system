package com.glauber.credit.application.system.dto

import com.glauber.credit.application.system.model.Credit
import com.glauber.credit.application.system.model.Customer
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDate

data class CreditDto(
    @field:NotNull val creditValue: BigDecimal,
    @field:Future val dayFirstInstallment: LocalDate,
    @field:Min(value = 1) @field:Max(value = 48) val numberOfInstallment: Int,
    @field:NotNull(message = "Invalid input") val customerId: Long
) {
    fun toEntity(): Credit = Credit(
        creditValue = this.creditValue,
        dayFirstInstallment = this.dayFirstInstallment,
        numberOfInstallment = this.numberOfInstallment,
        customer = Customer(id = this.customerId)
    )
}
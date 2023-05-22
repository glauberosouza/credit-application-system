package com.glauber.credit.application.system.service

import com.glauber.credit.application.system.model.Credit
import java.util.UUID

interface ICreditService {
    fun save(credit: Credit): Credit
    fun findAllByCustomer(customerId: Long): List<Credit>
    fun findByCreditCode(customerID: Long, creditCode: UUID): Credit
}
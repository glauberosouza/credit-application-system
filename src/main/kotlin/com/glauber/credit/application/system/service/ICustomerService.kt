package com.glauber.credit.application.system.service

import com.glauber.credit.application.system.model.Customer

interface ICustomerService {
    fun save(customer: Customer): Customer
    fun findById(id: Long): Customer
    fun delete(id: Long)
}
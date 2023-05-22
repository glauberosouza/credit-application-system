package com.glauber.credit.application.system.repository

import com.glauber.credit.application.system.model.Credit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CreditRepository: JpaRepository<Credit, Long> {
    fun findByCreditCode(creditCode: UUID) : Credit?
    @Query(value = "SELECT * FROM Credit WHERE CUSTOMER_ID =?1", nativeQuery = true) //O (?1) Pq só têm um parâmetro
    fun findAllByCustomerId(customerId: Long): List<Credit>
}
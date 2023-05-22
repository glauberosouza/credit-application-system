package com.glauber.credit.application.system.controller

import com.glauber.credit.application.system.dto.CreditDto
import com.glauber.credit.application.system.dto.CreditView
import com.glauber.credit.application.system.dto.CreditViewList
import com.glauber.credit.application.system.model.Credit
import com.glauber.credit.application.system.model.Customer
import com.glauber.credit.application.system.service.impl.CreditService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import java.util.stream.Collectors

@RestController
@RequestMapping("/api/credits")
class CreditController(
    private val creditService: CreditService
) {
    @PostMapping //RequestBody -> pelo corpo da requisição
    fun saveCredit(@RequestBody @Valid creditDto: CreditDto): ResponseEntity<String> {
        val credit: Credit = this.creditService.save(creditDto.toEntity())
        return ResponseEntity.status(HttpStatus.CREATED)
            .body("Credit ${credit.creditCode} - Customer ${credit.customer?.email} saved!")
    }

    @GetMapping
    fun findAllByCustomerId(@RequestParam(value = "customerId") customerId: Long):
            ResponseEntity<List<CreditViewList>> {
        val creditViewList: List<CreditViewList> = this.creditService.findAllByCustomer(customerId)
            .stream().map { credit: Credit -> CreditViewList(credit) }
            .collect(Collectors.toList())
        return ResponseEntity.status(HttpStatus.OK).body(creditViewList)
    }

    @GetMapping("/{creditCode}") // Não posso ter 2 gets iguais na url, não se pode ter o mesmo caminho
    fun findByCreditCode(
        @RequestParam(value = "customerId") // No postman informar o customerId no Params
        customerId: Long, @PathVariable creditCode: UUID //Obs: pegar o credit code para fazer a consulta no postman
    ): ResponseEntity<CreditView> {
        val credit: Credit = this.creditService.findByCreditCode(customerId, creditCode)
        return ResponseEntity.status(HttpStatus.OK).body(CreditView(credit))
    }
}
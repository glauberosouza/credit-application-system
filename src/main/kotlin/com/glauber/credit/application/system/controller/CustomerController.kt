package com.glauber.credit.application.system.controller

import com.glauber.credit.application.system.dto.CustomerDto
import com.glauber.credit.application.system.dto.CustomerUpdateDto
import com.glauber.credit.application.system.dto.response.CustomerView
import com.glauber.credit.application.system.model.Customer
import com.glauber.credit.application.system.service.impl.CustomerService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
 /*ResponseEntity nas classes dentro do controlador oferece maior controle sobre a
 resposta HTTP retornada pelas APIs, permitindo a personalização do código de status, dos cabeçalhos e
 do corpo da resposta. Isso permite que você desenvolva APIs mais flexíveis e aderentes às necessidades
 específicas da sua aplicação.*/
@RestController
@RequestMapping("/api/customers")
class CustomerController(
    private val customerService: CustomerService
) {

    @PostMapping
    fun saveCustomer(@RequestBody @Valid customerDto: CustomerDto): ResponseEntity<CustomerView> {
        val saveCustomer: Customer = this.customerService.save(customerDto.toEntity())
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerView(saveCustomer))
    }

    @GetMapping("/{id}") // PathVariable Indica que o Id do parâmetro vira através do caminho URL da requisição.
    fun findById(@PathVariable id: Long): ResponseEntity<CustomerView> {
        val customer: Customer = this.customerService.findById(id)
        return ResponseEntity.status(HttpStatus.OK).body(CustomerView(customer))
    }

    @PatchMapping // Essa anotação é usada para atualizar parcialmente uma entidade
    fun updateCustomer(
        @RequestParam(value = "customerId") //Utilizando o param não preciso colocar dentro de /{} o caminho
        id: Long, @RequestBody @Valid customerUpdateDto: CustomerUpdateDto
    ): ResponseEntity<CustomerView> {
        val customer: Customer = this.customerService.findById(id)
        val customerToUpdate: Customer = customerUpdateDto.toEntity(customer)
        val customerUpdated: Customer = this.customerService.save(customerToUpdate)
        return ResponseEntity.status(HttpStatus.OK).body(CustomerView(customerUpdated))
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Para o retorno 204, onde  o id não existe no banco de dados.
    fun deleteCustomer(@PathVariable id: Long) {
        this.customerService.delete(id)
    }

}
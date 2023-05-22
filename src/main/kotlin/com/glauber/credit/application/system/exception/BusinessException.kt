package com.glauber.credit.application.system.exception
// Sempre que eu quiser criar uma exception personalizada, extendo de RuntimeException e coloco a minha exception
// junto das outras na classe onde deixo as tratativas.
data class BusinessException(override val message: String?) : RuntimeException(message)

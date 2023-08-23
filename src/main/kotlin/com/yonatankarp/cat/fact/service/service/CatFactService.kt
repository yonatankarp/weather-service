package com.yonatankarp.cat.fact.service.service

import com.yonatankarp.cat.fact.service.repository.CatFactRepository
import com.yonatankarpcat.fact.client.ports.Fact
import org.springframework.stereotype.Service

@Service
class CatFactService(private val repository: CatFactRepository) {
    suspend fun storeFacts(facts: Set<Fact>) =
        facts.forEach { repository.storeFacts(it) }
}

package com.floxd.hogwartsbot.repository

import com.floxd.hogwartsbot.entity.Audit
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AuditRepository : CrudRepository<Audit, Long> {

}

package com.floxd.hogwartsbot.repository

import com.floxd.hogwartsbot.entity.Audit
import org.springframework.data.repository.CrudRepository

interface AuditRepository : CrudRepository<Audit, Long> {

}

package com.floxd.hogwartsbot.config

import org.hibernate.dialect.Dialect
import java.sql.Types

class SQLDialect : Dialect() {
    init {
        registerColumnType(Types.VARCHAR, "varchar")
        registerColumnType(Types.BIGINT, "bigint")
    }

    override fun getAddColumnString(): String? {
        return "add column"
    }
}

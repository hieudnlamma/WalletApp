package com.lmt.global.base.common

abstract class AbstractMapper<Entity, Domain> : BaseMapper<Entity, Domain> {

    override fun mapToDomainList(entities: List<Entity>): List<Domain> {
        return entities.map { mapToDomain(it) }
    }

    override fun mapToEntityList(domains: List<Domain>): List<Entity> {
        return domains.map { mapToEntity(it) }
    }
}
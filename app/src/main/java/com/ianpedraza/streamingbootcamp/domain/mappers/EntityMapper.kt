package com.ianpedraza.streamingbootcamp.domain.mappers

interface EntityMapper<Response, DomainModel> {
    fun fromResponseToDomainModel(
        response: Response
    ): DomainModel

    fun fromResponseListToDomainModelList(
        response: List<Response>
    ): List<DomainModel>
}

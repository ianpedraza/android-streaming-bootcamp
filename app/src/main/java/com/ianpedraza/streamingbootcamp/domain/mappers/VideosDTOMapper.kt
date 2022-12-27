package com.ianpedraza.streamingbootcamp.domain.mappers

import com.ianpedraza.streamingbootcamp.domain.Video
import com.ianpedraza.streamingbootcamp.framework.api.videos.response.VideoDTO
import javax.inject.Inject

class VideosDTOMapper @Inject constructor() : EntityMapper<VideoDTO, Video> {
    override fun fromResponseToDomainModel(response: VideoDTO): Video {
        return response.run {
            Video(
                id = videoId,
                title = title,
                description = description,
                creationDate = createdAt,
                tags = tags,
                url = assets.hls,
                thumbnail = assets.thumbnail
            )
        }
    }

    override fun fromResponseListToDomainModelList(response: List<VideoDTO>): List<Video> {
        return response.map { videoDTO ->
            fromResponseToDomainModel(videoDTO)
        }
    }
}

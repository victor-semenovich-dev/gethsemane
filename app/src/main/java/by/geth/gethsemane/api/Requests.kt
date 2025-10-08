package by.geth.gethsemane.api

abstract class BaseRequest {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

class GetBirthdaysRequest: BaseRequest()

abstract class GetPageRequest(val page: Int = 1, val limit: Int = 20): BaseRequest()

class GetNewsListRequest(page: Int = 1, limit: Int = 20): GetPageRequest(page, limit)

class GetNewsRequest(val id: Long): BaseRequest() {
    override fun equals(other: Any?): Boolean {
        if (!super.equals(other)) return false
        other as GetNewsRequest
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }
}

class GetWorshipNotesListRequest(page: Int = 1, limit: Int = 20): GetPageRequest(page, limit)

class GetToSeeChristListRequest(page: Int = 1, limit: Int = 20): GetPageRequest(page, limit)

class GetArticleDetailsRequest(val id: Long): BaseRequest() {
    override fun equals(other: Any?): Boolean {
        if (!super.equals(other)) return false
        other as GetArticleDetailsRequest
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }
}

class GetMusicGroupsRequest: BaseRequest()

class GetMusicGroupRequest(val id: Long): BaseRequest() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as GetMusicGroupRequest

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }
}

class GetSongsRequest(val groupId: Long): BaseRequest() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as GetSongsRequest

        if (groupId != other.groupId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + groupId.hashCode()
        return result
    }
}
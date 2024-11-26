package com.and04.naturealbum.data

import com.and04.naturealbum.data.repository.RetrofitRepository
import com.and04.naturealbum.data.room.HazardAnalyzeStatus
import com.and04.naturealbum.data.room.PhotoDetailDao
import com.and04.naturealbum.di.GreenEye
import javax.inject.Inject

interface Hazard {
    suspend fun hasPreviousAnalze(id: Int, photoData: String): HazardAnalyzeStatus
}

class HazardImpl @Inject constructor(
    private val photoDetailDao: PhotoDetailDao,
    @GreenEye private val retrofitRepository: RetrofitRepository,
) : Hazard {
    override suspend fun hasPreviousAnalze(id: Int, photoData: String): HazardAnalyzeStatus {
        return
    }

}

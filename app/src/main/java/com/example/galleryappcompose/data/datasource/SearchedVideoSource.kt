package com.example.galleryappcompose.data.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.galleryappcompose.data.models.PhotoResponse
import com.example.galleryappcompose.data.network.ApiService

class SearchedVideoSource(private val apiService: ApiService, private val query: String): PagingSource<Int, PhotoResponse.Video>() {
    override fun getRefreshKey(state: PagingState<Int, PhotoResponse.Video>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage: LoadResult.Page<Int, PhotoResponse.Video>? = state.closestPageToPosition(anchorPosition)
            anchorPage?.nextKey?.minus(1) ?: anchorPage?.prevKey?.plus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoResponse.Video> {
        return try {
            val curPage = params.key ?: 0
            val response = apiService.getSearchedVideos(curPage, params.loadSize, query)
            LoadResult.Page(
                data = response.videos!!,
                nextKey = if(curPage >= response.total_results) null else curPage.plus(1),
                prevKey = if(curPage == 1) null else curPage.minus(1)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
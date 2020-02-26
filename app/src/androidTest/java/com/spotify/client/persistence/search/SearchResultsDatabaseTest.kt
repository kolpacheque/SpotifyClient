package com.spotify.client.persistence.search

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.spotify.client.data.search.SearchResultItem
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SearchResultsDatabaseTest {

    private lateinit var database: SearchResultsDatabase
    private lateinit var dao: SearchResultDao

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, SearchResultsDatabase::class.java).build()
        dao = database.getDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    // region getSearchResults

    @Test
    @Throws(Exception::class)
    fun testGetSearchResults_1() {
        dao.insertRequest(TEST_SEARCH_REQUEST_1)
        dao.insertResults(listOf(TEST_SEARCH_RESULT_1_1, TEST_SEARCH_RESULT_1_2, TEST_SEARCH_RESULT_1_3))

        dao.insertRequest(TEST_SEARCH_REQUEST_2)
        dao.insertResults(listOf(TEST_SEARCH_RESULT_2_1, TEST_SEARCH_RESULT_2_2, TEST_SEARCH_RESULT_2_3))

        val results = dao.getSearchResults(TEST_REQUEST_1).blockingGet()
        assertEquals(TEST_SEARCH_REQUEST_1, results.request)
        assertEquals(TEST_SEARCH_RESULT_1_1.request, results.results[0].request)
        assertEquals(TEST_SEARCH_RESULT_1_2.request, results.results[1].request)
        assertEquals(TEST_SEARCH_RESULT_1_3.request, results.results[2].request)
    }

    @Test
    @Throws(Exception::class)
    fun testGetSearchResults_2() {
        dao.insertRequest(TEST_SEARCH_REQUEST_3)
        dao.insertResults(listOf(TEST_SEARCH_RESULT_3_1, TEST_SEARCH_RESULT_3_2, TEST_SEARCH_RESULT_3_3))

        dao.insertRequest(TEST_SEARCH_REQUEST_2)
        dao.insertResults(listOf(TEST_SEARCH_RESULT_2_1, TEST_SEARCH_RESULT_2_2, TEST_SEARCH_RESULT_2_3))

        val results = dao.getSearchResults(TEST_REQUEST_2).blockingGet()

        assertEquals(TEST_SEARCH_RESULT_2_1.thumbnail, results.results[0].thumbnail)
        assertEquals(TEST_SEARCH_RESULT_2_2.thumbnail, results.results[1].thumbnail)
        assertEquals(TEST_SEARCH_RESULT_2_3.thumbnail, results.results[2].thumbnail)

        assertEquals(TEST_SEARCH_RESULT_2_1.link, results.results[0].link)
        assertEquals(TEST_SEARCH_RESULT_2_2.link, results.results[1].link)
        assertEquals(TEST_SEARCH_RESULT_2_3.link, results.results[2].link)
    }

    @Test
    @Throws(Exception::class)
    fun testGetSearchResults_noResults() {
        dao.insertRequest(TEST_SEARCH_REQUEST_3)
        dao.insertResults(listOf(TEST_SEARCH_RESULT_3_1, TEST_SEARCH_RESULT_3_2, TEST_SEARCH_RESULT_3_3))

        dao.insertRequest(TEST_SEARCH_REQUEST_1)

        val results = dao.getSearchResults(TEST_REQUEST_1).blockingGet()
        assertEquals(TEST_SEARCH_REQUEST_1, results.request)
        assertEquals(0, results.results.count())
    }

    // endregion

    // region deleteResults

    @Test
    @Throws(Exception::class)
    fun testDeleteResults_1() {
        dao.insertRequest(TEST_SEARCH_REQUEST_1)
        dao.insertResults(listOf(TEST_SEARCH_RESULT_1_1, TEST_SEARCH_RESULT_1_2, TEST_SEARCH_RESULT_1_3))

        dao.insertRequest(TEST_SEARCH_REQUEST_2)
        dao.insertResults(listOf(TEST_SEARCH_RESULT_2_1, TEST_SEARCH_RESULT_2_2, TEST_SEARCH_RESULT_2_3))

        dao.deleteResults(TEST_REQUEST_1)

        val results1 = dao.getSearchResults(TEST_REQUEST_1).blockingGet()
        assertEquals(TEST_SEARCH_REQUEST_1, results1.request)
        assertEquals(0, results1.results.count())

        val results2 = dao.getSearchResults(TEST_REQUEST_2).blockingGet()
        assertEquals(TEST_SEARCH_REQUEST_2, results2.request)
        assertEquals(TEST_SEARCH_RESULT_2_1.name, results2.results[0].name)
        assertEquals(TEST_SEARCH_RESULT_2_2.name, results2.results[1].name)
        assertEquals(TEST_SEARCH_RESULT_2_3.name, results2.results[2].name)
    }

    @Test
    @Throws(Exception::class)
    fun testDeleteResults_nonexistentRequest() {
        dao.insertRequest(TEST_SEARCH_REQUEST_1)
        dao.insertResults(listOf(TEST_SEARCH_RESULT_1_1, TEST_SEARCH_RESULT_1_2, TEST_SEARCH_RESULT_1_3))

        dao.insertRequest(TEST_SEARCH_REQUEST_2)
        dao.insertResults(listOf(TEST_SEARCH_RESULT_2_1, TEST_SEARCH_RESULT_2_2, TEST_SEARCH_RESULT_2_3))

        dao.deleteResults(TEST_REQUEST_3)

        val results1 = dao.getSearchResults(TEST_REQUEST_1).blockingGet()
        assertEquals(TEST_SEARCH_REQUEST_1, results1.request)
        assertEquals(TEST_SEARCH_RESULT_1_1.request, results1.results[0].request)
        assertEquals(TEST_SEARCH_RESULT_1_2.request, results1.results[1].request)
        assertEquals(TEST_SEARCH_RESULT_1_3.request, results1.results[2].request)

        val results2 = dao.getSearchResults(TEST_REQUEST_2).blockingGet()
        assertEquals(TEST_SEARCH_REQUEST_2, results2.request)
        assertEquals(TEST_SEARCH_RESULT_2_1.request, results2.results[0].request)
        assertEquals(TEST_SEARCH_RESULT_2_2.request, results2.results[1].request)
        assertEquals(TEST_SEARCH_RESULT_2_3.request, results2.results[2].request)
    }

    // endregion

    // region insertSearchRequest

    @Test
    @Throws(Exception::class)
    fun testInsertSearchRequest_1() {
        dao.insertSearchRequest(TEST_REQUEST_1,
                listOf(getItem(TEST_SEARCH_RESULT_1_1),
                        getItem(TEST_SEARCH_RESULT_1_2),
                        getItem(TEST_SEARCH_RESULT_1_3)))

        dao.insertSearchRequest(TEST_REQUEST_2,
                listOf(getItem(TEST_SEARCH_RESULT_2_1),
                        getItem(TEST_SEARCH_RESULT_2_2),
                        getItem(TEST_SEARCH_RESULT_2_3)))

        val results2 = dao.getSearchResults(TEST_REQUEST_2).blockingGet()
        assertEquals(TEST_SEARCH_REQUEST_2, results2.request)
        assertEquals(TEST_SEARCH_RESULT_2_1.request, results2.results[0].request)
        assertEquals(TEST_SEARCH_RESULT_2_2.request, results2.results[1].request)
        assertEquals(TEST_SEARCH_RESULT_2_3.request, results2.results[2].request)

        assertEquals(TEST_SEARCH_RESULT_2_1.link, results2.results[0].link)
        assertEquals(TEST_SEARCH_RESULT_2_2.link, results2.results[1].link)
        assertEquals(TEST_SEARCH_RESULT_2_3.link, results2.results[2].link)
    }

    @Test
    @Throws(Exception::class)
    fun testInsertSearchRequest_overwrite() {
        dao.insertSearchRequest(TEST_REQUEST_3,
                listOf(getItem(TEST_SEARCH_RESULT_3_1),
                        getItem(TEST_SEARCH_RESULT_3_2),
                        getItem(TEST_SEARCH_RESULT_3_3)))

        val results3 = dao.getSearchResults(TEST_REQUEST_3).blockingGet()
        assertEquals(TEST_SEARCH_RESULT_3_1.link, results3.results[0].link)
        assertEquals(TEST_SEARCH_RESULT_3_2.link, results3.results[1].link)
        assertEquals(TEST_SEARCH_RESULT_3_3.link, results3.results[2].link)

        dao.insertSearchRequest(TEST_REQUEST_3,
                listOf(getItem(TEST_SEARCH_RESULT_3_2),
                        getItem(TEST_SEARCH_RESULT_3_3),
                        getItem(TEST_SEARCH_RESULT_3_4)))

        val overwrittenResults3 = dao.getSearchResults(TEST_REQUEST_3).blockingGet()

        assertEquals(TEST_SEARCH_RESULT_3_2.link, overwrittenResults3.results[0].link)
        assertEquals(TEST_SEARCH_RESULT_3_3.link, overwrittenResults3.results[1].link)
        assertEquals(TEST_SEARCH_RESULT_3_4.link, overwrittenResults3.results[2].link)
    }

    // endregion

    companion object {
        const val TEST_REQUEST_1 = "Avatar"
        const val TEST_REQUEST_2 = "Gorillaz"
        const val TEST_REQUEST_3 = "Carpenter Brut"

        val TEST_SEARCH_REQUEST_1 = SearchRequest(TEST_REQUEST_1)
        val TEST_SEARCH_REQUEST_2 = SearchRequest(TEST_REQUEST_2)
        val TEST_SEARCH_REQUEST_3 = SearchRequest(TEST_REQUEST_3)


        val TEST_SEARCH_RESULT_1_1 = SearchResult(TEST_REQUEST_1, "thumbnail.url.1.1", "Artist: Avatar", "Genres: Metal", "link.url.1.1")
        val TEST_SEARCH_RESULT_1_2 = SearchResult(TEST_REQUEST_1, "thumbnail.url.1.2", "Song: Paint Me Red", "Artists: Avatar", "link.url.1.2")
        val TEST_SEARCH_RESULT_1_3 = SearchResult(TEST_REQUEST_1, "thumbnail.url.1.3", "Song: King's Harvest", "Artists: Avatar", "link.url.1.3")

        val TEST_SEARCH_RESULT_2_1 = SearchResult(TEST_REQUEST_2, "thumbnail.url.2.1", "Artist: Gorillaz", "Genres: Trip-Hop", "link.url.2.1")
        val TEST_SEARCH_RESULT_2_2 = SearchResult(TEST_REQUEST_2, "thumbnail.url.2.2", "Song: Double Bass", "Artists: Gorillaz", "link.url.2.2")
        val TEST_SEARCH_RESULT_2_3 = SearchResult(TEST_REQUEST_2, "thumbnail.url.2.3", "Song: Rhinestone Eyes", "Artists: Gorillaz", "link.url.2.3")

        val TEST_SEARCH_RESULT_3_1 = SearchResult(TEST_REQUEST_3, "thumbnail.url.3.1", "Artist: Carpenter Brut", "Genres: Synthwave", "link.url.3.1")
        val TEST_SEARCH_RESULT_3_2 = SearchResult(TEST_REQUEST_3, "thumbnail.url.3.2", "Song: Hush, Sally, Hush!", "Artists: Carpenter Brut", "link.url.3.2")
        val TEST_SEARCH_RESULT_3_3 = SearchResult(TEST_REQUEST_3, "thumbnail.url.3.3", "Song: Paradise Warfare", "Artists: Carpenter Brut", "link.url.3.3")
        val TEST_SEARCH_RESULT_3_4 = SearchResult(TEST_REQUEST_3, "thumbnail.url.3.4", "Song: Leather Teeth", "Artists: Carpenter Brut", "link.url.3.4")

        fun getItem(result: SearchResult): SearchResultItem {
            return SearchResultItem(
                    thumbnail = result.thumbnail,
                    name = result.name,
                    description = result.description,
                    link = result.link)
        }
    }

}
package app.binthere.data.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.binthere.data.database.BinthereDatabase
import app.binthere.data.model.BinEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.time.Instant

@RunWith(AndroidJUnit4::class)
@Config(sdk = [33])
class BinDaoTest {
    private lateinit var db: BinthereDatabase
    private lateinit var dao: BinDao

    private val clock = Instant.parse("2026-04-20T12:00:00Z")

    @Before
    fun setup() {
        db =
            Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                BinthereDatabase::class.java,
            ).allowMainThreadQueries().build()
        dao = db.binDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun `upsert and observeByHousehold emits inserted bin`() =
        runTest {
            val bin = sampleBin("bin-1", code = "A1", name = "Toolbox")
            dao.upsert(bin)

            val fetched = dao.observeByHousehold(bin.householdId).first()
            assertThat(fetched).containsExactly(bin)
        }

    @Test
    fun `observeByHousehold orders by code`() =
        runTest {
            val z = sampleBin("z", code = "Z1", name = "Last")
            val a = sampleBin("a", code = "A1", name = "First")
            dao.upsertAll(listOf(z, a))

            val fetched = dao.observeByHousehold(z.householdId).first()
            assertThat(fetched.map { it.id }).containsExactly("a", "z").inOrder()
        }

    @Test
    fun `findByCode scopes results to the requested household`() =
        runTest {
            dao.upsert(sampleBin("mine", householdId = "house-1", code = "SHARED"))
            dao.upsert(sampleBin("theirs", householdId = "house-2", code = "SHARED"))

            val mine = dao.findByCode(householdId = "house-1", code = "SHARED")
            assertThat(mine?.id).isEqualTo("mine")
        }

    @Test
    fun `contentImagePaths list survives a round trip through Room`() =
        runTest {
            val bin =
                sampleBin("bin-1").copy(
                    contentImagePaths = listOf("a.jpg", "b.jpg", "c.jpg"),
                )
            dao.upsert(bin)

            val fetched = dao.observeById("bin-1").first()
            assertThat(fetched?.contentImagePaths).containsExactly("a.jpg", "b.jpg", "c.jpg").inOrder()
        }

    @Test
    fun `deleteById removes the bin`() =
        runTest {
            dao.upsert(sampleBin("bin-1"))
            dao.deleteById("bin-1")

            assertThat(dao.observeById("bin-1").first()).isNull()
        }

    private fun sampleBin(
        id: String,
        householdId: String = "house-1",
        code: String = "",
        name: String = "",
    ): BinEntity =
        BinEntity(
            id = id,
            householdId = householdId,
            code = code,
            name = name,
            createdAt = clock,
            updatedAt = clock,
        )
}

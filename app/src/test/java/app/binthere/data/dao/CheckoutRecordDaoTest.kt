package app.binthere.data.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.binthere.data.database.BinthereDatabase
import app.binthere.data.model.BinEntity
import app.binthere.data.model.CheckoutRecordEntity
import app.binthere.data.model.ItemEntity
import com.google.common.truth.Truth.assertThat
import java.time.Instant
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [33])
class CheckoutRecordDaoTest {
    private lateinit var db: BinthereDatabase
    private lateinit var binDao: BinDao
    private lateinit var itemDao: ItemDao
    private lateinit var checkoutDao: CheckoutRecordDao

    private val household = "house-1"
    private val checkedOutAt = Instant.parse("2026-04-20T10:00:00Z")
    private val checkedInAt = Instant.parse("2026-04-22T15:30:00Z")

    @Before
    fun setup() {
        db =
            Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                BinthereDatabase::class.java,
            ).allowMainThreadQueries().build()
        binDao = db.binDao()
        itemDao = db.itemDao()
        checkoutDao = db.checkoutRecordDao()

        runTest {
            binDao.upsert(BinEntity(id = "bin-1", householdId = household, createdAt = checkedOutAt, updatedAt = checkedOutAt))
            itemDao.upsert(
                ItemEntity(
                    id = "item-1",
                    householdId = household,
                    binId = "bin-1",
                    createdAt = checkedOutAt,
                    updatedAt = checkedOutAt,
                ),
            )
        }
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun `observeActive returns the currently checked-out record only`() =
        runTest {
            val past =
                CheckoutRecordEntity(
                    id = "past",
                    itemId = "item-1",
                    householdId = household,
                    checkedOutAt = checkedOutAt,
                    checkedInAt = checkedInAt,
                )
            val current =
                CheckoutRecordEntity(
                    id = "current",
                    itemId = "item-1",
                    householdId = household,
                    checkedOutAt = checkedOutAt.plusSeconds(3600),
                    checkedInAt = null,
                )
            checkoutDao.upsertAll(listOf(past, current))

            val active = checkoutDao.observeActive("item-1").first()
            assertThat(active?.id).isEqualTo("current")
        }

    @Test
    fun `nullable checkedInAt and expectedReturnDate survive round trip`() =
        runTest {
            val expected = Instant.parse("2026-05-01T00:00:00Z")
            val record =
                CheckoutRecordEntity(
                    id = "r-1",
                    itemId = "item-1",
                    householdId = household,
                    checkedOutAt = checkedOutAt,
                    checkedInAt = null,
                    expectedReturnDate = expected,
                    checkedOutTo = "Rosa",
                )
            checkoutDao.upsert(record)

            val fetched = checkoutDao.observeByItem("item-1").first().single()
            assertThat(fetched.checkedInAt).isNull()
            assertThat(fetched.expectedReturnDate).isEqualTo(expected)
            assertThat(fetched.checkedOutTo).isEqualTo("Rosa")
        }

    @Test
    fun `deleting an item cascades to its checkout records`() =
        runTest {
            checkoutDao.upsert(
                CheckoutRecordEntity(
                    id = "r-1",
                    itemId = "item-1",
                    householdId = household,
                    checkedOutAt = checkedOutAt,
                ),
            )

            itemDao.deleteById("item-1")

            assertThat(checkoutDao.observeByItem("item-1").first()).isEmpty()
        }
}

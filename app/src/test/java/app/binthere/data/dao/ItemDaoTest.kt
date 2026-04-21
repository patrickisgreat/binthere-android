package app.binthere.data.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.binthere.data.database.BinthereDatabase
import app.binthere.data.model.BinEntity
import app.binthere.data.model.CustomAttributeEntity
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
class ItemDaoTest {
    private lateinit var db: BinthereDatabase
    private lateinit var binDao: BinDao
    private lateinit var itemDao: ItemDao
    private lateinit var attributeDao: CustomAttributeDao

    private val clock = Instant.parse("2026-04-20T12:00:00Z")
    private val household = "house-1"

    @Before
    fun setup() {
        db =
            Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                BinthereDatabase::class.java,
            ).allowMainThreadQueries().build()
        binDao = db.binDao()
        itemDao = db.itemDao()
        attributeDao = db.customAttributeDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun `tags and customFields survive a round trip through Room`() =
        runTest {
            binDao.upsert(sampleBin("bin-1"))
            val item =
                sampleItem("item-1", binId = "bin-1").copy(
                    tags = listOf("heavy", "fragile", "summer"),
                    customFields = mapOf("weight" to "12kg", "origin" to "Ohio"),
                )
            itemDao.upsert(item)

            val fetched = itemDao.observeById("item-1").first()
            assertThat(fetched?.tags).containsExactly("heavy", "fragile", "summer").inOrder()
            assertThat(fetched?.customFields).containsExactly("weight", "12kg", "origin", "Ohio")
        }

    @Test
    fun `moveToBin reassigns binId and updates timestamp`() =
        runTest {
            binDao.upsertAll(listOf(sampleBin("bin-a"), sampleBin("bin-b")))
            itemDao.upsert(sampleItem("item-1", binId = "bin-a"))

            val newTimestamp = Instant.parse("2026-04-21T00:00:00Z").toEpochMilli()
            itemDao.moveToBin("item-1", "bin-b", newTimestamp)

            val fetched = itemDao.observeById("item-1").first()
            assertThat(fetched?.binId).isEqualTo("bin-b")
            assertThat(fetched?.updatedAt?.toEpochMilli()).isEqualTo(newTimestamp)
        }

    @Test
    fun `search matches on name and description, case-insensitive via LIKE`() =
        runTest {
            binDao.upsert(sampleBin("bin-1"))
            itemDao.upsertAll(
                listOf(
                    sampleItem("hammer", name = "Claw Hammer"),
                    sampleItem("screwdriver", name = "Phillips Screwdriver", itemDescription = "with magnetic tip"),
                    sampleItem("saw", name = "Hand Saw"),
                ),
            )

            val tipMatches = itemDao.search(household, "magnetic").first()
            assertThat(tipMatches.map { it.id }).containsExactly("screwdriver")
        }

    @Test
    fun `observeWithDetails joins custom attributes into the item`() =
        runTest {
            binDao.upsert(sampleBin("bin-1"))
            itemDao.upsert(sampleItem("item-1", binId = "bin-1"))
            attributeDao.upsertAll(
                listOf(
                    CustomAttributeEntity(
                        id = "attr-1",
                        itemId = "item-1",
                        householdId = household,
                        name = "warranty",
                        type = "date",
                        dateValue = Instant.parse("2027-01-01T00:00:00Z"),
                    ),
                    CustomAttributeEntity(
                        id = "attr-2",
                        itemId = "item-1",
                        householdId = household,
                        name = "price",
                        type = "currency",
                        numberValue = 129.99,
                    ),
                ),
            )

            val details = itemDao.observeWithDetails("item-1").first()
            assertThat(details?.customAttributes).hasSize(2)
            assertThat(details?.customAttributes?.map { it.name }).containsExactly("warranty", "price")
        }

    @Test
    fun `deleting a bin cascades to its items`() =
        runTest {
            binDao.upsert(sampleBin("bin-1"))
            itemDao.upsertAll(
                listOf(
                    sampleItem("item-1", binId = "bin-1"),
                    sampleItem("item-2", binId = "bin-1"),
                ),
            )

            binDao.deleteById("bin-1")

            assertThat(itemDao.observeByHousehold(household).first()).isEmpty()
        }

    private fun sampleBin(id: String): BinEntity =
        BinEntity(
            id = id,
            householdId = household,
            createdAt = clock,
            updatedAt = clock,
        )

    private fun sampleItem(
        id: String,
        binId: String? = null,
        name: String = "",
        itemDescription: String = "",
    ): ItemEntity =
        ItemEntity(
            id = id,
            householdId = household,
            binId = binId,
            name = name,
            itemDescription = itemDescription,
            createdAt = clock,
            updatedAt = clock,
        )
}

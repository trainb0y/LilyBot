package net.irisshaders.lilybot.database

import dev.kord.common.entity.Snowflake
import kotlinx.coroutines.runBlocking
import net.irisshaders.lilybot.configDatabase
import net.irisshaders.lilybot.database
import net.irisshaders.lilybot.database.DatabaseTables.GalleryChannelData
import net.irisshaders.lilybot.database.DatabaseTables.RoleMenuData
import net.irisshaders.lilybot.database.DatabaseTables.TagsData
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq

// TODO Organise into A-Z
object DatabaseGetters {

	suspend inline fun getSupportConfig(inputGuildId: Snowflake): DatabaseTables.SupportConfigData? {
		val collection = configDatabase.getCollection<DatabaseTables.SupportConfigData>()
		return collection.findOne(DatabaseTables.SupportConfigData::guildId eq inputGuildId)
	}

	suspend inline fun getModerationConfig(inputGuildId: Snowflake): DatabaseTables.ModerationConfigData? {
		val collection = configDatabase.getCollection<DatabaseTables.ModerationConfigData>()
		return collection.findOne(DatabaseTables.ModerationConfigData::guildId eq inputGuildId)
	}

	suspend inline fun getLoggingConfig(inputGuildId: Snowflake): DatabaseTables.LoggingConfigData? {
		val collection = configDatabase.getCollection<DatabaseTables.LoggingConfigData>()
		return collection.findOne(DatabaseTables.LoggingConfigData::guildId eq inputGuildId)
	}

	/**
	 * Gets the number of points the provided [inputUserId] has in the provided [inputGuildId] from the database.
	 *
	 * @param inputUserId The ID of the user to get the point value for
	 * @param inputGuildId The ID of the guild the command was run in
	 * @return null or the result from the database
	 * @author tempest15
	 * @since 3.0.0
	 */
	suspend inline fun getWarn(inputUserId: Snowflake, inputGuildId: Snowflake): DatabaseTables.WarnData? {
		val collection = database.getCollection<DatabaseTables.WarnData>()
		return collection.findOne(
			DatabaseTables.WarnData::userId eq inputUserId,
			DatabaseTables.WarnData::guildId eq inputGuildId
		)
	}

	/**
	 * Using the provided [inputMessageId] the associated [RoleMenuData] will be returned from the database.
	 *
	 * @param inputMessageId The ID of the message the event was triggered via.
	 * @return The role menu data from the database
	 * @author tempest15
	 * @since 3.4.0
	 */
	suspend inline fun getRoleData(inputMessageId: Snowflake): RoleMenuData? {
		val collection = database.getCollection<RoleMenuData>()
		return collection.findOne(RoleMenuData::messageId eq inputMessageId)
	}

	/**
	 * Gets Lily's status from the database.
	 *
	 * @return null or the set status in the database.
	 * @author NoComment1105
	 * @since 3.0.0
	 */
	fun getStatus(): String {
		var selectedStatus: DatabaseTables.StatusData?
		runBlocking {
			val collection = database.getCollection<DatabaseTables.StatusData>()
			selectedStatus = collection.findOne(DatabaseTables.StatusData::key eq "LilyStatus")
		}
		return selectedStatus?.status ?: "Iris"
	}

	/**
	 * Gets the given tag using it's [name] and returns its [TagsData]. If the tag does not exist.
	 * it will return null
	 *
	 * @param inputGuildId The ID of the guild the command was run in.
	 * @param name The named identifier of the tag.
	 * @return null or the result from the database.
	 * @author NoComment1105
	 * @since 3.1.0
	 */
	suspend inline fun getTag(inputGuildId: Snowflake, name: String): TagsData? {
		val collection = database.getCollection<TagsData>()
		return collection.findOne(TagsData::guildId eq inputGuildId, TagsData::name eq name)
	}

	/**
	 * Gets all threads into a list and return them to the user.
	 *
	 * @author NoComment1105
	 * @since 3.4.1
	 */
	suspend inline fun getAllThreads(): List<DatabaseTables.ThreadData> {
		val collection = database.getCollection<DatabaseTables.ThreadData>()
		return collection.find().toList()
	}

	/**
	 * Using the provided [inputThreadId] the thread is returned.
	 *
	 * @param inputThreadId The ID of the thread you wish to find the owner for
	 *
	 * @return null or the thread
	 * @author tempest15
	 * @since 3.2.0
	 */
	suspend inline fun getThread(inputThreadId: Snowflake): DatabaseTables.ThreadData? {
		val collection = database.getCollection<DatabaseTables.ThreadData>()
		return collection.findOne(DatabaseTables.ThreadData::threadId eq inputThreadId)
	}

	/**
	 * Using the provided [inputOwnerId] the list of threads that person owns is returned from the database.
	 *
	 * @param inputOwnerId The ID of the member whose threads you wish to find
	 *
	 * @return null or a list of threads the member owns
	 * @author tempest15
	 * @since 3.2.0
	 */
	suspend inline fun getOwnerThreads(inputOwnerId: Snowflake): List<DatabaseTables.ThreadData> {
		val collection = database.getCollection<DatabaseTables.ThreadData>()
		return collection.find(DatabaseTables.ThreadData::ownerId eq inputOwnerId).toList()
	}

	/**
	 * Collects every gallery channel in the database into a [List].
	 *
	 * @return The [CoroutineCollection] of [GalleryChannelData] for all the gallery channels in the database
	 * @author NoComment1105
	 * @since 3.3.0
	 */
	fun getGalleryChannels(): CoroutineCollection<GalleryChannelData> = database.getCollection()

	/**
	 * Gets every reminder in the database.
	 *
	 * @return A [List] of reminders from the database
	 * @since 3.3.2
	 * @author NoComment1105
	 */
	suspend inline fun getReminders(): List<DatabaseTables.RemindMeData> {
		val collection = database.getCollection<DatabaseTables.RemindMeData>()
		return collection.find().toList()
	}

	/**
	 * Gets all tags in the given [inputGuildId].
	 *
	 * @param inputGuildId The ID of the guild.
	 * @return A [List] of tags for the specified [inputGuildId].
	 * @author NoComment1105
	 * @since 3.1.0
	 */
	suspend inline fun getAllTags(inputGuildId: Snowflake): List<TagsData> {
		val collection = database.getCollection<TagsData>()
		return collection.find(TagsData::guildId eq inputGuildId).toList()
	}
}
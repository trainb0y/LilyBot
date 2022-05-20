package net.irisshaders.lilybot.extensions.config

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.application.slash.converters.impl.stringChoice
import com.kotlindiscord.kord.extensions.modules.unsafe.annotations.UnsafeAPI
import com.kotlindiscord.kord.extensions.modules.unsafe.extensions.unsafeSlashCommand
import com.kotlindiscord.kord.extensions.modules.unsafe.types.InitialSlashCommandResponse
import com.kotlindiscord.kord.extensions.utils.waitFor
import dev.kord.common.entity.TextInputStyle
import dev.kord.core.behavior.interaction.modal
import dev.kord.core.behavior.interaction.response.createEphemeralFollowup
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.ModalSubmitInteractionCreateEvent
import dev.kord.rest.builder.message.create.embed
import dev.kord.rest.builder.message.modify.embed
import net.irisshaders.lilybot.utils.TEST_GUILD_ID
import kotlin.time.Duration.Companion.seconds

class Config : Arguments() {
	val moduleChoice by stringChoice {
		name = "module"
		description = "The module you want to configure"
		choice("Support", "support")
		choice("Moderation", "moderation")
		choice("Logging", "logging")
		choice("Bot", "bot")
	}
}

@OptIn(UnsafeAPI::class)
suspend fun ConfigExtension.configCommand() = unsafeSlashCommand(::Config) {
	name = "config"
	description = "Configuring Lily's Modules"
	guild(TEST_GUILD_ID)

	initialResponse = InitialSlashCommandResponse.None

	action {
		val module = this.arguments.moduleChoice
		if (module == "support") {
			val response = event.interaction.modal("Support Module", "supportModuleModal") {
				actionRow {
					textInput(TextInputStyle.Paragraph, "msgInput", "Support Message") {
						placeholder = "This is where your ad could be!"
					}
				}
				actionRow {
					textInput(TextInputStyle.Short, "teamInput", "Support Team/Role") {
						placeholder = "Role ID or name"
					}
				}
				actionRow {
					textInput(TextInputStyle.Short, "supChannel", "Support Channel") {
						placeholder = "Channel ID"
					}
				}
				actionRow {
					textInput(TextInputStyle.Short, "supDuration", "Thread Auto-Archive Timer") {
						placeholder = "Supported formats: 10min, 3h, 1d, 5w"
					}
				}
			}

			val interaction = response.kord.waitFor<ModalSubmitInteractionCreateEvent>(60.seconds.inWholeMilliseconds) {
				interaction.modalId == "supportModuleModal"
			}?.interaction
			if (interaction == null) {
				response.createEphemeralFollowup {
					embed {
						description = "Configuration timed out"
					}
				}
				return@action
			}
			val supportMsg = interaction.textInputs["msgInput"]!!.value!!
			val supportTeam = interaction.textInputs["teamInput"]!!.value!!
			val supportChannel = interaction.textInputs["supChannel"]!!.value!!
			val supportDuration = interaction.textInputs["supDuration"]!!.value!!
			val modalResponse = interaction.deferEphemeralResponse()
			modalResponse.respond {
				embed {
					title = "Module configured: Support"
					description = supportMsg
					field {
						name = "Support Team"
						value = supportTeam
					}
					field {
						name = "Support Channel"
						value = supportChannel
					}
					field {
						name = "Support Duration"
						value = supportDuration
					}
					footer {
						text = "Configured by: ${user.asUser().tag}"
					}
				}
			}
		}
	}
}

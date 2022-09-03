/*
 * Copyright 2015 Austin Keener, Michael Ritter, Florian Spieß, and the JDA contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.dv8tion.jda.api.events.guild.scheduledevent.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.GuildScheduledEvent;
import net.dv8tion.jda.api.entities.StageChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.internal.utils.Checks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Indicates that the location of a {@link GuildScheduledEvent} has changed.
 *
 * <p>Can be used to detect when the {@link GuildScheduledEvent} location has changed.
 *
 * <p>Identifier: {@code location}
 *
 * <p><b>Requirements</b><br>
 *
 * <p>This event requires the {@link net.dv8tion.jda.api.requests.GatewayIntent#GUILD_SCHEDULED_EVENTS GUILD_SCHEDULED_EVENTS} intent to be enabled.
 * <br>{@link net.dv8tion.jda.api.JDABuilder#createDefault(String) createDefault(String)} and
 * {@link net.dv8tion.jda.api.JDABuilder#createLight(String) createLight(String)} disable this by default!
 *
 * Discord does not specifically tell us about the updates, but merely tells us the
 * {@link net.dv8tion.jda.api.entities.GuildScheduledEvent GuildScheduledEvent} was updated and gives us the updated {@link net.dv8tion.jda.api.entities.GuildScheduledEvent GuildScheduledEvent} object.
 * In order to fire a specific event like this we need to have the old {@link net.dv8tion.jda.api.entities.GuildScheduledEvent GuildScheduledEvent} cached to compare against.
 */
public class GuildScheduledEventUpdateLocationEvent extends GenericGuildScheduledEventUpdateEvent<String>
{
    public static final String IDENTIFIER = "location";

    public GuildScheduledEventUpdateLocationEvent(@Nonnull JDA api, long responseNumber, @Nonnull GuildScheduledEvent guildScheduledEvent, @Nonnull String previous)
    {
        super(api, responseNumber, guildScheduledEvent, previous, guildScheduledEvent.getLocation(), IDENTIFIER);
    }

    /**
     * The old {@link GuildScheduledEvent#getLocation() location}.
     *
     * @return The old location
     */
    @Nonnull
    public String getOldLocation()
    {
        return getOldValue();
    }

    /**
     * The new {@link GuildScheduledEvent#getLocation() location}.
     *
     * @return The new location
     */
    @Nonnull
    public String getNewLocation()
    {
        return getNewValue();
    }

    @Nonnull
    @Override
    public String getOldValue()
    {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public String getNewValue()
    {
        return super.getNewValue();
    }
}

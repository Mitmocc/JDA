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
package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.GuildScheduledEvent;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.scheduledevent.GuildScheduledEventDeleteEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EmoteImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.GuildScheduledEventImpl;
import net.dv8tion.jda.internal.entities.MemberImpl;
import net.dv8tion.jda.internal.requests.WebSocketClient;

public class GuildScheduledEventDeleteHandler extends SocketHandler
{
    public GuildScheduledEventDeleteHandler(JDAImpl api)
    {
        super(api);
    }

    @Override
    protected Long handleInternally(DataObject content)
    {
        final long guildId = content.getLong("guild_id");
        if (getJDA().getGuildSetupController().isLocked(guildId))
            return guildId;

        GuildImpl guild = (GuildImpl) getJDA().getGuildById(guildId);
        if (guild == null)
        {
            EventCache.LOG.debug("GUILD_SCHEDULED_EVENT_DELETE was received for a Guild that is not yet cached: {}", content);
            return null;
        }

        final long eventId = content.getLong("id");
        GuildScheduledEvent removedEvent = guild.getScheduledEventsView().remove(eventId);
        if (removedEvent == null)
        {
            WebSocketClient.LOG.debug("GUILD_SCHEDULED_EVENT_DELETE was received for a Role that is not yet cached: {}", content);
            return null;
        }

        getJDA().handleEvent(
            new GuildScheduledEventDeleteEvent(
                getJDA(), responseNumber,
                removedEvent));
        return null;
    }
}

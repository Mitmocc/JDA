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

package net.dv8tion.jda.internal.managers;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.GuildScheduledEventManager;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;


public class GuildScheduledEventManagerImpl extends ManagerBase<GuildScheduledEventManager> implements GuildScheduledEventManager
{
    protected GuildScheduledEvent event;
    protected String name, description;
    protected GuildChannel location;
    protected OffsetDateTime startTime, endTime;

    public GuildScheduledEventManagerImpl(GuildScheduledEvent event)
    {
        super(event.getJDA(), Route.Guilds.MODIFY_SCHEDULED_EVENT.compile(event.getGuild().getId(), event.getId()));
        this.event = event;
        if (isPermissionChecksEnabled())
            checkPermissions();
    }

    @Nonnull
    @Override
    public GuildScheduledEvent getGuildScheduledEvent()
    {
        GuildScheduledEvent realEvent = event.getGuild().getScheduledEventById(event.getIdLong());
        if (realEvent != null)
            event = realEvent;
        return event;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public GuildScheduledEventManagerImpl setName(@Nonnull String name)
    {
        Checks.notBlank(name, "Name");
        name = name.trim();
        Checks.notEmpty(name, "Name");
        Checks.notLonger(name, 100, "Name");
        this.name = name;
        set |= NAME;
        return this;
    }

    @NotNull
    @Override
    public GuildScheduledEventManager setDescription(@NotNull String description)
    {
        Checks.notLonger(description, 1000, "Description");
        this.description = description;
        set |= DESCRIPTION;
        return this;
    }

    @NotNull
    @Override
    public GuildScheduledEventManager setLocation(@NotNull StageChannel stageChannel)
    {
        this.location = stageChannel;
        set |= LOCATION;
        return this;
    }

    @NotNull
    @Override
    public GuildScheduledEventManager setLocation(@NotNull VoiceChannel voiceChannel)
    {
        this.location = voiceChannel;
        set |= LOCATION;
        return this;
    }

    @NotNull
    @Override
    public GuildScheduledEventManager setLocation(@NotNull String externalLocation)
    {
        return null;
    }

    @NotNull
    @Override
    public GuildScheduledEventManager setStartTime(@NotNull OffsetDateTime startTime)
    {
        this.startTime = startTime;
        set |= START_TIME;
        return this;
    }

    @NotNull
    @Override
    public GuildScheduledEventManager setEndTime(@Nullable OffsetDateTime endTime)
    {
        this.endTime = endTime;
        set |= END_TIME;
        return this;
    }

    @NotNull
    @Override
    public GuildScheduledEventManager setStatus(@NotNull GuildScheduledEvent.Status status)
    {
        return null;
    }

    @Override
    protected RequestBody finalizeData()
    {
        DataObject object = DataObject.empty().put("name", getGuildScheduledEvent().getName());
        if (shouldUpdate(NAME))
            object.put("name", name);
        if (shouldUpdate(DESCRIPTION))
            object.put("description", description);
        if (shouldUpdate(LOCATION))
            object.put("channel_id", location.getIdLong());
        if (shouldUpdate(START_TIME))
            object.put("scheduled_start_time", startTime.format(DateTimeFormatter.ISO_DATE_TIME));
        if (shouldUpdate(END_TIME))
            object.put("scheduled_end_time", endTime.format(DateTimeFormatter.ISO_DATE_TIME));
        return getRequestBody(object);
    }
}

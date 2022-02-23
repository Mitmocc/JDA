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
    protected long channelId;
    protected String location;
    protected Icon image;
    protected OffsetDateTime startTime, endTime;
    protected int entityType;
    protected GuildScheduledEvent.Status status;

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
    public GuildScheduledEventManagerImpl reset(long fields)
    {
        super.reset(fields);
        if ((fields & NAME) == NAME)
            this.name = null;
        if ((fields & DESCRIPTION) == DESCRIPTION)
            this.description = null;
        if ((fields & LOCATION) == LOCATION)
            this.location = null;
        if ((fields & START_TIME) == START_TIME)
            this.startTime = null;
        if ((fields & END_TIME) == END_TIME)
            this.endTime = null;
        if ((fields & IMAGE) == IMAGE)
            this.image = null;
        if ((fields & STATUS) == STATUS)
            this.status = null;
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public GuildScheduledEventManagerImpl reset(long... fields)
    {
        super.reset(fields);
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public GuildScheduledEventManagerImpl reset()
    {
        super.reset();
        this.name = null;
        this.description = null;
        this.location = null;
        this.startTime = null;
        this.endTime = null;
        this.image = null;
        this.status = null;
        return this;
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
    public GuildScheduledEventManager setImage(@NotNull Icon icon)
    {
        this.image = icon;
        set |= IMAGE;
        return this;
    }

    @NotNull
    @Override
    public GuildScheduledEventManager setLocation(@NotNull StageChannel stageChannel)
    {
        this.channelId = stageChannel.getIdLong();
        this.entityType = 1;
        set |= LOCATION;
        return this;
    }

    @NotNull
    @Override
    public GuildScheduledEventManager setLocation(@NotNull VoiceChannel voiceChannel)
    {
        this.channelId = voiceChannel.getIdLong();
        this.entityType = 2;
        set |= LOCATION;
        return this;
    }

    @NotNull
    @Override
    public GuildScheduledEventManager setLocation(@NotNull String externalLocation)
    {
        this.location = externalLocation;
        this.entityType = 3;
        set |= LOCATION;
        return this;
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
        this.status = status;
        set |= STATUS;
        return this;
    }

    @Override
    protected RequestBody finalizeData()
    {
        DataObject object = DataObject.empty();
        if (shouldUpdate(NAME))
            object.put("name", name);
        if (shouldUpdate(DESCRIPTION))
            object.put("description", description);
        if (shouldUpdate(LOCATION))
        {
            if (getGuildScheduledEvent().getStatus() != GuildScheduledEvent.Status.SCHEDULED)
                throw new IllegalArgumentException("Cannot update the location for a non-scheduled event.");

            object.put("entity_type", entityType);
            if (this.entityType == 1 || this.entityType == 2)
                object.put("channel_id", channelId);
            else if (this.entityType == 3)
            {

                if (location != null && location.length() > 0)
                {
                    object.put("entity_metadata", DataObject.empty().put("location", location));
                    object.put("channel_id", null);
                    if (endTime == null && getGuildScheduledEvent().getEndTime() == null)
                    {
                        throw new IllegalArgumentException("Missing required parameter: End Time");
                    }
                }
                else
                {
                    throw new IllegalArgumentException("Missing required parameter: Location");
                }
            }
        }
        if (shouldUpdate(START_TIME))

            object.put("scheduled_start_time", startTime.format(DateTimeFormatter.ISO_DATE_TIME));
        if (shouldUpdate(END_TIME))
        {
            if ((this.startTime == null ? getGuildScheduledEvent().getStartTime() : this.startTime).isAfter(endTime))
            {
                throw new IllegalArgumentException("Cannot schedule event to end before starting.");
            }
            object.put("scheduled_end_time", endTime.format(DateTimeFormatter.ISO_DATE_TIME));
        }
        if (shouldUpdate(IMAGE))
            object.put("image", image.getEncoding());
        if (shouldUpdate(STATUS))
            object.put("status", status.getKey());
        reset();
        return getRequestBody(object);
    }

}

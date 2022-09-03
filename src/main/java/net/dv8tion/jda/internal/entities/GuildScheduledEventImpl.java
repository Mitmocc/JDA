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
package net.dv8tion.jda.internal.entities;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.GuildScheduledEventManager;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.pagination.GuildScheduledEventMembersPaginationAction;
import net.dv8tion.jda.api.requests.restaction.pagination.GuildScheduledEventUsersPaginationAction;
import net.dv8tion.jda.internal.managers.GuildScheduledEventManagerImpl;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.restaction.AuditableRestActionImpl;
import net.dv8tion.jda.internal.requests.restaction.pagination.GuildScheduledEventMembersPaginationActionImpl;
import net.dv8tion.jda.internal.requests.restaction.pagination.GuildScheduledEventUsersPaginationActionImpl;
import net.dv8tion.jda.internal.utils.Checks;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.OffsetDateTime;

public class GuildScheduledEventImpl implements GuildScheduledEvent
{
    private final long id;
    private final Guild guild;

    private String name, description;
    private OffsetDateTime startTime, endTime;
    private String image;
    private Status status;
    private Type type;
    private User creator;
    private long creatorId;
    private int interestedUserCount;
    private String location;

    public GuildScheduledEventImpl(long id, Guild guild)
    {
        this.id = id;
        this.guild = guild;
    }

    @Nonnull
    @Override
    public String getName()
    {
        return name;
    }

    @Nullable
    @Override
    public String getDescription()
    {
        return description;
    }

    @Nullable
    @Override
    public String getImageUrl()
    {
        return image == null ? null : String.format(IMAGE_URL, getId(), image, image.startsWith("a_") ? "gif" : "png");
    }

    @Nullable
    @Override
    public User getCreator()
    {
        return creator;
    }

    @Override
    public long getCreatorIdLong()
    {
        return creatorId;
    }

    @Nonnull
    @Override
    public Status getStatus()
    {
        return status;
    }

    @Nonnull
    @Override
    public Type getType()
    {
        return type;
    }

    @Nonnull
    @Override
    public OffsetDateTime getStartTime()
    {
        return startTime;
    }

    @Nullable
    @Override
    public OffsetDateTime getEndTime()
    {
        return endTime;
    }

    @Nullable
    @Override
    public GuildChannelUnion getChannel()
    {
        if (type == Type.STAGE_INSTANCE || type == Type.VOICE)
            return (GuildChannelUnion) guild.getGuildChannelById(location);
        return null;
    }

    @Override
    public String getLocation()
    {
        return location;
    }

    @Override
    public int getInterestedUserCount()
    {
        return interestedUserCount;
    }

    @Nonnull
    @Override
    public Guild getGuild()
    {
        return guild;
    }

    @Override
    public long getIdLong()
    {
        return id;
    }

    @Nonnull
    @Override
    public GuildScheduledEventManager getManager()
    {
        return new GuildScheduledEventManagerImpl(this);
    }

    @Nonnull
    @Override
    public AuditableRestAction<Void> delete()
    {
        Guild guild = getGuild();
        if (!guild.getSelfMember().hasPermission(Permission.MANAGE_EVENTS))
            throw new InsufficientPermissionException(guild, Permission.MANAGE_EVENTS);

        Route.CompiledRoute route = Route.Guilds.DELETE_SCHEDULED_EVENT.compile(guild.getId(), getId());
        return new AuditableRestActionImpl<>(getJDA(), route);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public GuildScheduledEventUsersPaginationAction retrieveInterestedUsers()
    {
        return new GuildScheduledEventUsersPaginationActionImpl(this);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public GuildScheduledEventMembersPaginationAction retrieveInterestedMembers()
    {
        return new GuildScheduledEventMembersPaginationActionImpl(this);
    }

    public GuildScheduledEventImpl setName(String name)
    {
        this.name = name;
        return this;
    }

    public GuildScheduledEventImpl setType(Type type)
    {
        this.type = type;
        return this;
    }

    public GuildScheduledEventImpl setLocation(String location)
    {
        this.location = location;
        return this;
    }

    public GuildScheduledEventImpl setDescription(String description)
    {
        this.description = description;
        return this;
    }

    public GuildScheduledEventImpl setImage(String image)
    {
        this.image = image;
        return this;
    }

    public GuildScheduledEventImpl setCreatorId(long creatorId)
    {
        this.creatorId = creatorId;
        return this;
    }

    public GuildScheduledEventImpl setCreator(User creator)
    {
        this.creator = creator;
        return this;
    }

    public GuildScheduledEventImpl setStatus(Status status)
    {
        this.status = status;
        return this;
    }

    public GuildScheduledEventImpl setStartTime(OffsetDateTime startTime)
    {
        this.startTime = startTime;
        return this;
    }

    public GuildScheduledEventImpl setEndTime(OffsetDateTime endTime)
    {
        this.endTime = endTime;
        return this;
    }

    public GuildScheduledEventImpl setInterestedUserCount(int interestedUserCount)
    {
        this.interestedUserCount = interestedUserCount;
        return this;
    }

    @Override
    public int compareTo(@Nonnull GuildScheduledEvent guildScheduledEvent)
    {
        Checks.notNull(guildScheduledEvent, "Guild Scheduled Event");
        Checks.check(this.getGuild().equals(guildScheduledEvent.getGuild()), "Cannot compare two Guild Scheduled Events belonging to seperate guilds!");

        int startTimeComparison = OffsetDateTime.timeLineOrder().compare(this.getStartTime(), guildScheduledEvent.getStartTime());
        if (startTimeComparison == 0)
            return Long.compare(this.getIdLong(), guildScheduledEvent.getIdLong());
        else
            return startTimeComparison;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this)
            return true;
        if (!(o instanceof GuildScheduledEventImpl))
            return false;
        return this.id == ((GuildScheduledEventImpl) o).id;
    }

    @Override
    public int hashCode()
    {
        return Long.hashCode(id);
    }

    @Override
    public String toString()
    {
        return "GuildScheduledEvent:" + getName() + '(' + id + ')';
    }
}

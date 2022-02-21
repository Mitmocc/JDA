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

package net.dv8tion.jda.internal.requests.restaction;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.restaction.GuildScheduledEventAction;
import net.dv8tion.jda.api.requests.restaction.RoleAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class GuildScheduledEventActionImpl extends AuditableRestActionImpl<GuildScheduledEvent> implements GuildScheduledEventAction
{
    protected final Guild guild;
    protected String name, description;
    protected GuildChannel location;
    protected OffsetDateTime startTime, endTime;

    /**
     * Creates a new RoleAction instance
     *
     * @param  guild
     *         The {@link Guild Guild} for which the Role should be created.
     */
    public GuildScheduledEventActionImpl(Guild guild)
    {
        super(guild.getJDA(), Route.Guilds.CREATE_SCHEDULED_EVENT.compile(guild.getId()));
        this.guild = guild;
    }

    @Nonnull
    @Override
    public GuildScheduledEventActionImpl setCheck(BooleanSupplier checks)
    {
        return (GuildScheduledEventActionImpl) super.setCheck(checks);
    }

    @Nonnull
    @Override
    public GuildScheduledEventActionImpl timeout(long timeout, @Nonnull TimeUnit unit)
    {
        return (GuildScheduledEventActionImpl) super.timeout(timeout, unit);
    }

    @Nonnull
    @Override
    public GuildScheduledEventActionImpl deadline(long timestamp)
    {
        return (GuildScheduledEventActionImpl) super.deadline(timestamp);
    }


    @Nonnull
    @Override
    public Guild getGuild()
    {
        return guild;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public GuildScheduledEventActionImpl setName(@NotNull String name)
    {
        Checks.notEmpty(name, "Name");
        Checks.notLonger(name, 100, "Name");
        this.name = name;
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public GuildScheduledEventActionImpl setDescription(@NotNull String description)
    {
        Checks.notEmpty(description, "Description");
        Checks.notLonger(description, 1000, "Description");
        this.description = name;
        return this;
    }

    @NotNull
    @Override
    public GuildScheduledEventAction setLocation(@NotNull StageChannel stageChannel)
    {
        this.location = stageChannel;
        return this;
    }

    @NotNull
    @Override
    public GuildScheduledEventAction setLocation(@NotNull VoiceChannel voiceChannel)
    {
        this.location = voiceChannel;
        return this;
    }

    @NotNull
    @Override
    public GuildScheduledEventAction setLocation(@NotNull String externalLocation)
    {
        return null;
    }

    @NotNull
    @Override
    public GuildScheduledEventAction setStartTime(@NotNull OffsetDateTime startTime)
    {
        this.startTime = startTime;
        return this;
    }

    @NotNull
    @Override
    public GuildScheduledEventAction setEndTime(@Nullable OffsetDateTime endTime)
    {
        this.endTime = endTime;
        return this;
    }


    @Override
    protected RequestBody finalizeData()
    {
        DataObject object = DataObject.empty();
        object.put("entity_type", 2);
        object.put("privacy_level", 2);
        if (name != null)
            object.put("name", name);
        if (description != null)
            object.put("description", description);
        if (location != null)
            object.put("channel_id", location.getIdLong());
        if (startTime != null)
            object.put("scheduled_start_time", startTime.format(DateTimeFormatter.ISO_DATE_TIME));
        if (endTime != null)
            object.put("scheduled_end_time", endTime.format(DateTimeFormatter.ISO_DATE_TIME));
        return getRequestBody(object);
    }

    @Override
    protected void handleSuccess(Response response, Request<GuildScheduledEvent> request)
    {
        request.onSuccess(api.getEntityBuilder().createGuildScheduledEvent((GuildImpl) guild, response.getObject(), guild.getIdLong()));
    }
}

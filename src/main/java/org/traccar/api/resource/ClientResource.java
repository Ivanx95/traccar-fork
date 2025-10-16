/*
 * Copyright 2021 Anton Tananaev (anton@traccar.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.api.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.traccar.api.SimpleObjectResource;
import org.traccar.model.Client;
import org.traccar.model.User;
import org.traccar.storage.StorageException;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Condition;
import org.traccar.storage.query.Order;
import org.traccar.storage.query.Request;

import java.util.Collection;
import java.util.LinkedList;

@Path("clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClientResource extends SimpleObjectResource<Client> {

    public ClientResource() {
        super(Client.class, "name");
    }

    @Path("/v2")
    @GET
    public Collection<Client> get(
            @QueryParam("all") boolean all,
            @QueryParam("userId") long userId,
            @QueryParam("orderId") Long orderId) throws StorageException {

        if (orderId != null) {
            permissionsService.checkUser(getUserId(), userId);
            Condition condition = (new Condition.Permission(org.traccar.model.Order.class, orderId, baseClass).excludeGroups());

            return storage.getObjects(baseClass, new Request(
                    new Columns.All(), condition, new org.traccar.storage.query.Order("name")));
        }
        else {

            var conditions = new LinkedList<Condition>();

            if (all) {
                if (permissionsService.notAdmin(getUserId())) {
                    conditions.add(new Condition.Permission(User.class, getUserId(), baseClass));
                }
            } else {
                if (userId == 0) {
                    conditions.add(new Condition.Permission(User.class, getUserId(), baseClass));
                } else {
                    permissionsService.checkUser(getUserId(), userId);
                    conditions.add(new Condition.Permission(User.class, userId, baseClass).excludeGroups());
                }
            }

            return storage.getObjects(baseClass, new Request(
                    new Columns.All(), Condition.merge(conditions), new Order("name")));
        }
    }
}

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
import jakarta.ws.rs.core.Response;
import org.traccar.api.ExtendedObjectResource;
import org.traccar.model.Document;
import org.traccar.model.User;
import org.traccar.storage.StorageException;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Condition;
import org.traccar.storage.query.Order;
import org.traccar.storage.query.Request;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

@Path("documents")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DocumentResource extends ExtendedObjectResource<Document> {


    @Override
    public Response add(Document entity) throws Exception {
        entity.setCreatedAt(new Date());
        return super.add(entity);
    }

    public DocumentResource() {
        super(Document.class, "createdat");
    }


    @Path("/orders")
    @GET
    public Collection<Document> get(
            @QueryParam("userId") long userId,
            @QueryParam("orderId") long orderId) throws StorageException {

        var conditions = new LinkedList<Condition>();

        if (userId == 0) {
            conditions.add(new Condition.Permission(User.class, getUserId(), baseClass));
        } else {
            permissionsService.checkUser(getUserId(), userId);
            conditions.add(new Condition.Permission(User.class, userId, baseClass).excludeGroups());
        }
        if (orderId > 0) {

            permissionsService.checkPermission(org.traccar.model.Order.class, getUserId(), orderId);
            conditions.add(new Condition.Permission(Order.class, orderId, baseClass).excludeGroups());

        }

        return storage.getObjects(baseClass, new Request(
                new Columns.All(), Condition.merge(conditions), getSortField() != null ? new Order(getSortField()) : null));

    }

}

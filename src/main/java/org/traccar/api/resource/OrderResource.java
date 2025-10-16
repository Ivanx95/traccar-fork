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

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.traccar.api.ExtendedObjectResource;
import org.traccar.config.Config;
import org.traccar.config.Keys;
import org.traccar.model.Order;
import org.traccar.storage.StorageException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;

@Path("orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource extends ExtendedObjectResource<Order> {


    @Inject
    private Config config;

    public OrderResource() {
        super(Order.class, "description");
    }

    @Override
    public Response add(Order entity) throws Exception {
        entity.setUniqueId(UUID.randomUUID().toString().split("-")[0]);
        entity.setStartTime(new Date());
        return super.add(entity);
    }



    @GET
    @Path("/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM) // Or the specific MIME type of your file
    public Response downloadFile(@QueryParam("path") String path) {

        String root = config.getString(Keys.WEB_OVERRIDE, config.getString(Keys.WEB_PATH));

        java.nio.file.Path userPath = java.nio.file.Path.of(getUserId()+"");
        var rootPath = Paths.get(root).normalize();
        var outputPath = rootPath.resolve(userPath).resolve(path).normalize();

        if (!outputPath.startsWith(rootPath)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }


        // Replace with the actual path to your file
        File file = outputPath.toFile();

        if (!file.exists()) {
            return Response.status(Response.Status.NOT_FOUND).entity("File not found").build();
        }

        String fileName = file.getName();

        return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        String.format(
                                "attachment; filename=\"%s\"",
                                fileName
                        ))
                .build();
    }
    @Path("file/{path}")
    @POST
    @Consumes("*/*")
    public Response uploadOrderFiles(@PathParam("path") String path, File inputFile) throws IOException, StorageException {
        String root = config.getString(Keys.WEB_OVERRIDE, config.getString(Keys.WEB_PATH));

        java.nio.file.Path userPath = java.nio.file.Path.of(getUserId()+"");
        var rootPath = Paths.get(root).normalize();
        var outputPath = rootPath.resolve(userPath).resolve(path).normalize();

        if (!outputPath.startsWith(rootPath)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        var directoryPath = outputPath.getParent();

        if (directoryPath != null) {
            Files.createDirectories(directoryPath);
        }

        try (var input = new FileInputStream(inputFile); var output = new FileOutputStream(outputPath.toFile())) {
            input.transferTo(output);
        }
        return Response.ok().build();
    }

}

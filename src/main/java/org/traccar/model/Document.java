package org.traccar.model;

import org.traccar.storage.StorageName;

import java.util.Date;

@StorageName("tc_documents")
public class Document extends BaseModel{
    private String path;
    private Date createdAt;
    private String fileName;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

package com.fleo.javaforum.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@ConfigurationProperties("storage")
public class StorageProperties {

    private StorageLocation location = new StorageLocation();

    public StorageLocation getLocation() {
        return this.location;
    }

    public void setLocation(StorageLocation locations) {
        this.location = locations;
    }

    public static class StorageLocation {
        private String root;
        private String avatars;

        public String getRoot() {
            return root;
        }

        public void setRoot(String root) {
            this.root = root;
        }

        public String getAvatars() {
            return avatars;
        }

        public void setAvatars(String avatars) {
            this.avatars = avatars;
        }
    }
}

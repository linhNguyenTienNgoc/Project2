package com.cafe.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;

public class FXMLUtils {

    /**
     * Load FXML file and return the root node
     */
    public static <T> T loadFXML(String fxmlPath) throws IOException {
        URL fxmlURL = FXMLUtils.class.getResource(fxmlPath);
        if (fxmlURL == null) {
            throw new IOException("FXML file not found: " + fxmlPath);
        }

        FXMLLoader loader = new FXMLLoader(fxmlURL);
        return loader.load();
    }

    /**
     * Load FXML file and return both root and controller
     */
    public static <T, C> FXMLResult<T, C> loadFXMLWithController(String fxmlPath) throws IOException {
        URL fxmlURL = FXMLUtils.class.getResource(fxmlPath);
        if (fxmlURL == null) {
            throw new IOException("FXML file not found: " + fxmlPath);
        }

        FXMLLoader loader = new FXMLLoader(fxmlURL);
        T root = loader.load();
        C controller = loader.getController();

        return new FXMLResult<>(root, controller);
    }

    /**
     * Result wrapper for FXML loading
     */
    public static class FXMLResult<T, C> {
        private final T root;
        private final C controller;

        public FXMLResult(T root, C controller) {
            this.root = root;
            this.controller = controller;
        }

        public T getRoot() { return root; }
        public C getController() { return controller; }
    }
}
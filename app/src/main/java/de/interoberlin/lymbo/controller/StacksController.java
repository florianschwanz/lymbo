package de.interoberlin.lymbo.controller;

import java.io.File;
import java.util.Collection;

import de.interoberlin.lymbo.model.XmlLymbo;
import de.interoberlin.lymbo.model.XmlStack;

public class StacksController {
    private String currentFileString;
    private File currentFile;
    private XmlLymbo currentLymbo;
    private XmlStack currentStack;
    private Collection<File> lymboFiles;

    private static StacksController instance;

    // --------------------
    // Singleton
    // --------------------

    private StacksController() {
    }

    public static StacksController getInstance() {
        if (instance == null) {
            instance = new StacksController();
        }

        return instance;
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public String getCurrentFileString() {
        return this.currentFileString;
    }

    public void setCurrentFileString(String currentFileString) {
        this.currentFileString = currentFileString;
    }

    public File getCurrentFile() {
        return this.currentFile;
    }

    public void setCurrentFile(File currentFile) {
        this.currentFile = currentFile;
    }

    public XmlStack getCurrentStack() {
        return this.currentStack;
    }

    public XmlLymbo getCurrentLymbo() {
        return this.currentLymbo;
    }

    public void setCurrentLymbo(XmlLymbo currentLymbo) {
        this.currentLymbo = currentLymbo;
    }

    public void setCurrentStack(XmlStack currentStack) {
        this.currentStack = currentStack;
    }

    public Collection<File> getLymboFiles() {
        return this.lymboFiles;
    }

    public void setLymboFiles(Collection<File> lymboFiles) {
        this.lymboFiles = lymboFiles;
    }
}

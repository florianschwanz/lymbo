package de.interoberlin.lymbo.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.model.card.XmlLymbo;
import de.interoberlin.lymbo.model.card.XmlStack;

public class StacksController {
    private static List<XmlStack> stacks;

    private String currentFileString;
    private File currentFile;
    private XmlLymbo currentLymbo;
    private XmlStack currentStack;

    private static StacksController instance;

    // --------------------
    // Singleton
    // --------------------

    private StacksController() {
        init();
    }

    public static StacksController getInstance() {
        if (instance == null) {
            instance = new StacksController();
        }

        return instance;
    }

    // --------------------
    // Methods
    // --------------------

    private void init() {
        if (stacks == null) {
            getStacksFromLymbos();
        }
    }

    private void getStacksFromLymbos() {
        stacks = new ArrayList<XmlStack>();

        System.out.println("A");
        for (XmlLymbo xmlLymbo : LymboController.getInstance().getLymbos()) {
            stacks.add(xmlLymbo.getStack());
            System.out.println("HU");
        }
        System.out.println("B");
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public List<XmlStack> getStacks() {
        return stacks;
    }

    public void setStacks(List<XmlStack> stacks) {
        this.stacks = stacks;
    }

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
}

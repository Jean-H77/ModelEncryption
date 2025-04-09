package org.john.core.InputFile;

import org.john.core.config.Config;
import org.john.core.utils.FileUtils;

import javax.swing.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class InputFile {

    private static final List<InputFile> loadedFiles = new ArrayList<>();

    final String name;
    final byte[] data;

    public InputFile(String name, byte[] data) {
        this.name = name;
        this.data = data;
    }

    public static InputFile decode(File file) {
        return new InputFile(file.getName(), FileUtils.readFileToByteArray(file));
    }

    public byte[] encode() {
        var config = Config.getInstance();
        int length = getBufferLength();

        var buffer = ByteBuffer.allocate(length);

        if(config.isIncludeFileName()) {
            buffer.putInt(name.length());
            buffer.put(name.getBytes());
        }

        if(config.isIncludeFileLength()) {
            buffer.putInt(data.length);
        }

        buffer.put(data);
        return buffer.array();
    }

    public int getBufferLength() {
        var config = Config.getInstance();
        int length = data.length;

        if(config.isIncludeFileName()) {
            length += 4;
            length += name.getBytes().length;
        }

        if(config.isIncludeFileLength()) {
            length += 4;
        }

        return length;
    }

    public static List<InputFile> decodeAllInDir() {
        var path = String.valueOf(Config.getInstance().getInputDir());
        var directory = new File(path);
        var inputFiles = new ArrayList<InputFile>();

        if(!directory.exists()) {
            JOptionPane.showMessageDialog(null, "Directory does not exist");
            return inputFiles;
        }

        if(FileUtils.isEmpty(path)) {
            JOptionPane.showMessageDialog(null, "Empty path");
            return inputFiles;
        }

        var fileArray = directory.listFiles();
        assert fileArray != null;
        for (var inputFile : fileArray)
            inputFiles.add(decode(inputFile));

        return inputFiles;
    }

    public static byte[] packLoadedFiles() {;
        var loadedFiles = getLoadedFiles();
        var buffer = new ByteArrayOutputStream();

        for(var model : loadedFiles)
            buffer.writeBytes(model.encode());

        return buffer.toByteArray();
    }

    public String getName() {
        return name;
    }

    public byte[] getData() {
        return data;
    }

    public static List<InputFile> getLoadedFiles() {
        return loadedFiles;
    }
}

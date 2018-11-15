package org.myagkiy.labs.utils;

import java.io.File;
import java.util.Set;

class ClassLoadingFileHandler<T> extends FileFindHandlerAdapter {
    private Set<Class<? extends T>> foundClasses;
    private FileToClassConverter converter;
    private Class<?> toFind;

    public ClassLoadingFileHandler(Set<Class<? extends T>> foundClasses, Class<?> toFind) {
        this.foundClasses = foundClasses;
        this.toFind = toFind;
    }

    public void updateClassPathBase(String classPathRoot) {
        converter = converter == null ? new FileToClassConverter(classPathRoot) : converter;
        converter.setClassPathRoot(classPathRoot);
    }
    @Override
    public void handleFile(File file) {
        // if we get a Java class file, try to convert it to a class
        Class<? extends T> clazz = converter.convertToClass(file);
        if (clazz == null) return;
        if (toFind == null || toFind.isAssignableFrom(clazz)) foundClasses.add(clazz);
    }
}

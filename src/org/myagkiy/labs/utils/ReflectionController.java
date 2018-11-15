package org.myagkiy.labs.utils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

public class ReflectionController {

    private static final String JAVA_CLASS_PATH_PROPERTY = "java.class.path";
    private static final String CUSTOM_CLASS_PATH_PROPERTY = "custom.class.path";
    private static final String JAR_EXTENSION = ".jar";

    public void execute(int labNumber) {

    }

    public void executeOne() {

    }

    public void executeAll() {

    }

    public void executeAny() {
        Set<Class<? extends LabsController>> classes = findAllMatchingTypes(LabsController.class);
        for (Class c : classes)
            try {
                c.getMethod("init").invoke(c.newInstance());
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) { System.err.println(e.getMessage()); }
    }

    private <T> Set<Class<? extends T>> findAllMatchingTypes(Class<T> toFind) {
        Set<Class<? extends T>> foundClasses = new HashSet<>();
        walkClassPath(foundClasses, toFind);
        foundClasses.remove(toFind);
        return foundClasses;
    }

    private <T> void walkClassPath(Set<Class<? extends T>> foundClasses, Class<T> toFind) {
        ClassLoadingFileHandler fileHandler = new ClassLoadingFileHandler(foundClasses, toFind);
        JavaClassFileWalker fileWalker = new JavaClassFileWalker(fileHandler);

        for (String path : getClassPathRoots()) {
            if (path.endsWith(JAR_EXTENSION)) continue;
            // have to reset class path base so it can instance classes properly
            fileHandler.updateClassPathBase(path);
            fileWalker.setBaseDir(path);
            fileWalker.walk();
        }
    }

    private String[] getClassPathRoots() {
        return (System.getProperties().containsKey(CUSTOM_CLASS_PATH_PROPERTY) ?
                           System.getProperty(CUSTOM_CLASS_PATH_PROPERTY) :
                           System.getProperty(JAVA_CLASS_PATH_PROPERTY))
                .split(File.pathSeparator);
    }
}

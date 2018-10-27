package org.myagkiy.labs.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ReflectionController {

    public static final String JAVA_CLASS_PATH_PROPERTY = "java.class.path";
    public static final String CUSTOM_CLASS_PATH_PROPERTY = "custom.class.path";
    private Class<?> toFind;
    private ArrayList<Class<?>> foundClasses;
    private JavaClassFileWalker fileWalker;
    private ClassLoadingFileHandler fileHandler;

    public void execute(int labNumber) {

    }

    public void executeOne() {

    }

    public void executeAll() {

    }

    public void executeAny() {
        Set<Class<? extends LabsController>> classes = findAllMatchingTypes(LabsController.class);
        for (Class c : classes)
            System.out.println(c.getCanonicalName());
    }

    public <T> Set<Class<? extends T>> findAllMatchingTypes2(Class<T> toFind) {
        Set<Class<? extends T>> foundClasses = new HashSet<>();
        walkClassPath2(foundClasses);
        foundClasses.remove(toFind);
        return foundClasses;
    }

    public <T> Set<Class<? extends T>> findAllMatchingTypes(Class<T> toFind) {
        foundClasses = new ArrayList<>();
        Set<Class<? extends T>> returnedClasses = new HashSet<>();
        this.toFind = toFind;
        walkClassPath();
        foundClasses.remove(toFind);
        for (Class<?> clazz : foundClasses) {
            returnedClasses.add((Class<? extends T>) clazz);
        }
        return returnedClasses;
    }

    private <T> void walkClassPath2(Set<Class<? extends T>> foundClasses) {
        ClassLoadingFileHandler2<T> fileHandler = new ClassLoadingFileHandler2(foundClasses);
        JavaClassFileWalker fileWalker = new JavaClassFileWalker(fileHandler);

        String[] classPathRoots = getClassPathRoots();
        for (int i=0; i< classPathRoots.length; i++) {
            String path = classPathRoots[i];
            if (path.endsWith(".jar")) {
                //				LOG.warn("walkClassPath(): reading from jar not yet implemented, jar file=" + path);
                continue;
            }
            //			LOG.debug("walkClassPath(): checking classpath root: " + path);
            // have to reset class path base so it can instance classes properly
            fileHandler.updateClassPathBase(path);
            fileWalker.setBaseDir(path);
            fileWalker.walk();
        }
    }

    private void walkClassPath() {
        fileHandler = new ClassLoadingFileHandler();
        fileWalker = new JavaClassFileWalker(fileHandler);

        String[] classPathRoots = getClassPathRoots();
        for (int i=0; i< classPathRoots.length; i++) {
            String path = classPathRoots[i];
            if (path.endsWith(".jar")) {
                //				LOG.warn("walkClassPath(): reading from jar not yet implemented, jar file=" + path);
                continue;
            }
            //			LOG.debug("walkClassPath(): checking classpath root: " + path);
            // have to reset class path base so it can instance classes properly
            fileHandler.updateClassPathBase(path);
            fileWalker.setBaseDir(path);
            fileWalker.walk();
        }
    }

    public String[] getClassPathRoots() {
        String classPath;
        if (System.getProperties().containsKey(CUSTOM_CLASS_PATH_PROPERTY)) {
            //			LOG.debug("getClassPathRoots(): using custom classpath property to search for classes");
            classPath = System.getProperty(CUSTOM_CLASS_PATH_PROPERTY);
        } else {
            classPath = System.getProperty(JAVA_CLASS_PATH_PROPERTY);
        }
        String[] pathElements = classPath.split(File.pathSeparator);
        //		LOG.debug("getClassPathRoots(): classPath roots=" + StringUtil.dumpArray(pathElements));
        return pathElements;
    }

    private void handleClass(Class<?> clazz) {
        boolean isMatch = false;
        isMatch = toFind == null || toFind.isAssignableFrom(clazz);
        if (isMatch) {
            foundClasses.add(clazz);
        }
    }

    private void handleClass2(Class<?> clazz) {
        boolean isMatch = false;
        isMatch = toFind == null || toFind.isAssignableFrom(clazz);
        if (isMatch) {
            foundClasses.add(clazz);
        }
    }

    class ClassLoadingFileHandler extends FileFindHandlerAdapter {
        private FileToClassConverter converter;

        public void updateClassPathBase(String classPathRoot) {
            converter = (converter == null ? new FileToClassConverter(classPathRoot) : converter);
            converter.setClassPathRoot(classPathRoot);
        }
        @Override
        public void handleFile(File file) {
            // if we get a Java class file, try to convert it to a class
            Class<?> clazz = converter.convertToClass(file);
            if (clazz == null) {
                return;
            }
            handleClass(clazz);
        }
    }

    class ClassLoadingFileHandler2<T> extends FileFindHandlerAdapter {
        private Set<Class<? extends T>> foundClasses;
        private FileToClassConverter converter;
        private Class<?> toFind;

        public ClassLoadingFileHandler2(Set<Class<? extends T>> foundClasses, Class<?> toFind) {
            this.foundClasses = foundClasses;
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

}

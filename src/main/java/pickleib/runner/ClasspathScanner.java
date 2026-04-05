package pickleib.runner;

import utils.Printer;
import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/** Scans the classpath for classes annotated with a given annotation. */
public class ClasspathScanner {

    private static final Printer log = new Printer(ClasspathScanner.class);

    /**
     * @param annotation the annotation to scan for
     * @param packages   the packages to scan
     * @return list of classes annotated with the given annotation
     */
    public static List<Class<?>> scanForAnnotatedClasses(
            Class<? extends Annotation> annotation, String... packages) {
        List<Class<?>> results = new ArrayList<>();
        if (packages == null || packages.length == 0) return results;

        for (String pkg : packages) {
            try {
                results.addAll(scanPackage(pkg, annotation));
            } catch (Exception e) {
                log.warning("Failed to scan package: " + pkg + " — " + e.getMessage());
            }
        }
        return results;
    }

    private static List<Class<?>> scanPackage(String packageName, Class<? extends Annotation> annotation) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(path);

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getProtocol().equals("file")) {
                File directory = new File(resource.toURI());
                if (directory.exists()) {
                    findClasses(directory, packageName, annotation, classes);
                }
            }
        }
        return classes;
    }

    private static void findClasses(File directory, String packageName,
            Class<? extends Annotation> annotation, List<Class<?>> results) {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                findClasses(file, packageName + "." + file.getName(), annotation, results);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(annotation)) {
                        results.add(clazz);
                    }
                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                    log.warning("Could not load class: " + className + " — " + e.getMessage());
                }
            }
        }
    }
}

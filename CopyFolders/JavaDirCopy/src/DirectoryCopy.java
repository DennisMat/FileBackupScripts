import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;

public class DirectoryCopy {


    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java DirectoryCopy <source> <target> <logfile> <excludeFolder1> <excludeFolder2> ...");
            return;
        }

        Path source = Paths.get(args[0]);
        Path target = Paths.get(args[1]);
        Path logFile = Paths.get(args[2]);

        List<String> excludeFolders = new ArrayList<>();
        for (int i = 3; i < args.length; i++) {
            excludeFolders.add(args[i]);
        }

        try {
            copyDirectory(source, target, excludeFolders, logFile);
            System.out.println("Directory copy operation completed successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void copyDirectory(Path source, Path target, List<String> excludeFolders, Path logFile) throws IOException {
        List<Path> extraFilesInTarget = new ArrayList<>();

        // Walk the source tree
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path relativePath = source.relativize(dir);
                if (excludeFolders.contains(relativePath.toString())) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                Path targetDir = target.resolve(relativePath);
                if (!Files.exists(targetDir)) {
                    Files.createDirectories(targetDir);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relativePath = source.relativize(file);
                Path targetFile = target.resolve(relativePath);

                if (Files.exists(targetFile)) {
                    FileTime sourceTime = Files.getLastModifiedTime(file);
                    FileTime targetTime = Files.getLastModifiedTime(targetFile);

                    if (sourceTime.compareTo(targetTime) > 0) {
                        Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    }
                } else {
                    Files.copy(file, targetFile);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        // Walk the target tree to find extra files/folders
        Files.walkFileTree(target, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relativePath = target.relativize(file);
                Path sourceFile = source.resolve(relativePath);
                if (!Files.exists(sourceFile)) {
                    extraFilesInTarget.add(file);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path relativePath = target.relativize(dir);
                Path sourceDir = source.resolve(relativePath);
                if (!Files.exists(sourceDir)) {
                    extraFilesInTarget.add(dir);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        // Log the extra files/folders
        try (BufferedWriter logWriter = Files.newBufferedWriter(logFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            logWriter.write("Log Time: " + sdf.format(System.currentTimeMillis()) + "\n");
            logWriter.write("Extra files/folders in target directory:\n");
            for (Path extraFile : extraFilesInTarget) {
                logWriter.write(extraFile.toString() + "\n");
            }
            logWriter.write("\n");
        }
    }
}

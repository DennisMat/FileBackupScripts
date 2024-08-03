import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DirectoryCopy {

	static List<String> filesThatWereActuallyExcluded = new ArrayList<>();
	
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println(
					"Usage: java DirectoryCopy.java source=<source> destination=<destination> log_file=<log_file> move_extra_files_to=<dirname> exclude_files_dirs=<array of exclusions>");
			System.exit(1);
		}

		Map<String, String[]> params = parseArgs(args);

		params.forEach((key, value) -> {
			if (!key.equals("exclude_files_dirs")) {
				System.out.println(key + " = " + value[0]);
			} else {
				System.out.println("exclude_files_dirs are:");
				for (int i = 0; i < value.length; i++) {
					System.out.println("   " + value[i]);
				}
			}

		});
		
		if(params.get("source")[0].equalsIgnoreCase(params.get("target")[0])) {
			System.out.println("Source and target cannot be the same");
			System.exit(1);
		}


		Path source = Paths.get(params.get("source")[0]);
		Path target = Paths.get(params.get("target")[0]);
		Path logFile = Paths.get(params.get("log_file")[0]);
		String extrafileMoveTo = null;
		
		if(params.get("move_extra_files_to")!=null) {
			extrafileMoveTo=params.get("move_extra_files_to")[0];
		}

		List<String> excludeFolders = Arrays.asList(params.get("exclude_files_dirs"));

		try {
			List<Path> extraFilesInTarget=copyDirectory(source, target, excludeFolders, logFile, extrafileMoveTo);
			System.out.println("Directory copy operation completed successfully.");
			writeToLog(logFile, extraFilesInTarget);
			if(extrafileMoveTo!=null) {
				moveFilesWithStructure(logFile, target, extraFilesInTarget, extrafileMoveTo);
			}else {
				System.out.println("Extra files will not be moved");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<Path> copyDirectory(Path source, Path target, List<String> excludeFolders, Path logFile,
			String extrafileMoveTo) throws IOException {
		System.out.println("Started copying " + source + " to " + target );
		List<Path> extraFilesInTarget = new ArrayList<>();

		// Walk the source tree
		Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				Path relativePath = source.relativize(dir);
				if(isExcludeFolder(relativePath.toString(),excludeFolders)) {
					//System.out.println("Excluding folder = " + relativePath);
					filesThatWereActuallyExcluded.add(dir.toString());
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
						System.out.println("newer timestamp file = " + file.toString());
						try {
							Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
						} catch (Exception e) {
							System.err.println(e.getMessage());
						}
					}
				} else {
					try {
						//System.out.println("copying file = " + file.toString());
						Files.copy(file, targetFile);
						//System.out.println("copied file = " + file.toString() +" " + targetFile);
					} catch (Exception e) {
						System.err.println(e.getMessage());
					}
				}
				System.out.print(".");//just to show progress
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
					if(!filesThatWereActuallyExcluded.contains(relativePath.toString())) {
						extraFilesInTarget.add(dir);
					}
					
				}
				return FileVisitResult.CONTINUE;
			}
		});

		System.out.println("Done copying " + source + " to " + target );
		return extraFilesInTarget;
		
		
	}

	private static void writeToLog(Path logFile, List<Path> extraFilesInTarget) throws IOException {
		// Log the extra files/folders
		try (BufferedWriter logWriter = Files.newBufferedWriter(logFile, StandardOpenOption.CREATE,
				StandardOpenOption.APPEND)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			logWriter.write("Log Time: " + sdf.format(System.currentTimeMillis()) + "\n");
			logWriter.write("Extra files/folders in target directory:\n");
			for (Path extraFile : extraFilesInTarget) {
				logWriter.write(extraFile.toString() + "\n");
			}
			logWriter.write("\n\nLog Time: " + sdf.format(System.currentTimeMillis()) + "\n");
			logWriter.write("\n\nFiles/Folders that were excluded from copying:\n");
			for (String f : filesThatWereActuallyExcluded) {
				logWriter.write(f+"\n");
			}
			logWriter.write("\n");
		}
	}
	
	public static boolean isExcludeFolder(String filePath, List<String> excludePaths) {
		for (String excludePath : excludePaths) {
			if (filePath.contains(excludePath)) {
				return true;
			}
		}
		return false;
	}



	
    public static void moveFilesWithStructure(Path logFile,Path baseDirectorySourceForExtraFiles, List<Path> extraFilePaths, String targetDirectory) throws IOException {
        Path targetDirPath = Paths.get(targetDirectory);

        // Create the target directory if it doesn't exist
        if (!Files.exists(targetDirPath)) {
            Files.createDirectories(targetDirPath);
        }

        for (Path extraFilePath : extraFilePaths) {

        	 Path relativePath = baseDirectorySourceForExtraFiles.relativize(extraFilePath);
            Path targetPath = targetDirPath.resolve(relativePath);

            // Create target subdirectories if they don't exist
            if (!Files.exists(targetPath.getParent())) {
                Files.createDirectories(targetPath.getParent());
            }

            // Move the file to the target location
            try {
				Files.move(extraFilePath, targetPath);
			} catch (IOException e) {
				System.out.println("file/folder " + targetPath + " may have been already created");
				//e.printStackTrace();
			}
        }
    }
    
	public static Map<String, String[]> parseArgs(String[] args) {
		Map<String, String[]> paramMap = new HashMap<>();
		String key = null;
		StringBuilder value = new StringBuilder();

		for (String arg : args) {
			if (arg.contains("=")) {
				// If there's an existing key, save its value
				if (key != null) {
					paramMap.put(key, splitValues(value.toString().trim()));
				}
				// Split the argument into key and value parts
				String[] parts = arg.split("=", 2);
				key = parts[0];
				value.setLength(0); // Reset the StringBuilder

				if (parts.length > 1) {
					// Start capturing value
					value.append(parts[1]);
				}
			} else {
				// Continue capturing value if it spans multiple arguments
				value.append(" ").append(arg);
			}
		}
		// Store the last key-value pair
		if (key != null) {
			paramMap.put(key, splitValues(value.toString().trim()));
		}

		return paramMap;
	}

	private static String[] splitValues(String value) {
		// Check if the value is quoted and remove quotes if present
		if (value.startsWith("\"") && value.endsWith("\"")) {
			value = value.substring(1, value.length() - 1);
		}
		// Split the value by commas and trim any extra spaces
		return value.split("\\s*,\\s*");
	}

}

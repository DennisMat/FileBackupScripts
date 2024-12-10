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
					"Usage: java DirectoryCopy.java source=<source> destination=<destination> log_file=<log_file> do_not_move_extra_files_folders_in_target=<exclude_target_moving> move_extra_files_to=<dirname> exclude_from_copy=<array of exclusions>");
			System.exit(1);
		}

		Map<String, String[]> params = parseArgs(args);

		System.out.println("You provided the following parameters:");
		params.forEach((key, value) -> {
			if (!key.equals("exclude_from_copy")) {
				System.out.println(key + " = " + value[0]);
			} else {
				System.out.println("exclude_from_copy are:");
				for (int i = 0; i < value.length; i++) {
					System.out.println("   " + value[i]);
				}
			}

		});

		if (params.get("source")[0].equalsIgnoreCase(params.get("target")[0])) {
			System.out.println("Source and target cannot be the same");
			System.exit(1);
		}

		Path source = Paths.get(params.get("source")[0]);
		Path target = Paths.get(params.get("target")[0]);
		Path logFile = Paths.get(params.get("log_file")[0]);
		Path do_not_move_extra_files_folders_in_target = null;
		
		if(params.get("do_not_move_extra_files_folders_in_target")!=null) {
			do_not_move_extra_files_folders_in_target=Paths.get(params.get("do_not_move_extra_files_folders_in_target")[0]);
		}
				
		String extrafileMoveToDir = null;

		if (params.get("move_extra_files_to") != null) {
			extrafileMoveToDir = params.get("move_extra_files_to")[0];
		}

		List<String> excludeFolders = Arrays.asList(params.get("exclude_from_copy"));
		List<String> doNotMove = Arrays.asList(params.get("do_not_move_extra_files_folders_in_target"));

		try {
			List<Path> extraFilesInTarget = copyDirectory(source, target, excludeFolders, logFile, extrafileMoveToDir);
			System.out.println("Directory copy operation completed successfully.");
			writeToLog(logFile, extraFilesInTarget, do_not_move_extra_files_folders_in_target);
			if (extrafileMoveToDir != null) {
				moveFilesWithStructure(logFile, target, extraFilesInTarget, extrafileMoveToDir, doNotMove);
			} else {
				System.out.println("Extra files will not be moved");
			}
		} catch (Exception e) {
			e.printStackTrace();
			writeToLog(logFile, e.getMessage());
		}
		String logMess = "Finished copying " + source + " to " + target;
		System.out.println(logMess);
		writeToLog(logFile, logMess);
	}

	public static List<Path> copyDirectory(Path source, Path target, List<String> excludeFolders, Path logFile,
			String extrafileMoveTo) throws IOException {
		String logMess = "Started copying " + source + " to " + target;
		System.out.println(logMess);
		writeToLog(logFile, logMess);
		List<Path> extraFilesInTarget = new ArrayList<>();

		// Walk the source tree
		Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				Path relativePath = source.relativize(dir);
				if (isFileInList(relativePath.toString(), excludeFolders)) {
					 System.out.println("Excluding folder = " + relativePath);
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

				if (!isFileInList(file.toString(), excludeFolders)) {

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
							// System.out.println("copying file = " + file.toString());
							System.out.println("New file = " + file.toString());
							Files.copy(file, targetFile);
							// System.out.println("copied file = " + file.toString() +" " + targetFile);
						} catch (Exception e) {
							System.err.println(e.getMessage());
						}
					}
				} else {
					filesThatWereActuallyExcluded.add(file.toString());
					System.out.println("Skipping file = " + file.toString());
				}
				System.out.print(".");// just to show progress
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
					if (!filesThatWereActuallyExcluded.contains(relativePath.toString())) {
						extraFilesInTarget.add(dir);
					}

				}
				return FileVisitResult.CONTINUE;
			}
		});

		System.out.println("Done copying " + source + " to " + target);
		return extraFilesInTarget;

	}

	private static void writeToLog(Path logFile, List<Path> extraFilesInTarget,
			Path do_not_move_extra_files_folders_in_target) throws IOException {
		// Log the extra files/folders
		try (BufferedWriter logWriter = Files.newBufferedWriter(logFile, StandardOpenOption.CREATE,
				StandardOpenOption.APPEND)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			logWriter.write("Log Time: " + sdf.format(System.currentTimeMillis()) + "\n");
			logWriter.write("Extra files/folders in target directory:\n");

			for (Path extraFile : extraFilesInTarget) {
				if (do_not_move_extra_files_folders_in_target != null) {
					if (!extraFile.startsWith(do_not_move_extra_files_folders_in_target)) {
						logWriter.write(extraFile.toString() + "\n");
					}

				} else {
					logWriter.write(extraFile.toString() + "\n");
				}

			}
			logWriter.write("\n\nLog Time: " + sdf.format(System.currentTimeMillis()) + "\n");
			logWriter.write("\n\nFiles/Folders that were excluded from copying:\n");
			for (String f : filesThatWereActuallyExcluded) {
				logWriter.write(f + "\n");
			}
			logWriter.write("\n");
		}
	}

	private static void writeToLog(Path logFile, String message) {
		// Log the extra files/folders
		try {
			try (BufferedWriter logWriter = Files.newBufferedWriter(logFile, StandardOpenOption.CREATE,
					StandardOpenOption.APPEND)) {
				logWriter.write(message + "\n");
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public static boolean isFileInList(String filePath, List<String> list) {
		for (String l : list) {
			if (filePath.contains(l)) {
				return true;
			}
		}
		return false;
	}



	public static void moveFilesWithStructure(Path logFile, Path baseDirectorySourceForExtraFiles,
			List<Path> extraFilePaths, String targetDirectory, List<String> doNotMove) throws IOException {
		Path targetDirPath = Paths.get(targetDirectory);
		writeToLog(logFile, "Moved Files:");
		// Create the target directory if it doesn't exist
		if (!Files.exists(targetDirPath)) {
			Files.createDirectories(targetDirPath);
		}

		for (Path extraFilePath : extraFilePaths) {
			
			if(isFileInList(extraFilePath.toString(), doNotMove)) {
				
				continue;
			}

			Path relativePath = baseDirectorySourceForExtraFiles.relativize(extraFilePath);
			Path targetPath = targetDirPath.resolve(relativePath);

			// Create target subdirectories if they don't exist
			if (!Files.exists(targetPath.getParent())) {
				Files.createDirectories(targetPath.getParent());
			}

			// Move the file to the target location
			try {
				Files.move(extraFilePath, targetPath);
				writeToLog(logFile, extraFilePath.toString());
			} catch (Exception e) {
				writeToLog(logFile, e.getMessage());
				System.out.println("file/folder " + targetPath + " may have been already created");
				// e.printStackTrace();
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


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DuplicateFileEliminator {

	static List<String> filesThatWereActuallyExcluded = new ArrayList<>();

	final static int minFileSize = 500000;

	// Main method to run the duplicate eliminator
	public static void main(String[] args) throws Exception {

		if (args.length < 2) {
			System.out.println(
					"Usage: java DuplicateFileEliminator.java source=<directory_path> log_file=<log_file> exclude_files_dirs=<array of exclusions>");
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

		String directoryPath = params.get("source")[0];
		Path logFile = Paths.get(params.get("log_file")[0]);
		List<String> excludePaths = Arrays.asList(params.get("exclude_files_dirs"));

		List<FileGroup> fileList;

		try {
			fileList = findDuplicates(directoryPath, excludePaths, logFile);
			if (fileList.isEmpty()) {
				System.out.println("No duplicate files found.");
			} else {
				writeToLog(logFile, fileList);
				// Uncomment the following line to enable deletion of duplicates
				// removeDuplicates(duplicates);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static long getFileSize(File file) {
		return file.length();
	}

	public static List<FileGroup> findDuplicates(String directoryPath, List<String> excludePaths, Path logFile) {
		Map<Long, List<File>> filesBySize = new HashMap<>();

		final AtomicInteger file_count = new AtomicInteger(0);
		try {
			Files.walk(Paths.get(directoryPath)).filter(Files::isRegularFile).forEach(filePath -> {
				File file = filePath.toFile();
				long fileSize = getFileSize(file);
				if (fileSize > 2) {
					filesBySize.computeIfAbsent(fileSize, k -> new ArrayList<>()).add(file);
					System.out.print(".");
					file_count.incrementAndGet();
				}

			});
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.println(
				"Total file count = " + file_count.get() + "Total file count by unique sizes = " + filesBySize.size());
		List<FileGroup> fileList = new ArrayList<>();

		for (Map.Entry<Long, List<File>> entry : filesBySize.entrySet()) {
			List<File> files = entry.getValue();
			if (files.size() > 1) {

				Map<String, File> fileHashes = new HashMap<>();
				List<FileGroup> fileListGroupedByHashes = new ArrayList<>();// a single hash may map to multiple files.
				for (File file : files) {
					if (!isExcludeFile(file, excludePaths)) {
						String fileHash = null;
						try {
							fileHash = getFileHash(file);
						} catch (NoSuchAlgorithmException | IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (fileHashes.containsKey(fileHash)) {

							for (FileGroup fg : fileListGroupedByHashes) {
								try {
									if (!Files.isSameFile(file.toPath(), fg.original.toPath())) {
										if (comparefileByContents(file, fg.original)) {
											fg.copies.add(file);
										}
									}
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						} else {
							fileHashes.put(fileHash, file);
							FileGroup fg = new FileGroup();
							fg.original = file;
							fileListGroupedByHashes.add(fg);
						}
					}
				}

				for (FileGroup fg : fileListGroupedByHashes) {
					if (fg.copies.size() > 0) {
						fileList.add(fg);
						writeToLog(logFile, fg);
					}
				}

			}
		}

		return fileList;
	}

	public static boolean comparefileByContents(File file1, File file2) {
		try {
			if (file1.length() != file2.length()) {
				return false;
			}
			System.out.println("comparing file " + file1 + " and " + file2 + " by content...");
			try (FileInputStream fis1 = new FileInputStream(file1); FileInputStream fis2 = new FileInputStream(file2)) {
				// we don't have to compare every byte, if say first 1000 byte are the same its
				// the same file
				final int maxBytesCompare = 100;
				int bytesCompared = 0;
				int byte1, byte2;
				while ((byte1 = fis1.read()) != -1) {
					byte2 = fis2.read();
					if (byte1 != byte2) {
						return false;
					}
					bytesCompared++;
					if (bytesCompared > maxBytesCompare) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return false;
		}

		return true;

	}

	public static String getFileHash(File file) throws IOException, NoSuchAlgorithmException {
		MessageDigest digest;
		StringBuilder sb = new StringBuilder();
		try {
			digest = MessageDigest.getInstance("SHA-256");

			try (FileInputStream fis = new FileInputStream(file)) {
				byte[] byteArray = new byte[1024];
				int bytesCount = 0;
				while ((bytesCount = fis.read(byteArray)) != -1) {
					digest.update(byteArray, 0, bytesCount);
				}
			}
			byte[] bytes = digest.digest();

			for (byte b : bytes) {
				sb.append(String.format("%02x", b));
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		return sb.toString();
	}



	public static boolean isExcludeFile(File f, List<String> excludePaths) {
		String filePath = f.getAbsolutePath();
		if (f.length() < minFileSize) { // ignore files less that this figure
			return true;
		}
		for (String excludePath : excludePaths) {
			if (filePath.contains(excludePath)) {
				filesThatWereActuallyExcluded.add(filePath);
				return true;
			}
		}
		return false;
	}

	// Method to remove duplicates
	public static void removeDuplicates(List<File> duplicates) {
		for (File file : duplicates) {
			if (file.delete()) {
				System.out.println("Removed: " + file.getAbsolutePath());
			} else {
				System.err.println("Failed to remove: " + file.getAbsolutePath());
			}
		}
	}

	private static void writeToLog(Path logFile, FileGroup fg) {
		try {
			try (BufferedWriter logWriter = Files.newBufferedWriter(logFile, StandardOpenOption.CREATE,
					StandardOpenOption.APPEND)) {

				logWriter.write("-----------------\nOriginal = " + fg.original.getAbsolutePath() + " fsize="
						+ fg.original.length() + " fhash=" + getFileHash(fg.original) + "\n");
				for (File f : fg.copies) {
					logWriter.write("Dup = " + f.getAbsolutePath() + " fsize=" + f.length() + " fhash=" + getFileHash(f)
							+ "\n");
				}
				logWriter.write("\n\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void writeToLog(Path logFile, List<FileGroup> fileList) {
		try {

			// Log the extra files/folders
			try (BufferedWriter logWriter = Files.newBufferedWriter(logFile, StandardOpenOption.CREATE,
					StandardOpenOption.APPEND)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				logWriter.write("Log Time: " + sdf.format(System.currentTimeMillis()) + "\n");
				logWriter.write("Duplicate files found:\n");
				for (FileGroup fd : fileList) {
					logWriter.write("-----------------\nOriginal = " + fd.original.getAbsolutePath() + " fsize="
							+ fd.original.length() + " fhash=" + getFileHash(fd.original) + "\n");
					for (File f : fd.copies) {
						logWriter.write("Dup = " + f.getAbsolutePath() + " fsize=" + f.length() + " fhash="
								+ getFileHash(f) + "\n");
					}
					logWriter.write("\n\n");
				}

				logWriter.write("File that were excluded:\n");
				for (String f : filesThatWereActuallyExcluded) {
					logWriter.write(f + "\n");
				}

				logWriter.write("\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
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

class FileGroup {
	File original;
	List<File> copies = new ArrayList<>();

}

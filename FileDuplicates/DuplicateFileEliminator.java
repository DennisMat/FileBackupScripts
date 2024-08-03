
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DuplicateFileEliminator {



	static List<String> filesThatWereActuallyExcluded = new ArrayList<>();

	// Main method to run the duplicate eliminator
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.err.println("Usage: java DuplicateFileEliminator.java <directory_path> <log_file_path> <array of exclusions>");
			System.exit(1);
		}

		
		 
        List<String> excludePaths = new ArrayList<>();
        for (int i = 3; i < args.length; i++) {
        	excludePaths.add(args[i]);
        }

        
		String directoryPath = args[0];
		Path logFile = Paths.get(args[1]);
		List<FileDetails> fileList;

		try {
			fileList = findDuplicates(directoryPath, excludePaths);
			if (fileList.isEmpty()) {
				System.out.println("No duplicate files found.");
			} else {
				writeToLog(logFile, fileList);
				// Uncomment the following line to enable deletion of duplicates
				// removeDuplicates(duplicates);
			}
		} catch (IOException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public static long getFileSize(File file) {
		return file.length();
	}

	public static List<FileDetails> findDuplicates(String directoryPath, List<String> excludePaths) throws IOException, NoSuchAlgorithmException {
		Map<Long, List<File>> filesBySize = new HashMap<>();

		Files.walk(Paths.get(directoryPath)).filter(Files::isRegularFile).forEach(filePath -> {
			File file = filePath.toFile();
			long fileSize = getFileSize(file);
			filesBySize.computeIfAbsent(fileSize, k -> new ArrayList<>()).add(file);
		});

		List<FileDetails> fileList = new ArrayList<>();

		for (Map.Entry<Long, List<File>> entry : filesBySize.entrySet()) {
			List<File> files = entry.getValue();
			if (files.size() > 1) {
				// System.out.println("Dup found " + System.currentTimeMillis());
				List<File> duplicates = new ArrayList<>();
				Map<String, File> seenHashes = new HashMap<>();
				FileDetails fd = new FileDetails();
				for (File file : files) {
					String fileHash = getFileHash(file);
					if (seenHashes.containsKey(fileHash)) {
						duplicates.add(file);
						// System.out.println(file.getAbsolutePath());
					} else {
						seenHashes.put(fileHash, file);
						fd.original = file;

					}
				}
				fd.duplicates = duplicates;

				if (!isExcludeFile(fd,excludePaths)) {
					fileList.add(fd);
				}

			}
		}

		return fileList;
	}

	public static String getFileHash(File file) throws IOException, NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		try (FileInputStream fis = new FileInputStream(file)) {
			byte[] byteArray = new byte[1024];
			int bytesCount = 0;
			while ((bytesCount = fis.read(byteArray)) != -1) {
				digest.update(byteArray, 0, bytesCount);
			}
		}
		byte[] bytes = digest.digest();
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}

	public static boolean isExcludeFile(FileDetails fd, List<String> excludePaths) {
		if (fd.duplicates.size() == 0) {
			return true;
		}
		if(fd.original.length()<2) {
			return true;
		}
		String filePath = fd.original.getAbsolutePath();

		for (String excludePath : excludePaths) {
//			System.out.println("=========================");
//			System.out.println(filePath);
//			System.out.println(excludePath);
			
			if (filePath.contains(excludePath)) {
				filesThatWereActuallyExcluded.add(filePath);
//				System.out.println("============true=============");
				return true;
			}
		}
//		System.out.println("============false=============");
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

	private static void writeToLog(Path logFile, List<FileDetails> fileList) throws Exception {

		// Log the extra files/folders
		try (BufferedWriter logWriter = Files.newBufferedWriter(logFile, StandardOpenOption.CREATE,
				StandardOpenOption.APPEND)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			logWriter.write("Log Time: " + sdf.format(System.currentTimeMillis()) + "\n");
			logWriter.write("Duplicate files found:\n");
			for (FileDetails fd : fileList) {
				logWriter.write("-----------------\nOriginal = " + fd.original.getAbsolutePath()+" fsize=" +fd.original.length()+" fhash=" + getFileHash(fd.original) +"\n");
				for (File f : fd.duplicates) {
					logWriter.write("Dup = " + f.getAbsolutePath()+" fsize=" +f.length() +" fhash=" + getFileHash(f) +  "\n");
				}
				logWriter.write("\n\n");
			}

			logWriter.write("File that were excluded:\n");
			for (String f : filesThatWereActuallyExcluded) {
				logWriter.write(f+"\n");
			}

			logWriter.write("\n");
		}
	}

}

class FileDetails {
	File original;
	List<File> duplicates = new ArrayList<>();

}

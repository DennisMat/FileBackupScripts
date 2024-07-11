
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DuplicateFileEliminator {




    // Main method to run the duplicate eliminator
    public static void main(String[] args) {
//        if (args.length != 1) {
//            System.err.println("Usage: java DuplicateFileEliminator <directory_path>");
//            System.exit(1);
//        }

        //String directoryPath = args[0];
        String directoryPath = "D:/dennis";
        try {
            List<FileDetails> fileList = findDuplicates(directoryPath);
            if (fileList.isEmpty()) {
                System.out.println("No duplicate files found.");
            } else {
                System.out.println("Duplicate files found:");
                for (FileDetails fd : fileList) {
                    System.out.println("-----------------\nOriginal = " + fd.original.getAbsolutePath());
                    for (File f : fd.duplicates) {
                        System.out.println("Dup = " + f.getAbsolutePath());
                    }
                }

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

    public static List<FileDetails> findDuplicates(String directoryPath) throws IOException, NoSuchAlgorithmException {
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
            	//System.out.println("Dup found " + System.currentTimeMillis());
            	List<File> duplicates = new ArrayList<>();
                Map<String, File> seenHashes = new HashMap<>();
                FileDetails fd = new FileDetails();
                for (File file : files) {
                    String fileHash = getFileHash(file);
                    if (seenHashes.containsKey(fileHash)) {
                        duplicates.add(file);
                        //System.out.println(file.getAbsolutePath());
                    } else {
                        seenHashes.put(fileHash, file);
                        fd.original=file;
                        
                    }
                }
                fd.duplicates=duplicates;
                
                if(isIncludeFile(fd)) {
               	 
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
    
    public static boolean isIncludeFile(FileDetails fd){
    	if(fd.duplicates.size()>0) {
    		return true;
    	}
    	String filePath=fd.original.getAbsolutePath();
    	if(filePath.contains(".git") 
    			|| filePath.contains("dennis\\\\work\\\\Property\\\\WebContent")
    			|| filePath.contains("dennis\\\\work\\\\Trading\\\\Stock\\\\WebContent")

    			
    			) {
    		return false;
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

}

class FileDetails{
	File original;
	List<File> duplicates = new ArrayList<>();
	
}

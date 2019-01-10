package demo.nio;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.util.Set;

public class FileAttributes {
	public static void main(String[] args) throws IOException {
		FileSystem fs = FileSystems.getDefault();
		Set<String> views = fs.supportedFileAttributeViews();
		boolean dosFound = false;
		for (String view: views) {
			System.out.println(view);
			if (view.equals("dos")) dosFound = true;
		}
		Path[] paths = {
				Paths.get("/Temp/john/temp.lnk"),	
				Paths.get("/Temp/john/temp.lnk"),	
				Paths.get("/Temp/john/tempLog.txt")	
		};
		if (dosFound) {
			for (Path path: paths) {
				printDosPathDetails(path);
			}
		}
	}
	private static void printBasicAttributes(BasicFileAttributes attrs) throws IOException {
		System.out.println("Regular file:      " + attrs.isRegularFile());
		System.out.println("symbolic link:     " + attrs.isSymbolicLink());
		System.out.println("size (from attrs): " + attrs.size());
		System.out.println("last access time:  " + attrs.lastAccessTime());
		System.out.println("last modify time:  " + attrs.lastModifiedTime());
	}
	private static void printDosPathDetails(Path path) throws IOException {
		System.out.println(path + "; size=" + Files.size(path) + " ****************************");
		DosFileAttributes attrs = 
				Files.readAttributes(path, DosFileAttributes.class);
		printBasicAttributes(attrs);
		System.out.println("Archive:     " + attrs.isArchive());
		System.out.println("Hidden:      " + attrs.isHidden());
		System.out.println("Read only:   " + attrs.isReadOnly());
		System.out.println("system file: " + attrs.isSystem());
	}
}

package demo.nio;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

public class FilesUtils {

	public static void main(String[] args) throws IOException, InterruptedException {
		copyDelete();
		walkFileTree();
		System.out.println("adios mi amiguito");
	}
	private static void copyDelete() throws IOException {
		Path path = Paths.get("/Temp/john/tempLog.txt");
		Path pathCopy = path.resolve("../tempLogCopy.txt");
		if (Files.exists(pathCopy, LinkOption.NOFOLLOW_LINKS)) {
			Files.delete(pathCopy);
			System.out.println(pathCopy + " deleted");
		} else {
			Files.copy(path, pathCopy);
			System.out.println(path + " copied to " + pathCopy);
		}
	}
	private static void walkFileTree() throws IOException {
		Path path = Paths.get("/Temp");
		FileVisitor<Path> processor = new FileVisitor<Path>() {
			@Override
			public FileVisitResult postVisitDirectory(Path path,
					IOException ioe) throws IOException {
				System.out.println("postVisitDirectory(" + path + ", " + ioe + ")");
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult preVisitDirectory(Path path,
					BasicFileAttributes arg1) throws IOException {
				System.out.println("preVisitDirectory(" + path + ")");
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes arg1)
					throws IOException {
				System.out.println("visitFile(" + path + ")");
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path path, IOException ioe)
					throws IOException {
				System.out.println("visitFileFailed(" + path + ", " + ioe + ")");
				return FileVisitResult.CONTINUE;
			}
		};
		Files.walkFileTree(path, processor);
	}

}

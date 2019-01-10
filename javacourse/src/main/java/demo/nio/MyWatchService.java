package demo.nio;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyWatchService implements Runnable {
	private boolean stop = false;

	public static void main(String[] args) {
		new MyWatchService();
	}
	public MyWatchService() {
		try (Scanner scanner = new Scanner(System.in)) {
			System.out.println("type name or q for quit:");
			ExecutorService executor = Executors.newFixedThreadPool(1);
			executor.execute(this);
			while (true) {
				String userName = scanner.nextLine();
				if (userName.equals("q")) break;
				System.out.println("hello " + userName);
			}
			this.stop = true;
		}
	}
	@Override
	public void run() {
		try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
			Path path = Paths.get("/Temp");
			WatchKey watchKey = path.register(watchService, ENTRY_CREATE,
					ENTRY_DELETE, ENTRY_MODIFY);
			System.out.println("watching " + path);
			while (!this.stop) {
				while ((watchKey = watchService.poll()) == null) {
					Thread.sleep(2000);
					if (this.stop) return;
					System.out.print(".");
				}
				for (WatchEvent<?> event : watchKey.pollEvents()) {
					WatchEvent.Kind<?> kind = event.kind();
					WatchEvent<Path> ev = (WatchEvent<Path>) event;
					Path pathName = ev.context();
					if (kind == ENTRY_MODIFY) {
						System.out.println(pathName + " modified");
					} else if (kind == ENTRY_DELETE) {
						System.out.println(pathName + " deleted");
					} else {
						System.out.println(pathName + " created");
					}
				}
				watchKey.reset();
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}

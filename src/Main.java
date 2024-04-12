import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class Main {
	
	public Main() {
		try {
			run();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) {
		new Main();
	}
	
	private void run() throws IOException {
		Scanner sc = new Scanner(System.in);
		System.out.println("Path: ");
		FileInputStream fis = new FileInputStream(sc.nextLine());
		//FileInputStream fis = new FileInputStream("C:\\Users\\DumUM\\IdeaProjects\\PoolEditor\\out\\production\\PoolEditor\\Pool.class");
		//FileInputStream fis = new FileInputStream("C:\\Users\\DumUM\\IdeaProjects\\PoolEditor\\out\\production\\PoolEditor\\Test.class");
		//FileInputStream fis = new FileInputStream("C:\\Users\\DumUM\\IdeaProjects\\PoolEditor\\Main.class");
		ClassFile cf = new ClassFile(fis);
		cf.print();
		
		while (true) {
			System.out.println("Index: ");
			int index = sc.nextInt();
			sc.nextLine();
			if (index == -1) break;
			if (index == -2) {
				cf.print();
				continue;
			}
			
			System.out.println("New value: ");
			String repl = sc.nextLine();
			
			cf.changeString(index, repl);
			
		}
		
		System.out.println("Path: ");
		FileOutputStream fos = new FileOutputStream(sc.nextLine());
		cf.write(fos);
		fos.close();
		
		sc.close();
		fis.close();
		
		new Test().one();
		
	}
	
}

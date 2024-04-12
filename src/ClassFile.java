import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ClassFile {
	
	private byte[] before;
	private Pool p;
	private byte[] after;
	
	
	public ClassFile(InputStream is) {
		try {
			run(is);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void run(InputStream is) throws IOException {
		before = new byte[10];
		is.readNBytes(before, 0, 10);
		
		
		p = new Pool(((before[8] << 24) >>> 16 | (before[9] << 24) >>> 24) - 1);
		p.read(is);
		
		after = new byte[is.available()];
		is.read(after);
	}
	
	public void write(OutputStream os) throws IOException {
		os.write(before);
		p.write(os);
		os.write(after);
	}
	
	public void changeString(int idx, String repl) {
		p.changeString(idx, repl);
	}
	
	public void print() {
		p.print();
	}
	
}

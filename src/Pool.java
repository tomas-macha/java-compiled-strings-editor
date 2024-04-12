import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class Pool {
	
	private final Node[] pool;
	private InputStream is;
	private int poolSize;
	
	public Pool(int length) {
		pool = new Node[length];
		poolSize = length;
	}
	
	public void read(InputStream is) throws IOException {
		this.is = is;
		for (int i = 0; i < poolSize; i++) {
			Tag t = readTag();
			if (t == Tag.Utf8) {
				byte high = (byte) is.read();
				byte low = (byte) is.read();
				
				//int lng = high << 8 | low;
				int lng = (high << 24) >>> 16 | (low << 24) >>> 24;
				
				byte[] data = new byte[lng + 2];
				data[0] = high;
				data[1] = low;
				is.readNBytes(data, 2, lng);
				pool[i] = new Node(t, data);
			} else {
				pool[i] = new Node(t, is.readNBytes(t.dataLength));
			}
			
			if (t.dataLength == 8) poolSize--;
		}
	}
	
	public void print() {
		for (int i = 0; i < pool.length; i++) {
			Node n = pool[i];
			if (n == null) break;
			if (n.tag == Tag.Utf8) {
				System.out.println(i + ": " + new String(Arrays.copyOfRange(n.data, 2, n.data.length)));
			}
		}
	}
	
	public void changeString(int idx, String repl) {
		if (idx < 0 || idx >= poolSize) {
			System.err.println("Index out of bounds.");
			return;
		}
		if (pool[idx].tag != Tag.Utf8) {
			System.err.println("Not an UTF8 constant.");
			return;
		}
		byte[] str = repl.getBytes();
		byte[] data = new byte[str.length + 2];
		
		data[0] = (byte) (str.length >>> 8 & 0xff);
		data[1] = (byte) (str.length & 0xff);
		
		System.arraycopy(str, 0, data, 2, data.length - 2);
		
		Node n = new Node(Tag.Utf8, data);
		pool[idx] = n;
	}
	
	public void write(OutputStream os) throws IOException {
		for (int i = 0; i < poolSize; i++) {
			Node n = pool[i];
			os.write((byte) n.tag.val);
			os.write(n.data);
		}
	}
	
	private Tag readTag() throws IOException {
		return Tag.values()[is.read() - 1];
	}
	
	enum Tag {
		Utf8(1, -1), __(2, 0), Int(3, 4), Float(4, 4), Long(5, 8),
		Double(6, 8), Class(7, 2), String(8, 2), Field(9, 4), Method(10, 4),
		InterMethod(11, 4), NameAndType(12, 4), ___(13, 0), ____(14, 0),
		MethodHandle(15, 3), MethodType(16, 2), Dynamic(17, 4), InvokeDynamic(18, 4),
		Module(19, 2), Package(20, 2);
		final int dataLength;
		final int val;
		
		Tag(int val, int dataLength) {
			this.dataLength = dataLength;
			this.val = val;
		}
	}
	
	static class Node {
		Tag tag;
		byte[] data;
		
		public Node(Tag tag) {
			this.tag = tag;
		}
		
		public Node(Tag tag, byte[] data) {
			this.tag = tag;
			this.data = data;
		}
	}
	
}

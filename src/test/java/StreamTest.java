import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class StreamTest {

	public static void main(String[] args) throws Exception {
		StreamTest test = new StreamTest();
		
		long startTime = System.currentTimeMillis();
		
		test.testStream();
		
		long endTime = System.currentTimeMillis();
		
		System.out.println("elapse time : " + (endTime - startTime) / 1000D + " seconds");
	}
	
	private void testStream() throws Exception {
		String contents = new String(Files.readAllBytes(Paths.get("README.md")), StandardCharsets.UTF_8);
		List<String> words = Arrays.asList(contents.split("[\\P{L}]+"));
		
		int count = 0;
		for (String w : words) {
			if (w.length() > 12) {
				count++;
			}
		}
		
		long c = words.stream().filter(w -> w.length() > 12).count();
		
		System.out.println("count : " + count + ", stream's c : " + c);
	}

}

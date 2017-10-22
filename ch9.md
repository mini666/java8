## 9 혹시 놓쳤을 수도 있는 자바7 기능
* 핵심내용
  * AutoClosable을 구현하는 모든 객체를 다룰 때 try-with-resources 문을 사용
  * try-with-resources 문은 리소스를 닫는 작업이 또 다른 예외를 던지면 주 예외를 다시 던진다.
  * catch 절 하나로 관련 없는 여러 예외를 잡을 수 있다.
  * 리플렉션 연산에서 발생하는 예외는 이제 공통 슈퍼클래스로 ReflectiveOperationException 을 둔다.
  * File 클래스 대신 Path 인터페이스를 사용한다.
  * 단일 명령으로 텍스트 파일의 모든 몬자 또는 행을 읽고 쓸 수 있다.
  * Files 클래스는 파일을 복사, 이동, 삭제하고 파일과 디렉토리를 생성하는 정적 메서드를 제공한다.
  * 널 안전 동등성 테스트에 Objects.equals를 사용한다.
  * Objects.hash를 이용하면 hashCode 메서드를 쉽게 구현할 수 있다.
  * 비교자에서 숫자를 비교할 때는 정적 compare 메서드를 사용한다.
  * 애플릿과 자바 웹 스타트 애플리케이션은 기업 환경에서 계속 지원되지만, 가정 사용자용으로는 더 이상 적합하지 않다.
  * 모두가 반길 만한 사소한 변경 사항은 이제 예외를 던지지 않고 "+1"을 정수로 변활할 수 있다는 점이다.
  * ProcessBuilder는 표준 입력, 표준 출력, 표준 오류 스트림을 쉽게 리다이렉트할 수 있게 변경되었다.
  
### 예외 처리 변경 사항
*try-with-resources 문은 리소스를 닫은 후 catch/finally 절이 실행된다. 실제너에서 단일 try문에서 너무 많은 일을 하는 것은 좋은 생각이 아니다.*
#### 생략 예외
IOException 이 발생한 다음 리소스를 닫을때 close 호출에서 또 다른 예외를 던질때 자바에서는 finally 절에서 던져진 예외가 이전 예외를 버린다.  
try-with-resources 문은 이 동작을 반대로 바꾼다. AutoClosable 객체 중 하나으 ㅣclose 메서드에서 예외를 던질때 원래 예외를 다시 던지며 close 호출에서 발생한 예외를 잡아 '생략' 예외(Suppressesd expception)로 첨부한다.

```
try {
  ...
} catch (IOException e) {
  Throwable[] secondaryExceptions = e.getSuppressed();
}
```

try-with-resources 문을 사용할 수 없는 상황에서 이러한 메카니즘을 직접 구현하고 싶다면 다음과 같이 호출한다. `e.addSuppressed(secondaryException);`  
*Throwable, Exception, RuntimeException, Error 클래스는 생략 예외와 스택 추적(Stack trace)을 비활성화하는 옵션이 잇는 생성자를 포함한다. 생략 예외를 비활성화하면 addSupporessed 호출이 효력을 잃고 getSuppressed 는 길이 0인 배열을 리턴한다. 스택 추적을 비활성화하면 fillInStackTrace 호출이 효력을 잃고, getStackTrace는 길이 0인 배열을 리턴한다. 이러한 기능은 메모리가 부족할 때 발생하는 VM 오류 또는 VM 기반 프로그래밍 언어에서 중첩된 메서드 호출에서 빠져나오는 데 예외를 사용하는 경우에 유용하다.*

#### 더 쉬운 리플렉션 메서드 예외 처리

```
try {
  Class.forName(className).getMethod("main").invoke(null, new String[] {});
// catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) { =>
catch (ReflectiveOperationException e) {
  ...
}
```

### 파일 작업
#### 경로
`p.resolve(q)`
  * q가 절대 경로면, 결과는 q다.
  * q가 상대 경로면, 결과는 파일 시스템 규칙에 따라 'p 다음 q'다.
  
애플리케이션에서 홈 디렉토리를 기준으로 설정 파일을 찾을때 `Path configPath = homeDirectory.resolve("myprog/conf/user.properties");`  
경로의 부무를 대상으로 경로를 해석해서 이웃 경로를 돌려주는 resolveSibling이라느 편의 메서드가 있다. 예를 들어, workPath가 /home/cay/myprog/work 라면 다음 호출은 /home/cay/myprog/temp 를 돌려준다. `Path tempPath = workPath.resolveSibling("temp");`  

`Paths.get("/home/cay").relativize(Paths.get("/home/fred/myprog"));  // ../fred/myprog 가 리턴` normalize 메서드는 중복된 모든 . 및 .. 을 제거한다. toAbsolutePath 메서드는 주어진 경로의 절대 경로를 돌려준다. 경로가 이미 절대 경로가 아닌 경우 사용자 디렉토리를 기준으로 해석한다.

Path 인터페이스는 경로를 분리하고 다른 경로와 결합하는데 유용한 메서드를 다수 포함한다.

```
Path p = Paths.get("/home", "cay", "myprog.properties");
Path parent = p.getParent();  // 경로 /home/cay
Path file = p.getFileName();  // myprog.properties
Path root = p.getRoot();      // / (상대경로일때는 null
```

*경우에 따라 Path 인터페이스 대신 File 클래스를 사용하는 레거시 API와 상호 동작해야 할 수도 있다. Path 인터페이스는 toFile 메서드를 포함하며, File 클래스는 toPath 메서드를 포함한다.*

#### 파일 읽기 및 쓰기
```
byte[] bytes = Files.readAllBytes(path);
String content = new String(bytes, StandardCharsets.UTF_8);

// 행으로 읽기
List<String> lines = Files.readAllLines(path);
// 파일에 쓰기
Files.write(path, content.getBytes(StandardCharsets.UTF_8));
Files.write(path, lines); // 문자열 컬렉션 파일에 쓰기

// 파일에 추가하기
Files.write(path, lines, StandardOpenOption.APPEND);

// 파일이 클 때
InputStream in = Files.newInputStream(path);
OutputStream out = Files.newOutputStream(path);
Reader reader = Files.newBufferedReader(path);
Writer writer = Files.newBufferedWriter(path);

// InputStream의 내용을 파일에 저장하고 싶을 때
Files.copy(in, path);
// 반대
Files.copy(path, out);
```

*기본적으로 Files의 모든 문자 읽기 또는 쓰기 메서드는 UTF-8 인코딩을 사용한다. 다른 인코딩이 필요한 (바라건대 드문) 경우, Charset 인자를 제공할 수 있다. 플랫폼 디폴트를 사용하는 String 생성자와 getBytes 메서드와는 대조적이다. 일반적으로 사용하는 데스크톱 운영 체제는 여전히 UTF-8과 호환되지 않는 구식 8비트 인코딩을 사용한다. 따라서 문자열과 바이트 사이에서 변환할 때는 인코딩을 지정해야 한다.*

#### 파일과 디렉토리 생성하기
새로운 디렉토리 생성 `Files.createDirectory(path);`  
중간 경로를 포함하여 생성하려면 `Files.createDirectories(path);`  
빈 파일 생성 `Files.createFile(path);` 파일이 이미 존재하는 경우 예외발생. 파일 존재 검사와 생성은 원자적으로 동작함.   
`path.exists()` 메서드 호출은 주어진 파일 또는 디렉토리가 존재하는지 검사하지만, 리턴한 시점에는 존재하지 않을 수 있다.  
 지정 위치 또는 시스템 고유의 위치에 임시 파일 또는 디렉토리를 생성하는 편의 메서드
 
 ```
 // prefix, suffix는 널 일 수 있음.
 Path newPath = Files.createTempFile(dir, prefix, suffix);
 Path newPath = Files.createTempFile(prefix, suffix);
 Path newPath = Files.createTempDirectory(dir, prefix);
 Path newPath = Files.createTempDirectory(prefix);
 ```
 
#### 파일 복사, 이동, 삭제하기
파일 복사 `Files.copy(fromPath, toPath);`  
파일 이동 `Files.move(fromPath, toPath);` 빈 디렉토리 이동에도 사용.  
존재하는 대상을 덮어쓰려면 REPLACE\_EXISTING 옵선 사용. 모든 파일 속성을 복사하려면 COPY\_ATTRIBUTES 옵션 사용. `Files.copy(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);`  
이동이 원자적으로 동작해야 함을 명시할 수 있다. ATOMIC\_MOVE 사용. `Files.move(fromPath, toPath, StandardCopyOption.ATOMIC_MOVE);`  
파일 삭제 `boolean deleted = Files.deleteIfExists(path);` 빈 디렉토리도 지울 수 있다.   

*비어있지 않은 디렉토리를 삭제하거나 복사하는 편의 메서드는 없다. FileVisitor를 사용한다.*

### equals, hashCode, compareTo 메서드 구현
자바7은 equals, hashCode에서 널 값을 다루는 작업과 compareTo에서 숫자 비교 작업을 더 편리하게 해주는 몇 가지 메서드를 도입.

#### 널 안전 동등성 테스트
```
public class Person {
  private String first;
  private String second;
  ...
  
  public boolean equals(Object otherObject) {
    if (this == otherObject) return true;
    if (otherObject == null) return false;
    if (getClass() != otherObject.getClass()) return false;
    Person other = (Person)otherObject;
    ...
  }
  
  // 다음과 같이 변경
  public boolean equals(Object otherObject) {
    return Objects.equals(first, other.first) && Objects.equals(last, other.last);
  }
}
```

#### 해시 코드 계산하기
앞의 Person 클래스에 대한 히시 코드 구현

```
public int hashCode() {
  return 31 * Objects.hashCode(first) + Objects.hashCode(last); // Objects.hashCode는 null 인자의 경우 0을 리턴.
  return Objects.hash(first, last); // 어떤 값이든 연속해서 지정할 수 있으며, 지정한 값들의 해시 코드를 결합.
}
```

*Objects.hash는 단순히 Arrays.hashCode를 호출한다. Arrays.hashCode는 자바5부터 존재해왔지만 가변 인자 메소드가 아니어서 다소 불편하다.*  
*예전부터 toString 대신 String.valueOf(obj)를 호출하는 널 안전 호출 방법이 있었다. String.valueOf(obj)는 obj가 null이면 문자열 "null"을 리턴한다. 이 방법이 마음에 들지 않으면 Object.toString(obj, "")처럼 Object.toString을 사용하고 null인 경우 사용할 값을 전달할 수 있다.*

#### 숫자 타입 비교하기
비교자에서 정수들을 비교할때 어떤 음수 또는 양수든 리턴할 수 있기 때문에 정수 사이의 차이를 리턴하고 싶을 수 있다.

```
public int compareTo(Point other) {
  int diff = x - other.x;
  if (diff != 0) return diff;
  return y - other.y;
}

// other.x가 음수인 경우 오버플로우
public int compareTo(Point other) {
  int diff = Integer.compare(x, other.x); // 오버플로우 위험이 없다.
  if (diff != 0) return diff;
  return Integer.compare(y, other.y);
}
```

### 보안 요구 사항
원본 참고

### 기타 변경 사항
#### 문자열을 숫자로 변환하기
```
double x = Double.parseDouble("+1.0");  // 예전에도 지금도 에러 없이 처리.
int n = Integer.parseInt("+1");         // 1.7부터 처리
```

문자열로부터 int, long, short, byte, BigInteger 값을 만들어내는 다양한 메서드에서 이 문제가 모두 고쳐짐.  
parse(Int|Long|Short|Byte)외에 16진수와 8진수 입력을 다루는 decode 메서드, 래퍼 객체를 돌려주는 valueOf 메서드들이 있다. BigInteger(String) 생성자 또한 업데이터 되었다.

#### 전역 로거
간단한 경우에도 로깅의 사용을 장려하기 위해 Logger 클래스는 전역 로거 인스턴스를 포함한다. 언제라도 추적 문장을 `System.out.println("x=" + x);` 대한 `Logger.global.finest("x=" + x);` 형태로 추가할 수 있게 가능하면 사용하기 쉽게 만들어졌다.

유감스럽게도 이 인스턴스 변수는 어딘가에서 초기화되어야 하며, 만일 다른 로깅이 정적 초기화 코드에서 발생하면 교착 상태를 일으킬 수 있다. 따라서 자바6에서는 Logger.global 사용을 권장하지 않는다. 대신 `Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)`을 호출해야 하는데 누구도 이 방법을 빠르고 쉬운 로깅이라고 생각하지 않는다. 자바7에서는 대신 `Logger.getGlobal()`을 호출할 수 있으며 나쁘지 않는 방법이다.

#### 널 검사
Objects 클래스는 파리미터의 널 검사를 편리하게 해주는 requireNonNull 메서드를 제공.

```
public void process(String directions) {
  this.directions = Objects.requireNonNull(directions);
  ...
  this.directions = Objects.requireNonNull(directions, "directions must not be null");
}
```

#### ProcessBuilder
ProcessBuilder를 이용하면 작업 디렉토리를 변경할 수 있다. 자바7은 프로세스의 표준 입력, 표준 출력, 표준 오류 스트림을 파일로 연결하는 편의 메서드를 제공.

```
ProcessBuilder builder = new ProcessBuilder("grep", "-o", "[A-za-z_][A-Za-z_0-9]*");
builder.redirectInput(Paths.get("Hello.java").toFile());
builder.redirectOutput(Paths.get("identifiers.txt").toFile());
Process process = builder.start();
process.waitFor();
```

*자바8부터는 Process 클래스에서 타임아웃을 받는 waitFor 메서드를 제공한다. boolean completed = process.waitFor(1, TimeUnit.MINUTES);*

자바7에서는 ProcessBuilder에 inheritIO 메서드도 추가. 프로세스의 표준 입력, 표준 출력, 표준 오류 스트림을 자바 프로그램의 표준 입력, 표준 출력, 표준 오류 스트림으로 설정한다.  
다음은 ls 명령의 출력을 System.out으로 보낸다.

```
ProcessBuilder builder = new ProcessBuilder("ls", "-al");
builder.inheritIO();
builder.start().waitFor();
```

#### URLClassLoader
```
URL[] urls = {
  new URL("file:junit-4.11.jar"),
  new URL("file:hamcrest-core-1.3.jar")
};
URLClassLoader loader = new URLCloassLoader(urls);
Class<?> klass = loader.loadClass("org.junit.runner.JUnitCore");

// URLClassLoader는 자바7에서 AutoClosable를 구현한다.
try (URLClassLoader loader = new URLClassLoader(rls)) {
  Class<?> klass = loader.loadClass("org.junit.runner.JUnitCore");
  ...
}
```

#### BitSet
BitSet은 일련의 비트로 구현된 정수 집합이다. 만일 집합이 정수 i를 포함하면 i번째 비트가 설정된다. 따라서 효율적인 집합 연산을 가능하게 한다. 합집합/교집합/여집합은 단순한 비트 단위 or/and/not 연산이다.  
자바7 비트 집합을 생성하는 메서드를 추가했다.

```
byte[] bytes = {(byte)0b10101100, (byte)0b00101000 };
BitSet primes = BitSet.valueOf(bytes);    // 2, 3, 5, 7, 11, 13
long[] longs = { 0x100010116L, 0x1L, 0x1L, 0L, 0x1L };
BitSet powersOfTwo = BitSet.valueOf(longs);   // 1, 2, 4, 8, 16, 32, 64, 128, 256

// 역으로 비트 집합으로부터 각 배열을 만든느 메서드는 toByteArray와 toLongArray다.
byte[] bytes = powersOfTwo.toByteArray();     // 0b0010110, 1, 1, 0, 1, 0, 0, 0, 1, ...
```

*자바8부터 BitSet은 IntStream을 돌려주는 stream 메서드를 포함한다.*

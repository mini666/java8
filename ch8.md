## 8장 그 외 여러가지 주제
* 핵심내용
  * 구분자를 이용한 문자열 결합이 마침내 쉬워졌다. 예를 들어, a + ", " + b + ", " + c 대신 String.join((", ", a, b, c)를 사용할 수 있다.
  * 정수 타입에서 이제 부호 없는 산술 연산을 지원한다.
  * Math 클래스는 정수 오버플로우를 감지하는 메서드를 포함
  * x가 음수일 가능성이 있으면 x % n 대신 Math.floorMod(x, n)을 사용
  * Collection과 List에 새로운 변경자가 생겼다.(Collection의 removeIf와 List의 replaceAll, sort)
  * Files.lines는 행으로 구성된 스트림을 지연 방식으로 읽는다.
  * Files.list는 디렉토리 엔트리들을 지연 방식으로 나열하며, Files.walk는 디렉토리 엔트리들을 재귀적으로 순회한다.
  * Base64 인코딩을 공식 지원
  * 이제 Annotation을 반복할 수 있고, 타입 사용에 적용할 수 있다.
  * Objects 클래스에서 널 파라미터 검사를 편리하게 해주는 지원 사항을 찾을 수 있다.
  
### 문자열
문자열은 배열이나 Iterable<? extends CharSequence>로부터 올 수 있다. 자바8에서 String 클래스에 추가된 유일한 메서드가 join이다.

```
String joined = String.join("/", "usr", "local", "bin");
String ids = String.join(", ", ZoneId.getAvailableZoneIds());
```

### 숫자 클래스
숫자형 기본 타입 래퍼에 바이트 단위로 크기를 알려주는 BYTES 필드가 생김.
또한, 기본 타입 래퍼 8개 모두 박싱 없이도 인스턴스 메서드와 동일한 해시코드를 리턴하는 정적 hashCode 메서드 포함.
Short, Integer, Long, Float, Double 이 5가지 타입은 스트림 연산에서 리덕션 함수로 유용하게 사용할 수 있는 정적 메서드(sum, max, min)을 포함.
Boolean 클래스는 같은 목적으로 정적 메서드 localAnd, logicalOr, logicalXor를 포함.

정수 타입들은 이제 부호 없는 산술 연산을 지원. 예를 들어 -128 ~ 127 범위를 표현하는 Byte 대신, 정적 메서드 `Byte.toUnsignedInt(b)`를 호출하여 0 ~ 255 사이의 값을 얻을 수 있다.
Byte와 Short 클래스는 toUnsignedInt 메서드를 포함하며 Byte, Short, Integer 클래스는 toUnsignedLong 메서드를 포함.

Integer와 Long 클래스는 부호 없는 값을 다루는 compareUnsigned, divideUnsigned, remainderUnsigned 메서드를 포함.
정수 곱셈은 Integer.MAX_VALUE 보다 큰 부호 없는 정수일때 오버플로우를 일으킬 수 있으므로, toUnsignedLong을 호출해 long 값으로 곱해야 한다.

Float와 Double 클래스는 정적 메서드 isFinite를 포함. Double.isFinite(x) 호출은 x가 무한대, 음의 무한대, NaN이 아닐때 true를 리턴.과거에는 같은 결과를 얻기 위해 인스턴스 메서드 isInfinite와 isNaN을 호출해야 했다.

마지막으로 BigInteger 클래스는 값을 각각 long, int, short, byte로 리턴하는 인스턴스 메서드인 (long|int|short|byte)valueExact를 포함. 이들 메서드는 값이 대상 범위 안에 없으면 ArithmeticException을 던진다.

### 새로운 수학 함수
Math 클래스는 결과가 오버플로우될 때 예외를 던지는 **정확한** 산술을 지원하는 메서드를 제공. 예를 들어 100000 * 100000은 소리 없이 잘못된 결과인 1410065408을 주지만, multiplyExact(100000, 100000)은 예외를 던진다.
제공되는 메서드로는 (add|subtract|multiply|increment|decrement|megate)Exact가 있으며 각각 int와 long 파라미터 버전이 있다.toIntExact 메서드는 long을 같은 값의 int로 변환한다.

floorMod와 floorDiv 메서드는 정수 나머지와 관련한 오랜 난제의 해결을 목표로 한다. floorMod(position + adjustment, 12)는 adjustment가 음수여도 0 ~ 11 사이의 값을 리턴한다. 나누는 값이 음수일때는 음수를 리턴한다.

double과 float 파라미터 버전이 모두 정의된 nextDown 메서드는 주어진 숫자 다음으로 작은 부동소수점 수를 준다. 예를 들어 b보다 적은 숫자를 생산하기로 한(하지만 어쩌다 정확히 b로 계산되는) 경우 Math.nextDown(b)를 리턴할 수 있다(자바6부터는 이에 대응하는 Math.nextUp 메서드가 존재한다).

*이 절에서 설명한 모든 메서드는 StrictMath 클래스에도 존재한다.*

### 컬렉션
#### 컬렉션 클래스에 추가된 메서드
아래 표는 자바8에서 stream, parallelStream, spliterator 메서드 외에 컬렉션 클래스 및 인터페이스에 추가된 여러 가지 함수를 보여준다.

클래스/인터페이스 | 새로운 메서드
Iterable | forEach
Collection | removeIf
List | replaceAll, sort
Map | forEach, replace, replaceAll, remove(key, value)(key가 value에 맵핑되어 있는 경우에만 삭제), putIfAbsent, compute, computeIf(Absent|Present), merge
Iterator | forEachRemaining
BitSet | stream

Stream 인터페이스는 람다 표현식을 받는 수많은 메서드를 포함하지만 Collection 인터페이스는 이런 메서드가 하나(removeIf)밖에 없다. Stream의 메서드는 대부분 단일 값 또는 원본 스트림에는 없던 변환된 갓들의 스트림을 리턴한을 알 수 있다.
이 중 filter와 distinct 메서드은 예외. removeIf 메서드는 filter의 반대로 생각할 수 잇는데, 모든 일치 항목을 생산하기보다는 즉석에서 제거를 수행한다. 또한, distinct 메서드를 임의의 컬렉션을 대상으로 적용하기에는 많은 비용이 들 우려가 있다.

List 인터페이스는 replaceAll, sort 메서드 포함.  
Iterator 인터페이스는 남아있는 반복자 요소를 함수에 전달하는 벙법으로 반복자를 소진하는 forEachRemaining 메서드 포함.  
BitSet 클래스는 해당 비트 집합의 모든 멤버를 int 값들의 스트림으로 돌려주는 메서드 포함.

#### 비교자
Cmparator 인터페이스는 이제 인터페이스가 구체적인 메서드를 포함할 수 있다는 점을 이용한 유용하고 새로운 메서드를 다수 포함한다.  
정적 comparing 메서드는 타입 T를 비교 가능한 타입으로 맵핑하는 *키 추출* 함수를 받는다. 전달받은 함수를 비교대상 객체에 적용하고 리턴받은 키를 비교한다. 예를 들어 Persion 객체의 배열이 있을때 이름으로 정렬하는 방법

```
Arrays.sort(people, Comparator.comparing(Persion::getName)
  .thenComparing(Persion::getFirstName)     // 키가 같을 때 추가 비교를 위해 thenComparing 메서드로 비교자를 이을 수 있다.
  );
  
// 사람들의 이름 길이에 따라 정렬
Arrays.sort(people, Comparator.comparing(Persion::getName, (s, t) -> Integer.compare(s.length(), t.length())));

// int, long, double 값의 박싱을 피하는 변종
Arrays.sort(people, Comparator.comparingInt(p -> p.getName().length()));

// null 값이 있을 수도 있는 것을 비교할때 nullsFirst, nullsLast 사용
Arrays.sort(people, comparing(Persion::getMiddleName, nullsFirst(natualOrder())));

// 역 정렬하고자 할때 reverseOrder와 natualOrder().reversed()는 같다.
```

#### Collections 클래스
자바6에서는 요소 또는 키의 순서를 이용하는 NavigableSet과 NavigableMap 클래스를 도입. 이들 클래스는 모든 주어진 값 v에 대해 >= 또는 > v 인 가장 작은 요소나 <= 또는 < v 인 가장 큰 요소를 찾아내는 효율적인 메서드를 제공.  
이제 Collections 클래스도 다른 컬렉션 클래스들과 마찬가지로 (unmodifiable|synchronized|checked|empty)Navigable(Set|Map) 메서드를 통해 이들 클래스를 지원한다.

디버깅 보조 수단으로 checkedQueue 래퍼도 추가.

정렬된 컬렉션의 경량 인스턴스를 주는 emptySorted(Set|Map) 메서드.

### 파일 작업
자바8은 파일에서 행을 읽고, 디렉토리 엔트리를 방문하는 작업에스트림을 사용하는 소수의 편의 메서드 제공. 또한 Base64 인코딩 및 디코딩 지원.
#### 행 스트림
파일의 행들을 지연 방식으로 읽으려면 Files.lines 메서드 사용. 이 메서드는 문자열 스트림을 리턴한다.

```
// 2장의 Stream은 닫을 필요가 없으나 Files.lines는 닫아야 한다.
try (Stream<String> lines = Files.lines(path)) {
  Optional<String> passwordEntry = line.filter(s -> s.contains("password")).findFirst();
}
```

password를 포함하는 행을 처음 발견하는 즉시 이 후 행은 읽지 않는다.   
*로컬 문자 인코딩으로 파일을 열기 때문에 이식성이 악몽 같은 FileReader 클래스와 달리, Files.lines 메서드는 기본으로 UTF-8을 사용한다. Charset 인자를 전달하여 다른 인코딩을 지정할 수 있다.*

스트림이 닫힐때 통지를 받으려면 onClose 처리기를 붙인다.
```
try (Stream<String> filteredLines = Files.lines(path).onClose(() -> System.out.println("Closing")).fitler(s -> s.contains("password"))) {
  ...
}
```

스트림이 행을 가져올 때 IOException이 발생하면, 해당 예외가 스트림 연산에서 던지는 UncheckedIOException으로 포장된다(스트림 연산은 검사 예외를 던지도록 선언되어 있지 않기 때문에 이런 꼼수가 필요).   
파일 외의 소스에서 행을 읽고 싶다면 BufferedReader.lines 메서드를 사용

```
try(BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
  Stream<String> lines = reader.lines();
  ...
}
```

*대략 10년전, 자바 5는 다루기 힘든 BufferedReader를 대체하려고 Scanner 클래스를 도입했다. 자바 8 API 설계자들이 lines 메서드를 BufferedReader에 추가하고 Scanner에는 추가하지 않기로 결정한 것이느 유감이다.*

#### 디렉토리 엔트리 스트림
정적 Files.list 메서드는 디렉토리의 엔트리를 읽는 Stream<Path>를 리턴한다. 디렉토리를 지연 방식으로 읽기 때문에 엄청나게 많은 엔트리를 포함하는 디렉토리를 효율적으로 처리할 수 있다.  
디렉토리 읽기는 닫기가 필요한 시스템 리소스와 연관되므로 try 블록을 사용해야 한다.  

*스트림은 내부적으로 자바 7에서 거대한 디렉토리들을 효율적으로 순회하려고 도입한 DirectoryStream 인터페이스를 사용한다. 이 인터페이스는 자바8 스트림과 아무 관련이 없다. DirectoryStream은 향상된 for 루프에서 사용할 수 있도록 Iterable을 확장한다.

```
try (DirectoryStream stream = Files.newDirectoryStream(pathToDirectory)) {
  for (Path entry : stream) {
    ...
  }
}
```

자바8에서는 그냥 Files.list를 사용하면 된다.*

list 메서드는 서브디렉토리로 들어가지 않는다. 디렉토리의 모든 자손을 처리하려면 대신 Files.walk 메서드를 사용한다.

```
try (Stream<Path> entries = Files.walk(pathToRoot)) {
  // 너비 우선으로 방문
}
```

```Files.walk(pathToRoot, depth)```로 트리의 깊이를 제한할 수 있다. 두 walk 메서드 모두 FileVisitOption... 타입인 가변 인자 파라미터를 받지만, 현재 전달 할 수 있는 옶션은 심볼릭 링크를 따라가도록 하는 FOLLOW_LINKS 하나만 있다.  

*만일 walk에서 리턴한 경로들을 필터링하고, 필터 기준이 크기, 생성 시각, 타입(파일, 디렉토리, 심볼릭 링크) 같은 디렉토리와 함께 저장된 파일 속성과 연관되는 경우에는 walk 대신 find 메서드를 사용한다. 경로와 BasicFileAttributes 객체를 받는 Predicate 함수를 전달하며 find 메서드를 호출한다. 유일한 장점은 효율성이다.*

#### Base64 인코딩
수년동안 JDK는 비공개 클래스인 java.util.prefs.Base64와 문서화되지 않은 클래스인 sun.misc.BASE64Encoder를 포함했다. 마침내 자바 8에서 표준 인코더, 디코더를 제공한다.  
Base64 인코딩은 대문자(26개), 소문자(26개), 심볼(+,/ 또는 _,-) 2개를 사용한다.  
인코드된 문자열은 행 바꿈을 포함하지 않지만 이메일에 사용하는 MIME 표준은 76문자마다 **\r\n** 행 바꿈을 요구한다.  

인코딩인 경우 Base64 클래스의 정적 메서드 getEncoder, getUrlEncoder, getMimeEncoder 중 하나를 이용해 Base64.Encoder를 요청한다.  
Base64.Encoder 클래스는 바이트 배열 또는 NIO ByteBuffer를 인코드하는 메서드를 포함한다.

```
Base64.Encoder encoder = Base64.getEncoder();
String original = username + ":" + password;
String encoded = encoder.encodeToString(original.getBytes(StandardCharsets.UTF_*));
```

다른 방법으로 출력 스트림을 '포장'해서 해당 스트림으로 보내는 모든 데이터를 자동으로 인코드할 수 있다.

```
Path originalPath = ..., encodePath = ..;
Base64.Encoder encoder = Base64.getMimeEncoder();
try (OutputStream output =  Files.newOutputStream(encodedPath)) {
  Files.copy(originalPath, encoder.wrap(output));
}
```

디코딩하려면 이러한 작업을 역으로 수행한다.

```
Path encodedPath = ..., decodedPath = ...;
Base64.Decoder decoder = Base64.getMimeDecoder();
try (InputStream input = Files.newInputStream(encodedPath)) {
  Files.copy(decoder.wrap(input), decodedPath);
}
```

### Annotation
자바8 Annotation은 처리에서 반복 어노테이션과 타입 사용 어노테이션이라는 두가지 개선 사항을 포함한다. 아울러 리플렉션은 메서드 파라미터 이름을 보고하고도록 개선되었다. 이 개선점은 메서드 파라미터에 대한 어노테이션을 간소화할 가능성이 있다.

#### 반복 어노테이션
어노테이션이 처음 만들어졌을 당시에는 다음과 같이 메서드와 필드를 처리 목적으로 표식하는데 사용할 계획이었다.

```
@PostConstruct public void fetchData() { ... }    // 생성 후에 호출한다.
@Resource("jdbc:derby:sample") private Connection conn; //여기에 리소스를 주입한다.
```

이 맥락에서는 같은 애너테이션을 두번 적용하는 일은 성립되지 않는다. 한 필드를 두가지 방법으로 주입할 수는 없기 때문이다. 물론 같은 요소에 서로 다른 어노테이션을 적용하는 일은 문제가 없으며 일반적으로 사용하는 방법이다.
```@Stateless @Path("/service") public class Service { ... }``` 곧 어노테이션을 점차 많이 사용하게 되면서 같은 어노테이션을 반복하면 좋을 상활까지 이어졌다. 예를 들어, 데이터베이스에서 복합키를 기술하려면 여러 컬럼을 명시해야 한다.

```
@Entity
@PrimaryKeyJoinColumn(name="ID")
@PrimaryKeyJoinColumn(name="REGION")
public class Item { ... }
```

하지만 위와 같이 사용할 수 없기 때문에 다음과 같이 컨테이너 어노테이션으로 묶어서 넣었다.

```
@Entity
@PrimaryKeyJoinColumns({
  @PrimaryKeyJoinColumn(name="ID")
  @PrimaryKeyJoinColumn(name="REGION")
})
public class Item { ... }
```

이와 같은 어노테이션은 상당히 보기 불편할 뿐더러 자바 8에서는 더 이상 필요 없다.  
어노테이션 사용자로서는 여기까지만 알면 된다. 프레임워크 제공자가 반복 어노테이션을 활성화한 경우 그냥 사용하면 된다.  
프레임워크 구현자 입장에서는 이야기가 간단하지 않다. 결국 AnnotatedElement 인터페이스는 T 타입 어노테이션을 (있으면) 얻어오는 다음 메서드를 포함한다.

```
public <T extends Annotation> T getAnnotation(Class<T> annotationClass)
```

타입이 같은 여러 어노테이션이 있으면 위 메서드가 무엇을 해야 할까? 첫번째 어노테이션만 리턴해야 할까? 이렇게 하면 레거시 코드와 관련해 모든 종류의 원치 않은 동작을 하게 될 것이다. 이 문제를 해결하게 위해 반복 가능 어노테이션의 고안자는 반드시 다음 작업을 해야 한다.
* 반복 가능 어노테이션에 @Repeatable 어느테이션을 붙인다.
* 컨테이너 어노테이션을 제공한다.

예를 들어, 간단한 유닛 테스팅 프레임워크인 경우 다음과 같이 사용하는 반복 가능한 @TestCase 어노테이션을 정의할 수 있다.

```
@TestCase(params="4", expected="24")
@TestCase(params="0", expected="1")
public static long factorial(int n) { ... }

// 다음은 이 어노테이션을 정의하는 방법
@Repeatable(TestCase.class)
@interface TestCase {
  String params();
  String expected();
}

@interface TestCases {
  TestCase[] value();
}
```

사용자가 @TestCase 어노테이션을 두번 이상 제공하면 해당 어노테이션들이 자동으로 @TestCases 어노테이션으로 포장된다.  
어노테이션 처리 코드에서 factorial 메서드를 나타내는 요소를 대상으로 element.getAnnotation(TestCase.class)를 호출하면 null이 리턴한다. 실제로는 해당 요소에 컨테이너 어노테이션인 TestCases가 붙기 때문이다.

자신만의 반복 가능 어노테이션을 위한 어노테이션 처리기를 구현할 때 getAnnotationsByType 메서드를 사용하면 더 간편하다.element.getAnnotationsByType(TestCase.class) 호출은 모든 TestCases 컨테이넌를 '검토'해서 TestCase 어노테이션 배열을 준다.

*위에서 설명한 내용은 리플렉션 API를 이용한 실행 시간 어노테이션 처리와 연관된다. 소스 수준 어노테이션을 처리하는 경우에는 javax.lang.model과 javax.annotation.processing API를 사용한다. 이들 API에서는 컨테이너 '검토'를 지원하지 않는다. 개별 어노테이션(한 번 제공한 경우)과 컨테이너 어노테이션(같은 어노테이션을 두 번 이상 제공한 경우) 모두 처리해야 한다.*

#### 타입 사용 어노테이션
자바8 이전에는 어노테이션이 선언에 적용되었다. 선언은 새로운 이름을 도입하는 코드의 일부를 의미한다.  
@Entity public class **Persion** { ... }  
@SuppressWarinings("unchecked") List<Person> **people** = query.getResultList();

자바8에서는 모든 타입 사용에 어노테이션을 붙일 수 있다.  
private @NonNull List<String> names = nwe ArrayList<>();
private List<@NonNull String> names;

타입 사용 어노테이션이 나타날 수 있는 곳
* 제니릭 타입 인자 : List<@NonNull String>.Comparator.<@NonNull String>reverseOrder()
* 배열의 모든 위치 : @NonNull String[][] words(words[i][j]가 null이 아님). String @NonNull [][] words(words가 null이 아님). String[] @NonNull [] words(words[i]가 null이 아님)
* 슈퍼클래스와 구현할 인터페이스 : class Image implements @Rectangular Shape
* 생성자 호출 : new @Path String("/usr/bin")
* 캐스트와 instanceof 검사 : (@Path String) input, if (input instanceof @Path String) (외부 도구에서만 사용할 목적인 어노테이션으로, 캐스트 또는 instanceof 검사의 동작에는 영향을 주지 않음)
* 예외 명세 : public Person read() throws @Localized IOException
*와이드카드와 타입 경계 : List<@ReadOnly ? extends Person>, List<? extends @ReadOnly> Person
* 메서드 및 생성자 레퍼런스 : @Immutable Person : getName

어노테이션을 붙일 수 없는 몇가지 타입 위치가 있다.
@NonNull String.class // 규칙에 어긋남 - 클래스 리터럴에 어노테이션을 붙일 수 없다.
import java.lang.@NonNull String; // 규칙에 어긋남 - import에 어노테이션을 붙일 수 없다.

어노테이션에 어노테이션을 붙이는 일도 불가능하다. 예를 들어 @NonNull String name에 @NonNull 어노테이션을 붙일 수 없다(별도의 어노테이션을 붙일 수는 있지만 name 선언에 적용)
확장 타입 검사의 잠재 능력 탐구에 관심이 있다면 http://types.cs.washington.edu/checker-framework/tutorial
#### 메서드 파라미터 리플렉션
이제 리플렉션을 통해 파라미터의 이름을 알 수 있다. 어노테이션과 관련한 상투적인 부분을 줄일 수 있다. 전형적인 JAX-RS 메서드를 고려해 보자

```
Person getEmployee(@PathParam("dept") Long dept, @QueryParam("id") Long id)
// 자바8의 새로운 클래스인 java.lang.reflect.Parameter를 이용하면 아래와 같은 코드가 가능
// 필요한 정보가 클래스 파일에 나타나도록 소스를 javac -parameters 와 같은 형태로 컴파일해야 한다.
Person getEmployee(@PathParam Long dept, @QueryParam Long id)
```

### 기타 작은 변경 사항
#### 널 검사
Objects 클래스는 스트림에 유용한 정적  Predicate 메서드인 isNull과 nonNull을 포함한다. 다음은 stream이 널을 포함하고 있는지 검사한다.

```
stream.anyMatch(Objects::isNull)
// 모든 널을 제거
stream.filter(Objects::nonNull)
```

#### 지연 메시지
java.util.Logger 클래스의 log, logp, severe, warining, info, config, fine, finer, finest 메서드는 이제 지연 생성되는 메시지를 지원한다.

```
logger.finest("x: " + x + ", y: " + y);   // 문지열이 포맷팅된다.
logger.finest(() -> "x: " + x + ", y: " + y); // FINEST 로깅 레벨에서만 평가된다.

this.directions = Objects.requireNonNull(directions, () -> "directions for " + this.goal + " must not be null");
```

#### 정규 표현식
자바7은 네임드 캡처링 그룹(Named Capturing Group)을 도입했다.  
(?<city>[\p{L} ]+),\s*(?<state>[A-Z]{2})  

자바8에서는 Matcher의 start, end 그리고 group 메서드에서 이름을 사용할 수 있다.

```
Matcher matcher = pattern.matcher(input);
if (matcher.matchers()) {
  String city = matcher.group("city");
  ...
}
```

Pattern 클래스는 CharSequence를 정규 표현식에 따라 분리하는 splitAsStream 메서드를 포함한다.

```
String contents = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
Stream<String> words = Pattern.compile("[\\P{L}]+").splitAsStream(contents);    // 모든 비문자 시퀀스는 단어 분리자다.
// asPredicate 메서드는 정규 표현식과 일치하는 문자열을 필터링하는데 사용할 수 있다.
Stream<String> acronyms = words.filter(Pattern.compile("[A-Z]{2,}").asPredicate());
```

#### 로케일
로케일은 정보를 언어, 날짜 형식 등과 관련한 지역 설정에 맞춰 사용하에게 표시하기 위해 알아야 하는 모든 것을 명시한다.  
로케일은 위치, 언어 그리고 언어변종으로 간단하게 구성되어 왔다. 오늘날 로케일은 최대 5개 구성요소로 구성된다.

1. 소문자 2~3개로 기술하는 언어 : 예를 들면, en(영어) 또는 de(독일어, 또는 독일어로 표기하면 Deutsch)가 있다.
2. 대문자로 시작하는 4글자로 기술하는 스크립트 : 예를 들면 Latn(라틴문자), Cyrl(키릴 문자) 또는 Hant(전통 중북 문자: 번체)가 있다. 스크립트가 유용한 이유는 세르비아어 같은 일부 언어는 라틴 또는 키릴 문자로 작성하고, 중국어를 읽는 사람들 주우 일부는 간체보다 번체를 선호하기 때문이다.
3. 대문자 2개 또는 3개로 기술하는 국가 : 예를 들면, US(미국) 또는 CH(스위스)가 있다.
4. 선택적인 언어 변종 : 언어 변종이 더는 공통 항목이 아니다. 예를 들어, 노르웨이어의 뉘노르스크(Nynorsk) 스펠링은 이제 언어 no의 변종 NY가 아닌, 다른 언어 코드인 nn으로 표현된다.
5. 선택적인 확장 : 확장은 달력(일본력 등), 숫자(타이 숫자) 등에 대한 지역 설정을 기술한다. 유니코드 표준은 이들 확장 중 일부를 명시하고 있다. 이러한 확장은 u- 그리고 달력(ca), 숫자(nu) 등을 다루는지 여부를 나타내는 두 글자 코드로 시작한다. 예를 들어, 확장 u-nu-thai는 타이 숫자 사용을 나타낸다. 다른 확장은 대부분 제각각이며, x-java처럼 x-으로 시작한다.

여전히 로케일을 `new Locale("en", "US")` 같은 기존 방식으로 생성할 수 있지만, 자바7 이후로는 단순히 `Locale.forLanguageTag("en-US")`를 호출할 수 있다.

언어 범위는 사용자가 원하는 로케일 특성을 기술하는 문자열로, \*를 와일드 카드로 사용한다. 예를 들어, 스위스에서 독일어로 말하는 사람은 로케일로 독일어를 가장 선호하고, 이어서 스위스를 선호할 것이다. 이는 문자열 **de**와 **\*-CH**로 지정한 두 Locale.LanguageRange 객체로 표현할 수 있다. 또한, Locale.LangugaeRange를 생성할 때 선택적으로 0 ~ 1 사이의 가중치를 지정할 수 있다.

filter 메서드는 주어진 가중치 적용 언어 범위 리스트와 로케일의 컬렉션에서 일치하는 로케일들을 일치 순도에 따라 내림차순으로 정렬한 리스트를 리턴한다.

```
List<Locale.LanguageRange> ranges = Stream.of("de", "*-CH").map(Locale.LanguageRange::new).collect(Collectors.toList());
// 주어진 문자열에 해당하는 Locale.LanguageRange 객체들을 담은 리스트
List<Locale> matches = Locale.filter(ranges, Arrays.asList(Locale.getAvalilableLocales()));
// 일치하는 로케일 : de, de-CH, de-AT, de-LU, de-DE, de-GR, fr-CH, it_CH

// 정적 lookup 메서드는 최적 로케일만 찾는다.
Locale bestMatch = Locale.lookup(ranges, locales);
```

#### JDBC
자바8dptj ㅓ윷rk 4.2 버전으로 업데이트되었다.  
java.sql 패키지의 Date, Time, Timestamp 클래스는 java.tile 패키지의 대응 클래스인 LocalDate, LocalTime, LocalDateTime으로부터 변환하는 메서드와 이들 대응 클래스로 변환하는 메서드를 제공한다.  
Statement 크래스는 로우 개수가 Integer.MAX_VALUE를 초과하는 업데이트를 실행하는 executeLargeUpdate 메서드를 제공한다.  
자바7의 일부인 JDBC 4.1은 Statement와 ResultSet의 getObject(column, type) 제네릭 메서드를 명시하고 있다. 예를 들어, `URL url = result.getObject("link", URL.clas)`는 DATALINK를 URL로 추출한다. 이제 이 메서드에 대응하는 setObject도 제공한다.

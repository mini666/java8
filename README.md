# Java8

## 1장 람다표현식
* 핵심 내용
  * 람다 표현식은 파라미터가 있는 코드 블럭이다.
  * 코드 블록을 나중에 실행하고자 할 때 람다 표현식을 사용한다.
  * 람다 표현식을 함수형 인터페이스로 변환할 수 있다.
  * 람다 표현식은 자신을 감싸고 있는 유효 범위에 속한 사실상 final 변수를 접근할 수 있다.
  * 메서드 레퍼런스와 생성자 레퍼런스는 각각 메서드와 생성자를 호출 없이 춤조한다.
  * 이제 인터페이스에 실제 구현을 제공하는 디폴트 메서드와 정적 메서드를 추가할 수 있다.
  * 여러 인터페이스의 디폴트 메서드들 사이의 충돌을 해결해야 한다.
  
### 람다 표현식 문법
```
class LengthComparator implements Comparator<String> {
  public int compare(String first, String second) {
    return Integer.compare(first.length(), second.length());
  }
}
Arrays.sort(strings, new LengthComparator());

// 기존에는 compare를 위한 클래스를 만들어 넘겨줘야 했으나 이제는 그럴 필요 없다.
Arrays.sort(strings, (String first, String second) -> Integer.compare(first.length(), second.length());

// 람다 표현식이 여러 줄일 경우 {} 사용
(String first, String second) -> {
  if (first.length() < second.length()) return -1;
  else if (first.length() > second.length()) return 1;
  else return 0;
}

// 람다 표현식이 파라미터를 받지 않으면 빈 괄호 사용
() -> { for (int i = 0; i < 1000; i++) doWork(); }

// 파라미터 타입을 추정할 수 있는 경우 타입을 생략
Comparator<String> comp = (first, second) -> Integer.compare(first.length(), second.length());

// 메서드에서 추정되는 타입 한개를 파라미터로 받으면 괄호를 생략할 수 있다.
EventHandler<ActionEvent> listener = event -> System.out.println("Thanks for clicking!");
```

*메서드 파라미터와 마찬가지 방식으로 람다 파라미터에 어노테이션이나 final 수정자를 붙일 수 있다. `(final String name) -> ... (@NonNull String name) -> ...`*

람다 표현식의 결과 타입은 지정하지 않는다. 결과 타입은 항상 문맥으로부터 추정된다.

### 함수형 인터페이스
자바에는 Runnable, Comparator 등 코드 블럭을 캡슐화하는 수많은 기존 인터페이스가 있다. 람다는 이러한 기존 인터페이스와 호환된다.  
단일 추상 메서드를 갖춘 인터페이스의 객체를 기대할 때 람다 표현식을 사용할 수 있다. 그리고 이러한 인터페이스를 함수형 인터페이스라고 한다.

*Object 타입 변수에도 람다 표현식을 대입할 수 없다. Object는 함수형 인터페이스가 아니기때문이다.*

자바 API는 java.util.function 패키지에 다수의 아주 범용적인 함수형 인터페이스를 정의하고 있다. 이러한 인터페이스 중 하나인 BiFunction<T, U, R>은 파라미터 타입이 T와 U고, 리턴 타입이 R인 함수를 나타낸다. 문자열 비교 람다를 BiFunction 타입 변수에 저장할 수 있다. `BiFunction<String, String, Integer> com = (first, second) -> Integer.compare(first.length(), second.length());` 

*함수형 인터페이스에 @FunctionalInterface 어노테이션을 붙일 수 있다. 이렇게 하면 두가지 장점이 있다. 첫째, 컴파일러에서 어노테이션이 붙은 엔터티가 단일 추상 메서드를 갖춘 인터페이스인지 검사한다. 둘째, javadoc 페이지에서 해당 인터페이스가 함수형 인터페이스임을 알리는 문장을 포함한다. 어노테이션을 반드시 사용해야 하는 것은 아니다. 정의에 따르면, 단일 추상 메서드를 갖춘 모든 인터페이스가 곧 함수형 인터페이스다. 그럼에도 @FunctionalInterface 어노테이션을 사용하는 것은 좋은 생각이다.*

마지막으로, 람다 표현식이 함수형 인터페이스의 인스턴스로 변환될 때 검사 예외가 문제가 된다는 점을 유의하기 바란다. 람다 표현식의 몸체에서 검사 예외를 던질 수 있는 경우, 해당 예외가 대상 인터페이스의 추상 메서드에 선언되어 있어야 한다.

```
Runnable sleeper = () -> { System.out.println("Zzz"); Thread.sleep(1000); };  // 오류: Thread.sleep은 검사 예외인 InterruptedException을 던진다.
```

Runnable.run 메서드는 예외를 던질 수 없기 때문에 람다 표현식의 몸체에서 예외를 잡던지 return null을 추가하여 Callable<Void>로 바꿔야 한다.

### 메서드 레퍼런스
다른 코드에 전달하려고 하는 액션을 수행하는 메서드가 이미 존재할 경우

```
button.setOnAction(event -> System.out.println(event));
// 메서드 레퍼런스 사용
button.setOnAction(System.out::println);

// 대소문자를 가리지 않고 문자열을 정렬하고 싶은 경우
Arrays.sort(strings, String::compareToIgnoreCase) 
```

::연산자는 객체 또는 클래스와 메서드 이름을 구분하려 세가지 주요 경우가 있다.
* object::instanceMethod
* Class::staticMethod
* Class::instanceMethod

처음 두 경우에는 메서드의 파리미터를 제공하는 람다 표현식에 해당한다. `System.out::println`은 `System.out.println(x)`와 같다. 마찬가지로 `Math::pow`는 `(x, y) -> Math.pow(x, y)`에 해당한다.  
세번째 경우에서는 첫번째 파라미터가 해당 메서드의 대상이 된다. 예를 들어 `String::compareToIgnoreCase`는 `(x, y) -> x.compareToIgnoreCase(y)`와 같다.

*이름이 같은 여러 메서드가 오버로드되어 있을 때는 컴파일러가 의도한 문맥을 찾으려고 할 것이다. 예를 들어 Math.max 메서드는 정수와 부동소수점 수를 받는 버전이 있다. 이중 어느 버전이 선택되는지는 Match::max가 변환되는 대상 함수형 인터페이스의 메서드 파리미터에 의존한다. 람다 표현식과 마찬가지로, 메서드 레퍼런스는 독립적으로 존재하지 않고 항상 함수형 인터페이스의 인스턴스로 변환된다.*

메서드 레퍼런스에서 this 파라미터를 캡처할 수 있다. `this::equals`는 `x -> this.equals(x)`와 같다. super도 사용할 수 있다.

```
class Greeter {
  public void greet() {
    System.out.println("Hello, world!");
  }
}

class ConcurrentGreeter extends Greeter {
  public void greet() {
    Thread t = new Thread(super::greet);
    t.start();
  }
}
```

*이너 클래스에서는 바깥쪽 클래스의 this 레퍼런스를 EnclosingClass.this::method 또는 EnclosingClass.super::method로 캡처할 수 있다.*

### 생성자 레퍼런스
생성자 레퍼런스는 메서드의 이름이 new라는 점을 제회하면 메서드 레퍼런스와 유사. 실제 가리키는 생성자는 문맥에 따라 다르다.

```
List<String> labels = ...;
Stream<Button> stream = labels.stream().map(Button::new);
List<Button> buttons = stream.collect(Collections.toList());
```
  
배열 타입으로도 생성자 레퍼런스를 만들 수 있다. `int[]::new`는 `x -> new int[x]`와 같다.  
배열 생성자 레퍼런스는 자바의 한계를 극복하는데 유용하다. 자바에서는 제네릭 타입 T의 배열을 생성할 수 없다. 표현식 `new T[n]`은 `new Object[n]`으로 소거되기 때문에 오류다.

```
Object[] buttons = stream.toArray();
Button[] buttons = stream.toArray(Button[]:new);
```
  
### 변수 유효 범위
람다 표현식에서 해당 표현식을 감싸고 있는 메서드나 클래스에 있는 변수에 접근할 때.

```
public static void repeatMessage(String text, int count) {
  Runnable r = () -> {
    for (int i = 0; i < count; i++) {
      System.out.println(text);
      Thread.yield();
    }
  };
  new Thread(r).start();
}

repeatMessage("Hello", 1000);
```
람다 표현식은 세가지로 구성
* 코드 블럭
* 파라미터
* 자유 변수(파라미터도 아니고 코드 내부에도 정의도지 않는 변수)의 값

앞의 예제에서 람다 표현식은 자유 변수 두개(text, count)를 포함. 람다 표현식을 나타내는 자료 구조는 이들 변수의 값을 저장해야 한다. 이 경우 람다 표현식이 이들 값을 캡처했다고 말한다.  
람다 표현식에서는 값이 변하지 않는 변수만 참조할 수 있다.

```
public static void repeatMessage(String text, int count) {
  Runnable r = () -> {
    while (count > 0) {
      count--;      // 오류 : 캡처한 변수는 변경할 수 없다.
      System.out.println(text);
      Thread.yield()
    }
  };
  new Thread.start();
}

// 람다 표현식에서 변수를 변경하는 작업은 쓰레드에 안전하지 않다.
int matches = 0;
for (Path p : files)
  new Thread(() -> { if (p가 어떤 프로퍼티를 포함하면) matches++; }).start();
// matches++가 원자적이지 않기 때문에 결과를 예측할 수 없다.
```

*이너 클래스 역시 자신을 감싸고 있는 유효 범위에 있는 값들응 캡처랗 수 있다. 자바8 이전에는 이너 클래스가 final 지역변수만 접근할 수 있었다. 지금은 이 규칙이 람다 표현식과 일치하도록 완화되었다. 이너 클래스는 사실상 final인 모든 지역 변수(즉, 값이 변하지 않는 모든 지역 변수)를 접근할 수 있다.*

컴파일러가 모든 동시 접근 오류를 잡아낼 것으로 기대하지 말자. 변경 금지는 오직 지역 변수에만 해당한다. 만일 matches가 람다를 감싸고 있는 클래스의 인스턴스 변수 또는 정적 변수라면 오류가 보고되지 않는다.  
람다 표현식에서 this 키워드를 사용하면, 결국 해당 람다를 생성하는 메서드의 this 파라미터를 참조하는 결과가된다.

```
public class Application() {
  public void doWork() {
    Runnable runner = () -> { ...; System.out.println(this.toString(); ... };
    ...
  }
}
```

여기서 this는 Runnable이 아닌 Application이다.

### 디폴트 메서드
```
for(int i = 0; i < list.size(); i++)
  System.out.println(list.get(i));
  
list.forEach(System.out::println);
```
forEach는 Collections의 슈퍼인터페이스인 Iterable의 디폴트 메서드로 Collection을 구현한 기존의 코드에 영향을 주지 않는다.

```
interface Person {
  long getId();
  default String getName() { return "John Q. Public"; }
}
```

Person을 구현하는 클래스는 getId는 반드시 구현해야 하고 getNames은 선택 사항이다.  
디롶트 메서드는 인터페이스와 해당 인터페이스의 대부분 혹은 모든 메서드를 구현한 추상 클래스를 제공하는 고전적인 패턴(예를 들면, Collection/AbstractCollection 또는 WindowListener/WindowAdapter)의 종말을 선고했다.

똑같은 메서드가 한 인터페이스의 디폴트 메서드로 정의되어 있고, 슈퍼크래스나 다른 인터ㅔㅍ이스의 메서드로도 정의되어 있는 경우 규칙.
* 슈퍼클래스가 우선한다. 슈퍼클래스에서 구체적인 메서드를 제공하는 경우 이와 이름 및 파라미터 타입이 같은 디폴트 메서드는 단순히 무시된다.
* 인터페이스들이 충돌한다. 어떤 슈퍼인터페이스에서 디폴트 메서드를 제공하고 또 다른 인터페이스에서(디폴트 메서드든 아니든) 이름 및 파라미터 타입이 같은 메서드를 제공하는 경우에는 해당 메서드를 오버라이드해서 충돌을 해결해야 한다.

### 인터페이스의 정적 메서드
자바8부터는 인터페이스에 정적 메서드를 추가할 수 있다. 인터페이스의 정적 메서드를 금지해야 하는 기술적인 이유는 없었다. 단지 추상 명세라는 인터페이스의 정신에 어긋나는 것으로 보였을 뿐이다.  
지금까지는 일반적으로 인터페이스와 동반하는 클래스에 정적 메서드를 두었다. 자바 표준 라이브러리에서 Collection/Collections 또는 Path/Paths 같은 인터페이스와 유틸리티 클래스 쌍을 찾아볼 수 있다.  
Paths 클래스의 `Paths.get("jdk1.8.0", "jre", "bin")` 과 같은 메서드를 Path에 추가할 수 있고, Collections의 `public static void shuffle(List<?> list)`도 List 인터페이스의 인스턴스 메서드(`public default void shuffle()`)로 옮겨 올 수 있다.

팩토리 메서드인 경우 메서드를 호출할 대상 객체가 없으므로 동작하지 않는다. 바로 이 부분이 정적 인터페이스 메서드가 등장할 곳이다. 예를 들어 다음은 List 인터페이스의 정적 메서드가 될 수 있다.

```
// o 인스턴스 n개로 구성된 리스트를 생성한다.
public static <T> List<T> nCopies(int n, T o)
```

이 경우 Collections.nCopies(10, "Fred") 대신 List.nCopies(10, "Fred")를 호출할 수 있고 코드를 읽는 사람은 결과가 List 임을 분명히 알 수 있다.

자바8에서는 상당히 많은 인터페이스에 정적 메서드를 추가했다. 예를 들어, Comparator 인터페이스는 '키 추출' 함수를 받아서 추출된 키들을 비교하는 비교자를 돌려주는 아주 유용한 정적 comparing 메서드를 제공한다. Person 객체를 이름으로 비교하려면 `Comparator.comparing(Person::name)`을 사용하면 된다.

이장에서는 람다 표현식 `(first, second) -> Integer.compare(first.length(), seocnd.length()`를 이요해서 문자열을 길이로 비교했지만 정적comparing 메서드를 이용하면 더 낫다. `Comparator.comparing(String::length)`를 사용하면 된다.

## 2장 스트림 API
* 핵심 내용
  * 반복자는 특정 순회 전략을 내포하므로 효율적인 동시 실행을 방해한다.
  * 컬렉션, 배열, 발생기, 반복자로부터 스트림을 생설할 수 있다.
  * 요소를 선택하는데 filter를 사용하고 요소를 변환하는데 map을 사용한다.
  * 스트림을 변환하는 다른 연산으로는 limit, distinct, sorted가 있다.
  * 스트림에서 결과를 얻으려면 count, max, min, findFirst 또는 findAny 같은 리덕션 연산자를 사용한다. 이들 메서드 중 몇몇은 Optional 값을 리턴한다.
  * Optional 타입은 null 값을 다루는 안전한 대안을 목적으로 만들어졌다. Optional 타입을 안전하게 사용하려면 ifPresent와 orElse 메서드를 이용한다.
  * 스트림 결과들을 컬렉션, 배열, 문자열 또는 맵으로 모을 수 있다.
  * Collections 클래스의 groupingBy와 partitioningBy 메서드는 스트림의 내용을 그룹으로 분할하고 각 그룹의 결과를 얻을 수 있게 해 준다.
  * 기본 타입인 int, long, double용으로 특화된 스트림이 있다.
  * 병렬 스트림을 이용할 때는 부가 작용(Side Effect)을 반드시 피해야 하고, 순서 제약을 포기하는 방안도 고려한다.
  * 스트림 라이브러리를 사용하려면 몇 가지 함수형 인터페이스와 친숙해져야 한다.
  
### 반복에서 스트림 연산으로
```
String contents = new String(Files.readAllBytes(Paths.get("alice.txt")), StandardCharsets.UTF_8);
List<String> words = Arrays.asList(contents.split("[\\P{L}]+"));

int count = 0;
for (String w : words) {
  if (w.length > 12) count++;
}

long count = words.stream().filter(w -> w.length() > 12).count();
long count = words.parallelStream().filter(w -> w.length() > 12).count();   // 병렬 처리
```

* 스트림과 컬렉션의 차이
  * 스트림은 요소를 보관하지 않는다. 요소들은 하부의 컬렉션에 보관되거나 필요할 때 생성된다.
  * 스트림 연산은 원본을 변경하지 않는다. 대신 결과를 담은 새로운 스트림을 반환한다.
  * 스트림 연산은 가능하면 지연 처리된다.지연 처리란 결과가 필요하기 전에는 실행되지 않음을 의미한다. 예를 들어, 긴 단어를 모두 세는 대신 처음 5개 긴 단어를 요청하면, filter 메서드는 5번째 일치 후 필터링을 중단한다. 결과적으로 심지어 무한 스트림도 만들 수 있다.

스트림을 이용해 작업할 때 연산들의 파이프라인을 세단계로 설정한다.
1. 스트림을 생성한다.
2. 초기 스트림을 다른 스트림으로 변환하는 중간 연산들을 하나 이상의 단계로 지정한다.
3. 결과를 산출하기 위해 최종 연산을 적용한다. 이 연산은 앞선 지연 연산들의 실행을 강제한다. 이후로는 해당 스트림을 더는 사용할 수 없다.

*스트림 연산들은 요소를 대상으로 실행될 때 스트림에서 호출된 순서로 실행되지 않는다. 앞선 예세에서 count가 호출되기 전에는 아무일도 일어나지 않는다. count 메서드가 첫번째 요소를 요청하면, filter 메서드가 길이 > 12인 요소를 찾을 때까지 요소들을 요청하기 시작한다.*

### 스트림 생성
배열은 Stream.of 메서드를 사용한다.

```
Stream<String> words = Stream.of(contents.split("[\\P{L{]+"));

// of 메서드는 가변인자를 받는다.
Stream<String> song = Stream.of("gently", "down", "the", "stream");

// 배열의 일부에서 스트림 생성
Arrays.stream(array, from, to);

// 요소가 없는 스트림을 생성
Stream<String> silence = Stream.empty();    // Stream.<String>empty(); 와 같다.

// Stream 인터페이스는 무한 스트림을 만드는 generate와 iterate 제공
// generate는 인자없는 함수(Supplier<T>)를 받는다.
Stream<String> echos = Stream.generate(() -> "Echo");
// 난수 스트림
Stream<Double> randoms = Stream.generate(Math::random);

// 0 1 2 3 ... 같은 무한 수열을 만들려면 iterate 사용. seed 값과 함수(UnaryOperator<T>)를 받고, 해당 함수를 이전 결과에 반복적으로 적용한다.
Stream<BigInteger> integers = Stream.iterate(BigInteger.ZERO, n -> n.add(BigInteger.ONE));
```

*자바8은 스트림을 돌려주는 다수의 메서드를 추가했다. 예를 들면 Pattern 클래스는 이제 정규 표현식을 이용해 CharSequence를 분리하는 splitAsStream 메서드를 포함한다. 다음 문장을 사용해 문자열을 단어들로 분리할 수 있다. `Stream<String> words = Pattern.compile("[\\P{L}]+").splitAsStream(contents);` 정적 Files.lines 메서드는 파일에 있는 모든 행의 Stream을 리턴한다. Stream 인터페이스는 AutoCloseable을 슈퍼인터페이스로 둔다. 따라서 스트림에 close 메서드를 호출할 때 하부 파일 또한 닫힌다.* 

### filter, map, flatMap 메서드
스트림 변환은 한 스트림에서 데이터를 읽고 변환된 데이터를 다른 스트림에 넣는다.

```
List<String> wordList = ...;
Stream<String> words = wordList.stream();
Stream<String> longWords = words.filter(w -> w.length() > 12);
```

필터의 인자는 Predicate<T>이다.  
스트림에 있는 값들을 특정 방식으로 변환하고 싶을때는 map을 사용한다. `Stream<String> lowerCaseWords = words.map(String::toLowerCase);` `Stream<Character> firstChars = words.map(s -> s.charAt(0));`  
값들의 스트림을 리턴하는 함수가 있다고 하자.

```
public static Stream<Character> characterStream(String s) {
  List<Character> result = new ArrayList<>();
  for (char c : s.toCharArray()) result.add(c);
  return result.stream();
}
```

characterStream("boat")는 스트림 ['b', 'o', 'a', 't']를 리턴한다. 이 메서드를 문자열의 스트림에 맵핑한다고 하자. `Stream<Stream<Character>> result = words.map(w -> characterStream(w));` 결과로 [...['y', 'o', 'u', 'r'], ['b', 'o', 'a', 't'], ...] 처럼 스트림들로 구성된 스트림을 얻는다. 이 스트림을 문자들의 스트림 [... 'y', 'o', 'u', 'r', 'b', 'o', 'a', 't', ...]로 펼쳐내려면 map 대신 flatMap을 사용한다. `Stream<Character> letters = words.flatMap(w ->characterStream(w))`  

*스트림 외의 클래스에서도 `flatMap` 메서드를 접할 것이다. `flatMap`은 컴퓨터 과학에서 일반적인 개념이다. 제네릭 타입 G(예를 들면 Stream), 타입 T를 G<U>로 변환하는 함수 f 그리고 타입 U를 G<V>로 변환하는 함수 g가 있다고 하자. 그러면 flatMap을 사용해서 이 함수들을 합성 할 수 있다(즉, 먼저 를 적용한 후 g를 적용한다). 이는 모나드 이른에서 핵심 개념이다.*

### 서브스트림 추출과 스트림 결과
`stream.limit(n)` 호출은 n개 요소 이후(n보다 짧은 경우는 모두) 끝나는 새로운 스트림을 리턴한다.  
다음은 난수 100개를 포함하는 스트림을 리턴한다.

```
Stream<Double> randoms = Streams.generate(Math::random).limit(100);
// skip은 버린다.
Stream<String> words = Stream.of(contents.split("[\\P{L}]+")).skip(1);
// concat은 두 스트림을 연결한다.
Stream<Character> combined = Stream.concat(characterStream("Hello"), charaterStream("World"));
```

*`peek` 메서드는 원본과 동일한 요소들을 포함하는 다른 스트림을 돌려주짐나, 전달받은 함수를 요소 추출 시마다 호출한다. 따라서 디버깅 수행때 유용하다.*

```
Object[] powers = Stream.iterate(1.0, p -> p * 2).peek(e -> System.out.println("Fetching " + e)).limit(20).toArray();
```
*이 방법으로 `iterte` 메서드가 리턴하는 무한 스트림이 지연 처리됨을 확인할 수 있다.*

### 상태 유지 변환
지금까지 살펴본 스트림 변환은 무상태 변환이다. 다시 말해 필터링 또는 맵핑도니 스트림에서 요소를 추출할때 결과가 이전 요소에 의존하지 않는다. 몇가지 상태 유지 변환도 존재한다. 예를들어 distinct 메서드는 중복을 제거하는 점을 제외하면 원본 스트림으로부터 요소들을 같은 순서로 돌려주는 스트림을 리턴한다. 이 경우 스트림은 이미 만난 요소들을 확실히 기억해야 한다.

```
Stream<String> uniqueWords =
  = Stream.of("merrily", "merrily", "merirly", "gently").distinct();
```
  
`sorted` 메서드는 요소들을 돌려주기 전에 반드시 전체 스트림을 보고 정렬해야 한다. 무한 스트림은 정렬할 수 없다. `sorted` 메서드는 여러 버전이 있는데 한 버번은 Comparable 요소들의 스트림을 대상으로 작업하고 또 다른 버전은 Comparator를 받은다.

```
Stream<String> longestFirst = words.sorted(Comparator.comparing(String::Length).reversed()));
```

`sorted` 메서드는 정렬 과정이 스트림 파이르파인의 일부일때 유용하다.

*`Collections.sort` 메서드는 컬렉션을 직접 정렬한다. 한편 `Stream.sorted` 메서드는 새롭게 정렬된 스트림을 리턴한다.*

### 단순 리덕션
단순 리덕션으로 `count, min, max` 가 있는데 이들 메서드는 결과를 감싸고 있거나 결과가 없음을 나타내는 Optional<T> 값을 리턴한다.

```
Optional<String> largest = words.max(String::compareToIgnoreCase);
if (largest.isPresent()) {
  System.out.println("largest: " + largest.get());
}
```

`firstFirst` 메서드는 비어 있지 않은 컬렉션에서 첫번째 값을 리턴한다. 종종 이 메서드를 filter 메서드와 결합하면 유용하다. 존재한다면 글자 Q로 시작하는 첫번째 단어를 찾는다.

```
Optional<String> startWithQ =
  words.filter(s -> s.startsWith("Q")).findFirst());
```

첫번째 값은 물론 어떤 일치 결과든 괜찮다면 `findAny` 메서드를 사용한다. 이 메서드는 스트림을 병렬화할때 유용한데, 이 경우 조사 대상 세그먼트들에서 처음 일치가 발견되면 계산을 완료하기 때문이다.

```
Optional<String> startWithQ =
  words.parallel().filter(s -> s.starstWith("Q")).findAny();
```

단순히 일치하는 요소가 있는지 알고 싶은 경우에는 `anyMatch`를 사용한다. 이 메서드는 Preficate 인자를 받으므로 filter를 사용할 필요가 없다.

```
boolean aWordStartWithQ =
  words.parallel().anyMatch(s -> s.starstWith("Q"));
```

모든 요소가 Predicate와 일치하거나 아무것도 일치하지 않을때 true를 리턴하는 `allMatch`와 `noneMatch` 메서드도 있다. 이들 메서드는 항상 전체 스트림을 검사하지만, 여전히 병렬 실행을 통해 이점을 얻을 수 있다.

### 옵션 타입
Optional<T> 객체는 T 타입 객체 또는 객체가 없는 경우의 래퍼다. Optional<T>는 객체 또는 null을 가리키는 T 타입 레퍼런스보다 안전한 대안으로 만들어졌다. 하지만 올바르게 사용할 경우에만 더 안전하다.  
`get` 메서드는 감싸고 있는 요소가 존재할 대는 요소를 얻고, 그렇제 않으면 `NoSuchElementException`을 던진다.

```
Optional<T> optionalValue = ...;
optionalValue.get().someMethod();

// 위 예제는 다음 예제보다 안전할 것이 없다.
T value = ...
value.someMethod();
```

`isPresent` 메서드는 `Optional<T>` 객체가 값을 포함하는지 알려준다.

```
if (optionalValue.isPresent()) optionalValue.get().someMethod();
// 위 예제가 다음보다 쉽지는 않다.
if (value != null) value.someMethod();
```

이제 Optional 값을 이용해 작업하는 방법을 알아보자.

#### 옵션 값 다루기
`Optional`을 효과적으로 사용하는 핵심은 **올바른 값**을 소비하거나 **대체 값을 생산**하는 메서드를 사용하는 것이다.  
`ifPresent` 메서드는 함수를 받는 두번째 형태가 있다. 옵션 값이 존재하면 해당 함수로 전달되며 그렇지 않으면 아무 일도 일어나지 않는다. if문을 사용하는 대신 다음과 같이 호출할 수 있다. `optionalValue.isPresent(v -> v 처리);` 예를 들어, 값이 존재하는 경우 집합에 해당 값을 추가하려고 할때는 다음과 같이 호출한다. `optionalValue.ifPresent(v -> results.add(v));` 또는 `optionalValue.ifPresent(results:add);`  
함수를 받는 `ifPresent` 버전을 호출할 때는 값이 리턴되지 않는다. 따럿 결과를 처리하고 싶은 경우에는 대신 map을 사용한다. `Optional<Boolean> added = optionalValue.map(results::add);` 이제 `added`는 세가지 값(true/false/빈 Optional 중 하나를 가진다.

옵션 값이 없을때 대체 값 사용.

```
String result = optionalString.ofElse("");
// 디폴트를 계산하는 코드를 호출
String result = optionalString.orElseGet(() -> System.getProperty("user.dir"));
// 값이 없는 경우 예외를 던질때
String result = optionalString.ofElseThrow(NoSuchElementException::new);
```

#### 옵션 값 생성하기
`Optional.of(result)` 또는 `Optional.empty()`를 이용해 Optional 객체를 생성한다.

```
public static Optional<Double> inverse(Double x) {
  return x == 0 ? Optional.empty() : Optional.of(1 / x);
}
```

`ofNullable` 메서드는 null 값 사용을 옵션 값으로 이어주는 용도로 만들어졌다. `Optional.ofNullable(obj)`는 obj가 null이 아니면 `Optional.of(obj)`를 null이면 `Optional.empty()`를 리턴한다.

#### flatMap을 이용해 옵션 값 함수 합성하기
`Optional<T>`를 리턴하는 메서드 f가 있고, 대상 타입 T는` Optional<U>`를 리턴하는 메서드 g를 포함하고 있다고 할 때, 일반 메서드라면 s.f().g()를 호출하는 방법으로 이 메서드들을 합성할 수 잇다. 하지만 이 경우에는 s.f()에서 T가 아닌 Options<T> 타입을 리턴하므로 이러한 합성이 동작하지 않는다. 대신 다음과 같이 호출한다. `Optional<U> result = s.f().flatMap(T::g);` 이렇게 하면 s.f()가 존재하면 g가 적용되고, 그렇지 않으면 비어 있는 Optional<U>가 리턴된다. 앞 예제의 inverse 메서드에 안전한 루트 메서드도 있다고 하자.

```
public static Optional<Doube> squareRoot(Double x) {
  return x < 0 ? Optional.empty() : Optional.of(Math.sqrt(x));
}

// 다음과 같이 역수의 루트를 계산할 수 있따.
Optional<Double> result = inverse(x).flatMap(Test::squareRoot);
// 원한다면 다음과 같이 할 수도 있다.
Optional<Double> result = Optional.of(-4.0).flatMap(Test::inverse).flatMap(Test::squareRoot); // inverse나 squareRot 메서드 중 하나가 Optional.empty()를 리턴하면 결과는 비어있게 된다.
```

### 리덕션 연산
합계를 계산하거나 스트림의 요소들을 다른 방버으로 결합하고 싶은 경우, `reduce` 메서드들 중 하나를 사용할 수 있다. 가장 단순한 형태는 이항 함수를 받아서 처음 두 요소부터 시작하여 계속해서 해당함수를 적용한다. 

```
Stream<Integer> values = ...;
Optional<Integer> sum = values.reduce((x, y) -> x + y); 
// 또는 Optioanl<Integer> sum = values.reduce(Integer::sum);
```

연산은 결합 법칙을 지원해야 하고 병렬 스트림을 통한 효율적인 리덕션이 가능하다.  
*`e op x = x'* 같은 항등값 *e*가 있을 때는 해당 요소를 계산의 시작으로 사용할 수 있다.

```
Stream<Integer> values = ...;
Integer sum = values.redue(0, (x, y) -> x + y);
```

스트림이 비어 있으면 항등값을 리턴하므로 더는 Optional 클래스를 다룰 필요가 없다.

문자열의 길이를 누적시키고자 할 때에 스트림 요소들은 String이고, 누적 결과는 정수다. 이때 전달하는 누적 함수는 `(total, word) -> total + word.length()` 다. 하지만 병렬화하면 이와 같은 계산이 여러개 존재하므로 각각의 결과를 결합해야 한다. 따라서 각 부분의 결과를 결합하는데 사용할 두번째 함수를 전달한다. 완성된 형태는 다음과 같다.

```
int result = words.reduce(0, 
  (total, word) -> total + word.length(), 
  (total1, total2) -> total1 + total2);
```

*실전에서는 reduce 메서드를 많이 사용하지 않을 것이다. 보통은 숫자 스트림에 맵핑한 후 스트림의 합계, 최대값, 최소값 계산 메서드를 사용하는 것이 더 쉽다. 앞의 예제에서는 `words.mapToInt(String::length).sum()`을 호출하는 방법으로 해결할 수 있고 이렇게 하면 박싱이 일어나지 않기 때문에 더 단순하면서도 더 효율적이다.*
c### 결과 모으기
스트림 작업을 마칠 때 보통은 값으로 리듀스하기보다는 결과를 살펴보길 원하기 마련이다. 이때 요소들을 방문하는데 사용할 수 있는 잔통적인 반복자를 돌려주는 iterator 메서드를 호출할 수 있고 다른 방법으로 toArray를 호출해서 스트림 요소들의 배열을 얻을 수 있다. 실행 시간에 제너릭 배열을 생성할 수 없기 때문에 `Stream.toArray()`는 Object[]를 리턴한다. 올바른 타입의 배열을 원하는 경우 배열 생성자를 전달한다. `String[] result = words.toArray(String::new);`  
이제 HashSet에 결과를 모드려 한다고 하자. HashSet 객체는 스레드에 안전하지 않기 때문에 컬렉션을 병렬화하면 요소들을 단일 HashSet에 넣을 수 없다. 이와 같은 이유로 reduce를 사용할 수 없다. 각 부분은 자체적인 빈 해시 집합으로 작업을 시작해야 하는데 reduce는 항등값 하나만 전달하도록 허용한다. 따라서 reduce 대신 collect를 사용해야 한다. collect느 ㄴ세가지 인자를 받는다.
1. 공급자(supplier) : 대상 객체의 새로운 인스턴스를 만든다.
2. 누산자(accumulator) : 요소를 대상에 추가한다.
3. 결합자(comnbiner) : 두 객체를 하나로 병합한다.
*대상 객체가 컬렉션일 필요는 없다. StringBuilder나 카운트와 합게를 관리하는 객체라면 대상이 될 수 있다.*

다음은 해시 집합을 대상으로 collect 메서드가 동작하는 방법을 보여준다.

```
HashSet<String> result = stream.collect(HashSet::new, HashSet::add, HashSet::addAll);
```

실전에서는 이들 세 함수를 제공하는 편리한 Collector 인터페이스와 공통 컬렉토용 팩토리 메서드를 제공하는 Collectors 클래스가 있으므로 이와 같이 일일이 지정할 필요가 없다. 스트림을 리스트나 집합으로 모으려면 단순히 다음과 같이 호출할 수 있다.

```
List<String> result = stream.collect(Collectors.toList());
Set<String> result = stream.collect(Collectors.toSet());

// 집합의 종류를 제어하고자 할 경우
TreeSet<String> result = stream.collect(Collectors.toCollection(TreeSet::new));

// 스트림에 있는 모든 문자열을 서로 연결해서 모으려고 할때
String result = stream.collect(Collectors.joining());
// 요소간에 구분자가 필요하다면
Stream result = stream.collect(Collectors.joining(", "));
// 스트림이 문자열 외의 객체를 포함하는 경우 먼저 해당 객체들을 문자열로 변환
String result = stream.map(Object::toString).collect(Collectors.joining(", "));
```

스트림 결과를 합계, 평균, 최대값, 최소값으로 리듀스하려는 경우 summarizing(Int|Long|Double) 메서드 중 하나를 사용한다. 이들 메서드는 스트림 객체를 숫자로 맵핑하는 함수를 받고 합계, 평균, 최대값, 최소값을 얻는 메서드를 제공하는 (Int|Long|Double)SummaryStatistics 타입 결과를 돌려준다.

```
IntSummaryStatistics summary = words.collect(Collectors.summarizingInt(String::length));
double averageWordLength = summary.getAverage();
double maxWordLength = summary.getMax();
```

*단순히 값들을 출력하거나 데이터베이스에 저장하고 싶을때는 forEach 메서드 사용. `stream.forEach(System.out::println)` 병렬 스트림에서는 요소들을 임의 순서로 순화할 수 있다. 스트림 순서로 실행하고 싶으면 대신 forEachOrdered 메서드를 호출한다. 물론 이 경우 병렬성이 주는 대부분 또는 모든 이점을 포기해야 한다. forEach, forEachOrdered 메서드는 최종 연산이다. 이들 메서드를 호출한 후에는 스트림을 사용할 수 없다. 스트림을 계속 사용하고 싶으면 대신 peek를 사용해야한다.*

### 맵으로 모으기
Stream<Person>의 요소들을 맵으로 모아서 추후 ID로 사람을 조회할 수 있게 하려 한다고 할때 Collectors.toMap 메서드는 각각 맵의 키와 값을 생산하는 두 함수 인자를 받는다.

```
Map<Integer, String> idToName = people.collect(Collectors.toMap(Person::getId, Person::getName));
// 값이 실제 요소여야 하는 경우
Map<Integer, Person> idToPerson = people.collect(Collectors.toMap(Person:getId, Function.identity());
```

키가 같은 요소가 두개 이상이면 커렉터는 IllegalStateException을 던진다. 이 동작은 기존 값과 새 값을 받아서 키에 해당하는 값을 결정하는 세번째 함수 인자를 제공하는 방법으로 재정의할 수 있다. 여기서 세번째 인자로 제공하는 함수는 기존 값, 새 값 또는 두 값의 조합을 리턴할 수 있다.  
여기서는 사용 가능한 로케일에 있는 각 언어를 포함하는 맵을 생성한다. 이 맵에서 키는 디폴트 로케일에서 언어 이름, 값은 지역화된 이름이다.

```
Stream<Locale> locales = Stream.of(Locale.getAvaliableLocales());
Map<String, String> languageNames = locales.collect(
  Collectors.toMap(
    l -> l.getDisplayLanguage(),
    l -> l.getDisplayLanguage(l),
    (existingValue, newValue) -> existingValue));   // 무조건 첫번째만 유지
```

특정 국가에서 사용하는 모든 언어를 알고 싶을때 Map<String, Set<String>>이 필요한다. 예를 들면, "Switzerland"에 해당하는 집합은 [French, German, Italian]이다. 주어진 국가에서 새로운 언어를 발견할 때마다 기조 ㄴ집합과 새 집합의 합집합을 만든다.

```
Map<String, Set<String>> countryLanguageSets = locales.collect(
  Collectors.toMap(
    l -> l.getDisplayCountry(),
    l -> Collectors.singleton(l.getDisplayLanguage()),
    (a, b) -> {
      Set<String> r = new HashSet<>(a);
      r.addAll(b);
      return r; }));
// TreeMap을 원하는 경우 네번째 인자로 TreeMap 생성자를 전달. 이때 반드시 병합 함수를 제공해야 한다.
Map<Integer, Person> idToPerson = people.collect(
  Collectos.toMap(
    Person::getId,
    Function.identity(),
    (existingValue, newValue) -> { throw new IllegalStateException(); },
    TreeMap::new));
```

*toMap 메서드의 각 형태에 대응해 병행 맵을 리턴하는 toConcurrentMap 메서드가 있다. 병렬 컬렉션 처리에서는 병행 맵 하나를 사용한다. 병렬 스트림과 함께 사용하면 공유 맵 하나가 여러 맵을 병합하는 방법보다 효율적이다. 물론 이 경우 정렬은 포기해야 한다.*

### 그룹핑과 파티셔닝
성질이 같은 값들의 그룹을 만드는 일은 아주 흔한 작업으로 groupingBy 메서드는 그룹 작업을 직접 지원한다.  
로케일을 국가별로 묶는 문제를 다시 살펴보자.

```
Map<String, List<Locale>> countryToLocales = locales.collect(Collectors.groupingBy(Locale::getCountry));
List<Locale> swissLocales = countryToLocales.get("CH");   // it_CH, de_CH, fr_CH
```

*로케일에 관해 빠르게 복습해보자. 각 로케일은 언어 코드(영어인 경우 en)와 국가 코드(미국인 경우 US)를 포함한다. 로케일 en_US는 미국에서 사용하는 영어를 말하며, en_IE는 아일랜드에서 사용하는 영어를 말한다. 몇몇 국가에는 여러 로케일이 있다. 예를 들어, ga_IE는 아일랜드에서 사용하는 게일어다. 또한 앞의 예에서 볼 수 있듯이 저자의 JVM은 세가지 로케일을 파악하고 있따.* 

분류 함수가 Predicate 함수(즉, boolean을 리턴하는 함수)인 경우, 스트림 요소가 리스트 두개(각각 함수에서 true와 false를 리턴하는 경우에 해당)로 분할된다. 이 경우에는 groupingBy 대신 partitioningBy를 사용하면 훨씬 효율적이다. 예를 들어, 다음 예저는 모든 로케일을 영어를 사용하는 경우와 그 외의 경여루 분리한다.

```
Map<Boolean, List<Locale>> englishAndOtherLocales = locales.collect(Collections.patitioningBy(l -> l.getLanguage().equals("en")));
List<Locale>> englishLocales = englishAndOtherLocales.get(true);
```

*groupingByConcurrent 메서드를 호출하면 병행 맵을 얻으며, 이를 병렬 스트림에서 사용하면 동시에 내용이 채워진다. 전체적으로 볼 때 이 메서드는 toConcurrentMap 메서드에 해당한다.*

groupingBy 메서드는 값이 리스트인 맵을 돌려진다. 이들 리스트를 특정 방식으로 처리하려면 *다운 스트림 컬렉터*를 제공한다. 예를 들어, 리스트 대신 집합을 원하는 경우 앞 절에서 본 Collectors.toSet 컬렉터를 사용할 수 있다.

```
Map<String, Set<Locale>> countryToLocaleSet = locales.collect(groupingBy(Locales.getCountry, Collectors.toSet()));
```

그룹으로 묶인 요소들의 다운스트림 처리용으로 몇 가지 다른 커렉터가 제공된다.
* countring은 모인 요소들의 개수를 센다.예를 들어 다음 코드는 각 국가의 로케일 개수를 센다. `Map<String, Long> countryToLocaleCounts = locales.collect(groupingBy(Locale::getCountry, Collectors.counting()));`
* summing(Int|Long|Double)은 함수 인자 하나를 받아서 해당 함수를 다운스트림 요소들에 적용하고 합계를 구한다. 예를 들어 다음 코드는 도시로 구성된 스트림에서 주별 인구의 합계를 계산한다. `Map<String, Integer> stateToCityPopulation = cities.collect(groupingBy(City::getState, summingInt(City::getPopulation)));`
* maxBy와 minBy는 비교자 하나를 받아서 다운스트림 요소들의 최대값과 최솟값을 구한다. 예를 들어, 다음 코드는 주별로 가장 큰 도시를 구한다. `Map<Sting, City> stateToLargestCity = cities.collect(groupingBy(City::getState, maxBy(Comparator.comparing(City::getPopulation))));`
* mapping은 함수를 다운스트림 결과에 적용하며, 이 결과를 처리하는데 필요한 또 다른 컬럭터를 요구한다. 다음 예제를 보자.

```
// 도시를 주별로 묶고 각 주에서 도시들의 이름을 얻고 최대 길이로 리듀스한다.
Map<String, Optional<String>> stateToLongestCityName = cities.collect(
  groupingBy(City::getState, 
    mapping(City::getName, 
      maxBy(Comparator.comparing(String::length)))));
// mapping 메서드는 앞 절의 문제에 좀 더 훌륭한 해결책을 제시. 각 국가에서 사용하는 모든 언어의 집합을 모으려면
Map<String, Set<String>> countryToLanguage = locales.collect(
  groupingBy(l -> l.getDisplayCountry(),
    mapping(l -> l.getDisplayLanguage(),
      toSet())));
```
* 그룹핑이나 맵핑 함수가 int, long 또는 double 타입을 리턴한다면 요소들을 통계 객체 안으로 모을 수 있다.

```
Map<String, IntSummaryStatistics> stateToCityPopulationSummary
  = cities.collect(
    groupingBy(City::getState, 
      summarizingInt(City::getPopulation)));
```
* 마지막으로 reducing 메서드는 다운스트림 요소들에 범용 리덕션을 적용한다. 세가지 메서드 형태 reducing(binaryOperator), reducing(identity, binaryOperator), reducing(identity, mapper, binaryOperator)가 있다. 첫번째 형태에서는 항등값이 null 이다.(항등 파라미터가 없으면 Optional 결과를 돌려주는 Stream::reduce의 형태와는 다르다는 점을 주목하기 바란다). 세번째 형태에서는 mapper 함수가 적용되고 이 함수의 값이 리류스 된다.

```
// 각 주에 있는 모든 도시의 이름을 컴마로 연결
Map<String, String> stateToCityName = cities.collect(
  groupingBy(City::getState, 
    reducing("", City::getName,
      (s, t) -> s.length() == 0 ? t : s + ", " + t)));
// Stream.reduce와 마찬가지로 Collectors.reducing은 거의 사용할 필요가 없다. 다음 코드로 같은 결과를 더 자연스럽게 얻을 수 있다.
Map<String, String> stateToCityNames = cities.collect(
  groupingBy(City::getState,
    mapping(City:getName,
      joining(", "))));
```

### 기본 타입 스트림
Stream<Integer>와 같은 래퍼 객체를 사용하는 것은 비효율적이기 때문에 IntStrean, LongStream, DoubleStream 같은 기본 타입 값들을 직접 자장하는데 특화된 타입을 사용한다. short, char, byte, boolean 타입은 IntStream, float는 DoubleStream을 사용한다.

```
IntStream stream = IntStream.of(1, 1, 2, 3, 5);
stream = Arrays.stream(values, from, to);
```

객체 스트림과 마찬가지로 정적 generate와 iterate 메서드를 사용할 수 있다. 또한 IntStream과 LongStream은 크기 증가 단위가 1인 정수 범위를 생성하는 정적 range와 rangeClosed 메서드를 포함한다.

```
IntStream zeroToNinetyNine = IntStream.range(0, 100); // 상한값 제외
IntStream zoroToHundred = IntStream.rangeClosed(1, 100);  // 상한값 포함
```

CharSequence 인터페이스는 각각 문자의 유니코드와 UTF-16 인코딩의 코드 단위로 구성된 IntStream을 돌려주는 codePoint와 chars 메서드를 포함한다.

```
Streing sentent = "\uD835\uDD46 is the set of octionions.";
IntStream codes = sentence.codePoints();
```

객체 스트림은 mapToInt, mapToLong, mapToDouble 메서드를 이용해 기본 타입 스트림으로 변환할 수 있다. 예를 들어 문자열 스트림에서 요소의 길이를 정수로 처리하려는 경우 IntStream으로도 수행할 수 있다.

```
Stream<String> words = ...;
IntStream lengths = words.mapToInt(String::lenght);
// 기본 타입 스트림을 객체 스트림으로 변환
Stream<Integer> integers = Integer.range(0, 100).boxed();
```

일반적으로 기본 타입 스트림을 대상으로 동작하는 메서드는 객체 스트림 대상 메서드와 유사하다. 다음은 가장 주목할 만한 차이점이다.
* toArray 메서드는 기본 타입 배열을 리턴한다.
* 옵션 결과를 돌려주는 메서드는 OptionalInt, OptionalLong, OptionalDouble을 리턴한다. 이들 클래스는 Optional 클래스와 유사하지만, get 메서드 대신 getAsInt, getAsLong, getAsDouble 메서드를 포함한다.
* 각각 합계, 평균, 최대값, 최소값을 리턴하는 sum, average, max, min 메서드가 있다. 객체 스트림에는 이러한 메서드가 정의되어 있지 않다.
* summaryStatistics 메서드는 스트림의 합계, 평균, 최대값, 최소값을 동시에 보고할 수 있는 IntSummaryStatistics, LongSummaryStatistics, DoubleSummaryStatistics 타입 객체를 돌려준다.

### 병렬 스트림
parallel 메서드는 순차 스트림을 병렬 스트림으로 변환한다. `Stream<String> parallelWords = Stream.of(wordArray).parallel();`  
스트림이 병렬 모드에 있으면 최종 메서드가 실행될 때 모든 지연 처리 중간 스트림 연산이 병렬화된다.

병렬 스트림 연산에 전달하는 함수가 스레드에 안전함을 보장하는 일은 여러분의 책임이다.

기본적으로 순서 유지 컬렉션(배열과 리스트), 범위(Range), 발생기(Genrator), 반복자 또는 Stream.sorted를 호출해서 얻는 스트림은 순서를 유지한다. 순서 유지 스트림의 결과들은 원본 요소들의 순서로 쌓이고, 전체적으로 예측 가능하게 동작하낟. 따라서 같은 연산들을 두번 실행해도 왼전히 같은 결과를 얻는다. 순서 때문에 병렬화를 이용할 수 없는 것은 아니다. 예를 들어 Stream.map(fun)을 계산할 때 스트림은 n개 세그먼트로 분할되어 각각이 동시에 처리될 수 있다. 그런 다음 순서대로 재조립된다.

몇몇 연산은 순서에 대한 요구 사항을 버리면 더 효과적으로 병렬화될 수 있다. Stream.unordered 메서드를 호출함으로써 순서에는 관심이 없음을 나타낼 수 있다. 이로부터 이점을 얻을 수 잇는 한가지 연산은 Stream.distinct다. 순서 유지 스트림에서 distinct는 같은 요소 중 첫번째를 보존한다. 하지만 이 동작은 병렬화를 방해한다(세그먼트를 처리 중인 스레드는 이전 세그먼트가 처리되기 전에는 어느 요소들을 버려야 하는지 알 수 없다). 유일한 요소라면 어느 것이든 보존해도 괜찮다면 (중복을 추적하기 위해 공유 집합을 사용해) 모든 세그먼트를 동시에 처리할 수 있다.

순서를 포기하면 limit 메서드를 빠르게 할 수 있다. 스트림에서 단지 n개 요소를 원할뿐 어느 것을 얻는지는 상관하지 않는다면 `Stream<T> sample = stream.parallel().unordered().limit(n);`

맵으로 모으기에 있는 맵을 병합하는 일은 비용이 많이 든다. 이 때문에 Collectos.groupingByConcurrent 메서드는 공유되는 병행 맵을 사용한다. 분명히 병렬화의 이점을 얻기 위해 맵 값들의 순서는 스트림 순서와 달라질 것이다. 이 컬렉터는 심지어 순서 유지 스트림에서도 순서을 유지하지 않는 성질이 있다. 따라서 스트림이 순서를 유지하지 않게 만들 필요 없이도 효율적으로 상요할 수 있다. 그럼에도 여전히 스트림을 병렬로 만들어야 한다.

```
Map<String, List<String>> result = cities.parallel().collect(
  Collectos.groupingByConcurrent(City::getState));
```

> 스트림 연산을 수행하는 동안에는 해당 스트림을 뒷받침하는 컬렉션을 절대 수정하면 안된다(스레드에 안전한 수정인 경우에도). 스트림은 자체적으로 데이터를 모이지 않음을 명심하기 바란다(데이터는 항상 별도의 컬렉션에 존재한다). 만딜 해당 컬렉션을 수정하면 스트림 연산들의 결과는 정의되지 않는다. JDK 문서에서는 이 요구 사항을 방해 금지(Noninterference)라고 언급하고 있다. 이 사항은 순차 스트림과 병렬 스트림 모두에 적용된다.  
> 엄밀히 말하면 중간 스트림 연산은 지연 처리되기 때문에 최종 연산이 실행하는 시점 이전까지는 컬렉션을 변경할 수 있다.

### 함수형 인터페이스
Stream.filter 메서드는 함수 인자를 받는다.

```
Stream<String> longWords = words.filter(s -> s.length() >= 12);
```

Stream 클래스의 javadoc에는 filter 메서드가 다음과 같이 선언되어 있다.
> Stream<T> filter(Predicate<? super T> predicate)




















## 3장 람다를 이용한 프로그래밍







## 4장 JavaFX

## 5장 새로운 날짜 및 시간 API
* 핵심내용
  * 모든 java.time 객체는 수정 불가
  * Instant는 타임 라인의 한 시점(Date와 유사)
  * 자바의 시간에서 하루는 정확히 86,400초로 윤초가 없다.
  * Duration은 두 인스턴트 사이의 차이
  * LocalDateTime은 시간대 정보를 포함하지 않는다.
  * TemporalAdjuster의 메서드들은 (특정 월의 첫번째 화요일 찾기 같은) 일반적인 캘린더 계산을 처리
  * ZonedDateTime은 주어진 시간대에서 특정 시점(GregortianCalendar와 유사)
  * 구역 시간을 앞으로 가게 할 때는 일광 절약 시간 변경을 고려하기 위해 Duration이 아닌 Period를 사용
  * DateTimeFormatter를 사용해 날짜와 시간을 해석

### 타임 라인
Instant.now() 는 현재 인스턴트를 준다. 
두 인스턴트를 비교할때는 equals와 compareTo를 사용.

실행시간 측정

```
Instant start = Instant.now();
runAlgorithm();
Instant end = Instant.now();
Duration timeElapsed = Duration.between(start, end);
long millis = timeElapsed.toMillis();
```

메서드 | 설명
------------|------------
plus.minus | Instant 또는 Duration에 기간을 더하거나 뺀다.
plusManos, plusMillis, plusSeconds, plusMinutes, plusHours, plusDays | Instant 또는 Duration에 주어진 시간 단위의 수를 더한다.
minusNanos, minusMillis, minusSeconds, minusMinutes, minusHours, minusDays | Instant 또는 Duration에서 주어진 시간 단위의 수를 뺀다.
multipliedBy, divideBy, negted | 해당 Duration을 주어진 long 값 또는 -1로 곱하거나 나누어서 얻은 Duration을 리턴한다. Instant가 아닌 Duration만 크기 변경 할 수 있다.
isZero, isNegative | Duration이 0 또는 음수인지 검사

어떤 알고리즘이 다른 알고리즘보다 최소 10배 빠른지 검사

```
Duration timeElapsed2 = Duration.between(start2, end2);
boolean overTenTimesFaster = timeElapsed.multipliedBy(10).minus(timeElapsed2).isNegative();
// 또는 timeElapsed.toNanos() * 10 < timeElapsed2.toNanos()
```

### 지역 날짜
지역날짜/시간(Local date/time)과 구역 시간(Zoned time) 두종류.
지역 날짜/시간에 연관된 시간대 정보는 포함하지 않음.
LocalDate를 생성하는 데 정적 메서드 of나 now를 사용.

```
LocalDate today = LocalDate.now();
LocalDate alonzosBirthday = LocalDate.of(1903, 6, 14);
alonzosBirthday = LocalDate.of(1903, Month.JUNE, 14);
```

월은 1부터 시작. Month enum을 사용할 수도 있음.
유용한 메서드

메서드 | 설명
------------|------------
not, of | 현재 시각 또는 주어진 연, 월, 일로부터 LocalDate를 생성하는 정적 메서드
plusDays, plusWeeks, plusMonth, plusYears | 해당 LocalDate에 일, 주, 월, 년을 더한다.
minusDays, minusWeeks, minusMonths, minusYears | 해당 LocalDate에서 일, 주, 월, 년을 뺀다.
plus, minus | Duration 또는 Period를 더하거나 뺀다.
withDayOfMonth, withDayOfYear, withMonth, withYear | 월 단위 일, 연 단일 일, 월, 년을 주어진 값으로 변경한 새로운 LocalDate를 리턴.
getDayOfMonth | 월 단위 일을 얻는다.(1~31)
getDayOfYear | 연 단위 일을 얻는다.(1~366)
getDayOfWeek | 요일을 얻는다.(DayOfWeek 열거 타입값으로 리턴)
getMonth, getMonthValue | 월을 Month 열거 타입 값 또는 1 ~ 12 사이의 숫자로 얻는다.
getYear | -999,999,999 ~ 999,999,999 사이의 연도를 얻는다.
until | 두 날짜 사이에서 Perid 또는 주어진 ChronoUnit 수를 구한다.
isBefore, isAfter | 해당 LocalDate를 다른 LocalDate와 비교
isLeapYear | 해당 연도가 윤년(4로 나눌 수 있지만 100으로 나눌 수 없거나 400으로 나눌 수 있는 연도)이면 true.

두 시간 인스턴트 사이의 차이는 Duration, 지역날짜에서는 Period.

### 날짜 조정기
TemporalAdjusters 클래스는 일반적인 조정(매월 첫번째 월요일)에 사용하는 다수의 정적 메서드를 제공.
이러한 조정 메서드의 결과를 with 메서드에 전달.

특정 월의 첫번째 화요일
```
LocalDate firstTuesday = LocalDate.of(year, month, 1).with(TemporalAdjusters.nextOfSame(DayOfWeek.TUESDAY));
```
with 메서드는 원본을 수정하지 않고 새로운 LocalDate 객체를 리턴.
TemporalAdjusters 클래스의 날짜 조정기

메서드 | 설명
------------|------------
next(dayOfWeek), previous(dayOfWeek) | 지정 요일에 해당하는 다음 또는 이전 날짜
nextOrSame(dayOfWeek), previousOrSame(dayOfWeek) | 주어진 날짜부터 시작해서 지정 요일에 해당하는 다음 또는 이전 날짜
dayOfWeekInMonth(n, dayOfWeek) | 해당 월의 n번째 지정 요일
lastInMonth(dayOfWeek) | 해당 월의 마지막 지정 요일
firstDayOfMonth(), firstDayOfNextMonth(), firstDayOfNextYear(), lastDayOfMonth(), lastDayOfPreviousMonth(), lastDayOfYear() | 메서드 이름이 기술된 날짜

TemporalAdjuster 인터페이스를 구현하면 자신만의 조정기를 만들 수 있음.
다음번 평일을 계산하는 조정기

```
TemporalAdjuster NEXT_WORKDAY = w -> {
  LocalDate result = (LocalDate)w;
  do {
    result = result.plusDay(1);
  } while (result.getDayOfWeek().getValue() >= 6);
  return result;
};

LocalDate backToWork = today.with(NEXT_WORKDAY);
```

람다 표현식의 파라미터는 Temporal 타입이기 때문에 LocalDate로 캐스트해야 한다.

UnaryOperator<LocalDate> 타입의 람다를 기대하는 ofDateAdjuster를 사용하면 이 캐스트를 피할 수 있다.

```
TemporalAdjuster NEXT_WORKDAY = TemporalAdjusters.ofDateAdjuster(w -> {
  LocalDate result = w;   // 캐스트가 없다.
  do {
    result = result.plusDay(1);
  } while (result.getDayOfWeek().getValue() >= 6);
  return result;
};
```

### 지역 시간
LocalTime은 04:50:00 같은 시간을 표현. LocalTime의 인스턴스는 of 나 now를 이용해 생셩.

```
LocalTime rightNow = LocalTime.now();
LocalTime bedtime = LocalTime.of(22, 30); // 또는 LocalTime.of(22, 30, 0)
```

LocalTime 메서드

메서드 | 설명
------------|------------
now, of | 현재 시각 또는 주어진 시, 분 그리고 선택적으로 초와 나노초로부터 LocalTime을 생성
plusHours, plusMinus, plusSeconds, plusNanos | 해당 LocalTime에 시, 분, 초, 나노초를 더한다.
minusHous, minusMinutes, minusSeconds, minusNanos | 해당 LocalTime에서 시, 분, 초, 나노초를 뺀다.
plus, minus | Duration을 더하거나 뺀다.
withHour, withMinute, withSecond, withNano | 시, 분,초,나노초가 주어진 값으로 변경된 새로운 LocalTime 리턴.
getHour, getMinute, getSecond, getNano | 해당 LocalTime의 시, 분, 초, 나노초를 얻는다.
toSecondOfDay, toNanoOfDay | 자정과 해당 LocalTime 사이의 초 또는 나노초 수를 리턴.
isBefore, isAfter | 해당 LocalTime을 다른 LocalTime과 비교

LocalTime은 AM/PM은 신경 쓰지 않는다. 이런 일은 포맷터의 몫이다.

날짜와 시간을 표현하는 LocalDateTime 클래스는 고정 시간대에서 시간의 한 점을 저장하는데 적합. 일광 절약 시간을 포함하는 계산이나 서로 다른 시간대에 있는 사용자들을 다뤄야 한다면 ZonedDateTime 클래스를 사용.

### 구역시간
자바는 IANA(인터넷 할당 번호 관리 기관-Internet Assigned Numbers Authority) 데이터베이스를 사용한다.

모든 시간대를 얻으려면 ZoneId.getAvailableIds를 호출.

정적 메서드 ZoneId.of(id)는 시간대 ID를 넘겨주면 ZoneId 객체를 돌려준다. 이 객체를 사용해 local.atZone(zoneId) 형식으로 호출하면 LocalDateTime 객체를 ZonedDateTime 객체로 변환할 수 있다.
또한 ZonedDateTime.of(year, month, day, hour, minute, second, nano, zoneId)를 호출해 ZonedDateTime을 생성할 수도 있다.

```
ZonedDateTime apollo111launch = ZonedDateTime.of(1969, 7, 16, 9, 32, 0, 0, ZoneId.of("America/New_York"));
Instant instant = apollo11launch.toInstant();   // ZonedDateTime을 인스턴트로 변환
ZonedDateTime other = instant.atZone(ZoneId.of("UTC"));   // instant를 ZonedDateTime으로 변환.
```

ZonedDateTime 메서드는 LocalDateTime과 비슷.

일광절약시간이 시작할때는 시계가 1시간 후로 간다. 중유럽은 2013년 3월 31일 2시에 일광 절약시간으로 변환했다. 존재하지 않은 시간인 3월 31일 2시 30분을 생성하면 실제로는 3시 30분을 얻는다.

```
ZonedDateTime skipped = ZonedDateTime.of(
  LocalDate.of(2013, 3, 31),
  LocalTime.of(2, 30),
  ZoneId.of("Europe/Berlin"));
  // 3월 31일 3시 30분을 생성
```

반대로 일광 절약 시간이 끝날때는 시계가 1시간 전으로 설정되어, **지역시간이 동일한 두 인스턴트가 생긴다.** 이 범위에 속하는 시간을 생성하면 둘 중 이른 인스턴트를 얻는다.

```
ZonedDateTime ambigous = ZoneDateTime.of(
  LocalDate.of(2013, 10, 27), // 일광 절약 시간 끝
  LocalTime.of(2, 30),
  ZoneId.of("Europe/Berlin"));
  // 2013-10-27T02:30+02:00[Europe/Berlin]
  
ZonedDateTime anHourLater = ambigous.plusHour(1);   // 2013-10-27T02:30+01:00[Europe/Berlin]
```

일광 절약 시간 경계를 가로질러 날짜를 조정할때에는 Duration이 아닌 Period를 사용해야 한다.

```
ZonedDateTime nextMeeting = meeting.plus(Duration.ofDays(7));   // Bad
ZonedDateTime nextMeeting = meeting.plus(Period.ofDays(7));     // OK
```

시간대 규칙 없이 UTC로부터 오프셋으로 시간을 나타내는 OffsetDateTime 클래스도 있다. 특정 네트워크 프로토콜처럼 특별히 시간대 규칙의 부재를 요구하는 특수 애플리케이션용으로 의도 되었다.

### 포맷팅과 파싱
DateTimeFormatter 클래스는 날짜/시간 값을 출력하는 세종류의 포맷터 제공.
* 미리 정의된 표준 포맷터(표 참고)
* 로케일 종속 포맷터
* 커스텀 패턴을 이용하는 포맷터

미리 정의된 포맷터

포맷터 | 설명 | 설명
------------|------------|------------
BASIC_ISO_DATE | 구분자 없는 연, 월, 일, 시간대 오프셋 | 19890716-0500
ISO_LOCAL_DATE, ISO_LOCAL_TIME, ISO_LOCAL_DATE_TIME | -, :, T 구분자 사용 | 1989-07-16, 09:32:00, 1969-07-16T09:32:00
ISO_OFFSET_DATE, ISO_OFFSET_TIME, ISO_OFFSET_DATE_TIME | ISO_LOCAL_XXX와 유사하지만 시간대 오프셋을 포함 | 1969-07-16-05:00, 09:32:00-05:00, 1969-07-16T09:32:00-05:00
ISO_ZONED_DATE_TIME | 시간대 오프셋과 시간대 ID 포함 | 1969-07-16T09:32:00-05:00[America/New_York]
ISO_INSTANT | 시간대 ID Z로 표기하는 UTC 형식 | 1969-07-16T14:32:00Z
ISO_DATE, ISO_TIME, ISO_DATE_TIME | ISO_OFFSET_DATE, ISO_OFFSET_TIME, ISO_ZONED_DATE_TIME과 유사하지만 시간대 정보가 선택적임 | 1969-07-16-05:00, 09:32:00-05:00, 1969-07-16T09:32:00-05:00[America/New_York]
ISO_ORDINAL_DATE | LocalDate에 해당하는 연도와 연 단위 일 | 1969-197
ISO_WEEK_DATE | LocalDate에 해당하는 연, 주, 요일 | 1969-W29-3
RFC_1123_DATE_TIME | 이메일 타임스탬프 표준으로 RFC 822에서 표준화되었고 RFC-1123에서 연도에 4자리 숫자를 사용하도록 업데이트 됨 | Wed, 16 Jul 1969 09:32:00 -0500

표준 포맷터 중 하나를 사용하려면 format 메서드 호출

```
String formatted = DateTimeFormatter.ISO_DATE_TIME.format(apollo11launch);
// 1969-07-16T09:32:00-05:00[America/New_York]
```

표준 포맷터는 대부분 기계가 읽을 수 있는 타임스탬프 용으로 만들어졌다. 사람이 읽을 수 있는 날짜와 시간을 표현하려면 로케일 종속 포맷터를 사용한다.

로케일 종속 포맷터에는 **SHORT, MEDIUM, LONG, FULL** 네가지 형식 존재.

형식 | 날짜 | 시간
------------|------------|------------
SHORT | 7/16/69 | 9:32 AM
MEDIUM | Jul 16, 1969 | 9:32:00 AM
LONG | July 16, 1969 | 9:32:00 AM EDT
FULL | Wednesday, July 16, 1969 | 9:32:00 AM EDT

정적 메서드 ofLocalizedDate, ofLocalizedTime, ofLocalizedDateTime은 로케일 종속 포맷터를 생성

```
DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG);
String formatted = formatter.forater(apollo11launch);
// July 16, 1969 9:32:00 AM EDT

// 다른 로케일 사용시
formatted = formatter.withLocale(Locale.FRENCH).format(apollo11launch); // 16 juillet 1969 09:32:00 EDT

// Pattern 지정시
formatter = DateTimeFormatter.ofPattern("E yyyy-MM-dd HH:mm");  // Wed 1969-07-16 09:32
```

날짜/시간 형식에 흔히 사용하는 포맷팅 심볼
ChronoField 또는 용도 | 예
------------|------------
EAR | G:AD, GGGG:Anno Domini, GGGGG:A
YEAR_OF_ERA | yy:69, yyyy:1969
MONTH_OF_YEAR | M:7, MM:07, MMM:Jul, MMMM:July, MMMMM:J
DAY_OF_MONTH | d:6, dd:06
DAY_OF_WEEK | e:3, E:Wed, EEEE:Wednesday, EEEEE:W
HOUR_OF_DAY | H:9, HH:09
CLOCK_HOUR_OF_AM_PM | K:9, KK:09
AMPM_OF_DAY | a:AM
MINUTE_OF_HOUR | mm:02
SECOND_OF_MINUTE | ss:00
NANO_OF_SECOND | nnnnnn:000000
시간대 ID | VV:America/New_York
시간대 이름 | z:EDT, zzzz:Eastern Daylight Time
시간대 오프셋 | x:04, xx:-0400, xxx:-04:00, XXX:xxx와 같지만 0에 Z를 사용
지역화된 시간대 오프셋 | O:GMT-4, 0000:GMT-04:00

문자열로부터 날짜/시간 값을 파싱하려면 정적 parse 메서드들 중 하나를 사용

```
LocalDate churchsBirthday = LocalDate.parse("1903-06-14");
ZonedDateTime apollo11launch = ZonedDateTime.parse("1969-07-16 03:32:00-0400", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssxx"));
```

### 레거시 코드와 상호 동작
Instant 클래스는 java.util.Date와 유사. Date 클래스에 Instant로 변환하는 toInstant와 Instant를 Date로 변환하는 from 정적 메서드를 추가.

ZonedDateTime은 java.util.GregorianCalendar에 해당하며 GregorianCalendar에 toZonedDateTime, from 메서드를 추가.

java.time 클래스들과 레거시 클래스들 사이의 변환
클래스 | 레거시 클래스로 변환 | 레거시 클래스로부터 변환
------------|------------|------------
Instant ↔ java.util.Date | Date.from(instant) | date.toInstant()
ZonedDateTime ↔ java.util.GregorianCalendar | GregorianCalendar.from(zonedDateTime) | cal.toZonedDateTime()
Instant ↔ java.sql.Timestamp | Timestamp.from(instant) | timestamp.toInstant()
LocalDateTime ↔ java.sql.Timestamp | Timestamp.valueOf(localDateTime) | timestamp.toLocalDateTime()
LocalDate ↔ java.sql.Date | Date.valueOf(localDate) | date.toLocalDate()
LocalTime ↔ java.sql.Time | Time.valueOf(localTime) | time.toLocalTime()
DateTimeFormatter ↔ java.text.DateFormat | formatter.toFormat() | 
java.util.TimeZone ↔ ZoneId | Timezone.getTimeZone(id) | timeZone.toZoneId()
java.nio.file.attribute.FileTime ↔ Instant | FileTime.from(instant) | fileTime.toInstant()

## 6장 병행성 향상점
* 핵심내용
  * updateAndGet/accumulateAndGet 으로 원자적 업데이트
  * **높은 경쟁상황**에서는 LongAccumulator/DoubleAccumulator가 AtomicLong/AtomicDouble 보다 효율적
  * compute와 merge 덕분에 ConcurrentHashMap에 있는 항목의 업데이터가 간단해 짐.
  * ConcurrentHashMap은 search, reduce, forEach 같은 벌크 연산을 지원하며, 각각 키, 값, 키와 값, 엔트리에 작용하는 변종을 제공.
  * 집합 뷰(SetView)는 ConcurrentHashMap을 Set으로 사용할 수 있게 해준다.
  * Arrays 클래스는 병렬 정렬, 채우기, 프리픽스 연산을 위한 메서드를 포함
  * 완료 가능한 퓨처(Completable Future)는 비동기 연산들을 합성할 수 있게 해준다.
  
### 원잣값
값을 원자적으로 더하고 빼는 메서드가 있지만 좀 더 복잡한 업데이트를 수행하려면 compareAndSet 메서드를 사용해야 한다. 예를 들어 서로 다른 쓰레드들에서 주시하고 있는 가장 큰 값을 추적하고 싶은 경우 다음 코드는 예상대로 동작하지 않는다.

```
public static AtomicLong largest = new AtomicLong();
// 어떤 쓰레드에서
largest.set(Math.max(largest.get(), observed)); // 오류 - 경쟁 조건
```

이 업데이트는 원자적이지 않다. 대신 루프에서 새로운 값을 계산하고 compareAndSet을 사용해야 한다.

```
do {
  oldValue = largest.get();
  newValue = Math.max(oldValue,observed);
} while (!largest.compareAndSet(oldValue, newValue);
```

자바8에서는 루프가 필요 없고 람다 표현식을 이용하여 처리

```
largest.updateAndGet(x -> Math.max(x, observed));
// or
largest.accumulateAndGet(observed, Math::max);
```

AtomicInteger, AtomicIntegerArray, AtomicIntegerFieldUpdater, AtomicLongArray, AtomicLongFieldUpdater, AtomicReference, AtomicReferenceArray, AtomicReferenceFieldUpdater 클래스에도 이와 같은 메서드를 제공

동일한 원잣값을 접근하는 쓰레드가 아주 많은 경우에는 지나친 업데이트르 너무  많은 재시도가 필요하기 때문에 성능이 떨어진다. 이를 위해 LongAdder, LongAccumulator를 제공. LongAdder는 각각을 모두 합하면 현재 값이 되는 여러 변수로 구성. 여러 쓰레드가 서로 다른 피가수(Summand)를 업데이트할 수 있고  쓰레드 수가 증가하면 새루운 서맨드가 자동으로 제공된다. 이 방법은 모든 작업을 마치기 전에는 합계 값이 필요하지 않은 일반적인 상황에서 효율적.

높은 경쟁을 예상할 때는 AtomicLong 대신 무조건 LongAdder를 사용해야 한다. LongAdder의 메서드 이름은 AtomicLong과 약간씩 다르다. 카운터를 증가시키려면 increment를 수량을 추가할 때는 add, 합계를 추출할 때는 sum을 호출

```
final LongAdder adder = new LongAdder();
for (...)
  pool.submit(() -> {
    while (...) {
      ...
      if (...) adder.increment();
    }
  });
...
long total = adder.sum());
```

LongAccumulator는 이 개념을 임의의 누적 연산으로 일반화. 생성자에는 필요한 연산과 해당 연산의 중립 요소를 제공. 다음은 LongAdder와 같은 효과를 낸다.

```
LongAccumulator adder = new LongAccumulator(Long::sum, 0);
// 어떤 쓰레드에서
adder.accumulator(value);
```

Long::sum이 아닌 Math.min/max를 선택하면 최소/최대값을 구할 수 있다. 해당 연산은 결합 법칙과 교환법칙이 성립해야 한다.
DoubleAdder와 DoubleAccumulator도 같은 방식으로 동작.

자바8은 낙관적 읽기(Optimistic read)를 구현하는데 사용할 수 있는 StampedLock 클래스를 추가. tryOptimisticRead를 호출하여 스탬프를 얻은 다음 값을 읽고 스탬프가 여전히 유효(즉, 다른 쓰레드에서 읽기 잠금을 획득하지 않음)한지 검사하여 아직 유효하다면 값을 이융하고 그렇지 않으면(쓰기 작업을 하는 쓰레드를 블록하는) 읽기 잠금을 얻는다.

```
public class Vector {
  private int size;
  private Object[] elements;
  private StampedLocak lock = new StampedLocak();
  
  public Object get(int n) {
    long stamp = lock.tryOptimisticRead();
    Object[] currentElements = elements;
    int currentSize = size;
    if (!lock.validate(stamp)) {    // 다른 누군가가 쓰기 잠금을 가지고 있다.
      stamp = lock.readLock();      // 비관적 잠금(Pessimistic lock)을 얻는다.
      currentElements = elements;
      currentSize = size;
      lock.unlockRead(stamp);
    }
    return n < currentSize ? currentElements[n] : null;
  }
  ...
}
```

### ConcurrentHashMap 향상점
자바8은 크기를 long으로 리턴하는 mappingCount 메서드를 도입하여 20억개가 넘는 엔트리를 담은 맵을 지원.
HashMap은 HashCode가 같은 모든 엔트리를 같은 Bucket에 유지하는데, 자바8부터 ConcurrentHashMap은 키 타입이 Comparable을 구현하는 경우 버킷을 리스트가 아닌 트리로 조작하여 O(log(n)) 성능을 보장한다.

#### 값 업데이트 하기
여러 쓰레드에서 단어들을 마주치는 상황의 빈도를 세고자 할때

```
Long oldValue = map.get(word);
Long newValue = oldValue == null ? 1 : oldValue + 1;
map.put(word, newValue);  // 오류 - oldValue를 교체하지 않을 수도 있다.

// 해결책
do {
  oldValue = map.get(word);
  newValue = oldValue == null ? 1 : oldValue + 1;
} while (!map.replace(word, oldValue, newValue));

// 또 다른 해결책
map.putIfAbsent(word, new LongAdder());
map.get(word).increment();
```

자바8에서 원자적 업데이트를 편리하게 해주는 메서드.
compute 메서드는 키와 새로운 값을 계산하는 함수를 전달 받는다.

```
map.compute(word, (k, v) -> v == null ? 1 : v + 1);
```

※ ConcurrentHashMap에는 내부적으로 null 값을 사용하고 있기 때문에  null 값을 저장할 수 없다.

computeIfPresent/computeIfAbsent

```
map.computeIfAbsent(word, k -> new LongAdder()).increment();
```

키가 처음으로 추가될때 뭔가 특별한 작업이 필요한 경우 merge 메서드 사용. 이 메서드는 키가 아직 존재하지 않을때 사용할 초기값을 파라미터로 받는다. 키가 존재하면 파라미터로 전달한 함수가 기존 값과 초깃값을 받는다.

```
map.merge(word, 1L, (existingValue, newValue) -> existingValue + newValue);
// or
map.merge(word, 1L, Long::sum);
```

compute나 merge에 전달한 함수가 null을 리턴하면 기존 엔트리가 맵에서 제거된다.
compute나 merge를 사용할 때는 이들 메서드에 전달한 함수에서 많을 일을 수행하면 안된다. 전달한 함수가 실행되는 동안 해당 맵을 대상으로 하는 다른 업데이트가 블록될 수 있다. 또한 함수 내부에서 맵의 다른 부분을 업데이터하면 안된다.

#### 벌크 연산
연산의 종료
* search는 함수가 널이 아닌 결과를 돌려줄 때까지 각 키 그리고/또는 값에 함수를 적용한다.
* reduce는 제공받는 누적 함수를 이용해 모든 키 그리고/또는 값을 결합한다.
* forEach는 함수를 모든 키 그리고/또는 값에 적용한다.

각 연산의 네가지 버전
* operationKeys : 키를 대상으로 동작
* operationValues : 값을 대상으로 동작
* operation : 키와 값을 대상으로 동작
* opertionEntries : Map.Entry 객체를 대상으로 동작

이들 연산 각각에 병렬성 임계값(Parallelism threshold)을 지정해야 한다. 맵이 임계값보다 많은 요소를 담고 있으면 벌크 연산이 병렬화된다.

```
// 1000번 이상 나타나는 첫번째 단어를 찾고 싶을때는 키와 값을 모두 검색해야 한다.
String result = map.search(threshold, (k, v) -> v > 1000 ? k : null);

// forEach 함수는 두가지 변종
// 첫번째 단순히 각 엔트리에 소비함수 적용
map.forEach(threshold, (k, v) -> System.out.println(k + " -> " + v));
// 두번째는 추가로 변환함수를 받아 이를 먼저 적용한수 소비함수에 전달
map.forEach(threshold,
  (k, v) -> k + " -> " + v, // 변환 함수
  System.out::println);     // 소비 함수
  
// 변환함수는 필터로 사용될 수 있다. null을 리턴하면 건너뛴다.
map.forEach(threshold, (k, v) -> v > 1000 ? k + " -> " + v : null, System.out::println);

// reduce 연산은 입력을 누적 함수와 결합
Long sum = map.reduceValues(threshold, Long::sum);
// forEach와 마찬가지로 변환함수를 전달 할 수도 있다. 가장 긴 키의 길이
Integer maxLength = map.reduceKeys(threshold, String::length, Integer::max);
// 변환함수를 필터로 사용. 값이 1000보다 큰 엔트리의 개수
Long count = map.reduceValues(threshold, v -> v > 1000 ? 1L : null, Long::sum);
```

맵이 비었거나 모든 엔트리가 필터링된 경우에는 reduce 연산이 null을 리턴. 맵에 요소가 한개만 있다면 해당 요소의 변환이 리턴되고 누적함수는 적용되지 않는다.

int, long, double 결과를 얻는 ToInt, ToLong, ToDouble 접미가가 붙은 특화 번전이 있는데 이 메서드를 사용할 때는 입력을 기본 타입 값으로 변환하고 디폴트 값과 누적 함수를 지정해야 한다. 맵이 비어 있는 경우 디폴트 값이 리턴된다.

```
long sum = map.reduceValuesToLong(threshold,
  Long::longValue,    // 기본 타입으로 변환하는 함수
  0,                  // 비어 있는 맵인 경우 디폴트 값
  Long::sum);         // 기본 타입 누적 함수
```

기본 타입 특화 버전은 요소가 하나뿐인 맵과 다르게 동작한다. 특화 버전에서는 변환된 요소를 리턴하는 대신 해당 요소가 디폴트 값과 누적되어 리턴된다.

#### 집합 뷰
쓰레드에 안전한 Set.
정적 메서드인 newKeySet은  실제로 ConcurrentHashMap<K, Boolean>을 감싸는 Set<K>를 리턴한다. Boolean은 모두 Boolean.TRUE로 신경쓰지 않아도 된댜.

```
Set<String> words = ConcurrentHashMap.<String>newKeySet();
```

기존 맵이 있는 경우 keySet 메서드는 키 집합을 돌려준다. 이 집합은 수정 가능하고 집합의 요소를 제거하면 맵에서 키가 제거된다. 하지만 키 집합에 요소를 추가하는 일은 키에 대응해 추가할 값이 없으므로 디폴트 값을 받아 추가해야 한다.

```
Set<String> words = map.keySet(1L);
words.add("Java");
```

### 병렬 배열 연산
Arrays 클래스는 다수의 병렬 연산을 제공. 정적 Arrays.parallelSort 메서드는 기본 타입 또는 객체들의 배열을 정렬할 수 있다.

```
String contents = new String(Files.readAllBytes(Paths.get("alice.txt")), StandardCharsets.UTF_8);
String[] words = contents.split("[\\P{L}]+"); // 비문자로 분리
Arrays.parallelSort(words);
```

객체를 정렬 할때 Comparator을 전달할 수 있다. 또한 모든 정렬 메서드에 범위의 경계를 전달할 수 있다.

```
values.parallelSort(values.length / 2, value.length);   // 상위 절반을 정렬한다.
```

parallelSetAll 메서드는 전달받은 함수에서 계산한 값들로 배열을 채운다. 이 메서드에 전달하는 함수는 요소의 인덱스를 받고 그 위치에 있는 값을 계산한다.

```
Arrays.parallelSetAll(values, i -> i % 10);
```

parallelPrefix 메서드는 결합 법칙형 연산에 대한 프리픽스의 누적으로 교체한다.

```
// values = [1, 2, 3, 4, ...]
Arrays.parallelPrefix(values, (x, y) -> x * y);
// => [1, 1*2, 1*2*3, 1*2*3*4, ...]
```

### 완료 가능한 퓨처
#### 퓨처

```
public void Future<String> readPage(URL url) { ... }
public static List<URL> getLinks(String page) { ... }

Future<String> contents = readPagee(url);
String page = contents.get();             // 블록킹 호출이므로 메서드를 직접 호출하는 것보다 나을게 없다.
List<URL> links = Parser.getLinks(page);
```

#### 퓨처 합성하기
readPage에서 CompletableFuture<String>을 리턴하도록 변경. CompletableFuture는 후처리 함수를 전달할 수 있는 tehnApply 메서드를 제공.

```
CompletableFuture<String> contents = readPage(url);
CompletableFuture<List<String>> links = contents.thenApply(Parser::getLinks);
```

thenApply 메서드는 블록되지 않고 또 다른 퓨처를 리턴한다. 첫번째 퓨처가 완료될 때 그 결과가 getLinks 메서드에 전달되며, 이 메서드의 리턴값이 최종 결과가 된다.

#### 합성 파이프라인
스트림 파이프라인이 스트림생성 -> 변환 -> 최종 연산의 흐름을 갖는 것처럼 퓨처의 파이프라인도 동일하다.
일반적으로 정적 메서드 supplyAsync로 CompletableFuture를 생성하여 파이프라인을 시작한다. 이 메서드는 Supply<T>를 요구한다. Supply<T>는 파라미터가 없고 T를 리턴하는 함수이다. 이 함수는 별도의 쓰레드에서 호출된다.

```
CompletableFuture<String> contents = CompletableFuture.supplyAsync(() -> blockingReadPage(url));
```

Runnable을 전달받고 CompletableFutur<Void>를 돌려주는 정적 runAsync 메서드도 있다. 이 메서드는 액션 사이에 데이터를 전달하지 않고 단순히 어떤 액션 다음에 다른 액션을 스케줄하려고 할 때 유용.  

*Async로 끝나는 모든 메서드는 두가지 변종이 있다. 이 중 하나는 제공된 액션을 공통 ForkJoinPool에서 실행한다. 나머지 하나는 java.util.concurrent.Executor 타입 파라미터를 받아서 액션을 실행하는데 사용한다.*

다음으로, 또 다른 액션을 같은 쓰레드 또는 다른 쓰레드에서 실행하게 위해 thenApply 또는 thenApplyAsync를 호출할 수 있다. 이들 메서드를 이용할 때는 함수를 전달하고 CompletableFuture<U>를 얻는다.

```
CompletableFuture<List<String>> links = CompletableFuture.supplyAsync(() -> blockingReadPage(url))
                                                         .thenApply(Parser::getLinks);
// 단순 결과 출력
CompletableFuture<Void> links = CompletableFuture.supplyAsync(() -> blockingReadPage(url))
                                                 .thenApply(Parser::getLinks)
                                                 .thenAccept(System.out::println);
```

thenAccept 메서드는 Consumer(리턴타입이 void인 함수)를 파라미터로 받는다.  
이상적으로는 퓨처의 get를 호출하지 않아야 한다. 파라이프라인에서 마지막 단계는 단순히 결과를 자신이 속한 곳에 둔다.  

#### 비동기 연산 합성하기
표현의 단순화를 위해 Function<? super T, U>를 T -> U로 표기.  

단일 퓨처를 다루는 메서드  

메서드 | 파라미터 | 설명
------------|------------|------------
thenApply | T -> U | 결과에 함수를 적용한다.
thenCompose | T -> CompletableFuture<U> | 결과를 대상으로 함수를 호출하며, 리턴된 퓨처를 실행한다.
handle | (T, Throwable) -> U | 결과 또는 오류를 처리한다.
thenAccept | T -> void | handle과 유사하지만 결과가 void다.
thenRun | Runnable | Runnable을 실행하며, 결과가 void다.

여러 퓨처를 결합하는 메서드

메서드 | 파라미터 | 설명
------------|------------|------------
thenCombine | CompletableFuture<U>, (T, U) -> V | 둘 모두 실행하고 주어진 함수로 결과들을 결합한다.
thenAcceptBoth | CompletableFuture<U>, (T, U) -> void | thenCombine과 유사하지만 결과가 void다.
runAfterBoth | CompletableFuture<?>, Runnable | 둘 모두 완료한 후에 Runnable을 실행
applyToEither | CompletableFutore<T>, T -> V | 둘 중 하나에서 결과를 얻을 수 있게 될 때 해당 결과를 주어진 함수에 전달
acceptEither | CompletableFuture<?>, T -> void | applyToEither와 유사하지만 결과가 void 다
runAfterEither | CompletableFuture<?>, Runnable | 둘 중 하나가 완료한 후에 Runnable을 실행한다.
static allOf | CompletableFuture<?>... | 주어진 모든 CompletableFuture가 완료하면 void 결과로 완료한다.
static anyOf | CompletableFuture<?>... | 주어진 CompletableFuture 중 하나가 완료하면 void 결과로 완료한다.

처음 세 메서드는 CompletableFuture<T>와 CompletableFuture<U> 액션을 병렬로 실행하고 결과들을 결합한다.  
다음 세 메서드는 두 CompletableFuture<T> 액션을 병렬로 실행한다. 둘 중 하나가 완료하는 즉시 해당 결과를 전달하며, 나머지 결과는 무시한다.  
마지막으로 정적 메서드 allOf와 anyOf는 가변 개수의 완료 가능한 퓨처를 파라미터로 받고, 모두 또는 이중 하나가 와료하는 CompletableFuture<Void>를 돌려준다. 그리고 어떤 결과도 전파되지 않는다.

*기술적으로 말하면, 이 절에서 설명하는 메서드들은 CompletableFuture가 아니라 CompletionStage 타입 파라미터를 받는다. CompletionStage는 40개 가까운 추상 메서드를 포함하는 인터페이스 타입으로, 아직은 CompletableFuture에서만 구현하고 있다.*

## 7장 Nashorn 자바 스크립트 엔진

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

정수 타입들은 이제 부호 없는 산술 연산을 지원. 예를 들어 -128 ~ 127 범위를 표현하는 Byte 대신, 정적 메서드 `Byte.toUnsignedInt(b)를 호출하여 0 ~ 255 사이의 값을 얻을 수 있다.
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

언어 범위는 사용자가 원하는 로케일 특성을 기술하는 문자열로, *를 와일드 카드로 사용한다. 예를 들어, 스위스에서 독일어로 말하는 사람은 로케일로 독일어를 가장 선호하고, 이어서 스위스를 선호할 것이다. 이는 문자열 **de**와 **\*-CH**로 지정한 두 Locale.LanguageRange 객체로 표현할 수 있다. 또한, Locale.LangugaeRange를 생성할 때 선택적으로 0 ~ 1 사이의 가중치를 지정할 수 있다.

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
자바7dms 비트 집합을 생성하는 메서드를 추가했다.

```
byte[] bytes = {(byte)0b10101100, (byte)0b00101000 };
BitSet primes = BitSet.valueOf(bytes);    // 2, 3, 5, 7, 11, 13
long[] longs = { 0x100010116L, 0x1L, 0x1L, 0L, 0x1L };
BitSet powersOfTwo = BitSet.valueOf(longs);   // 1, 2, 4, 8, 16, 32, 64, 128, 256

// 역으로 비트 집합으로부터 각 배열을 만든느 메서드는 toByteArray와 toLongArray다.
byte[] bytes = powersOfTwo.toByteArray();     // 0b0010110, 1, 1, 0, 1, 0, 0, 0, 1, ...
```

*자바8부터 BitSet은 IntStream을 돌려주는 stream 메서드를 포함한다.*













































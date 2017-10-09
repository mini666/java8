# Java8

## 1장 람다표현식
1. 람다 표헌식 문법
2. 함수형 인터페이스
3. 메서드 레퍼런스
4. 생성자 레퍼런스
5. 변수 유효 범위
6. 디폴트 메서드
7. 인터페이스의 정적 메서드

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
이제 HashSet에 결과를 모드려 한다고 하자. HashSet 객체는 스레드에 안전하지 않기 때문에 컬렉션을 병렬화하면 요소들을 단일 HashSet에 넣을 수 없다. 이와 같은 이유로 reduce를 사용할 수 없다. 각 부분은 자체적인 빈 해시 집합으로 작업을 시작해야 하는데 reduce는 항등값 하나만 전달하도록 허용한다. 따라서 reduce 대신 collect를 사용해야 한다. collect는 세가지 인자를 받는다.
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

특정 국가에서 사용하는 모든 언어를 알고 싶을때 Map<String, Set<String>>이 필요한다. 예를 들면, "Switzerland"에 해당하는 집합은 [French, German, Italian]이다. 주어진 국가에서 새로운 언어를 발견할 때마다 기존 집합과 새 집합의 합집합을 만든다.

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

*로케일에 관해 빠르게 복습해보자. 각 로케일은 언어 코드(영어인 경우 en)와 국가 코드(미국인 경우 US)를 포함한다. 로케일 en_US는 미국에서 사용하는 영어를 말하며, en_IE는 아일랜드에서 사용하는 영어를 말한다. 몇몇 국가에는 여러 로케일이 있다. 예를 들어, ga_IE는 아일랜드에서 사용하는 게일어다. 또한 앞의 예에서 볼 수 있듯이 저자의 JVM은 세가지 로케일을 파악하고 있다.* 

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
Predicate는 boolean 값을 리턴하는 넌디폴트 메서드 한 개를 포함하는 인터페이스다.

```
public interface Predicate {
  boolean test(T argument);
}
```

실전에서는 보통 람다 표현식이나 메서드 레퍼런스를 전달하기 때문에 메서드의 이름은 실제로 문제가 되지 않는다.  

*Stream.filter의 선언부를 자세히 살펴보면 와일드카드 타입 Predicate<? super T>를 주목하게 될 것이다. 흔히 함수 파라미터로 이와 같은 타입을 사용한다. 예를 들어 Employee는 Person의 서브클래스이고, Stream<Employee>가 있다고 하자. 이 경우 Predicate<Employee>, Predicate<Person> 또는 Preicate<Object>로 스트림을 필터링할 수 있다(여기서 T는 Employee). 이와 같은 유연성은 메서드 레퍼런스를 전달할 때 특히 중요한다. 예를들어, Stream<Employee>를 필터링 하는 데 Person::isAlive를 사용하려 한다고 하자. 이 작업은 순전히 filter 메서드의 파라미터에 있는 와일드카드 덕분에 동작한다.*

다음표는 Stream과 Collectors에 속한 메서드들의 파라미터로 나타나는 함수형 인터페이스를 요약해서 보여준다.  
스트림 API에서 사용하는 함수형 인터페이스

함수형 인터페이스 | 파라마터 타입 | 리턴 타입 | 설명
------------|------------|------------|------------
Supplier<T> | 없음 | T | T 타입 값을 공급한다.
Consumer<T> | T | void | T 타입 값을 소비한다.
BiConsumer<T, U> | T, U | void | T와 U 타입 값을 소비한다.
Predicate<T> | T | boolean | boolean 값을 리턴한다.
ToIntFunction<T>, ToLongFunction<T>, ToDoubleFunction<T> | T | int, long, double | T 타입을 인자로 받고 각각 int, long, double 값을 리턴하는 함수
IntFunction<R>, LongFunction<R>, DoubleFunction<R> | int, long, double | R | 각각 int, long, double을 인자로 받고 R 타입을 리턴한다.
Function<T, R> | T | R | T 타입을 인자로 받고 R 타입을 리턴
BiFuction<T, U, R> | T, U | R | T와 U 타입을 낮로 받고 R을 리턴
UnaryOperator<T> | T | T | T 타입에 적용되는 단항 연산자
BinaryOperator<T> | T, T | T | T 타입에 적용되는 이항 연산자

## 3장 람다를 이용한 프로그래밍
* 핵심 내용
  * 란다 표현식을 사용하는 주 이유는 적절한 시점까지 코드의 실행을 지연하기 위한 것
  * 람다 표현식을 실행할 때 필요한 모든 데이터를 입력으로 제공
  * 가능하면 기존 함수형 인터페이스 중 하나를 선택
  * 종종 함수형 인터페이스의 인스턴스를 리턴하는 메서드를 작성하는 것이 유용
  * 변환들을 다룰 때는 각 변환을 어떻게 합성할지 고려
  * 변환들을 지연해서 합성하려면, 모든 지연 변환의 목록을 관리하고 마지막에는 변환들을 적용해야 한다.
  * 람다를 여러 번 적용해야 하는 경우, 종종 해당 작업을 동시에 실행하는 하위 작업들로 분리할 기회가 있다.
  * 예외를 던지는 람다 표현식을 다룰 때는 무슨 일이 일어나야 하는지 생각해야 한다.
  * 제니릭 함수형 인터페이스를 다룰 때 인자 타입에는 ? super 와일드카드를 사용하고, 리턴 타입에는 ? extends 와일드카드를 사용한다.
  * 함수를 통해 변환할 수 있는 제네릭 타입을 다룰 때는 map과 flapMap 메서드를 제공하는 방안을 고려해 본다.
  
### 지연 실행
코드를 나중에 실행하려는 이유
* 별도의 스레드에서 코드 실행
* 코드를 여러번 실행
* 알고리즘에서 코드를 적절한 시점에 실행(에를 들면, 정렬에서 비교 연산)
* 어떤 일이 발생했을 때 코드 실행(버튼 클릭, 데이터 도착)
* 필요할 때만 코드 실행
이벤트를 로그로 남길 때 

```
logger.info("x: " + x + ", y: " + y);
// 필요할 때만 문자열 연산 발생
() -> "x: " + x + ", y: " + y
// 1. 람다를 받는다.
// 2. 람다를 호출해야 하는지 검사
// 3. 필요할 때 람다를 호출
public static void info(Logger logger, Supplier<String> message) {
  if (logger.isLoggable(Level.INFO)) {
    logger.info(message.get());
  }
}
```

### 람다 표현식의 파라미터
사용자에게 비교자를 제공하도록 요구할 경우에는 해당 비교자에서 인자 두개를 받는다는 사실이 분명하다.

```
Arrays.sort(name, (s, t) -> Integer.compare(s.lenth(), t.length()));

// 다른 예제
public static void repeat(int n, IntConsumer action) {
  for (int i = 0; i < n; i++) {
    action.accept(i);
  }
}

repeat(10, i -> System.out.println("Countdown: " + (9 - i)));
button.setOnAction(event -> 액션);
```
왜 Runnable이 아니고 IntConsumer일까? 여기서는 몇번째 반복에서 실행하고 있는지를 액션에 알려주는데, 이는 유용한 정보가 될 수 있다. 액션은 파라미터로 들어온 입력을 캡처해야 한다.  
인자가 필요없다면 사용자에게 불필요한 인자들을 받도록 강제하지 않는 방안을 고려한다.

```
public static void repeat(int n, Runnable action) {
  for (int i = 0; i < n; i++) {
    action.run();
  }
}

repeat(10, () -> System.out.println("Hello, World!"));
```
### 함수형 인터페이스 선택
대부분의 함수형 프로그래밍 언어에서 함수 타입은 구조적이다. 문자열 두개를 정수로 맵핑하는 함수를 명시하려면 Function2<String, String, Integer> 또는 (String, String) -> int 형태의 타입을 사용한다. 자바에서는 이 대신 Comparator<String> 같은 함수형 인터페이스를 사용해 함수의 의도를 선언한다. 프로그래밍 언어 이론에서는 이를 명목적 타이핑이라고 한다.  
공통 함수형 인터페이스  

함수형 인터페이스 | 파라미터 타입 | 리턴 타입 | 추상 메서드 이름 | 설명 | 다른 메서드
------------|------------|------------|------------|------------|------------
Runnable | 없음 | void | run | 인자와 리턴 값 없이 액션을 실행한다 | 
Supplier<T> | 없음 | T | get | T 타입 값을 공급한다 | 
Consumer<T> | T | void | accept | T 타입 값을 소비한다 | chain
BiConsumer<T, U> | T, U | void | accept | T와 U 타입값을 소비 | chain
Function<T, R> | T | R | apply | T 타입 인자를 받고 R 타입을 리턴 | compose, andThen, identity
BiFunction<T, U, R> | T, U | R | apply | T와 U 타입 인자를 받고 R 타입을 리턴 | andThen
UnaryOperator<T> | T | T | apply | T 타입을 대상으로 동작하는 단항 연산자 | compose, andThen, identity
BinaryOperator<T> | T, T | T | apply | T 타입을 대상으로 동작하는 이항 연산자 | andThen
Predicate<T> | T | boolean | test | boolean 값을 리턴 | and, or, negate, isEqual
BiPredicate<T, U> | T, U | boolean | test | 인자 두개를 받고 boolean 값을 리턴 | ant, or, negate

예를 들어, 특정 기준을 만족하는 파일을 처리하는 메서드를 작상한다고 하자. 이때 서술적인 java.io.FileFilte 클래스를 사용해야 하는가, 아니면 Prediate<File>을 사용해야 하는가? 둘 중에 Predicate<File>의 사용을 강력히 추천한다. 아마도 FileFilter 인스턴스를 생선하는 수많은 유용한 메서드를 이미 갖추고 있을 경우에나 Predicate<File>을 사용하지 않을 것이다.

*대부분의 표준 함수형 인터페이스는 함수를 생산하거나 결합하는 비추상 메서드를 포함한다. 예를 들어 `Prediate.isEqual(a)`는 a가 null이 아닌 경우 `a::equals`와 같다. 또한 Predicate들을 결합하는데 사용하는 디폴트 메서드인 and, or, negate가 있다. 예를들어, `Predicate.isEqual(a).or.Predicate.isEqual(b))`는 `x -> a.equals(x) || b.equals(x)`와 같다.*

각 픽셀에 Color -> Color 함수를 적용해서 이미지를 변환하려 한다고 하자. `Image brightenedImage = transform(image, Color::brighter);`  
이 용도로 사용할 수 있는 UnaryOperator<Color> 라는 표준 인터페이스가 있다.

```
// 이 메서드는 java.awt가 아닌 JavaFX의 color와 Image 클래스를 사용한다.
public static Image transform(Image in, UnaryOperator<Color> f) {
  int width = (int) in.getWidth();
  int height = (int) in.getHeight();
  WritableImage out = new WritableImage(width, height);
  for (int x = 0; x < width; x++) {
    for (int y = 0; y < height; y++) {
      out.getPixelWriter().setColor(x, y, f.apply(in.getPixelReader().getColor(x, y)));
    }
  }
  return out;
}
```
다음 표는 기본 타입인 int, long, double 용으로 이용할 수 있는 34가지 특화 버전 목록을 보여준다. 오토박싱을 줄일 수 있도록 가능하면 특화 버전을 사용한다.  
기본 타입용 함수형 인터페이스 : *p, q*는 int, long, double을 *P, Q*는 Int, Long, Double을 나타냄  

함수형 인터페이스 | 파라미터 타입 | 리턴 타입 | 추상 메서드 이름
------------|------------|------------|------------
BooleanSupplier | 없음 | boolean | getAsBoolean
*P*Supplier | 없음 | *p* | getAs*P*
*P*Consumer | *p* | void | accept
Obj*P*Consumer<T> | T, *p* | void | accept
*P*Function<T> | *p* | T | apply
*P*To*Q*Function | *p* | *q* | applyAs*Q*
To*P*Function<T> | T | *p* | applyAs*P*
To*P*BiFunction<T, U> | T, U | *p* | applyAs*P*
*P*UnaryOperator | *p* | *p* | applyAs*P*
*P*BinaryOperator< | *p, p* | *p* | applyAs*P*
*P*Predicate | *p* | boolean | test

때로는 표준 라이브러리에 원하는 인터페이스가 없어서 자시남ㄴ의 함수형 인터ㅔㅍ이스를 제공해야 할 수도 있다. 사용자가 이미지의 (x, y) 위치에 따라 새로운 색상을 계산하는 함수인 `(int, int, Color) -> Color`를 제공하게 하는 방법으로 이미지의 색상을 수정하고 샆디고 하자. 이 경우 다음과 같이 자신만의 인터페이스를 정의할 수 있다.

```
@FunctionalInterface
public interface ColorTransformer {
  Color apply(int x, int y, Color colorAtXY);
}
```

### 함수 리턴
함수형 프로그래밍 언어에서는 함수가 일차 구성원이다. 인자와 리턴 값이 함수일 수 있다. 자바는 함수형 인터페이스를 사용하기 때문에 함수형 언어와는 거리가 있지만 원칙은 같다. 리턴 타입이 함수형 인터페이스인 메서드를 알아보자.  
다시 이미지 변환을 고려. 다음 코드를 호출하면 이미지가 고정량만큼 밝아진다.

```
Image brighternedImage = transform(image, Color::brighter);

// 이미지를 더 밝게 하거나 너무 밝지 않게 하고 싶은 경우 밝기를 추가 파라미터로 제공
Image brightenedImage = transform(image, (c, factor) -> c.deriveColor(0, 1, factor, 1), 1.2);
// 이 경우 transform을 오버로드
public static <T> Image transform(Image in, BiFunction<Color, T> f, T arg)

// 인자 두개를 전달하고 싶은 경우, 밝기를 설정하고 적절한 UnaryOperator<Color>를 리턴하는 메서드를 생성
public static UnaryOperator<Color> brighten(double factor) {
  return c -> c.deriveColor(0, 1, factor, 1);
}
Image brightenedImage = transform(image, brighten(1.2));
```

### 합성
변환이 두가지 있다면

```
Image image = new Image("eiffel-tower.jpg");
Image image2 = transform(image, Color::brighter);
Image finalImage = transform(image2, Color::grayscale);
```

중간 이미지를 만들어야 하므로 효율적이지 않다. 연산들을 합성햐서 적용하면 좋을 것.  
위 경우 연산이 UnaryOperation<Color>의 인스턴스이다. UnaryOperator 타입은 compose 메서드를 포함하고 있는데 여기서는 유용하지 않다. 여기서는 자체저으로 만든다.

```
public static <T> UnaryOperator<T> compose(UnaryOperator<T> op1, UnaryOperator<T> op2) {
  return t -> op2.apply(op1.apply(t));
}

Image finalImage = transform(image, compose(Color::brighter, Color::grayscale));
```

### 지연
지연 처리를 할 때는 API에서 수행할 작업을 쌓아두는 중간 연산과 결과를 주는 최종연산을 구별해야 한다. 이미지 처리 예제에서는 transform이 지연 처리를 하도록 만들 수 있다. 이경우에 transform에서 Image가 아닌 다른 객체를 리턴해야 한다.

```
LatentImage latent = transform(image, Color::brighter);

public LatentImage {
  private Image in;
  private List<UnaryOperator<Color> pendingOperations;
  ...
  
  LatentImage transform(UnaryOperator<Color> f) {
    pendingOperations.add(f);
    return this;
  }
  
  // LatentImage 생성자는 정적 팩토리 메서드를 제공한다.
  LatentImage latent = LatentImage.from(image).transform(Color::brighter).transform(Color::grayscale).toImage();
  
  public Image toImage() {
    int width = (int) in.getWidt();
    int height = (int) in.getHeight();
    WritableImage out = new WritableImage(width, height);
    for (int x = 0; i x < width; x++) {
      for (int y = 0; y < height; y++) {
        Color c = in.getPixelReader().getColor(x, y);
        for (UnaryOperator<Color> f : pendingOperations) {
          c = f.apply(c);
        }
        out.pixelWriter().setColor(x, y, c);
      }
    }
    
    return out;
  }
}
```

### 연산 병렬화
이미지 변환을 병렬로 처리.

```
// JavaFX의 PixelWriter가 스레드에 안전하지 않기 때문에 Image 객체 대신 Color[][] 배열을 대상으로 처리
public static Color[][] parallelTransform(Color[][] in, UnaryOperor<Color> f) {
  int n = Rntime.getRuntime().availableProcessors();
  int height = in.length;
  int width = in[0].length;
  Color[][] out = new Color[height][width];
  
  try {
    ExecutorService pool = Executors.newCachedThreadPool();
    for (int i = 0; i < n; i++) {
      int fromY = i * height / n;
      int toY = (i + 1) * height / n;
      pool.submit(() -> {
        for (int x = 0; x < width; x++) {
          for (int y = fromY; y < toY; y++) {
            out[y][x] = f.apply(in[y][x]);
          }
        }
      });
    }
    pool.shutdown();
    pool.awaitTermination(1, TimeUnit.HOURS);
  } catch (InterruptedException ex) {
    ex.printStackTrace();
  }
}
```

### 예외 다루기
람다를 받는 메서드를 작성할 때는 람다 표현식을 실행할 때 발생할 수 있는 예외를 처리할고 보고할 방법을 생각해야 한다.  
보통은 해당 표현식이 호출자에 예외를 전파하도록 두는게 적절하다.

```
public static void doInOrder(Runnable first, Runnable second) {
  first.run();
  second.run();
}
```

first.run()이 예외를 던지면 doInOrder 메서드는 종료되고 second는 아예 실행되지 않으며 호출자는 해당 예외를 다룬다.  
비동기로 실행한다고 하자

```
public static void doInOrderAsync(Runnable first, Runnable second) {
  Thread t = new Thread() {
    public void run() {
      first.run();
      second.run();
    }
  };
  t.start();
}
```

first.run()이 예외를 던지면, 스레드가 종료되고 second는 아예 실행되지 않는다. 하지만 doInOrderAsync는 바로 리턴하고 별도의 스레드에서 작업을 수행하기 때문에 이 메서드에서 예외를 다시 던지는 일은 불가능하다. 이와 같은 상황에서는 처리기(handler)를 전달하는 것이 좋다.

```
public static void doInOrderAsync(Runnable first, Runnable second, Consumer<Throwable> handler) {
  Thread t = new Thread() {
    public void run() {
      try {
        first.run();
        second.run();
      } catch (Throwable t) {
        handler.accept(t);
      }
    }
  }
  t.start();
}

// 이제 first가 second에서 소비하는 결과를 생산한다고 하자. 이때도 여전히 처리기를 사용할 수 있다.
public static <T> void doInOrderAsync(SupplierT> first, Consumer<T> second, Consumer<Throwable> handler) {
  Thread t = new Thread() {
    public void run() {
      try {
        T result = first.get();
      } catch (Throwable t) {
        handler.accept(t);
      }
    }
  };
  t.start();
}
// second를 BiConsumer<T, Throwable>로 만들고 first에서 발생한 예외를 다루게 할 수도 있다.
```

종종 함수형 인터페이스의 메서드ㅓ가 검사 예외를 허용하지 않는 것이 불편할 때가 있다. 물론 메서드에서 Supplier<T> 대신 Callable<T> 같은 검사 예외를 허용하는 함수형 인터페이스를 받을 수 있다. Consumer나 Function에 적용할 수 있는 버전이 필료하다면 직접 만들어야 한다.  
때로는 이 문제를 다음과 같이 제네릭 래퍼를 이용해 해결하라는 제안을 보게 된다.

```
public static <T> Suppliter<T> unchecked(Callable<T> f) {
  return () -> {
    try {
      return f.call();
    } catch (Throwable t) {
      throw t;
    }
  }
}
//이렇게 하면 readAllBytes 메서드가 IOException을 던지는데도 불구하고 Supplier<String>에 다음 코드를 전달할 수 있다.
unchecked(() -> new String(Files.readAllBytes(Paths.get("/etc/passwd")), StandardCharsets.UTF_8));
```

### 람다와 제네릭
타입 소거의 결과는 실행시간에 제네릭 배열을 생성할 수 없다.  
배열을 받는 두번째 메서드를 제공하는 방법으로 문제를 해결했다. 예를 들어 Collection<T>는 toArray(T[] a) 메서드를 포함한다. 이제 람다 덕분에 생성자를 전달하는 새로운 옵션이 생겼다. 스트림을 이용할 때 이 방법을 사용한다. 

```
String[] result = words.toArray(String[]::new);
```

이와 같은 메서드를 구현할 때 생성자 표현식은 IntFunction<T[]>이다. 생성자에 배열의 크기가 전달되기 때문이다. 메서드 구현부에서는 `T[] result = constr.apply(n)`을 호출한다.

Employee가 Person의 서브타입이라고 하자. List<Employee>는 List<Person>의 특수한 경우일까? 마치 그래야 할 것처럼 보인다. 하지만 실제로는 말이 되지 않는다. 다음 코드를 보자.

```
List<Employee> staff = ...
List<Person> tenants = staff;   // 규칙에 어긋나지만 일단은 맞는 것으로
tenants.add(new Person("John Q. Public"));  // Person을 staff에 추가한다.
```

여기서 staff와 tenants는 같은 리스트를 가리키는 레퍼런스임을 유의하기 바란다. 이런 오류가 발생하지 않게 하려면 List<Employee>에서 List<Person>으로의 변환을 금지해야 한다. 이때 List<T>의 타입 파라미터 T를 불변이라고 말한다.  
함수형 프로그래밍 언어에서처럼 List가 수정 불가인 경우 이 문제가 사라지고 공변(covariant) 리스트를 갖게 될 것이다. 스칼라 같은 언어에서 바로 이와 같이 동작한다. 하지만 자바는 제네릭을 만들 당시 극히 소수의 수정 불가 제네릭 클래스를 갖추고 있었기 때문에, 언어 설계자들은 대신 사용처 가변성(use-site variance), 다른 말로 와일드카드라는 다른 개념을 도입했다.

메서드가 리스트에서 읽기만 한다면 List<? extends Person>을 받도록 할 수 있다. 이 경우 List<Person>과 List<Employee> 모두를 전달 할 수 있다. 반대로 메서드가 리스트에 쓰기만 한다면 List<? super Employee>를 받을 수 있다. List<Person>안에 Employee를 써넣는 일은 문제가 없기 때문에 이러한 리스트를 전달할 수 있다. 일반적으로 읽기는 공변(covaria
nt:서브타입도 가능)이고, 쓰기는 역변(contravariant:슈퍼타입도 사용 가능)이다. 사용처 가변성은 수정 가능한 자료 구조에 딱 들어맞는다. 사용처 가변성은 각 서비스에서(가변성이 존재하는 경우) 어떤 가변성이 적합한지 고를 수 있게 해준다. 하지만 함수 타입에서는 사용처 가변성이 성가신 문제가 된다. 함수 타입에서는 항상 인자는 역변이고 리턴 값은 공변이다. 예를 들면 Function<Employee, Person>을 요구하는 곳에 Function<Person, Employee>를 안전하게 전달할 수 있다. 이 함수를 전달받는 쪽에서는 Employee로만 호출하겠지만 해당 함수에서는 어떤 Person이든 다룰 수 있다. 또한, 전달받는 쪽에서는 함수에서 Person을 리턴할 것이라 기대할 것이고, 해당 함수는 오히려 더 적합한 Person(여기서는 Employee)을 전달할 수 있다.

자바에서는 제네릭 함수형 인터페이스를 선언할 때 함수 인자가 항상 역변이고 리턴 타입이 항상 공변이라고 명시할 수 없다. 따라서 각 사용별로 명시해야 한다. 예를 들어, Stream<T>의 javadoc을 보면

```
void forEach(Consumer<? super T> action)
Stream<T> filter(Perdicate<? super T> predicate)
<R> Stream<R> map(Function<? super T, ? extends R> mapper)
```

일반적인 규칙은 인자 타입에는 super를 사용하고 리턴 타입에는 extends를 사용하는 것이다. 이 방식으로 Stream<String>의 forEach에 Consumer<Object>를 전달할 수 있다. Consumer가 어떤 객체든 기꺼이 소비하려고 한다면, 당연히 문자열도 소비할 수 있다.

하지만 항상 와일드카드를 사용할 수 있는 것은 아니다. 다음 선언을 보자

```
T reduce(T identity, BinaryOperator<T> accumulator)
```

여기서 T는 BinaryOperator의 인자인 동시에 리턴 타입이므로 타입이 변하지 않는다. 실제 역변성과 공변성은 서로 상쇄된다.

제네릭 타입과 함께 람다 표현식을 받는 메서드의 구현자 입장에서는 단순히 리턴 타입으로 사용되지 않는 모든 인자 타입에 ? super를 추가하고, 인자 타입으로 사용되지 않는 모든 리턴 타입에 ? extends를 추가하면 된다.  
doInOrderAsync 메서드는 다음과 같이 선언해야 한다.

```
public static <T> void doInOrderAsync(Supplier<T> first, Consumer<T> second, Consumer<Throwable> handler)
// =>
public static <T> void doInOrderAsync(Supplier<? extends T> first, Consumer<? super T> second, Consumer<? super Throwable> handler)
```

### 모나드 연산
제네릭 타입과 이들 타입으로부터 값을 돌려주는 함수를 다룰 때는 해당 함수들을 합셩할 수 있게 해주는 메서드를 제공하면 유용하다.  
List<T>(T 타입 값이 0개 이상), Optional<T>(T 타입 값이 0개 또는 1개), Future<T>(추후에 이용할 수 있는 T 타입 값)와 같이 타입 파라미터가 하나인 제네릭 타입 G<T>를 고려해 보자. 또한 T -> U 함수(또는 Function<T, U> 객체)가 있다고 하자.  
종종 이 함수를 G<T>(즉, List<T>, Optional<T>, Future<T> 등)에 적용하면 좋은 상황이 있다. 이때 어떻게 동작하는지는 정확히 제네릭 타입 G의 본성에 의존한다.

*v*를 답고 있는 Optional<T>에 *f*를 적용하는 일은 *f(v)*를 담은 Optional<U>를 생성함을 의미한다. 하지만 *f*를 값 없이 비어 있는 Optional<T>에 적용하면 결과는 비어 있는 Optional<U>가 된다.  
*f*를 Future<T>에 적용하는 일은 단순히 T 타입 값을 이용할 수 있을 때 함수를 적용함을 의미한다. 이때 결과는 Future<U>가 된다.  
전통에 따라 이 연산을 일반적으로 map이라고 한다. Stream과 Optional에는 map 메서드가 있다. 6장에서 설명할 CompletableFuture 클래스는 map이 해야 하는 일이 수행하는 연산을 제공하지만 thenApply라고 부른다. 일반 Future<V>에는 map이 없지만, 직접 만드는 것도 어렵지는 않다.

지금까지는 상당히 직관적인 면을 살펴봤다. 하지만 T -> U 대신 T -> G<U> 함수를 고려하면 상황이 더 복잡해진다. 예를 들면, URL에 해당하는 웹 페이지를 얻어오는 경우를 보자. 페이지를 가져오는 데는 어느 정도 시간이 걸리기 때문에 URL -> Future<String> 함수다. 이제 Future<URL>(얼마 후 도착할 URL)이 있다고 하자. 분명 앞의 함수를 이 Future에 맵핑할 수 있다. 이 경우 URL이 도착하기를 기다린 후, 도착한 URL을 함수에 전달하고 문자열이 도착하기를 기다린다. 이 연산을 전통적으로 flapMap이라고 부른다.

Optional<T> 클래스는 flatMap도 제공한다. T -> Optional<U> 함수가 있을 때 flatMap은 (소스 또는 대상 옵션 값이 없을 때를 제외하고) Optional에 들어 있는 값을 꺼내서 함수를 적용한다. Optional<T>의 flatMap은 집합 기반 flatMap이 크기가 0 또는 1인 집합을 대상으로 하는 것과 완전히 같은 작업을 수행한다.

일반적으로 타입 G<T>와 함수 T -> U를 설계할 때는 G<U>를 결과로 주는 map을 정의할 필요가 있는지 생각해봐야 한다. 그 다음 T -> G<U> 함수로 일반화하고 필요하면 flatMap을 제공한다.

*이들 연산은 모나드 이론에서 중요하다. 하지만 모다드 이론을 알아야만 map과 flapMap을 이해할 수 있는 것은 아니다. 함수를 맵핑하는 개념은 직관적이고도 유용하며, 이 절의 핵심은 독자들이 이 개념을 인식하게 만드는 것이다.*

## 4장 JavaFX
1. 자바 GUI 프로그래밍의 간략한 역사
2. Hello, JavaFX!
3. 이벤트 처리
4. JavaFX 프로퍼티
5. 바인딩
6. 레이아웃
7. FXML
8. CSS
9. 애니메이션과 특수 효과
10. 화려한 컨트롤

* 핵심내용
  * 씬그래프(scene graph)는 다른 노드를 포함할 수도 있는 노드들로 구성된다.
  * 씬(scene)은 스테이지, 즉 최상위 윈도우, 애플릿 서피스 또는 전체 화면 위에 표시된다.
  * 버튼 같은 일부 컨트롤은 이벤트를 발생시키지만, 대부분의 JavaFX 이벤트는 프로퍼트 변경으로 발생한다.
  * JavaFX   프로퍼티는 변경 및 무효화 이벤트를 발생시킨다.
  * 프로퍼티를 또 다른 프로퍼티에 바인드하면 다른 프로퍼티가 변경될 때 해당 프로퍼티도 업데이트된다.
  * JavaFX는 스윙의 레이아웃 관리자와 유사하게 동작하는 레이아웃 페인을 사용한다.
  * FXML 마크업 언어을 이용해 레이아웃을 지정할 수 있다.
  * 애플리케이션의 외관을 변경하는데 CSS를 이용할 수 있다.
  * 애니메이션과 특수 효과를 구현하기 쉽다.
  * JavaFX는 자체적으로 차트, 내장 웹킷 브라우저, 미디어 플레이어 같은 몇 가지 고급 컨트롤을 제공한다.

### 자바 GUI 프로그래밍의 간략한 역사
AWT(Abstract Windows Toolkit) : 크로스 플랫폼 지원이라는 특징이 있지만 각 운영체제에서 사용자 인터페이스 위젯의 기능에 미묘한 차이점이 존재하여 **한 번 작성하고 어디에서나 실행** 한다는 생각이 결국은 **여러번 작성하고 모든 곳에서 디버그** 하는 결과를 낳음.  
Swing : 네이티브 위젯을 사용하지 않고 자체적으로 그리는 것. 이 방식으로 사용자 인터페이스는 모든 플랫폼에서 룩앤필을 유지할 수 있게 됨. 원한다면 플랫폼 고유의 룩앤필을 요청할 수도 있음. 하지만 느렸고, 컴퓨터가 빨라지자 UI가 볼품없다고 사용자들은 불평. 그 결과로 플래시를 사용게 됨.  
JavaFX : 2007년에 플래시의 경쟁 기술로 소개되었지만 자체 언어를 익혀야 하므로 개발자들이 기피함. 2011년에 자바 API로 된 새로운 버전의 JavaFX 2.0 발표하였고 자바 7 업데이터 6부터는 JDK와 JRE에 JavaFX 2.2가 번들됨. 자바8부터는 JavaFX 8로 자바와 버전을 맞춤.

### Hello, JavaFX !
메시지를 보여주는 간단한 프료그램.

```
Label message = new Label("Hello, JavaFX !");
message.setFont(new Font(100));   // 폰트 크기 증가
```

*눈에 거슬리는 접두어 J가 없다. 스윙에서는 레이블 컨트롤의 이름이 AWT Label과 구분하기 위해 JLabel이다.*

JavaFX에서는 보여주고 싶은 모든 것을 씬(Scene)에 넣는다. 씬에서 '액터' 즉 컨트롤과 도형을 장식하고 애니메이션을 줄 수 있다. 그리고, 이 씬은 반드시 스테이비(Stage)안에 있어야 한다. 스테이비는 프로글매을 데스크톱에서 실행하는 경우 최상위 윈도우를, 애플릿에서 실행하는 경우에는 사각 영역을 의미한다. 스테이지는 Application 클래스의 서브클래스에서 반드시 오버라이드를 해야하는 start 메서드의 파라미터로 전달된다.

```
public class HelloWorld extends Application {

    @Override
    public void start(Stage stage) {
      Label message = new Label("Hello JavaFX !");
      message.setFont(new Font(100));

      stage.setScene(new Scene(message));
      stage.setTitle("Hello");
      stage.show();
    }
}
```

*이 예제에서 볼 수 있듯이 JavaFX 애플리케이션을 실행하는데는 main 메서드가 필요하지 않다. 이전 JavaFX 버전에서는 다음과 같은 형태의 main 메서드를 포함해야 했다.*

```
public class MyApp extends Application {
  public static void main(String[] args) {
    launch(args);
  }
}
```

#### 이벤트 처리
스윙에서는 버튼 클릭시에 통지를 받을 수 있도록 이벤트 처리기를 버튼에 추가한다. 람다 표현식은 이 작업을 아주 간단하게 만들어준다.

```
Button red = new Button("Red");
red.setOnAction(event -> message.setTextFill(Color.RED));
```

하지만 대부분의 JavaFX 컨트롤에서는 이벤트 처리가 이와 다르다. JavaFX는 프로퍼티의 값이 변할 때 이벤트를 내보낸다.

```
slider.valueProperty().addListener(property -> message.setFont(new Font(slider.getValue())));
```

JavaFX에서는 프로퍼티의 이벤트를 수신하는 일이 아주 흔하다. 예를 들어, 사용자가 텍스트 필드에 텍스트를 입력할 때 사용자 인터페이스의 일부를 변경하려는 경우 text 프로퍼티에 리스너를 추가한다.

*버튼은 특수한 경우다. 버튼 클릭은 버튼의 프라퍼티를 변경하지 않는다.*

#### JavaFX 프로퍼티
프로퍼티는 읽거나 쓸 수 있는 클래스의 속성이다. 필드라고도 한다. 필드에 게터와 세터가 있으면 프라퍼티가 된다. 대입의 오른쪽에 사용하면 게터를 호출하고 왼쪽에 사용하면 세터를 호출한다. 아쉽게도 자바에는 이러한 문법이 없다.  
하지만, 자바 1.1부터는 관례를 이용한 프로퍼티를 지원한다. 자바빈즈명세에서는 게터/세터 쌍으로부터 프로퍼티를 추정해야 한다고 설명하고 있다. 예를 들어, `String getText()`와 `void setText(String newValue)` 메서드를 포함하는 클래스는 text 프로퍼티가 있는 것으로 간주한다. java.beans 패키지의 Introspector와 BeanInfo 클래스를 이ㅛㅇ하면 클래스의 모든 프포퍼티를 나열 할 수 있다.

자바빈즈 명세는 또한 객체에서 세터가 호출되었때 프로퍼티 변경 이벤트를 내보내는 바운드 프로퍼티를 정의하고 있다. JavaFX는 이 부분은 사용하지 안는다. 대신 JavaFX 프로퍼티는 게터와 세터 외에, Property 인터페이스를 구현하는 객체를 리턴하는 세번째 메서드를 포함한다. 예를 들어, JavaFX text 프로퍼티는 `Property<String> textProperty()`라는 메서드를 포함한다. 프로퍼티 객체에 리스너를 추가할 수 있다. 이점이 전통적인 자바빈즈와 다르다. JavaFX는 Bean이 아니라 Property 객체가 통지를 보낸다. 이러한 변화에는 마땅한 이유가 있다. 바운드 자바빈즈 프로퍼티를 구현하려면 리스너를 추가 및 삭제하고, 리스너에 이벤트를 보내는 상투적인 코드가 필요하다. JavaFX에서는 이 작업을 모두 해주는 라이브러리 클래스들이 존재하기 때문에 훨씬 간편하다.

Greeting 클래스에 text 프로퍼티를 구현하는 방법을 살펴보자.

```
public class Greeting {
  private StringProperty text = new SimpleStringProperty("");
  public final StringProperty textProperty() { return text; }
  public final void setText(String newValue) { text.set(nweValue); }
  public final String getText() { return text.get(); }
}
```

StringProeprty 클래스는 문자열을 감싼다. 이 클래스는 감싸고 있는 값을 얻고 설정하는 메서드와 리스너를 관리하는 메서드를 포함한다. 여기서 볼 수 있듯이 JavaFX 프로퍼티를 구현할 때도 몇몇 상투적인 코드가 필요하며, 유감스럽게도 자바에서 해당 코드를 자동으로 생성하는 방법은 없다. 하지만 적어도 리스너 관리에는 신경쓰지 않아도 된다.  
프로퍼티 게터와 세터를 final로 선언하는 것이 필수는 아니지만, JavaFX 설계자들은 이를 권항한다.

*이 패턴에서는 각 프로퍼티에 대해(누군가 주의를 기울리는지 여부와 상관없이) 프로퍼티 객체가 필요하다.*

앞의 예제에서는 StringProperty르 정의했다. 기본 타입 프로퍼티인 경우 IntegerProperty, LongProperty, DoubleProperty, FloatProperty 또는 BooleanProperty 중 하나를 사용한다. 이외에도 ListProperty, MapProperty, SetProperty 클래스가 있다. 나머지 타입에는 ObjectProperty<T>를 사용한다. 이들 모두 SimpleIntegerProeprty, SimpleObjectProperty<T> 같은 구체적인 서브클래스가 있는 추상클래스다.

*리스너 관리에만 관심이 있다면, 프로퍼티 메서드에서 ObjectProperty<T> 클래스나 심지어 Property<T> 인터페이스를 리턴할 수 있다. 프로퍼티를 이용한 계산에는 더 특화된 클래스가 유용하다.*

*프로퍼티 클래스는 get과 set 메서드 외에도 getValue와 setValue 메서드를 포함한다. StringProperty 클래스에서는 get이 getValue와 같고, set이 setValue와 같다. 하지만 기본타입인 경우에는 다르다. 예를 들어, IntegerProperty에서 getValue는 Integer를 get은 int를 리턴한다. 일반적으로 모든 타입의 프로퍼티를 다뤄야 하는 제네릭 ㅗ코드를 작성하지 않는 한 get과 set을 사용한다.*

프로퍼티에 붙일 수 잇는 리스너의 종류는 두가지다. ChangeListener느 ㄴ프로퍼티 값이 변경되었을때 통지를 받고, InvalidationListener는 값이 변경되었을 수 있을때 호출된다. 프로퍼티가 지연 평가된다면 두 경우에 차이가 발생한다. 몇몇 프로퍼티는 다른 프로퍼티로부터 계산되며, 이 계산은 필요할 때만 일어난다. ChangeListener 콜백은 기존 값과 새로운 값을 알려주므로, 먼저 새로운 값을 계산해야 함을 의미한다. InvalidationListener는 새로운 값을 계산하지 않으며, 값이 실제로 변경되지 않았을 때도 콜백을 받을 수 있음을 의미한다.

대부분의 상황에서는 이 차이가 중요하지 않다. 새로운 값을 콜백 파라미터로 얻는지, 프로퍼티로부터 얻는지는 크게 상관이 없다. 그리고 보통은 입력중 하나가 변경되었는데도 계산된 프로퍼티가 변화지 않은 경우를 걱정하지 않아도 된다.

> 숫자 프로퍼티에 ChangeListener 인터페이스를 사용하는 것은 약간 까다롭다. 다음과 같이 호출할 수도 있다.
> `slider.valueProperty().addListener((property, oldValue, newValue) -> message.setFont(new Font(newValue)));`
> 위 코드는 동작하지 않는다. DoubleProperty는 Property<Double>이 아니라 Property<Number>를 구현한다. 따라서 oldValue와 newValue의 타입은 Double이 아닌 Number가 되므로 다음과 같이 수동으로 언박싱 처리를 해줘야 한다.
> `slider.valueProperty().addListener((property, oldValue, newValue) -> message.setFont(new Font(newValue.doubleValue())));`

### 바인딩
JavaFX 프로퍼티의 존재이유는 **바인딩** 이라는 개념(다른 프로퍼티가 변경될 때 자동으로 특정 프로퍼티를 업데이터하는 것)이다. 한 프로퍼티를 다른 프로퍼티에 바인딩하면 이처럼 할 수 있다.  
`billing.textProperty().bind(shipping.textProperty());`  
내부적으로는 shipping의 text 프로퍼티에 billing의 text 프로퍼티를 설정하는 변경리스터가 추가된다. 다음과 같이 호출할 수도 있다. `billing.textProperty().bindBidirectional(shipping.textProperty());` 이 경우 두 프로퍼티 중 하나가 변경되면 다른 하나가 업데이터된다. 바인딩을 취소하려면 unbind 또는 unbindBidirectional을 호출한다.

바인딩 메커니즘은 사용자 인터페이스 프로그래밍에서 흔히 발생하는 문제를 해결한다. 예를 들어, 데이트 필드와 캘린더 픽커가 있다고 하자. 사용자가 캘린더에서 날짜를 선택하면, 모델의 데이트 프로퍼티는 물론 데이터 필드도 자동으로 업데이트되어야 한다.

물론 많은 경우 한 프로퍼티가 다른 프로퍼티에 의존하지만 관계가 더 복잡하다. 항상 씬의 한가운데에 원을 두려고 하면, 원의 centerX 프로퍼티는 씬의 width 프로퍼티의 1/2이 되어야 한다. 이렇게 하려면 계산 프로퍼티를 만들어내야 한다. Bindings 클래스는 이 목적에 사용할 수 있는 정적 메서드를 제공한다. 예를 들어, `Bindings.divide(scene.widthProperty(), 2)` 는 값이 씬 ㅓ닙의 1/2인 프로퍼티를 준다. 씬 너비가 변하면 이 프로퍼티도 변경된다. 남은 것은 계산 프로퍼티를 원의 centerX 프로퍼티에 바인드하는 일이다. `circle.centerXProperty().bind(Bindings.divide(scene.widthProperty(), 2));`

*앞의 호출 대신 `scene.widthProperty().divide(2)`를 호출할 수도 있다. 표현식이 더 복잡해지면 Bindings의 정적 메서드가 좀 더 읽기 쉽다. 정적 임포트를 하면 더 간결해 진다.*

게이지가 너무 작거나 크면 Smaller와 Larger 버튼을 비활성화하려고 하면

```
smaller.disableProperty().bind(Bindings.lessThanOrEqual(gauge.widthProperty(), 0));
larger.disableProperty().bind(Bindings.greaterThanOrEqual(gauge.widthProperty(), 100));
```

다음의 표는 Bindings 클래스가 제공하는 모든 연산자 목록을 보여준다. 이 연산자들의 인자 하나 또는 둘 다 Observable 인터페이스 또는 그 서브인터페이스 중 하나를 구현한다. Observable 인터페이스는 InvalidationListener를 추가하고 삭제하는 메서드를 제공한다. ObservableValue 인터페이스는 여기에 ChangeListener 관리와 getValue 메서드를 추가한다. ObservableValue의 서브인터페이스들은 값을 적절한 타입으로 얻을 수 있는 메서드를 제공한다. 예를 들어, ObservableStringValue의 get 메셔드는 String을, ObservableIntegerValue의 get 메서드는 int를 리턴한다. Bindings에서 제공하는 메서드의 리턴 타입은 Bindig 인터페이스의 서브인터페이스이며, Binding 자체도 Observable의 서브 인터페이스다. Binding은 자신이 의존하는 모든 프로퍼티에 관해 알고 있다. 실전에서는 이들 인터페이스에 대해 걱정하지 않아도 된다. 그저 프로퍼티를 결합하면 다른 프로퍼티에 바인드할 수 있는 무언가를 얻는다.

메서드 이름 | 인자
------------|------------|------------
add, subtract, multiply, divide, max, min | ObservableNumberValue, int, log, float, double 두개
megate | ObservableNumberValue 한개
greaterThan, greateerThanOrEqual, lessThan, lessThanOrEqual | ObservableNumberValue, int, long, float, double 두 개 또는 ObservableStringValue, String 두개
equal, notEqual | ObservalueObjectValue, ObservableNumberValue, int, long, float, double, Object 두 개
equalIgnoreCase, notEqualIgnoreCase | ObservableStringValue, String 두개
isEmpty, isNotEmpty | Observable(List|Map|Set|String) 한개
isNull, isNotNull | ObservableObjectValue 한개
length | ObservableStringValue 한개
size | Observable(List|Map|Set) 한개
and, or | ObservableBooleanValue 두개
not | ObservableBooleanValue 한 개
convert | 문자열 바인딩으로 변환되는 ObservableValue 한개
concat | toString 값이 연결될 일련의 객체. 객체 중에 값이 변하는 ObservableValue가 있으면 문자열 연결도 변한다.
format | 옵션인 로케일, MessageFormat 문자열. 포맷 대상이 되는 일련의 객체. 객체 중에 값이 변하는 ObservableValue가 있으면 포맷된 문자열도 변한다.
valueAt, (double|float|integer|long)ValueAt, stringValueAt | ObservableList와 인덱스, 또는ObservableMap과 키
create(Boolean|Double|Float|Integer|Long|Object|String)Binding | Calable과 의존목록
select, select(Boolean|Double|Float|Integer|Long|String) | Object 또는 ObservableValue 그리고 일련의 public 프로퍼티 이름. *obj.p1.p2.....pn* 프로퍼티를 돌려준다.
when | 조건 연산자 빌더를 돌려준다. 바인딩 when(*b*).then(*v1*).otherwise(*v2*)는 ObservableBooleanValue *b*가 true인지 아닌지에 따라 *v1* 또는 *v2*를 돌려준다. 여기서 *v1* 또는 *v2*는 일반 또는 옵저버블 값이 될 수 있다. 옵저버블 값이 변하면 조건 값이 다시 계산된다.

Bindings의 메서드를 이용해 계산 프로퍼티를 만드는 일은 상당히 거추장스러울 수 있다. 계산 바인딩을 더 쉽게 만들어내는 또 다른 접근법이 있다. 단순히 계산하고자 하는 표현식을 람다 안에 넣고, 의존 프로퍼티 목록을 제공하는 것이다. 의존 프로퍼티 중 하나가 변경되면 해당 람다가 다시 계산된다. 예를 들면, 다음과 같다.

```
larger.disableProperty.bind(
  createBooleanBinding(
    () -> gauge.getWidth() >= 100,    // 이 표현식이 계산된다.
    gauge.widthProperty()             // 이 프로퍼티가 변할때
  )
);
```

*JavaFX 스크립트 언어에서는 컴파일러가 바인딩 표현식을 분석해 자동으로 의존 프로퍼티들을 찾아낸다. 따라서 단순히 disable bind gauge.width >= 100 과 같이 선언하면 컴파일러가 gauge.width 프포퍼티에 리스너를 붙여준다. 물론 자바에서는 프로그래머가 이러한 정보를 제공해야 한다.*

### 레이아웃
디자인 도구를 이용하는 방법은 다국어 버전 프로그램의 경우 레이블의 길이가 달라질수 있다.  
Swing에서 처럼 프로그래밍을 통해 레이아웃을 만드는 방법.  
CSS같은 선언형 언어로 레이아웃을 지정하는 방법.  
JavaFX는 세가지 방법 모두 지원. [JavaFX SceneBuilder](http://www.oracle.com/technetwork/java/javase/downloads/javafxscenebuilder-1x-archive-2199384.html)

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
1. 명령행에서 Nashorn 실행
2. 자바에서 Nashorn 실행
3. 메서드 호출
4. 객체 생성
5. 문자열
6. 숫자
7. 배열 작업
8. 리스트와 맵
9. 람다
10. 자바 클래스 확장과 자바 인터페이스 구현
11. 예외
12. 쉘 스크립팅
13. Nashorn과 JavaFX

* 핵심 내용
  * Nashorn은 Rhino 자바스크립트 인터프리터의 후속 인터프리터로 훨씬 성능이 뛰어나고 자바스크립트 표준에도 충실하다.
  * Nashorn은 자바 API를 실험하기 좋은 환경이다.
  * jjs 인터프리터 또는 자바에서 스크립팅 API를 이용해 자바스크립트를 실행할 수 있다.
  * 가장 일반적인 패키지에 접근할 때는 미리 정의된 자바스크립트 객체를 사용하고, 어떤 패키지든 접근하려 할때는 Java.type 함수를 사용한다.
  * 자바스크립트와 자바 사이에서 문자열과 숫자 변환을 할 때 일어나는 복잡한 문제를 주의한다.
  * 자바스크립트는 자바빈즈 프로퍼트는 물론 자바 리스트와 맵을 다루는 편리한 문법을 제공한다.
  * 람다 표헌식 사용과 아주 유사한 방식으로 자바스크립트 함수를 자바 인터페이스로 변환할 수 있다.
  * 자바스크립트에서 자바 클래스를 확장하고 자바 인터페이스를 구현할 수 있지만 몇가지 제한이 있다.
  * Nashorn은 자바스크립트를 이용한 쉘 스크립트 작성을 잘 지원한다.
  * 자바스크립트로 JavaFX 프로그램을 작성할 수 있지만 기대만큼 통합이 잘 지원되지는 않는다.

### 명령행에서 Nashorn 실행
자바8은 jjs라는 명령행 도구를 함께 제공한다.

> $ jjjs
> jjs> 'Hello, World'
> Hello, World

리스프(Lisp), 스칼라(Scala) 등에서 '읽기-평가-출력' 루프 줄여서 REPL 이라고 부르는 것을 얻게 된다.

*자바스크립트에서는 문자열을 '...' 또는 "..." 형식으로 표현할 수 있다는 점을 상기하기 바란다. 이 장에서는 자바가 아니라 자바스크립트 코드임을 눈으로 확인할 수 있게 자바스크립트 문자열에는 작은 따옴표를 사용한다.*

다음과 같이 함수를 정의하고 호출할 수 있다.

```
jjs> function factorial(n) { return n <= 1 ? 1 : n * factorial(n - 1) }
function factorial(n) { return n <= 1 ? 1 : n * factorial(n - 1) }
jjs> factorial(10)
3628800
```

다음과 같이 자바 메서드를 호출할 수 있다.

```
jjs> var input = new java.util.Scanner(new java.net.URL('http://horstman.com').openStream())
jjs> input.useDelimiter('$')
java.util.Scanner[delimiters=$][position=0][match valid=false][need input=false][source closed=false][skipped=false][group separator=\,][decimal separator=\.][positive prefix=][negative prefix=\Q-\E][positive suffix=][negative suffix=][NaN string=\Q?\E][infinity string=\Q▒▒\E]
jjs> var contents = input.next()

// contents를 타이핑하면 웹 페이지 내용이 출력된다.
```

*자바스크립트 REPL이 스칼라 REPL 만큼은 신선하지 못한 두 가지 문제가 있다. 스칼라 REPL에는 명령 완성 기능이 있다. 따라서 탭 키를 누르면 현재 표현식에서 가능한 완성 목록을 얻을 수 있다. 사실 자바스크립트 같은 동적 타입 언어용으로 명령 완성을 만드는 일은 어려운 문제긴 하다. 좀 더 근본적인 누락 기능은 명령행 다시 불러오기다. ↑ 키를 누르면 이전 명령을 얻어올 수 있어야 한다. 이 기능이 작동하지 않으면 rlwrap을 설치하고 rlwrap jjs를 실행한다. 다른 방법으로 이맥스 안에서 jjs를 실행할 수도 있다. 이렇게 한다고 문제가 생기지 않으니 걱정하지 말자. 이맥스를 시작하고 M-x(즉, Alt+x 또는 Esc x) shell엔터를 누른 다음, jjs를 타이핑한다. 그런 다음 평소처럼 표현식을 입력하면 된다. 이전 또는 다음 행을 불러오려면 M-p와 M-n을 사용한다. 같은 행 안에서 움직이려면 왼쪽, 오른쪽 화살표 키를 사용한다. 마지막으로, 명령을 수정한 다음 엔터를 눌러서 실힝한다.*

### 자바에서 Nashorn 실행
또 다른 용도는 프로그램의 사용자가 스크립트를 실행할 수 있게 해주는 것이다. 자바에서 Nashorn 스크립트를 실행하는 데는 자바 6에서 등장한 스크립트 엔진 메커니즘을 사용한다. 그루비, JRuby 또는 Jython처럼 스크립트 엔진을 갖춘 모든 JVM 언어로 작성한 스크립트를 실행하는데 이 메커니즘을 사용할 수 있다. PHP나 스킴(Scheme)처럼 JVM 밖에서 실행하는 스크립트 엔진도 있다.

스크립트를 실행하려면 ScriptEngine 객체를 얻어야 한다. 엔진이 등록되어 있는 경우에는 단순히 이름으로 얻어올 수 있다. 자바8은 "nashorn"이라는 엔진을 포함한다. 다음은 이 엔진을 사용하는 방법이다.

```
ScriptEngineManager manager = new ScriptEngineManager();
ScriptEngine engine = manager.getEngineByName("nashorn");
Object result = engine.eval("'Hello, World!'.length");
System.out.println(result);
```

Reader로부터 스크립트를 읽어올 수도 있다.

```
Object result = engine.eval(Files.newBufferedReader(path));
```

자바 객체를 스크립트에서 이용할 수 있게 하려면 ScriptEngine 인터페이스의 put 메서드를 사용한다. 예를 들어, JavaFX 스테이지를 보이게 해서 자바스크립트 코드를 사용해 내용을 채울 수 있다.

```
public void start(Stage stage) {
  engine.put("stage", stage);
  engine.eval(script);    // 스크립트 코드에서 stage로 객체를 접근할 수 있다.
}
```

변수를 유효 범위에 두는 대신 Bindings 타입 객체에 모아 놓고, 이 객체를 eval 메서드에 전달할 수 있다.

```
Bindings scope = engine.createBindings();
scope.put("stage", stage);
engine.eval(script, scope);
```

이 방법은 일련의 바인딩이 이후의 eval 메서드 호출 시점에는 존재하지 않아야 하는 상황에 유용하다.

### 메서드 호출
스크립트 엔진이 자바 스크립트에서 자바 객체를 접근할 수 있게 해주면 그 다음에는 제공받은 객체를 대상으로 메서드를 호출할 수 있다. 예를 들어, 자바 코드에서 자바 코드에서 다음과 같이 호출했다고 하자. `engine.put("stage", stage);` 이제 자바스크립트 코드는 다음과 같이 호출할 수 있다. `stage.setTitle('Hello')` 사실 다음과 같은 문법을 사용할 수도 있다. `stage.title = 'Hello'`  
Nashorn은 getter와 setter를 위한 편리한 프로퍼티 문법을 지원한다. 만일 표현식 stage.title이 = 연산자의 왼쪽에 나타나면 setTitle 메서드 호출로 변환된다. 반대로 오른쪽에 나타나면 stage.getTitle() 호출로 변환된다.  
프로퍼티를 접근하는데 자바스크립트 대괄호 표기법을 사용할 수도 있다. `stage['title'] = 'Hello'` 

[] 연산작의 인자가 문자열이라는 점을 주목하자. 이 문맥에서는 유용하지 않지만 문자열 변수로 stage[str]을 호출하는 방법으로 임의의 프로퍼티에 접근할 수 있다.

*자바스크립트에서는 행 끝의 세미콜론이 옵션이다. 많은 자바스크립트 프로그래머가 세미콜론을 두지만, 이 장에서는 자바와 자바스크립트 코드 조각을 쉽게 구분할 수 있게 세미콜론을 생략한다.*

자바스크립트에는 메서드 오버로딩이라는 개념이 없다. 주어진 이름을 가진 메서드는 오직 한 개만 있을 수 있고, 받을 수 있는 파라미터의 타입과 개수에는 제한이 없다. Nashorn은 파라미터의 개수와 타입에 따라 올바른 자바 메서드를 선택하려고 시도한다. 거의 모든 경우에 제공된 파라미터와 일치하는 자바 메서드는 오직 한개만 있다. 그렇지 않은 경우에는 다음처럼 다소 이상한 문법을 이용해 올바른 메서드를 수동으로 선택할 수 있다.

```
list['remove(Object)'](1)
```

여기서는 Integer 객체 1을 list에서 제거하는 remove(Object) 메서드를 지정했다(위치 1에 있는 객체를 제거하는 remove(int) 메서드도 있다).

### 객체 생성
자바스크립트에서 객체를 생성하려고 할 때(스크립트 엔진에서 전달해줄 때와는 반대 경우)는 자바 패키지에 접근하는 방법을 알아야 한다. 자바 패키지 접근에는 두가지 메커니즘이 있다.  
점 표기법을 통해 패키지와 클래스 객체를 돌려주는 전역 객체 java, javax, javafx, com, org 그리고 edu가 있다. 예를 들면, 다음과 같다.

```
var javaNetPackage = java.net     // JavaPackage 객체
var URL = java.net.URL            // JavaClass 객체
```

앞의 식별자 중 하난로 시직하지 않는 패키지에 접근해야 할 때는 `Package.ch.cern`과 같이 Package 객체에서 찾을 수 있다.  
Java.type 함수를 호출하는 방법도 있다. `var URL = Java.type('java.net.URL')` 이 방법은 점 표기법을 사용한 java.net.URL 보다 약간 빠르게 동작하며, 더 나은 오류 검사를 받는다(java.net.Url 같은 철자 오류를 내면, Nashorn은 이를 패키지로 생각한다). 하지만 빠른 속도와 훌륭한 오류 처리를 원할 때는 처음부터 스크립팅 언어를 사용하지 않아야 한다.

*Nashorn 문서에서는 자바 파일의 최상단에 임포트 문을 두듯이 클래스 객체를 스크립트 파일의 최상단에 정의하는 방법을 권장한다.*

클래스 객체를 얻은 후에는 정적 메서드를 호출할 수 있다.

```
var URL = Java.type('java.net.URL')
var JMath = Java.type('java.lang.Math')

JMath.floorMod(-3, 10)
```

객체를 생성하려면 자바스크립트 new 연산자에 클래스 객체를 전달한다. 이때 평소처럼 모든 생성자 파라미터를 전달한다.

```
var URL = java.net.URL
var url = new URL('http://horstmann.com')
```

효율성에 신경 쓰지 않는다면 다음과 같이 호출할 수도 있다. `var url = new java.net.URL('http://horstmann.com')`

> Java.type을 new와 함께 사용할 때는 추가로 괄호가 필요하다.
> var url = new (Java.type('java.net.URL'))('http://horstmann.com')

이너 클래스를 지정할 때는 점 표기법을 사용할 수 있다.

```
var entry = new java.util.AbstractMap.SimpleEntry('hello', 42)

// Java.type을 사용하는 경우에는 JVM처럼 $ 사용
var Entry = Java.type('java.util.AbstractMap$SimpleEntry')
```

### 문자열
Nashorn의 문자열은 당연히 자바스크립트 객체다.

```
'Hello'.slice(-2)   // 결과는 'lo'다
```

여기서는 자바스크립트 메서드인 slice를 호출한다. 자바에는 이 메서드가 없다. 하지만 다음 호츨은 자바스크립트에 compareTo 메서드가 없는데도 동작한다(자바스크립트에서는 단순히 < 연산자를 사용한다).

```
'Hello'.compareTo('World')
```

이 경우 자바스크립트 문자열이 자바 문자열로 변환된다. 일반적으로 자바스크립트 문자열은 자바 메서드에 전달될 때 자바 문자열로 변환된다. 또한, 모든 자바스크립트 객체는 String 파라미터를 받는 자바 메서드에 전달될 때 문자열로 변환된다는 점도 유념하기 바란다. 다음 코드를 보자

```
// 자바스크립트 RegExp가 자바 String으로 변환된다.
var path = java.nio.file.Paths.get(/home/)
```

여기서 /home/ 부분은 정규 표현식이다. Paths.get 메서드는 String을 기대하며, 이 상황에서는 말이 되지 않는데도 실제로 String을 받는다. 이 문제로 Nashorn을 비난하면 안된다. 문자열을 기대할 때는 모든 것을 문자열로 변환하는 일반적인 자바스크립트 동작을 따른 것이다. 숫자와 Boolean 값인 경우에도 마찬가지 변환이 일어난다. 예를 들어, `'Hello'.slice('-2')`는 완전히 유효하며, 문자열 '-2'가 숫자 -2로 소리 없이 변환된다. 이와 같은 특징이 동적 타입 언어를 이용한 프로그래밍을 흥미로운 모험으로 만들어 준다.

### 숫자
자바스크립트는 명시적으로 정수를 지원하지 않는다. 자바스크립트의 Number 타입은 자바의 double 타입과 같다. int 또는 long을 기대하는 자바 코드에 숫자를 전달하면 소수부가 소리없이 제거된다. 예를 들어, `'Hello'.slice(-2.99)`는 `'Hello'.slice(-2)`와 같다.  
Nashorn은 효율성을 위해 계산을 가능하면 정수로 유지하지만, 이 차이는 일반적으로 눈에 띄지 않는다. 다음은 차이가 드러나는 한가지 상황을 보여준다.

```
var price = 10
java.lang.String.format('Price: %.2f', price)   // 오류: java.lang.Integer에는 f형식이 유효하지 않다.
```

price 값은 정수고, format 메서드는 Object... 가변인자 파라미터를 받기 때문에 이 값이 Object에 대입된다. 따라서 Nashorn은 java.lang.Integer를 만든다. 하지만 f 형식은 부동소수점 수에 사용할 목적으로 만들어졌기 때문에 결국 format 메서드 호출은 실패한다. 이 예제에서는 Number 함수를 호출해서 java.lang.Double로 강제 변환할 수 있다.

```
java.lang.String.format('Unit price: %.2f', Number(price))
```

### 배열 작업
자바 배열을 생성하려면 우선 클래스 객체를 만들어야 한다.

```
var intArray = Java.type('int[]')
var StringArray = Java.type('java.lang.String[]')
// 다음으로 new 현산자를 호출하면서 배열의 길이를 전달
var numbers = new intArray(10)
var names = new StringArray(10)
numbers[0] = 42
print(numbers[0])
// 향상된 for 루프
for each (var elem in names) {
  print(elem)
}
// 인덱스가 필요하다면
for (var i in names) {
  print(names[i])
}
```

> 위 루프(`for (var i...)`)는 자바의 향상된 for 루프처럼 보이지만 인덱스 값을 방문한다. 자바스크립트 배열은 요소들이 드문드문 있을 수 있다. 자바스크립트 배열을 다음과 같이 초기화한다고 하자.
> var names = []
> names[0] = 'Fred'
> names[2] = 'Barney'
> 이 경우 루프 `for (var i in names) print(i)`는 0과 2를 출력한다.

자바와 자바스크립트 배열은 아주 다르다. 자바 배열을 기대하는 기대하는 위치에 자바스크립트 배열을 전달하면 Nashorn이 변환을 수행한다. 하지만 때로는 이 변환을 도와야 한다. 주어진 자바스크립트 배열에 해당하는 자바 배열을 얻으로면 Java.to 메서드를 사용한다.

```
var javaNames = Java.to(names, StringArray)   // String[] 타입 배열
// 자바 배열을 자바스크립트 배열로 변환하려면 Java.forn을 사용
var jsNumbers = Java.from(numbers)
jsNumbers[-1] = 42
```

오버로딩과 관련한 모호함을 해결하려면 Java.to를 사용해야 한다. 예를 들어 다음은 Nashorn에서 int[] 배열로 변환해야 하는지, 아니면 Object[] 배열로 변환해야 하는지 판단할 수 없기 때문에 모호하다.

```
java.util.Arrays.toString([1, 2, 3])
// 이 상황에서는 다음과 같이 호출해야 한다.
java.util.Arrays.toString(Java.to([1, 2, 3], Java.type('int[]')))
// 또는
java.util.Arrays.toString(Java.to([1, 2, 3], 'int[]'))
```

### 리스트와 앱
Nashorn은 자바 리스트와 맵을 위한 *달콤한 문법(Syntactic sugar)*을 제공한다. 모든 자바 List에 대괄호 연산자를 사용해 get과 set 메서드를 호출할 수 있다.

```
var names = java.util.Arrays.asList('Fred', 'Wilma', 'Barney')
var first = names[0]
names[0] = 'Duke'
// 대괄호 연산자는 자바 맵에도 동작한다.
var scores = new java.util.HashMap
scores['Fred'] = 10     // scores.put('Fred', 10)을 호출한다.

// 맵 순회
for (var key in scores) ...
for each (var value in scores) ...
for each (var e in scores.entrySet)) {
  e.key와 e.value를 처리
}
```

*for each 루프는 Itreable 인터페이스를 구현하는 모든 자바 클래스를 대상으로 동작한다.*

### 람다
자바스크립트는 다음과 같은 익명함수를 지원한다.

```
var square = function(x) { return x * x }
var result = square(2)
```

문법적으로 이러한 익명 함수는 자바의 람다 표현식과 아주 유사하다. 자바 메서드의 함수형 인터페이스 인자로 익명 함수를 사용할 수 있다.

```
java.util.Arrays.sort(words,
  function(a, b)
    { return java.lang.Integer.compare(a.length, b.length)}
)
```

Nashorn은 몸체가 단일 표현식인 함수의 단축 형식을 지원한다. 이런 함수에서는 중괄호와 return 키워드를 생략할 수 있다.

```
java.util.Arrays.sort(words, function(a, b) java.lang.Integer.compare(a.length, b.length))
```

*'표현식 클로저'라 부르는 이 단축 표기법은 공식 자바스크립트 언어 표준(ECMAScript5.1)의 일부는 아니지만, 모질라 자바스크립트 구현체에서도 지원한다.*

### 자바 클래스 확장과 자바 인터페이스
자바 클래스를 활장하거나 자바 인터페이스를 구현하려면 Java.extend 함수를 사용한다. 이때 슈퍼클래스 또는 인터페이스의 클래스 객체와 오버라이드 또는 구현하려는 메서드를 포함하는 자바스크립트 객체를 전달한다. 예를 들어, 다음은 무한으로 난수를 생산하는 반복자(Iterator)다. 여기서는 next와 hasNext 두 메서드를 오버라이드한다. 각 메서드의 구현을 익명 자바스크립트 함수로 제공한다.

```
var RandomIterator = Java.extend(java.util.Iterator, {
  next: function() Math.random(),
  hasNext: function() true
})    // RandomIterator는 클래스 객체다.
var iter = new RandomIterator()
```

*Java.extend를 호출할 때 슈퍼클래스와 슈퍼인터페이스를 몇 개든 지정할 수 있다. 필요한 모든 클래스 객체를 실제 구현 메서드를 포함하는 객체 앞에 나열하면 된다.*

또 다른 Nashorn 문법 확장을 이용하면 인터페이스 또는 추상 클래스의 익명 서브클래스를 정의할 수 있다. 자바스크립트 객체 앞에 new JavaClassObject가 있으면 확장된 클래스의 객체가 리턴된다. 예를 들면, 다음과 같다.

```
var iter = new java.util.Iterator {
  next: function() Math.random(),
  hasNext: function() true
}
```

슈퍼타입이 추상이고 추상 메서드를 하나만 포함하면, 메서드 이름조차 명시하지 않아도 된다. 대신 함수를 생성자 파라미터처럼 전달한다.

```
var task = new java.lang.Runnable(function() { print('Hello') })
// task는 Runnable을 구현하는 익명 클래스의 객체다.
```

> 구체 클래스를 확장할 때는 이와 같은 생성자 문법을 사용할 수 없다. 예를 들어, new java.lang.Thread(function() { print('Hello')})는 Thread 생성자인 Thread(Runnable)을 호출한다. 따라서 new 호출로 Thread의 서브클래스가 아니라 Thread 클래스의 객체를 리턴한다.

서브클래스에서 인스턴스 변수가 필요한 경우에는 해당 변수를 자바스크립트 객체에 추가한다. 예를 들어, 다음은 난수 10개를 만들어내는 반복자다.

```
var iter = new java.util.Iterator {
  count: 10,
  next: function() { this.count--; return Math.random() },
  hasNext: function() this.count > 0
}
```

메서드를 오버라이드할 때 슈퍼클래스 메서드를 호출하는 일도 가능하지만 아주 까다롭다. Java.super(obj) 호출은 obj가 속한 클래스의 슈퍼클래스 메서드를 호출할 수 있게 해주는 객체를 돌려주지만, 먼저 해당 obj 객체가 존재해야 한다. 다음은 슈퍼클래스 메서드를 호출하는 방법을 보여준다.

```
var arr = new (Java.extend(java.util.ArrayList)) {
  add: function(x) {
    print('Adding ' + x);
    return Java.super(arr).add(x)
  }
}
```

이제 arr.add('Fred')를 호출하면 값이 ArrayList에 추가되기에 앞서 메시지가 출력된다. Java.super(arr) 호출에서는 new를 통해 리턴되는 값으로 설정되는 arr 변수가 필요하다는 점을 주목하기 바란다. Java.super(this)로 호출하면 동작하지 않는다(이렇게 하면 자바 프록시가 아닌 해당 메서드를 정의하는 자바스크립트 객체를 얻을 뿐이다). Java.super 메커니즘은 서브클래스가 아니라 개별 객체를 정의하는 경우에만 유용하다.

*Java.super(arr).add(x)를 호출하는 대신 arr.super$add(x) 문법을 사용할 수도 있다.*

### 예외
자바 메서드에서 예외를 던질 때 평소 방법대로 자바스크립트에서 잡을 수 있다.

```
try {
  var first = list.get(0)
  ...
} catch (e) {
  if (e instanceof java.lang.IndexOutOfBoundException) {
    print('list is empty')
  }
}
```

예외를 타입으로 잡을 수 있는 자바와는 달리 catch 절이 하나만 있다는 점을 주목하자. 이 또한 모든 타입 조사가 실행시에 일어나는 동적 언어 방식이다.

### 쉘 스크립팅
컴퓨터에서 반복적인 작업을 자동화해야 한다면 명령을 쉘 스크립트안에 둘 수 있다. 

#### 쉘 명령 실행하기
Nashorn에서 스크립팅 확장을 사용하려면 다음 명령을 실행한다. `$ jjs -scripting` 아니면 다음 명령을 실행하도 된다. `$ jrunscript`  
이제 다음 예와 같이 쉘 명령을 역 따옴포 안에 넣어서 실행할 수 있다.

```
\`ls -al\`
```

최근 명령의 표준 출력과 표준 오류 스트림이 $OUT과 $ERR로 캡처된다. 명령의 종료 코드는 $EXIT에 들어 있다(관례에 따라 종료 코드 0은 성공을 0이 아닌 종료 코드는 오류 상황을 나타낸다).  
역 따옴표로 감싼 명령의 결과를 변수에 대입하는 방법으로 표준 출력을 캡처할 수도 있다.

```
var output = \`ls -al\`
```

명령에 표준 입력을 제공하고 싶을 때는 다음 함수를 사용한다. `$EXEC(command, input)` 예를 들어, 다음 명령은 ls -al의 출력을 grep -v class에 전달한다.

```
$EXEC('grep -v class', \`ls -al\`)
```

파이프만큼 깔끔하지는 않지만, 필요하면 파이프를 쉽게 구현할 수 있다.

#### 문자열 인터폴레이션
${...} 내부에 있는 표현식은 큰따옴표 또는 역 따옴표로 감싼 문자열 안에서 평가된다. 이를 '문자열 인터폴레이션(문자열 채워넣기-string interpolation)'이라고 한다. 예들 들어, 다음 코드는 변수 classpath와 mainclass의 내용을 명령 안에 집어넣는다.

```
var cmd = "javac -classpath ${classpath} ${mainclass}.java"
$EXEC(cmd)
```

또는 단순히 다음과 같이 해도 같은 결괄르 얻는다. `\`javac -classpath ${classpath} ${mainclass}.java\``  
${...} 안에 임의의 표현식을 사용할 수 있다.

```
var message = "The curent time is ${java.time.Instant.now()}"
// message를 The current time is 2013-10-12T21:48:58.545Z 같은 문자열로 설정한다.
```

Bash 쉘과 마찬가지로 작은따옴표로 감싼 문자열 안에서는 문자열 인터폴레이션이 동작하지 안는다.

```
var message = 'The current time is ${java.time.Instant.now()}'
// message를 The current time is ${java.time.Instant.now()} 문자열로 설정한다.
```

문자열은 '즉석 문서(스크립트 내부의 인라인 문서)' 안에서도 인터폴레이션 된다. 이러한 인라인 문서는 명령이 표준 입력에서 여러 행을 읽고, 스크립트 작성자가 입력을 별도의 파일로 두고 싶지 않을 때 유용하다. 예를 들어, 다음은 GlassFish 관리 도구에 명령을 제공하는 방법을 보여준다.

```
name='myapp'
dir='/opt/apps/myapp'
$EXEC("asadmin", <<END)
start-domain
start-database
deploy ${name} ${dir}
exit
END
```

<<END 구문은 "다음 행부터 시작하고 END 행으로 마치는 문자열을 삽입하라"는 의미다(문자열 내부에 나타나지 않는 어떤 식별자든 END 대신 사용할 수 있다). 여기서는 애플리케이션의 이름과 위치가 인터폴레이션 된다는 점을 주목하기 바란다. 문자열 인터폴레이션과 즉석 문서는 ㅓ오직 스크립팅 모드에서만 이용할 수 있다.

#### 스크립트 입력
스크립트에 명령행 인자를 제공할 수 있다. jjs 명령행에 여러 스크립트 파일을 포함할 수 있기 때문에 스크립트 파일과 인자를 더블하이픈(--)으로 구분해야 한다.

```
$ jjs script1.js script2.js -- arg1 arg2 arg3
```

*이 명령행은 조금 보기 불편하다. 스크립트 파일이 한 개만 있을 때는 대신 다음과 같이 실행할 수 있다.
$ jrunscript -f script.js arg1 arg2 arg3*

> 스크립트의 첫번째 행은 '쉬뱅-shebang(스크립트 인터프리터의 위치가 뒤따라오는 #! 기호)'이 될 수 있다.
> #!/opt/java/bin/jjs
> 또는 다음과 같이 해도 된다.
> #!/opt/java/bin/jrunscript -f
> 그 다음은 해당 스크립트 파일을 실행 파일로 만들어 단순히 path/script.js로 실행할 수 있다. 스크립트가 쉬뱅으로 시작할 때는 스크립팅 모드가 자동으로 활성화 된다.

> 스크립트에서 인자를 받고 쉬뱅에 jjs를 사용하면, 스크립트 사용자가 --을 사용해야 한다(예를 들면, path/script.js -- arg1 arg2 arg3). 사용자는 이 부분을 좋아하지 않을 것이다 이런 경우에는 대신 jrunscript를 사용해야 한다.

스크립트 파일에서는 명령행 인자를 arguments 배열로 받는다.

```
var deployCommand = "deploy ${arguments[0]} ${arguments[1]}"
```

jjs를 이용할 때는 arguments 대신 $ARG를 사용할 수 있다(jrunscript에서는 사용 불가). $ARG 객체를 문자열 인터폴레이션과 함께 사용할 때는 달러 기홀르 두 번 사용해야 한다.

```
var deployCommand = "deploy ${$ARG[0]} ${${ARG[1]}"
```

스크립트 안에서 $ENV 객체를 통해 쉘의 환경 변수를 얻을 수 있다. `var javaHome = $ENV.JAVA_HOME` 스크립팅 모드에서는 readLine 함수를 이용해 사용자에게 입력을 유도할 수 있다. `var username = readLine('Username: ')`  
마지막으로 스크립트를 종료하려면 exit 함수를 사용한다. 이때 추가로 종료 코드를 제공할 수 있다.

```
if (username.length == 0) exit(1)
```

### Nashorn과 JavaFX
Nashorn은 JavaFX 애플리케이션을 실행하는 편리한 방법을 제공한다. 단순히 평소 Application 서브클래스의 start 메서드에 집어넣던 명령어들을 스크립트 안에 넣으면 된다. Stage 파라미터에는 $STAGE를 사용한다. 심지어 Stage 객체의 show를 호출할 필요도 없다(내부적으로 대신 호출된다). 예를 들어, 다음은 4장에서 살펴본 "Hello" 프로그램을 자바스크립트로 작성한 버전이다.

```
var message = new javafx.scene.control.Label("Hello, JavaFX!");
message.font = new javafx.scene.text.Font(100);
$STAGE.scene = new javafx.scene.Scene(message);
$STAGE.title = "Hello";
```

다음과 같이 -fx 옵션을 지정해 스크립트를 실행한다.  `$ jjs -fx hellofx.js` 이게 전부다. "Hello"라는 타이틀이 붙은 윈도우 안에 "Hello, JavaFX!" 메시지를 담은 레이블이 100포인트 폰트로 표시단다. `message.setFont(new Font(100))` 대신 `message.font = new Font(100)` 으로 사용할 수 있다.

*start 외에 Application 클래스의 생명 주기 메서드인 init 또는 stop을 오버라이드해야 하는 경우에는 해당 메서드를 스크립트에서 최상위 레벨에 포함한다. 그런 다음 -fx 옵션을 이용하면 스크립트 메서드를 포함하는 Application의 서브클래스를 얻는다.*

이제 이벤트 처리를 살펴보자. 4장에서 살펴본 것처럼 대부분의 JavaFX 이벤트는 JavaFX 프로퍼티 리스너를 통해 처리된다. 이 부분에서 자바스크립트의 사정은 썩 좋지 않다. JavaFX 프로퍼티는 InvalidationListener와 ChangeListener 두 리스너 인터페이스(둘 다 addListener로 추가한다)를 가진다는 점을 상기하기 바란다. 자바에서 람다 표현식으로 addListener를 호출하면, 컴파일러는 파라미터 타입으로부터 둘 중 어느 리스너를 추가할지 알 수 있다. 하지만 자바스크립트에서는 함수 파라미터에 타입이 없다. 폰트 크길르 제어하는 슬라아더가 있다고 하자. 슬라이더 값이 변할 때 폰트 크기를 업데이트하는 리스너를 추가하려고 한다.

```
// 오류 - Nashorn은 어느 리스너 타입을 추가할지 판단하지 못한다.
slider.valueProperty().addListener(function(property)
  message.font = new Font(slider.value)
)
```

Nashorn은 추가하고자 하는 리스너가 InvalidationListener인지 ChangeListener인지 모르기 때문에 직접 선택을 해야 한다.

```
slider.valueProperty().addListener(
  new javafx.beans.InvalidationListener(function(property)
    message.font = new Font(slider.value)
  )
)
```

결과적으로 자바 코드보다 무겁다(적어도 경량 스크립팅 언어에서 보고 싶은 코드는 아니다). 

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













































## 1장 람다표현식
* 핵심 내용
  * 람다 표현식은 파라미터가 있는 코드 블럭이다.
  * 코드 블록을 나중에 실행하고자 할 때 람다 표현식을 사용한다.
  * 람다 표현식을 함수형 인터페이스로 변환할 수 있다.
  * 람다 표현식은 자신을 감싸고 있는 유효 범위에 속한 사실상 final 변수를 접근할 수 있다.
  * 메서드 레퍼런스와 생성자 레퍼런스는 각각 메서드와 생성자를 호출 없이 참조한다.
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

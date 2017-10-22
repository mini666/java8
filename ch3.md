## 3장 람다를 이용한 프로그래밍
* 핵심 내용
  * 람다 표현식을 사용하는 주 이유는 적절한 시점까지 코드의 실행을 지연하기 위한 것
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

예를 들어, 특정 기준을 만족하는 파일을 처리하는 메서드를 작상한다고 하자. 이때 서술적인 java.io.FileFilte 클래스를 사용해야 하는가, 아니면 Prediate<File>을 사용해야 하는가? 둘 중에 Predicate<File>의 사용을 강력히 추천한다. 아마도 FileFilter 인스턴스를 생산하는 수많은 유용한 메서드를 이미 갖추고 있을 경우에나 Predicate<File>을 사용하지 않을 것이다.

*대부분의 표준 함수형 인터페이스는 함수를 생산하거나 결합하는 비추상 메서드를 포함한다. 예를 들어 `Predicate.isEqual(a)`는 a가 null이 아닌 경우 `a::equals`와 같다. 또한 Predicate들을 결합하는데 사용하는 디폴트 메서드인 and, or, negate가 있다. 예를들어, `Predicate.isEqual(a).or.Predicate.isEqual(b))`는 `x -> a.equals(x) || b.equals(x)`와 같다.*

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

때로는 표준 라이브러리에 원하는 인터페이스가 없어서 자신만의 함수형 인터페이스를 제공해야 할 수도 있다. 사용자가 이미지의 (x, y) 위치에 따라 새로운 색상을 계산하는 함수인 `(int, int, Color) -> Color`를 제공하게 하는 방법으로 이미지의 색상을 수정하고 샆디고 하자. 이 경우 다음과 같이 자신만의 인터페이스를 정의할 수 있다.
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

종종 함수형 인터페이스의 메서드가 검사 예외를 허용하지 않는 것이 불편할 때가 있다. 물론 메서드에서 Supplier<T> 대신 Callable<T> 같은 검사 예외를 허용하는 함수형 인터페이스를 받을 수 있다. Consumer나 Function에 적용할 수 있는 버전이 필요하다면 직접 만들어야 한다.  
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

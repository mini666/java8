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

*스트림 외의 클래스에서도 `flatMap` 메서드를 접할 것이다. `flatMap`은 컴퓨터 과학에서 일반적인 개념이다. 제네릭 타입 G(예를 들면 Stream), 타입 T를 G<U>로 변환하는 함수 f 그리고 타입 U를 G<V>로 변환하는 함수 g가 있다고 하자. 그러면 flatMap을 사용해서 이 함수들을 합성 할 수 있다(즉, 먼저 f를 적용한 후 g를 적용한다). 이는 모나드 이론에서 핵심 개념이다.*

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

*`peek` 메서드는 원본과 동일한 요소들을 포함하는 다른 스트림을 돌려주지만, 전달받은 함수를 요소 추출 시마다 호출한다. 따라서 디버깅 수행때 유용하다.*

```
Object[] powers = Stream.iterate(1.0, p -> p * 2).peek(e -> System.out.println("Fetching " + e)).limit(20).toArray();
```
*이 방법으로 `iterte` 메서드가 리턴하는 무한 스트림이 지연 처리됨을 확인할 수 있다.*

### 상태 유지 변환
지금까지 살펴본 스트림 변환은 무상태 변환이다. 다시 말해 필터링 또는 맵핑된 스트림에서 요소를 추출할때 결과가 이전 요소에 의존하지 않는다. 몇가지 상태 유지 변환도 존재한다. 예를들어 distinct 메서드는 중복을 제거하는 점을 제외하면 원본 스트림으로부터 요소들을 같은 순서로 돌려주는 스트림을 리턴한다. 이 경우 스트림은 이미 만난 요소들을 확실히 기억해야 한다.

```
Stream<String> uniqueWords =
  = Stream.of("merrily", "merrily", "merirly", "gently").distinct();
```
  
`sorted` 메서드는 요소들을 돌려주기 전에 반드시 전체 스트림을 보고 정렬해야 한다. 무한 스트림은 정렬할 수 없다. `sorted` 메서드는 여러 버전이 있는데 한 버전은 Comparable 요소들의 스트림을 대상으로 작업하고 또 다른 버전은 Comparator를 받는다.

```
Stream<String> longestFirst = words.sorted(Comparator.comparing(String::Length).reversed()));
```

`sorted` 메서드는 정렬 과정이 스트림 파이프라인의 일부일때 유용하다.

*`Collections.sort` 메서드는 컬렉션을 직접 정렬한다. 한편 `Stream.sorted` 메서드는 새롭게 정렬된 스트림을 리턴한다.*

### 단순 리덕션
단순 리덕션으로 `count, min, max` 가 있는데 이들 메서드는 결과를 감싸고 있거나 결과가 없음을 나타내는 Optional<T> 값을 리턴한다.

```
Optional<String> largest = words.max(String::compareToIgnoreCase);
if (largest.isPresent()) {
  System.out.println("largest: " + largest.get());
}
```

`findFirst` 메서드는 비어 있지 않은 컬렉션에서 첫번째 값을 리턴한다. 종종 이 메서드를 filter 메서드와 결합하면 유용하다. 존재한다면 글자 Q로 시작하는 첫번째 단어를 찾는다.

```
Optional<String> startWithQ =
  words.filter(s -> s.startsWith("Q")).findFirst());
```

첫번째 값은 물론 어떤 일치 결과든 괜찮다면 `findAny` 메서드를 사용한다. 이 메서드는 스트림을 병렬화할때 유용한데, 이 경우 조사 대상 세그먼트들에서 처음 일치가 발견되면 계산을 완료하기 때문이다.

```
Optional<String> startWithQ =
  words.parallel().filter(s -> s.starstWith("Q")).findAny();
```

단순히 일치하는 요소가 있는지 알고 싶은 경우에는 `anyMatch`를 사용한다. 이 메서드는 Predicate 인자를 받으므로 filter를 사용할 필요가 없다.

```
boolean aWordStartWithQ =
  words.parallel().anyMatch(s -> s.starstWith("Q"));
```

모든 요소가 Predicate와 일치하거나 아무것도 일치하지 않을때 true를 리턴하는 `allMatch`와 `noneMatch` 메서드도 있다. 이들 메서드는 항상 전체 스트림을 검사하지만, 여전히 병렬 실행을 통해 이점을 얻을 수 있다.

### 옵션 타입
Optional<T> 객체는 T 타입 객체 또는 객체가 없는 경우의 래퍼다. Optional<T>는 객체 또는 null을 가리키는 T 타입 레퍼런스보다 안전한 대안으로 만들어졌다. 하지만 올바르게 사용할 경우에만 더 안전하다.  
`get` 메서드는 감싸고 있는 요소가 존재할 때는 요소를 얻고, 그렇지 않으면 `NoSuchElementException`을 던진다.

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
`ifPresent` 메서드는 함수를 받는 두번째 형태가 있다. 옵션 값이 존재하면 해당 함수로 전달되며 그렇지 않으면 아무 일도 일어나지 않는다. if문을 사용하는 대신 다음과 같이 호출할 수 있다. `optionalValue.isPresent(v -> v 처리);` 예를 들어, 값이 존재하는 경우 집합에 해당 값을 추가하려고 할때는 다음과 같이 호출한다. `optionalValue.ifPresent(v -> results.add(v));` 또는 `optionalValue.ifPresent(results::add);`  
함수를 받는 `ifPresent` 버전을 호출할 때는 값이 리턴되지 않는다. 따라서 결과를 처리하고 싶은 경우에는 대신 map을 사용한다. `Optional<Boolean> added = optionalValue.map(results::add);` 이제 `added`는 세가지 값(true/false/빈 Optional 중 하나를 가진다.

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
`Optional<T>`를 리턴하는 메서드 f가 있고, 대상 타입 T는 `Optional<U>`를 리턴하는 메서드 g를 포함하고 있다고 할 때, 일반 메서드라면 s.f().g()를 호출하는 방법으로 이 메서드들을 합성할 수 잇다. 하지만 이 경우에는 s.f()에서 T가 아닌 Options<T> 타입을 리턴하므로 이러한 합성이 동작하지 않는다. 대신 다음과 같이 호출한다. `Optional<U> result = s.f().flatMap(T::g);` 이렇게 하면 s.f()가 존재하면 g가 적용되고, 그렇지 않으면 비어 있는 Optional<U>가 리턴된다. 앞 예제의 inverse 메서드에 안전한 루트 메서드도 있다고 하자.

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
*`e op x = x`* 같은 항등값 *e*가 있을 때는 해당 요소를 계산의 시작으로 사용할 수 있다.

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

### 결과 모으기
스트림 작업을 마칠 때 보통은 값으로 리듀스하기보다는 결과를 살펴보길 원하기 마련이다. 이때 요소들을 방문하는데 사용할 수 있는 잔통적인 반복자를 돌려주는 iterator 메서드를 호출할 수 있고 다른 방법으로 toArray를 호출해서 스트림 요소들의 배열을 얻을 수 있다. 실행 시간에 제너릭 배열을 생성할 수 없기 때문에 `Stream.toArray()`는 Object[]를 리턴한다. 올바른 타입의 배열을 원하는 경우 배열 생성자를 전달한다. `String[] result = words.toArray(String[]::new);`  
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

분류 함수가 Predicate 함수(즉, boolean을 리턴하는 함수)인 경우, 스트림 요소가 리스트 두개(각각 함수에서 true와 false를 리턴하는 경우에 해당)로 분할된다. 이 경우에는 groupingBy 대신 partitioningBy를 사용하면 훨씬 효율적이다. 예를 들어, 다음 예저는 모든 로케일을 영어를 사용하는 경우와 그 외의 경우로 분리한다.

```
Map<Boolean, List<Locale>> englishAndOtherLocales = locales.collect(Collectors.partitioningBy(l -> l.getLanguage().equals("en")));
List<Locale> englishLocales = englishAndOtherLocales.get(true);
```

*groupingByConcurrent 메서드를 호출하면 병행 맵을 얻으며, 이를 병렬 스트림에서 사용하면 동시에 내용이 채워진다. 전체적으로 볼 때 이 메서드는 toConcurrentMap 메서드에 해당한다.*

groupingBy 메서드는 값이 리스트인 맵을 돌려준다. 이들 리스트를 특정 방식으로 처리하려면 *다운 스트림 컬렉터*를 제공한다. 예를 들어, 리스트 대신 집합을 원하는 경우 앞 절에서 본 Collectors.toSet 컬렉터를 사용할 수 있다.

```
Map<String, Set<Locale>> countryToLocaleSet = locales.collect(groupingBy(Locales.getCountry, Collectors.toSet()));
```

그룹으로 묶인 요소들의 다운스트림 처리용으로 몇 가지 다른 컬렉터가 제공된다.
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
* 마지막으로 reducing 메서드는 다운스트림 요소들에 범용 리덕션을 적용한다. 세가지 메서드 형태 reducing(binaryOperator), reducing(identity, binaryOperator), reducing(identity, mapper, binaryOperator)가 있다. 첫번째 형태에서는 항등값이 null 이다.(항등 파라미터가 없으면 Optional 결과를 돌려주는 Stream::reduce의 형태와는 다르다는 점을 주목하기 바란다). 세번째 형태에서는 mapper 함수가 적용되고 이 함수의 값이 리듀스 된다.

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
IntStream lengths = words.mapToInt(String::length);
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

기본적으로 순서 유지 컬렉션(배열과 리스트), 범위(Range), 발생기(Genrator), 반복자 또는 Stream.sorted를 호출해서 얻는 스트림은 순서를 유지한다. 순서 유지 스트림의 결과들은 원본 요소들의 순서로 쌓이고, 전체적으로 예측 가능하게 동작한다. 따라서 같은 연산들을 두번 실행해도 왼전히 같은 결과를 얻는다. 순서 때문에 병렬화를 이용할 수 없는 것은 아니다. 예를 들어 Stream.map(fun)을 계산할 때 스트림은 n개 세그먼트로 분할되어 각각이 동시에 처리될 수 있다. 그런 다음 순서대로 재조립된다.

몇몇 연산은 순서에 대한 요구 사항을 버리면 더 효과적으로 병렬화될 수 있다. Stream.unordered 메서드를 호출함으로써 순서에는 관심이 없음을 나타낼 수 있다. 이로부터 이점을 얻을 수 있는 한가지 연산은 Stream.distinct다. 순서 유지 스트림에서 distinct는 같은 요소 중 첫번째를 보존한다. 하지만 이 동작은 병렬화를 방해한다(세그먼트를 처리 중인 스레드는 이전 세그먼트가 처리되기 전에는 어느 요소들을 버려야 하는지 알 수 없다). 유일한 요소라면 어느 것이든 보존해도 괜찮다면 (중복을 추적하기 위해 공유 집합을 사용해) 모든 세그먼트를 동시에 처리할 수 있다.

순서를 포기하면 limit 메서드를 빠르게 할 수 있다. 스트림에서 단지 n개 요소를 원할뿐 어느 것을 얻는지는 상관하지 않는다면 `Stream<T> sample = stream.parallel().unordered().limit(n);`

맵으로 모으기에 있는 맵을 병합하는 일은 비용이 많이 든다. 이 때문에 Collectos.groupingByConcurrent 메서드는 공유되는 병행 맵을 사용한다. 분명히 병렬화의 이점을 얻기 위해 맵 값들의 순서는 스트림 순서와 달라질 것이다. 이 컬렉터는 심지어 순서 유지 스트림에서도 순서을 유지하지 않는 성질이 있다. 따라서 스트림이 순서를 유지하지 않게 만들 필요 없이도 효율적으로 사용할 수 있다. 그럼에도 여전히 스트림을 병렬로 만들어야 한다.

```
Map<String, List<String>> result = cities.parallel().collect(
  Collectos.groupingByConcurrent(City::getState));
```

> 스트림 연산을 수행하는 동안에는 해당 스트림을 뒷받침하는 컬렉션을 절대 수정하면 안된다(스레드에 안전한 수정인 경우에도). 스트림은 자체적으로 데이터를 모으지 않음을 명심하기 바란다(데이터는 항상 별도의 컬렉션에 존재한다). 만일 해당 컬렉션을 수정하면 스트림 연산들의 결과는 정의되지 않는다. JDK 문서에서는 이 요구 사항을 방해 금지(Noninterference)라고 언급하고 있다. 이 사항은 순차 스트림과 병렬 스트림 모두에 적용된다.  
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

*Stream.filter의 선언부를 자세히 살펴보면 와일드카드 타입 `Predicate<? super T>`를 주목하게 될 것이다. 흔히 함수 파라미터로 이와 같은 타입을 사용한다. 예를 들어 Employee는 Person의 서브클래스이고, `Stream<Employee>`가 있다고 하자. 이 경우 `Predicate<Employee>, Predicate<Person>` 또는 `Preicate<Object>`로 스트림을 필터링할 수 있다(여기서 T는 Employee). 이와 같은 유연성은 메서드 레퍼런스를 전달할 때 특히 중요한다. 예를들어, `Stream<Employee>`를 필터링 하는 데 Person::isAlive를 사용하려 한다고 하자. 이 작업은 순전히 filter 메서드의 파라미터에 있는 와일드카드 덕분에 동작한다.*

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

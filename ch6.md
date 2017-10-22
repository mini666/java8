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

# java8
가장빨리만나는자바8

## 1장 람다표현식

### 연습문제
#### 01. Arrays.sort 메서드에서 비교자 코드는 sort 호출과 같은 스레드에서 호출되는가. 다른 스레드에서 호출되는가?

#### 02. java.io.File 클래스의 listFiles(FileFilter)와 isDirectory 메서드를 이용해 주어진 디렉터리의 모든 서브디렉토리를 리턴하는 메서드를 작성하라. FileFilter 객체 대신 람다 표현식을 사용하라. 메서드 표현식을 이용해 같은 작업을 반복하라.

#### 03. java.io.File 클래스의 list(FilenameFilter) 메서드를 이용해 주어진 디렉토리에서 주어진 확장자를 지닌 모든 파일을 리턴하는 메서드를 작성하라. FilenameFilter가 아닌 람다 표현식을 사용하라. 이 람다 표현식이 자신을 감싸는 유효 범위에 있는 어느 변수를 캡처하는가?

#### 04. File 객체 배열이 주어졌을때 디렉토리가 파일보다 앞에 위치하고 각 그룹 안에서 요소들이 경로 이름에 따라 정렬되도록 정렬하라.

#### 05. 다수의 ActionListener, Runnable 등을 포함하는 프로젝트 중 하나에서 파일을 불러와서 이러한 인터페이스를 람다 표현식으로 교체하라. 이 교체 작업으로 몇 행을 줄였는가? 코드가 더 읽기 쉬워졌는가? 메서드 레퍼런스를 사용할 수 있었는가?

#### 06. Runnable에서 검사 예외를 다뤄야 하는 점이 싫지 않은가? 모든 검사 예외를 잡아내서 비검사 예외로 바꾸는 uncheck 메서드를 작성하라. 예를 들면, 다음과 같이 사용할 것이다.
'''
new Thread(uncheck(
  () -> { System.out.println("Zzz"); Thread.sleep(1000); })).start();
  // 여길 보자. catch (InterruptedException) 부분이 없다!
'''
힌트: run 메서드에서 모든 예외를 던질 수 있는 RunnableEx라는 인터페이스를 정의한다. 다음으로 public static Runnable uncheck(RunnableEx runner)를 구현한다. uncheck 함수 안에서 람다 표현식을 사용한다.



#### 07. Runnable 인스턴스 두 개를 파라미터로 받고, 첫번째 인스턴스를 실행한 후 두번째를 실행하는 Runnable을 리턴하는 andThen 이라는 정적메서드를 작성하라. main 메서드에서 andThen 호출에 람다 표현식 두개를 전달하고, 결과로 받은 인스턴스를 실행하라.

#### 08. 람다 표현식이 다음과 같은 향상된 for 루프에 있는 변수를 캡처할 때 무슨 일이 일어나는가?
<code>
String[] names = { "Perter", "Paul", "Mary" };
List<Runnable> runners = new ArrayList<>();
for (String name : names) {
  runners.add(() -> System.out.println(name));
}
</code>
규칙에 맞는 작업인가? 각 람다 표현식이 다른 값을 캡처하는가? 아니면 모두 마지막 값을 얻는가? 만일 전통적인 루프인 for (int i = 0; i < names.length; i++)를 사용하면 무슨 일이 일어나는가?

#### 09. Collection으로부터 Collection2라는 서브클래스를 만들고, filter가 true를 리턴하는 각 요소를 대상으로 액션(action)을 적용하는 디폴트 메서드인 void forEachIf(Consumer<T> action, Predicate<T> filter)를 추가하라. 이 디폴트 메서드를 어떻게 사용할 수 있는가?

#### 10. Collections 클래스의 메서드들을 살펴보자. 만일 하루 동안 왕이 된다면 어느 인터페이스에 각 메서들르 둘 것인가? 각 메서드는 디폴트 메서드가 될 것인가? 정적 메서드가 될 것인가?

#### 11. 두 인터페이스 I와 J를 구현하는 클래스가 있다고 가정하자. 각각은 void f() 메서드를 포함한다. f가 I의 추상메서드, 디폴트 메서드 또는 정적 메서드인 경우와 J의 추상메서드, 디폴트 메서드 또는 정적메서드인 경우 정확히 무슨 일이 있어나는가? 클래스가 슈퍼클래스 S를 확장하고 인터페이스 I를 구현하며, 둘 모두 void f() 메서드를 포함하는 경우에 대해서도 무슨 일이 일어나는지 설명하라.

#### 12. 과거에는 인터페이스에 메서드를 추가하면 기존 코드를 망가뜨릴 수 있기 때문에 잘못된 형태라고 했다. 하지만 이제는 디폴트 구현을 함께 제공한다면 새로운 메서드를 추가하는 것도 괜찮다고 한다. Collection 인터페이스의 새로운 Stream 메서드가 레거시 코드 컴파일을 실패하게 하는 시나리오를 설명하라. 바이너리 호환성은 어떠한가? JAR 파일에 들어 있는 레거시 코드는 여전히 실행될 것인가?









## 2장 스트림 API
* 스트림과 컬렉션의 차이
** 스트림은 요소를 보관하지 않는다. 요소들은 하부의 컬렉션에 보관되거나 필요할 때 생성된다.
** 스트림 연산은 원본을 변경하지 않는다. 대신 결과를 담은 새로운 스트림을 반환한다.
** 스트림 연산은 가능하면 지연 처리된다.지연 처리란 결과가 필요하기 전에는 실행되지 않음을 의미한다. 예를 들어, 긴 단어를 모두 세는 대신 처음 5개 긴 단어를 요청하면, filter 메서드는 5번째 일치 후 필터링을 중단한다. 결과적으로 심지어 무한 스트림도 만들 수 있다.

## 3장 람다를 이용한 프로그래밍







## 4장 JavaFX

## 5장 새로운 날짜 및 시간 API
* 핵심내용
** 모든 java.time 객체는 수정 불가
** Instant는 타임 라인의 한 시점(Date와 유사)
** 자바의 시간에서 하루는 정확히 86,400초로 윤초가 없다.
** Duration은 두 인스턴트 사이의 차이
** LocalDateTime은 시간대 정보를 포함하지 않는다.
** TemporalAdjuster의 메서드들은 (특정 월의 첫번째 화요일 찾기 같은) 일반적인 캘린더 계산을 처리
** ZonedDateTime은 주어진 시간대에서 특정 시점(GregortianCalendar와 유사)
** 구역 시간을 앞으로 가게 할 때는 일광 절약 시간 변경을 고려하기 위해 Duration이 아닌 Period를 사용
** DateTimeFormatter를 사용해 날짜와 시간을 해석

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


## 6장 병행성 향상점

## 7장 Nashorn 자바 스크립트 엔진

## 8장 그 외 여러가지 주제

## 9 혹시 놓쳤을 수도 있는 자바7 기능


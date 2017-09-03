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





















































## 7장 Nashorn 자바 스크립트 엔진

## 8장 그 외 여러가지 주제

## 9 혹시 놓쳤을 수도 있는 자바7 기능


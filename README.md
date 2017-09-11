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



## 9 혹시 놓쳤을 수도 있는 자바7 기능


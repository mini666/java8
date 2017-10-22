## 7장 Nashorn 자바 스크립트 엔진
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

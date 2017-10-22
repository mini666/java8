## 4장 JavaFX
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
------------|------------
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
프로그래밍을 통한 레이아웃은 스윙과 아주 유사하나 임의의 패널에 추가되는 레이아웃 관리자대신 Pane을 사용한다. 페인이란 레이아웃 정책을 갖춘 컨테이너를 말한다. 예를 들어, BorderPane은 North, West, South, East, Center 라는 다섯개 영역을 포함한다.

```
BorderPane pane = new BorderPane();
pane.setTop(new Button("Top"));
pane.setLeft(new Button("Left"));
pane.setCenter(new Button("Center"));
pane.setRight(new Button("Right"));
pane.setBotton(new Button("Bottom"));
stage.setScene(new Scene(pane));
```

*스윙의 BorderLayout에서는 버튼이 레이아웃의 각 영역을 채우도록 늘어난다. 하지만 JavaFX에서는 버튼이 자연스러운 크기 이상으로 늘어나지 않는다.*

South 영역에 버튼을 두개 이상 배치 하려 한다면 HBox를 사용한다.

```
HBox buttons = new HBox(10);    // 컨트롤 간 10픽셀
buttons.getChilder().addAll(yesButton, noButton, maybeButton);
```

컨트롤을 세로로 배치하는 VBox도 있다.

```
VBox pane = new VBox(10);
pane.getChildren().addAll(question, buttons);
pane.setPadding(new Insets(10));
```

> JavaFX에서는 치수를 픽셀로 지정한다. 오늘날에는 기기마다 픽셀 밀도가 크게 다를 수 있기 때문에 픽셀 단위 지정이 그리 적합하지 않다. 이 문제를 극복하는 한가지 방법은 치수를 CSS3에서처럼 rem 단위로 계산하는 것이다. 여기서 rem, 즉 'root em'은 문서 루트의 기본 폰트 높이를 말한다.
> `final double rem = new Text("").getLayoutBounds().getHeight();`
> `pane.setPadding(new Inset(0.8 * rem));`

HBox, VBox로는 원하는 레이아웃을 만드는데 한계가 있다. 스윙에서 '모든 레이아웃 관리자의 어머니'로 GridBagLayout을 포함하는 것과 마찬가지로 JavaFX는 GridPane을 포함한다. GridPane이 HTML 테이블에 해당한다고 생각하면 된다. 모든 셀의 수평 및 수직 정렬을 설정할 수 있다. 원한다면 셀은 여러 행과 열을 차지할 수 있다. 일반적인 로그인 대화상자를 고려해 보자.

* 레이블 "User name:"과 "Password:"는 오른쪽 정렬된다.
* 버튼들은 2개 열을 차지하는 HBox 안에 위치한다.

GridPane에 추가할 때는 열과 행 인텍스(각각 *x, y* 좌표로 생각하면 된다)를 지정한다.

```
pane.add(usernameLable, 0, 0);
pane.add(username, 1, 0);
pane.add(passwordLable, 0, 1);
pane.add(password, 1, 1);
```

자식이 여러 열 도는 행을 차지하면, 위치 다음에 해당 스팬(Span)을 지정한다. `pane.add(buttons, 0, 2, 2, 1)` 은 버튼 패널이 2열 1행을 차지한다. 자식이 남아있는 모든 행 또는 열을 차지하게 하려면 `GridPazne.REMAINING`을 사욯할 수 있다.

자식의 수평 정렬을 설정하려면 정적 setHalignment 메서드를 사용하고, 이 메서드에 해당 자식의 레퍼런스와 열거 타입 HPos의 상수 LEFT, CENTER 또는 RIGHT를 전달한다. `GridPane.setHalignment(usernameLabel, HPos.RIGHT);`  
유사하게 수직 정렬인 경우 열거 타입 VPos의 TOP, CENTER, BOTTOM을 ㅣ용해 setValignment를 호출한다.

*자바 코드에서는 이러한 정적 메서드 호출이 우아하지 못하게 보이지만, FXML에서는 일리가 있는 방법이다.*

> 그리드 안에서 버튼을 포함하는 HBox를 가운데 정렬하지 말자. 박스는 그리드의 가로 크기만큼 확장되기 때문에 가운데 정렬을 통해 위치가 변경되지 않는다. 대신 다음과 같이 HBox가 내용을 가운데 정렬하게 해야 한다.
> `buttons.setAlignment(Pos.CENTER);`

행과 열 사이에 간격을 주고, 테이블 주변에 패딩을 넣고 싶으면 다음과 같이 한다.

```
pane.setHgap(0.8 * em);
pane.setVgap(0.8 * em);
pane.setPadding(new Insets(0.8 * em));
```

> 디버깅할 때는 셀의 경계를 보면 유용할 수 있다. 셀의 경계를 보려면 다음과 같이 호출한다. `pane.setGridLinesVisible(true);`
> 개별 자식 컨트롤의 경계를 보려면 해당 컨트롤의 경계를 설정한다(예를 들면, 자식 컨트롤이 전체 셀을 채우도록 확장되는지 확인할때). CSS를 이용하면 가장 쉽게 할 수 있다.
> buttons.setStyle("-fx-border-color: red;");

JavaFX에서 제공하는 모든 레이아웃

페인 클래스 | 설명
------------|------------
HBox, VBox | 자식을 가로, 세로로 배치한다.
GridPane | 스윙의 GridBagLayout과 유사하게 자식들을 테이블 형태 그리드에 배치한다.
TilePane | 스윙의 GridLayout과 유사하게 자식들을 그리드에 배치하고 모든 자식에 같은 크기를 부여한다.
BorderPane | 스윙의 BorderLayout과 유사하게 North, East, South, West, Center 영역을 제공한다.
FlowPane | 스윙의 FlowLayout과 유사하게 행 안에 자식들을 흘려서 배치하며, 충분한 공간이 없을 때는 새로운 행을 만든다.
AnchorPane | 자식들을 절대 위치로 배치하거나 페인의 경계를 기준으로 상대적으로 배치할 수 있다. SceneBuilder 레이아웃 도구의 기본값이다.
StackPane | 자식들을 서로 쎃아서 배치한다. 색상을 입힌 사각형 위에 버튼을 배치할 때처럼 컴포넌트들을 장식하는데 유용하다.

*이 절에서는 페인과 컨트롤들을 수작업으로 중첩해서 사용자 인터페이스를 만들었다. JavaFX 스크립트에서는 이렇게 중첩된 구조(씬 그래프)를 기술하는데 사용하는 빌더 문법을 지원했다. JavaFX 2에서는 이 문법을 흉내 내는 빌더 클래스를 사용했다. 다음은 빌더 클래스를 이용해 로그인 다얼로그를 만드는 방법이다.*
```
GridPane pane = GridPaneBuilder.create()
  .hgap(10)
  .vgap(10)
  .padding(new Insets(10))
    .children(
      usernameLabel = LabelBuilder.create()
        .text("User name:")
        .build(),
      passwordLabel = LabelBuilder.create()
        .text("Password:")
        .build(),
      username = TextFieldBuilder.create().build(),
      password = PasswordFieldBuilder.create().build(),
      buttons = HBoxBuilder.create()
        .spacing(10)
        .alignment(Pos.CENTER)
        .children(
          okButton = ButtonBuilder.create().text("Ok").build(),
          cancelButton = ButtonBuilder.create().text("Cancel").build()
        ).build()

    ).build();
```

*굉장히 장황한데도 이게 끝이 아니다(여전히 그리드 제약 조건을 지정해야 한다). JavaFX 8에서는 장황한 이유보다는 구현 문제로 빌더 사용을 권장하지 않는다. 코드를 절약하기 위해 빌더는 각각 상응하는 노드의 상속 구조와 일치하는 상속 트리를 갖춘다. 예를 들면, GridPane이 Pane을 상속하기 때문에 GridPaneBuilder는 PaneBuilder를 상속한다. 하지만 여기서 문제가 생긴다. PaneBuilder.children은 무엇을 리턴해야 할까? 만일 PaneBuilder를 리턴하면 사용자는 서브클래스 프로퍼티를 설정한 후에 슈퍼클래스 프로퍼티를 설정하도록 상당한 주의를 기울려야 한다. JavaFX 설계자들은 이 문제를 제너릭으로 해결하려고 노력했다. PaneBuilder\<B\>의 메서드는 B를 리턴함으로써 GridPaneBuilder가 PaneBuilder\<GridPaneBuilder\>를 상속할 수 있게 한다. 잠깐, 이 방법은 동작하지 않는다. GridPaneBuilder 자체가 제네릭이기 때문에 GridPaneBuilder\<GridPaneBuilder\>여야 하며, 실제로는 `GridPaneBuilder<GridPaneBuilder<something>>`이 된다. 이와 같은 순환성을 몇가지 트릭으로 극복했지만 이러한 트릭은 불안정하기 때문에 자바의 차기 버전에서는 동작하지 않을 것이다. 따라서 빌더 개발은 철회되었다. 빌더 형태를 좋아한다면 스칼라나 그루비의 JavaFX 바인딩을 사용할 수 있다.*

### FXML
JavaFX에서 레이아웃을 기술하는데 사용하는 마크업 언어를 FXML이라고 한다. 로그인 다이얼로그에 해당하는 FXML 마크업이다.

```
<?xml version="1.0" encoding="UTF-8" ?>

<?import java.lang.*?>
<?import java.util.*?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<GridPane hgap="10" vgap="10">
  <padding>
    <Insets top="10" right="10" bottom="10" left="10" />
  </padding>
  <children>
    <Label text="User name:"
      GridPane.columnIndex="0" GridPane.rowIndex="0"
      GridPane.halignment="RIGHT" />
    <Lable text="Password:"
      GridPane.columnIndex="0" GridPane.rowIndex="1"
      GridPane.halignment="RIGHT" />
    <TextField GridPane.columnIndex="1" GridPane.rowIndex="0" />
    <PasswordField GridPane.columnIndex="1" GridPane.rowIndex="1" />
    <HBox GridPane.columnIndex="0" GridPane.rowIndex="2"
          GridPane.columnSpan="2" alignment="CENTER" spacing="10" >
      <children>
        <Buton text="Ok" />
        <Button text="Cancel" />
      </children>
    </HBox>
  </children>
</GridPane>
```

`<GridPane hgap="10" vgap="10">` GridPane 생성후 hgap과 vgap 설정.  
속성이 클래스 이름으로 시작하고 정적 메서드가 오면 해당 메서드가 호출된다. `<TextField GridPane.columnIndex="1" GridPane.rowIndex="0" />`은 정적 메서드인 `GridPane.setColumnIndex(thisTextField, 1)`과 `GridPane.setRowIndex(thisTextField, 0)`이 호출됨을 의미한다.

*일반적으로 FXML 요소는 자바빈즈 명세에 입각해 디폴트 생성자로 생성되며, 프로퍼티 세터나 정적 메서드를 호출함으로써 커스터마이즈된다. 몇가지 예외는 나중에 살펴본다.*

FXML 파일은 다음과 같이 로드한다.

```
public void start(Stage stage) {
  try {
    Parent root = FXMLLoader.load(getClass().getResource("dialog.fxml"));
    stage.setScene(new Scene(root));
    state.show();
  } catch (IOException e) {
    e.printStackTrace();
    System.exit(0);
  }
}
```

이 자체로는 유용하지 않다. 사용자 인터페이스가 표시되지만 프로그램은 사용자가 제공한 값들을 접근할 수 없기 때문이다. 컨트롤과 프로그램 사이를 연결하는 한가지 방법은 자바스크립트에서처럼 id 속성을 사용하는 것이다. 다음과 같이 FXML 파일에 id 속성을 제공한다.

```
<TextField id="username" GridPane.columnIndex="1" GridPane.rowIndex="0" />
```

다음으로 프로그램에서 해당 컨트롤을 조회한다. `TextField username = (TextField)root.lookup("#username");`  
이것보다 좋은 방법은 @FXML 어노테이션을 사용해 컨트롤러 클래스에 컨트롤 객체를 주입할 수 있다. 컨트롤러 클래스는 반드시 Initializable 인터페이스를 구현해야 한다. 컨트롤러의 initialize 메서드는 바인더와 이벤트 처리기를 설치한다. 심지어 FX 애플리케이션 자체를 포함해 어떤 클래스든 컨트롤러가 될 수 있다.  
예를 들어, 다음은 로그인 다이얼로그용 컨트롤러다.

```
public class LoginDialogController implements Initializable {
  @FXML private TextField username;
  @FXML private PasswordField password;
  @FXML private Button okButton;

  public void initialize(URL url, ResourceBundle rb) {
    okButton.disableProperty().bind(
      Bindings.createBooleanBinding(
        () -> username.getText().lenght() == 0 || password.getText().length() == 0,
        username.textProperty(),
        password.textProperty()
      )
    );
    okButton.setOnAction(event -> System.out.println("Verifying " + username.getText() + ":" + password.getText()));
  }
}  
```

FXML에서는 (id가 아닌) fx:id 속성을 이용해 FXML의 컨트롤 요소에 해당하는 컨트롤러의 인스턴스 변수 이름을 제공한다.

```
<TextField fx:id="username" GridPane.columnIndex="1" GridPane.rowIndex="0" />
<PasswordField fx:id="password" GridPane.columnIndex="1" GridPane.rowIndex="1" />
<Button fx:id="okButton" text="Ok" />
```

또한 루트 요소에서 fx:controller 속성를 사용해 컨트롤러 클래스를 선언해야 한다.

```
<GridPane xmlns:fx="http://javafx.com/fxml" hgap="10" vgap="10" fx:controller="LoginDialogController">
```

*컨트롤러에 디폴트 생성자가 없으면(아마도 비즈니스 서비스에 대한 레퍼런스로 초기화되기 때문일 것이다) 프로그래밍을 통ㅎ 설정할 수 있다.*

```
FXMLLoader loader = new FXMLLoader(getClass().getResource(...));
loader.setController(new Controller(service));
Parent root = (Parent)loader.load();
```

> 프로그래밍을 통해 컨트롤러를 설정하는 경우 실제로 위의 코드를 사용한다. 다음 코드는 컴파일은 되지만 정적 메서드인 FXMLLoader.load를 호출해 먼저 생성해둔 로더를 무시하게 된다.
> `FXMLLoader loader = new FXMLLoader();`
> `loader.setController(...);`
> `Parent root = (Parent)loader.load(getClass().getResource(...));  // 오류 - 정적 메서드를 호출한다.`

FXML 파일이 로드될 때 씬 그래프가 만들어지고, 이름을 지정한 컨트롤 객체에 대한 레퍼런스가 컨트롤러 객체의 어노테이션을 붙인 필드에 주입된다. 그런 다음 initialize 메서드가 호출된다. 심지어 FXML 파일에서 대부분의 초기화를 수행할 수도 있다. 단순한 바인딩을 정의하고 어노테이션을 붙인 컨트롤러 메서드를 이벤트 리스너로 설정할 수 있다. 문법은 http://docs.oracle.com/javafx/2/api/javafx/fxml/doc-files/introduction_to_fxml.html 에 문서화되어 있다. 가장 좋은 방법은 시각적인 디자인을 프로그램 동작으로부터 분리하여 사용자 인터페이스 디자이너는 디자인을 만들어내고, 프로그래머는 동작을 구현할 수 있게 하는 것이다.

### CSS
JavaFX는 CSS를 이용해 사용자 인터페이스의 외관을 변경할 수 있게 해준다. 보통은 CSS를 이용하는 방법이 FXML 속성을 전달하거나 자바 메서드를 호출하는 방법보다 훨씬 편리하다. 프로그래밍을 통해 CSS 스타일 시트를 로그하고 씬 그래프에 적용할 수 있다.

```
Scene scene = new Scene(pane);
scene.setStylesheets().add("scene.css");
```

스타일시트에서는 ID가 있는 모든 컨트롤을 참조할 수 있다. 예를 들어, 다음은 GridPane의 외관을 제어하는 봉법을 보여준다. 코드에서는 ID를 설정한다.

```
GridPane pane = new GridPane();
pane.setId("pane");
```

코드에서 패딩이나 간격을 설정하지 않고 CSS를 사용한다.

```
#pane {
  -fx-padding: 0.5em;
  -fx-hgap: 0.5em;
  -fx-vgap: 05em;
  -fx-background-image: url("metal.jpg")
}
```

유감스럽게도 익숙한 CSS 속성을 사용할 수 없고, -fx-로 시작하는 FX 전용 속성을 알아야 한다. 속성 이름은 프로퍼티 이름을 소문자로 바꾸고, 낙타식 표기 대산 하이픈을 사용해 만들어진다. 예를 들어, textAlignment 프로퍼티는 -fx-text-alignment 속성이 된다. ["JavaFX CSS 레퍼런스"](http://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html)에서 지원되는 모든 속성을 찾을 수 있다.

레이아웃 세부 사항으로 코드를 어지럽히는 것보다 CSS를 사용하는 것이 좋다. 더욱이 해상도에 독립적인 em 단위를 쉽게 사용할 수 있다.

개별 ID로 스타일을 주는 대신 스타일 클래스를 사용할 수 있다.

```
HBox buttons = new HBox();
buttons.getStyleClass().add("buttonrow");

// css
.buttonrow {
  -fx-spacing: 0.5em;
}
```

모든 JavaFX 컨트롤과 도형 클래스는 자바 클래스 이름을 소문자로 바꾼 CSS 클래스에 속한다. 예를 들어, 모든 Label 노드는 label 클래스에 소간다. 다음은 모든 레이블의 폰트를 Comic Sans로 변경하는 방법이다.

```
.label {
  -fx-font-family: "Comic Sans MS";
}
```

하지만 이렇게 하지 말자.  
FXML 레이아웃과 함께 CSS를 사용할 수도 있다. 먼저 다음과 같이 루트 페인에 스타일 시트를 첨부한다. `<GridPane id="pane" stylesheets="scene.css">` 다음으로 FXML 쿄드에 id 또는 styleClass 속성을 넣는다. `<HBox styleClass="buttonrow">`  
이제 대부분의 스타일링을 CSS에서 지정하고 FXML은 레이아웃용으로만 사용할 수 있다. 불행히도 FXML에서 모든 스타일링을 완진히 제거할 수는 없다. 예를 들어, 아직까지는 CSS에서 그리드 셀 정렬을 지정할 수 있는 방법이 없다.

*다음과 같이 프로그래밍을 통해 CSS 스타일을 적용할 수도 있다. `buttons.setStyle("-fx-border-color: red;");` 이렇게 하면 디버깅에는 유용하지만, 일반적으로 외부 스타일시트를 사용하는 것이 좋다.*

### 애니메이션과 특수 효과
JavaFX가 생거날 당시 특수 효과가 엄청나게 유행했다. JavaFX는 Shadow, Blur, Movement를 만들어내기 쉽게 해준다. 웹에서 많은 데모를 찾을 수 있다.  
JavaFX는 일정 시간 동안 노드의 프포퍼티를 변하게 하는 다양한 트랜지션을 정의한다. 다음은 3초 동안 노드를 *x, y* 방향으로 커지게 하는 방법이다.

```
ScaleTransition st = new ScaleTransition(Duration.millis(3000));
st.setByX(1.5);
st.setByY(1.5);
st.setNode(yesButton);
st.play();
```

기본으로 트랜지션은 목표점을 만나면 종료한다. 다음과 같이 무한정 반복되게 할 수 있다.

```
st.setCycleCount(Animation.INDDEFINTE);
st.setAutoReverse(true);
```

FadeTransition은 노드의 불투명도를 변경한다. 다음은 No 버튼을 배경으로 사라지게 하는 방법이다.

```
FadeTransition ft = new FadeTransition(Duration.millis(3000));
ft.setFromValue(1.0);
ft.setToValue(0);
ft.setNode(noButton);
ft.play();
```

모든 JavaFX 노드는 자신의 중앙을 기준으로 회전할 수 있다. RotateTransition은 노드의 rotate 프로퍼티를 변경한다. 다음 코드는 Maybe 버튼의 회전에 애니메이션을 준다.

```
RotationTransition rt = new RotateTransition(Duration.millis(3000));
rt.setByAngle(180);
rt.setCycleCount(Animation.INDEFINITE);
rt.setAutoReverse(true);
rt.setNode(maybeButton);
rt.play();
```

ParallelTransition과 SequentialTransition 콤비네이터를 이용하면 트랜지션을 합성해 병렬 또는 순차적으로 수행할 수있다. 여러 노드에 애니메이션을 적용해야 하는 경우 Group 노드에 집어넣고, 이 노드에 애니메이션을 줄 수 있다. 이와 같은 종류의 동작을 만들어야 할때 JavaFX의 클래스들을 이용하면 좋다.  
특수효과 또한 적용하기가 아주 쉽다. 만일 멋진 캡션에 드롭 새도우가 필요하면 DropShadow 효과를 만들어서 노드의 effect 프로퍼티로 설정한다. Text 노드에 드롭 새도우를 적용하는 코드이다.

```
DropShadow dropShadow = new DropShadow();
dropShadow.setRadius(5.0);
dropShadow.setOffsetX(3.0);
dropShadow.setOffsetY(3.0);
dropShadow.setColor(Color.GRAY);

Text text = new Text();
text.setFill(Color.RED);
text.setText("Drop shadow");
text.setFont(Font.font("sans", FondWeight.BOLD, 40));
text.setEffect(dropShadow);

text2.setEffect(new Glow(0.8));     // Glow 효과
text3.setEffect(new GaussianBlur());    // Blur 효과
```

### 화려한 컨트롤
JavaFX는 스윙과 마찬가지로 콤보 박스, 탭 페인, 테이블 지원은 물론이고 스윙에서는 전혀 볼수 없던 데이트픽커와 어코디언 같은 사용자 인터페이스 컨트롤도 지원한다.  
다음은 파이 차트를 만드는 코드이다.

```
ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
  new PieChart.Data("Asia", 4298723000.0),
  new PieChart.Data("North America", 355361000.0),
  new PieChart.Data("South America", 616644000.0),
  new PieChart.Data("Europe", 742452000.0),
  new PieChart.Data("Africa", 1110635000.0),
  new PieChart.Data("Oceania", 38304000.0),
);
final PieChart chart = new PidChart(pidChartData);
chart.setTitle("Population of the Continents");
```

사용 및 커스터마이즈 할 수 있ㅇ는 차트 유형은 총 여섯가지다. 더 자세한 정보는 http://docs.oracle.com/javafx/2/chart/chart-overview.htm 을 참고.

스윙에서는 HTML을 JEditPane에서 보여줄 수 있었지만, 대부분의 실세계 HTML을 표시할 때는 렌더링 품질이 떨어졌다. 실제로 브라우저 구현은 아주 어렵기 때문에 대부분의 브라우저는 오픈 소스 웹킷 엔진을 기반으로 만들어졌다. JavaFX도 마찬가지다. WebView는 내장형 네이티브 웹킷 윈도우를 표시한다.

```
String location = "http://daum.net";
WebView browser = new WebView();
WebEngine engine = browser.getEngine();
engine.load(location);
```

*WebView는 플러그인을 지원하지 않으므로, 플래시 애니메이션이나 PDF 문서를 보여주는데는 사용할 수 없다. 또한, 애플릿도 보여주지 못한다.*

JavaFX 이전에는 자바에서 미디어 재생 기능이 초라했다. 오디오 및 비디오 재생을 구현하는 작업은 브라우저 구현보다도 어렵기 때문에 JavaFX에서는 이미 존재하는 툴킷인 GStreamer 프레임워크를 이용한다. 비디오를 재생하려면 먼저URL 문자열로부터 Media 객체를 생성한다. 다음으로 미디어를 재생하는 MediaPlayer와 이 플레이어를 보여주는 MediaView를 생성한다.

```
Path path = Paths.get("moonlanding.mp4");
String location = path.toUri().toString();
Media media = new Media(location);
MediaPlayer player = new MediaPlayer(media);
player.setAutoPlay(true);
MediaView view = new MedaiView(player);
view.setOnError(e -> System.out.println(e));
```

비디오가 재성되지만, 비디오 컨트롤이 없다. [자신만의 컨트롤](http://docs.oracle.com/javafx/2/media/playercontrol.htm)을 직접 추가할 수 있지만, 기본 컨트롤 집합이 제공되었다면 좋았을 것이다.

*가끔 GStreamer가 특정 비디오 파일을 다룰 수 없을 것이다.코드 샘플에 있는 오류 처리기에서는 재생 문제를 진단할 수 있도록 GStreamer 메시지를 표시한다.*

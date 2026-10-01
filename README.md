
# 🍀  SimpleLotto  🍀

<br/>

<p align="center"> 
 
  <br/>
  Spring MVC의 요청과 응답 흐름을 이해하기 위해 만든 첫 웹 프로젝트입니다. <br/>
  로또 번호 생성과 댓글 기능을 직접 구현하며 Controller, Service, Repository의 역할과 서버 렌더링 흐름을 학습했습니다. <br/>
  <br/> <br/>
 
<img width="700" height="400" alt="5" src="https://github.com/user-attachments/assets/b1a6e070-3d39-4770-8e1e-488c9cf048fc" />
</p>

<br/><br/><br/>

### 🔶 프로젝트 관련 링크

+ [Blog (프로젝트 기록)](https://post-this.tistory.com/category/%F0%9F%92%BB%20%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8/%F0%9F%8C%BC%EB%A1%9C%EB%98%90%20%ED%8E%98%EC%9D%B4%EC%A7%80%F0%9F%8C%BC)

+ [YouTube (동작화면)](https://youtu.be/WAs3csJd8Lc)

+ [Figma (다이어그램)](https://www.figma.com/board/R88KQNiUEI03DpuBg9kzxM/SimpleLotto?node-id=0-1&p=f&t=FbS9SmuOUwQKIBRH-0)

<br/><br/>


### 🔶 프로젝트 설명
처음 읽는 분도 이해할 수 있도록 주요 기능과 구현 과정을 중심으로 정리했습니다. <br/>

<br/>

<p align="center"> 
<img width="770" height="300" alt="SimpleLotto 다이어그램" src="https://github.com/user-attachments/assets/e6681159-b8d6-4622-bb7c-e9bbc5bb022a" />
</p>
  
<br/>

+ 버튼을 클릭하면 중복되지 않는 로또 번호 6개를 생성합니다.
+ 생성된 번호를 클립보드에 복사할 수 있습니다.
+ 댓글을 작성하면 H2 Database에 저장합니다.
+ 댓글 화면에는 가장 최근에 작성된 댓글 10개를 표시합니다.
+ Spring Boot MVC와 Thymeleaf를 사용해 서버에서 데이터를 전달하고 화면을 렌더링합니다.


<br/><br/>


### 🔶 기술 스택 & 라이브러리
+ Java 17
+ Spring Boot 3.3.5
+ Spring Data JPA
+ Thymeleaf
+ H2 Database
+ JavaScript


<br/><br/>


### 🔶 프로젝트 목표
+ Spring MVC의 기본적인 요청과 응답 흐름 이해하기
+ Controller, Service, Repository, Entity의 역할 구분하기
+ Service에서 처리한 데이터를 Model을 통해 Thymeleaf 화면으로 전달하기
+ GET과 POST 요청이 각각 어떤 상황에서 사용되는지 이해하기
+ JPA를 활용해 데이터를 저장하고 조회하는 흐름 경험하기


<br/><br/>


### 🔶 핵심 구현과 배운 점
1) 중복 없는 로또 번호 생성과 화면 전달 <br/>
로또 번호는 1부터 45 사이에서 중복 없이 6개를 생성해야 했고, 화면에서는 번호를 각각 원형 UI에 표시해야 했습니다.

<br/>

+ 처음에는 중복 제거와 오름차순 정렬을 처리하기 위해 TreeSet을 사용했습니다.

```
Set<Integer> lottoSet = new TreeSet<>();

while (lottoSet.size() < 6) {
 lottoSet.add(rd.nextInt(45) + 1); }
```

<br/>

+ `TreeSet`은 중복 제거와 정렬에는 편리했지만 인덱스로 값을 가져올 수 없었습니다.
+ 당시에는 화면의 각 영역에 번호를 하나씩 전달하기 위해 `ArrayList`로 변환한 뒤 Model에 값을 담았습니다.
```
List<Integer> lottoList = new ArrayList<>(lottoSet);
```
```
model.addAttribute("Number1",lottoList.get(0));
model.addAttribute("Number2",lottoList.get(1));
model.addAttribute("Number3",lottoList.get(2));
...
```

<br/>

+ 이 구현을 통해 자료구조마다 제공하는 기능과 접근 방식이 다르다는 점을 직접 경험할 수 있었습니다.
+ 현재 다시 구현한다면 각 번호를 별도의 Model 값으로 전달하기보다, 번호 목록 자체를 전달하고 Thymeleaf 반복문으로 출력하는 방식으로 더 단순하게 구성할 수 있습니다.

<br/>

----

2) 최신 댓글 10개만 조회하기 <br/>
전체 댓글을 조회한 뒤 화면에서 10개만 보여주는 방식도 생각했지만, 사용하지 않을 데이터까지 DB에서 가져올 필요가 없다고 판단했습니다.

<br/>

+ 그래서 Spring Data JPA의 `Pageable`을 사용해 조회 단계에서부터 10개만 가져오도록 구현했습니다.

```
public Page<Comment> get10Comments() {
    Pageable pageable = PageRequest.of(0, 10);
    return boardRepository.findAllByOrderByIdDesc(pageable);
}
```
```
public interface BoardRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByOrderByIdDesc(Pageable pageable);
}
```

<br/>

+ 이를 통해 화면에서 데이터를 잘라내는 것과 DB 조회 자체를 제한하는 것은 다르다는 점을 배울 수 있었습니다.

<br/>


----

3) 브라우저와 서버의 역할 구분 <br/>

+ 생성된 로또 번호를 복사하는 기능을 처음 구현할 때는 Java에서 처리하는 방법을 고민했습니다.
+ 하지만 클립보드는 사용자의 브라우저에서 동작하는 기능이기 때문에 서버에서 실행되는 Java보다 브라우저에서 실행되는 JavaScript가 담당하는 것이 적절하다고 판단했습니다.
  
```
const numbers = Array.from(document.querySelectorAll(".second h1"))
    .map(el => el.textContent.trim());

const numbersString = numbers.join(" ");
```

+ 반대로 댓글처럼 서버에 저장되는 데이터는 브라우저의 입력 제한만 신뢰하지 않고 서버에서도 한 번 더 검증하도록 구현했습니다.
+ 아래는 초기에 댓글 길이만 확인하던 코드입니다. 이후 보완한 내용은 다음 항목에 정리했습니다.

```
public void saveComment(String content) {
    if (content.length() >= 1 && content.length() <= 25) {
        boardRepository.save(new Comment(content));
    }
}
```

+ 이 과정에서 기능의 위치를 정할 때 단순히 `어떤 언어로 구현할 수 있는가`가 아니라 `어디에서 실행되어야 하는 기능인가`를 구분해야 한다는 점을 배웠습니다.


<br/><br/>


### 🔶 개선한 점 혹은 개선할 점

1) 댓글 입력값 검증 보완 <br/>

+ 초기 구현에서는 댓글 길이만 확인했습니다.

```
content.length() >= 1 && content.length() <= 25
```

+ 하지만 이후 null 값이나 공백만 입력된 경우도 고려해야 한다는 점을 확인했고 입력값 검증 범위를 보완했습니다.

<br/>

2) 댓글 URL을 리소스 중심으로 변경 <br/>

+ 초기 구현에서는 댓글 화면의 저장 버튼을 누르면 브라우저가 `POST /save` 요청을 보냈습니다.
+ 기능 자체는 정상적으로 동작하지만 URL만 보고 어떤 데이터를 저장하는지 알기 어렵다는 문제가 있었습니다.
+ 이후 댓글이라는 리소스가 드러나도록 `POST /board/comments`로 변경했습니다.
+ 이를 통해 URL도 단순히 요청을 처리하기 위한 문자열이 아니라 서버가 제공하는 자원을 표현하는 설계의 일부라는 점을 배웠습니다.

<br/>

3) 운영 환경에서의 댓글 관리 <br/>

+ 현재 화면에서는 최신 댓글 10개만 조회하지만, 작성된 댓글 데이터 자체는 계속 DB에 저장됩니다.
+ 학습 목적의 프로젝트이기 때문에 별도의 데이터 보관 정책은 구현하지 않았습니다.
+ 실제 운영 서비스라면 서비스 요구사항에 따라 페이지네이션을 제공하거나, 데이터 보관 기간과 삭제 정책을 별도로 정의하는 방식을 고려할 수 있습니다.

<br/><br/>

### 🔶 회고
+ SimpleLotto는 Spring MVC와 JPA를 처음 사용하며 웹 요청이 서버를 거쳐 화면과 데이터베이스로 이어지는 기본 흐름을 이해하기 위해 만든 프로젝트입니다.
+ 처음에는 기능을 동작시키는 것 자체에 집중했지만, 이후 프로젝트를 경험하고 다시 살펴보면서 같은 기능도 자료구조 선택, DB 조회 범위, 입력값 검증, URL 설계처럼 다양한 관점에서 개선할 수 있다는 것을 알게 되었습니다.
+ 첫 구현을 그대로 남기는 것에서 끝내지 않고 이후 배운 내용을 기준으로 다시 검토하고 개선하면서 동작하는 코드를 만드는 것과 유지하기 좋은 구조를 만드는 것은 다르다는 점을 배웠습니다.

<br/>

### 🔶 실행 방법

+ Java 17 환경에서 이 저장소 폴더의 `./gradlew bootRun`을 실행합니다.
+ 실행 후 브라우저에서 `localhost:8080`으로 접속하면 됩니다.
+ 테스트는 `./gradlew test`로 확인할 수 있습니다.

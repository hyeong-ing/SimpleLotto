
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

+ YouTube (동작화면)

+ [Figma (다이어그램)](https://www.figma.com/board/R88KQNiUEI03DpuBg9kzxM/SimpleLotto?node-id=0-1&p=f&t=FbS9SmuOUwQKIBRH-0)

<br/><br/>


### 🔶 프로젝트 설명

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


</br></br>


### 🔶 기술 스택 & 라이브러리
+ Java 17
+ Spring Boot 3.3.5
+ Spring Data JPA
+ Thymeleaf
+ H2 Database
+ JavaScript


</br></br>


### 🔶 프로젝트 목표
+ Spring MVC의 기본적인 요청과 응답 흐름 이해하기
+ Controller, Service, Repository, Entity의 역할 구분하기
+ Service에서 처리한 데이터를 Model을 통해 Thymeleaf 화면으로 전달하기
+ GET과 POST 요청이 각각 어떤 상황에서 사용되는지 이해하기
+ JPA를 활용해 데이터를 저장하고 조회하는 흐름 경험하기


</br></br>


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

```
public void saveComment(String content) {
    if (content.length() >= 1 && content.length() <= 25) {
        boardRepository.save(new Comment(content));
    }
}
```

+ 이 과정에서 기능의 위치를 정할 때 단순히 `어떤 언어로 구현할 수 있는가`가 아니라 `어디에서 실행되어야 하는 기능인가`를 구분해야 한다는 점​을 배웠습니다. 


<br/><br/>


### 🔶 아쉬운 점 및 개선 방향

1) 로또 번호 생성 로직 (수정 완료) <br/>

+ 현재 로또 번호 생성 기능은 @GetMapping과 @PutMapping에서 동일한 로직이 반복되고 있음을 발견했습니다. <br/>
+ 하지만 로또 번호는 단순히 생성해서 화면에 보여주는 기능입니다. <br/>
+ 서버 데이터를 수정하는 동작이 아니기 때문에, 하나의 @GetMapping만으로도 충분히 처리할 수 있습니다.<br/>

<br/>

2) 오래된 댓글 관리 기능 부재 <br/>

+ 현재 댓글은 Pageable을 사용해 최신 댓글 10개만 조회합니다.
+ 하지만 오래된 댓글을 삭제하거나 관리하는 기능은 없기 때문에, 댓글이 계속 작성되면 DB에는 데이터가 누적됩니다.
+ 현재 프로젝트는 실제 서버를 운영할 목적이 아니었기 때문에 이 부분까지 구현하지 않았습니다.
+ 만약 실제 운영 환경이라면 최신 댓글 10개만 유지하고 그 외 댓글은 즉시 삭제하거나 일정 주기마다 오래된 댓글을 정리하는 방식으로 개선할 수 있습니다. <br/>

<br/>

3) 댓글 입력값 검증 보완 (수정 완료) <br/>

+ 현재는 댓글 길이가 1자 이상 25자 이하일 때만 저장되도록 처리했습니다.
+ 다만 content가 null이거나 공백만 입력된 경우까지 고려하면 더 안전한 코드가 될 수 있습니다.

<br/>

4) 댓글 저장 URL 설계 (수정 완료) <br/>

+ 댓글 화면에서 저장 버튼을 누르면 브라우저가 POST /save 요청을 보냅니다.
+ 동작 자체는 문제가 없지만, /save라는 URL이 동작 이름 중심입니다.
+ REST 관점으로 보면 댓글은 하나의 자원이므로, POST /board/comments와 같은 형태로 변경할 필요가 있습니다.

<br/>











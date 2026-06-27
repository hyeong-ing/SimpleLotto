
# 🍀  SimpleLotto  🍀

<br/>

<p align="center"> 
 
  <br/>
  첫 프로젝트로 간단하게 로또 번호를 출력시키고 댓글을 남기는 기능을 만들었습니다. <br/>
  지금 보니 너무나도 엉성하고 부족한 부분이 많이 보여 부끄럽습니다. <br/>
  그래도 발전해나가는 그 과정 중 첫 시작이라고 생각해주시면 감사하겠습니다. <br/>
  <br/> <br/>
 
<img width="700" height="400" alt="5" src="https://github.com/user-attachments/assets/b1a6e070-3d39-4770-8e1e-488c9cf048fc" />
</p>

<br/><br/><br/>

### 🔶 프로젝트 관련 링크

+ [Blog (프로젝트 기록)](https://post-this.tistory.com/category/%F0%9F%92%BB%20%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8/%F0%9F%8C%BC%EB%A1%9C%EB%98%90%20%ED%8E%98%EC%9D%B4%EC%A7%80%F0%9F%8C%BC)

+ Youtube (동작화면)

+ [Figma (다이어그램)](https://www.figma.com/board/R88KQNiUEI03DpuBg9kzxM/SimpleLotto?node-id=0-1&p=f&t=FbS9SmuOUwQKIBRH-0)

<br/><br/>


### 🔶 프로젝트 설명

<br/>

<p align="center"> 
<img width="770" height="300" alt="SimpleLotto 다이어그램" src="https://github.com/user-attachments/assets/e6681159-b8d6-4622-bb7c-e9bbc5bb022a" />
</p>
  
<br/>

+ 사용자가 버튼을 클릭하면 로또 번호 6개를 랜덤으로 생성합니다.
+ 사용자가 생성된 번호를 복사 가능합니다.
+ JPA와 H2 데이터 베이스를 이용해 댓글 데이터를 저장합니다.
+ Spring Boot MVC와 Thymeleaf를 활용해 서버에서 화면을 렌더링합니다.


</br></br>


### 🔶 프로젝트 구조
+ Java 17
+ Spring Boot MVC
+ Thymeleaf
+ H2 DataBase


</br></br>


### 🔶 프로젝트 목표
+ Spring MVC의 기본 요청과 응답 흐름을 이해하기
+ Controller, Service, Repository, Entity 역할 나누기
+ Controller에서 요청을 받고, Service에서 로직을 처리하고 Model을 통해 Thymeleaf 화면에 데이터 전달하기
+ @GetMapping과 @PostMapping이 어떻게 다르게 동작하는지 이해하기


</br></br>


### 🔶 문제 해결 및 핵심 로직
1) 로또 번호 개별 출력 문제 <br/>
로또 번호는 중복 없이 6개를 생성해야하고, 화면에서는 각 번호를 원형 영역 안에 하나씩 출력해야 했습니다.

<br/>

+ 처음에는 중복 제거와 오름차순 정렬을 동시에 처리하기 위해 TreeSet을 사용했습니다.

```
Set<Integer> lottoSet = new TreeSet<>();

while (lottoSet.size() < 6) {
 lottoSet.add(rd.nextInt(45) + 1); }
```

<br/>

+ 하지만 TreeSet은 인덱스로 접근할 수 없다는 특징 때문에 생성된 번호를 하나씩 분리해 화면에 전달하기 어려웠습니다.
+ 이를 해결하기 위해 TreeSet을 ArrayList로 변환한 뒤, 각 번호를 Model 담아 Thymeleaf 화면으로 전달했습니다.

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
<br/>

----

2) 최신 댓글 10개만 조회하기 <br/>
전체 댓글을 가져온 뒤 화면에서 10개만 보여주는 방식을 생각했지만, 조회 단계에서부터 필요한 데이터만 가져오는 것이 더 효율적이라고 생각했습니다.

<br/>

+ 그래서 Spring Data JPA의 pageable을 사용해 최신 댓글 10개만 조회하도록 구현했습니다.
  
```
@GetMapping("/board")
public String boardPage(Model model) {
    model.addAttribute("comments", boardService.get10Comments());
    return "board";
}
```
+ PageRequest.of(0, 10)으로 조회 개수를 10개로 제한하고, id 기준 내림차순 정렬을 사용해 최신 댓글부터 가져오도록 했습니다.

```
public Page<Comment> get10Comments() {
    Pageable pageable = PageRequest.of(0,10);
    return boardRepository.findAllByOrderByIdDesc(pageable);
}
```
```
public interface BoardRepository extends JpaRepository<Comment, Long> { Page<Comment> findAllByOrderByIdDesc(Pageable pageable); }
```

<br/>
<br/>

----

3) 댓글 길이 검증 <br/>
댓글은 짧은 한 줄 댓글 형태로 사용하기 위해 1자 이상 25자 이하만 저장되도록 제한했습니다.

<br/>

+ 화면에서는 maxlength를 사용해 입력 길이를 제한했습니다.

``` 
<input class="write"
       type="text"
       name="content"
       placeholder="짧은 댓글 남기기 (25자이내)"
       maxlength="25">
```

+ 서버에서도 한 번 더 길이를 검증해 조건에 맞는 댓글만 저장하도록 했습니다.
  
```
public void saveComment(String content) {
    if (content.length() >= 1 && content.length() <= 25)
        boardRepository.save(new Comment(content));
}
```

<br/>
<br/>

----

4) 로또 번호 복사 기능 <br/>
처음에는 Java로 복사 기능을 처리하는 방법을 고민했습니다.


+ 그런데 클립보드 복사는 사용자의 브라우저에서 일어나는 동작이기 때문에 서버에서 실행되는 Java보다 JavaScript로 처리하는 것이 더 적합하다고 판단했습니다.
+ 화면에 출력된 번호를 선택해 텍스트만 추출하고, 공백으로 연결한 뒤 클립보드에 저장하도록 구현했습니다.
  
```
const numbers = Array.from(document.querySelectorAll(".second h1"))
    .map(el => el.textContent.trim());

const numbersString = numbers.join(" ");
navigator.clipboard.writeText(numbersString).then(() => {
    alert("럭키 로또 숫자 복사 완료!");
}).catch(err => {
    console.error("복사 실패:", err);
    alert("복사에 실패했습니다.");
});
```

<br/><br/>


### 🔶 아쉬운 점 및 개선 방향

1) 로또 번호 생성 로직 (수정완료) <br/>

+ 현재 로또 번호 생성 기능은 @GetMapping과 @PutMapping에서 같은 로직이 반복되고 있는 점을 발견했습니다. <br/>
+ 하지만 로또 번호는 단순히 생성해서 화면에 보여주는 기능입니다. <br/>
+ 서버 데이터를 수정하는 동작이 아니기 때문에, 하나의 @GetMapping만으로도 충분히 처리할 수 있습니다.<br/>

<br/>

2) 오래된 댓글 관리 기능 부재 <br/>

+ 현재 댓글은 Pageable을 사용해 최신 댓글 10개만 조회합니다.
+ 하지만 오래된 댓글을 삭제하거나 관리하는 기능은 없기 때문에, 댓글이 계속 작성되면 DB에는 데이터가 누적됩니다.
+ 현재 프로젝트는 실제 서버를 운영할 목적이 아니었기 때문에 이 부분까지 구현하지 않았습니다.
+ 만약 실제 운영 환경이라면 최신 댓글 10개만 유지하고 그 외 댓글은 즉시 삭제하거나 일정 주기마다 오래된 댓글을 정리하는 방식으로 개선할 수 있습니다. <br/>

<br/>

3) 댓글 입력갑 검증 보완 (수정완료) <br/>

+ 현재는 댓글 길이가 1자 이상 25자 이하일 때만 저장되도록 처리했습니다.
+ 다만 content가 null이거나 공백만 입력된 경우까지 고려하면 더 안전한 코드가 될 수 있습니다.

<br/>

4) 댓글 저장 URL 설계 (수정완료) <br/>

+ 댓글 화면에서 저장 버튼을 누르면 브라우저가 POST/save 요청을 보냅니다.
+ 동작 자체는 문제가 없지만, /save라는 URL이 동작 이름 중심입니다.
+ REST 관점으로 보면 댓글은 하나의 자원이므로, POST/board/comments와 같은 형태로 변경할 필요가 있습니다.

<br/>











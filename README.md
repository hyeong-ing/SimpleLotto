
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

<br/><br/>

****

<br/><br/>

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


### 🔶 코드 설명
1) 로또 번호 생성 로직

<br/>

+ TreeSet을 사용해서 중복을 허용하지 않도록 하면서 값을 자동으로 오름차순 정렬하도록 했습니다.

```
Set<Integer> lottoSet = new TreeSet<>();
```

<br/>

+ 하지만 TreeSet은 인덱스로 접근할 수 없다는 특징이 있습니다. <br/>
로또 번호는 각각 다른 원형 이미지에 하나씩 나눠 담아야해서 결국 ArrayList로 변환했습니다.

```
List<Integer> lottoList = new ArrayList<>(lottoSet);
```

<br/>

+ get( ) 메서드를 사용해 각 번호를 Model에 개별적으로 담아 Thymeleaf 화면으로 전달했습니다.

```
lottoList.get(0)
lottoList.get(1)
lottoList.get(2)
...
```
```
model.addAttribute("Number1",lottoList.get(0));
model.addAttribute("Number2",lottoList.get(1));
model.addAttribute("Number3",lottoList.get(2));
...
```

<br/>
<br/>


2) 댓글 페이지 조회 로직

<br/>

+ 사용자가 댓글 페이지에 들어가면 /board 요청이 발생합니다. <br/>
그러면 컨트롤러는 boardService.get10Comments()를 호출해서 댓글 목록을 가져옵니다.
  
```
@GetMapping("/board")
public String boardPage(Model model) {
    model.addAttribute("comments", boardService.get10Comments());
    return "board";
}
```

<br/>

+ JPA의 Pageable을 사용해 첫 번째 페이지에서 10개만 가져옵니다. <br/>
그리고 댓글을 id 기준 내림차순으로 조회합니다.

```
public Page<Comment> get10Comments() {
    Pageable pageable = PageRequest.of(0,10);
    return boardRepository.findAllByOrderByIdDesc(pageable);
}
```   

<br/>
<br/>

3) 뎃글 저장 로직

<br/>

+ 사용자가 댓글을 입력하고 저장 버튼을 누르면 /save 요청이 발생합니다. <br/>
@RequestParam으로 댓글 내용을 받습니다. <br/>
그 다음 Service에서 처리 후 /board로 다시 이동을 시켜 최신 댓글 목록을 다시 조회하도록 했습니다.
```
@GetMapping("/save")
public String saveComment( @RequestParam String content) {
    boardService.saveComment(content);
    return "redirect:/board";
}
```

















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


### 🔶 문제 해결
+ TreeSet으로 받은 랜덤한 로또 번호 6자리를 하나씩 분리하기 <br/>

<br/>

TreeSet은 중복을 허용하지 않고 값을 오름차순으로 정렬할 수 있다는 장점이 있어 로또 번호 생성에 적합했습니다.<br/>
하지만 인덱스 기반 접근을 지원하지 않아, 생성된 6개의 번호를 분리하는데 어려움이 있었습니다. <br/>
이를 해결하기 위해 TreeSet으로 번호를 생성한 뒤 ArrayList로 변환했습니다. 중복 제거와 정렬은 유지하면서, 각 번호를 개별적으로 출력할 수 있었습니다.

<br/><br/>

+ 화면에 보여지는 댓글을 10개로 제한하기 <br/>
전체 댓글을 가져온 뒤 화면에서 10개만 보이게 처리하는 방식을 떠올렸습니다. <br/>
하지만 그렇게 하면 필요하지 않은 데이터까지 모두 가져오게 되어 비효율적이라고 판단했습니다. </br>
그래서 JPA의 Pagealbe을 사용해 조회 단계에서부터 최신 댓글 10개만 가져오도록 수정했습니다. </br>
PageRequest.of(0, 10)으로 조회 개수를 제한하고, id 기준 내림차순으로 정렬했습니다. 

<br/><br/>



<br/><br/>

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


2) 최신 댓글 10개만 조회하기 <br/>
전체 댓글을 가져온 뒤 화면에서 10개만 보여주는 방식을 생각했지만, 조회 단계에서부터 필요한 데이터만 가져오는 것이 더 효율적이라고 생각했습니다.

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

3) 로또 번호 복사 로직
<br/>

+ 생성된 로또 번호를 사용자가 복사할 수 있도록 JavaScript의 Clipboard API를 사용했습니다. <br/>
번호 영역을 선택해 텍스트를 추출하고, 공백으로 연결한 뒤 클립보드에서 저장하도록 구현했습니다.

``` 
        const numbers = Array.from(document.querySelectorAll(".second h1")).map(el => el.textContent.trim());
        const numbersString = numbers.join(" ");
```
```
      navigator.clipboard.writeText(numbersString).then(() => {
            alert("럭키 로또 숫자 복사 완료!");
        }).catch(err => {
            console.error("복사 실패:", err);
            alert("복사에 실패했습니다.");
        });
    }
```
```
    const copyImage = document.querySelector("img[alt='copy']");
    if (copyImage) {
        copyImage.addEventListener("click", copyNumbers);
    }
```













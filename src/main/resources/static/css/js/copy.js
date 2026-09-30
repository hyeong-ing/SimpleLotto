
document.addEventListener("DOMContentLoaded", () => {
    function copyNumbers() {

        const numbers = Array.from(document.querySelectorAll(".second h1")).map(el => el.textContent.trim());
        const numbersString = numbers.join(" ");

        navigator.clipboard.writeText(numbersString).then(() => {
            alert("럭키 로또 숫자 복사 완료!");
        }).catch(err => {
            console.error("복사 실패:", err);
            alert("복사에 실패했습니다.");
        });
    }

    const copyImage = document.querySelector("img[alt='copy']");
    if (copyImage) {
        copyImage.addEventListener("click", copyNumbers);
    }
});

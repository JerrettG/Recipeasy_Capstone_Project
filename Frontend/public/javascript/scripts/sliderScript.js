let sliderRightArrows = document.querySelectorAll(".slider-arrow-right");
let sliderLeftArrows = document.querySelectorAll(".slider-arrow-left");

sliderLeftArrows.forEach(leftArrow => leftArrow.addEventListener("click", slideLeft));
sliderRightArrows.forEach(rightArrow => rightArrow.addEventListener("click", slideRight));

function slideLeft(event) {
    let slider = event.srcElement.closest(".userRecommendations-arrow").nextElementSibling;
    let totalChildren = slider.children.length;
    let sliderWith = slider.scrollWidth;
    let childWidth = sliderWith / totalChildren;
    let currentIndex = Math.floor(slider.scrollLeft / childWidth);
    let nextIndex = Math.round(currentIndex - 1);

    if (nextIndex < 0)
        nextIndex = 0
    slider.children[nextIndex].scrollIntoView({behavior: "smooth", inline:"start", block:"center"});
}

function slideRight(event) {
    let slider = event.srcElement.closest(".userRecommendations-arrow").previousElementSibling;

    let totalChildren = slider.children.length;
    let sliderWith = slider.scrollWidth;
    let childWidth = sliderWith / totalChildren;
    let currentIndex = Math.floor(slider.scrollLeft / childWidth);
    let nextIndex = Math.round(currentIndex + 1);
    if (nextIndex > totalChildren-1)
        nextIndex = 0;

    slider.children[nextIndex].scrollIntoView({behavior: "smooth", inline:"start", block:"center"});
}
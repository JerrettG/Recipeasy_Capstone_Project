function toggleDropdown(event) {
    let dropButton = event.srcElement.closest(".account-dropbutton");
    const ariaExpanded = dropButton.getAttribute('aria-expanded');
    if (ariaExpanded === 'true') {
        const dropButtonContainer = dropButton.closest('.dropbutton-container');
        dropButton.setAttribute('aria-expanded', false);
        const dropdownContent = dropButtonContainer.nextElementSibling;
        dropdownContent.style.maxHeight = '40em';
    } else if (ariaExpanded === 'false') {
        const dropButtonContainer = dropButton.closest('.dropbutton-container');
        dropButton.setAttribute('aria-expanded', true);
        const dropdownContent = dropButtonContainer.nextElementSibling;
        dropdownContent.style.maxHeight = '0';
    }
}

const loadNav = () => {
    const dropButton = document.querySelector(".account-dropbutton");
    dropButton.addEventListener("click", toggleDropdown);
    let loginLogout = document.getElementById("login-logout");
    loginLogout.innerHTML = isAuthenticated ? '<a class="dropdown-links" href="/logout">Logout</a>' : '<a class="dropdown-links" href="/login">Login</a>';

}

window.addEventListener("DOMContentLoaded", loadNav);

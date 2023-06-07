import Toastify from "toastify-js";

export default class BaseClass {

    /**
     * Binds all of the methods to "this" object. These methods will now have the state of the instance object.
     * @param methods The name of each method to bind.
     * @param classInstance The instance of the class to bind the methods to.
     */
    bindClassMethods(methods, classInstance) {
        methods.forEach(method => {
            classInstance[method] = classInstance[method].bind(classInstance);
        });
    }

    formatCurrency(amount) {
        const formatter = new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD',
        });
        return formatter.format(amount);
    }

    capitalizeFirstLetter(str) {
        let words = str.split(" ");
        words = words.map(word => word[0].toUpperCase() + word.substring(1));
        return words.join(" ");
    }

    showLoading(isLoading) {
        document.getElementById("loadingScreen").style.display = isLoading ? "flex" : "none";
    }

    showPopup(event) {
        let btn = event.srcElement;
        let popup = btn.getAttribute("data-popup");
        document.querySelector(popup).style.display = "block";
        document.querySelector(".popup-screen").style.display = "block";
    }

    closePopup(event) {
        let btn = event.srcElement;
        let popup = btn.closest(".popup");
        popup.remove();
        document.querySelector(".popup-screen").style.display = "none";
    }

    showMessage(message) {
        Toastify({
            text: message,
            duration: 4500,
            gravity: "top",
            position: 'right',
            close: true,
            style: {
                background: "var(--chalk-brd-green)"
            }
        }).showToast();
    }

    errorHandler(error) {
        Toastify({
            text: error,
            duration: 4500,
            gravity: "top",
            position: 'right',
            close: true,
            style: {
                background: "var(--error-color)"
            }
        }).showToast();
    }
}


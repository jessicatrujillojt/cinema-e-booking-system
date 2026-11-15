const paymentForm =
    document.getElementById(
        "paymentForm"
    );

const paymentMessage =
    document.getElementById(
        "paymentMessage"
    );


paymentForm.addEventListener(
    "submit",
    function (event) {

        event.preventDefault();

        paymentMessage.textContent =
            "Payment processing will be completed in a future sprint.";
    }
);
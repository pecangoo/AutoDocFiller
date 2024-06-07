function checkText() {
    console.log("85858");
    var textField = document.getElementById("name_template");
    var textValue = textField.value.trim();
    if (textValue === "") {
        alert("Введите имя.");
        console.log("85858")
    }
}
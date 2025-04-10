document.getElementById("mybtn").addEventListener("click", function(e) {
    e.preventDefault();

    // Get the input values
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    // Validate username and password. Remember to replace with an authentication process)
    if (username === "admin" && password === "password123") {
        //Open dashboard homepage when credentials are validated and login button is clicked
        document.getElementById("mybtn").addEventListener("click", function(){
          window.location.href = "http://127.0.0.1:5500/Web/user_manage.html";
        });
    } else {
        // Show error messages
        document.getElementById("error-message").textContent = "Invalid username or password";
        document.getElementById("error-message").style.display = "block";
    }
});


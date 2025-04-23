//const loginForm = document.querySelector('.form');
//
//loginForm.addEventListener('submit', event => {
//  event.preventDefault();
//
//  const formData = new FormData(loginForm);
//  const data = Object.fromEntries(formData);
//
//  fetch('https://softwareengineeringproject-production.up.railway.app/api/login', {
//    method: 'POST',
//    headers : {
//      'Content-Type':'application/json'
//    },
//    body: JSON.stringify(data)
//  });
//});



//const web_api = "https://softwareengineeringproject-production.up.railway.app/api/register";
//
//fetch(web_api)
//  .then(response => {
//    if (!response.ok) {
//      throw new Error('Network response was not ok');
//    }
//    return response.json();
//  })
//  .then(data => {
//    console.log(data);
//  })
//  .catch(error => {
//    console.error('Error:', error);
//  });
//
document.getElementById("mybtn").addEventListener("click", function(e) {
    e.preventDefault();

    // Get the input values
    const username = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    // Validate username and password. Remember to replace with an authentication process)
    if (username === "Cmorris@gmail.com" && password === "Password97") {
        //Open dashboard homepage when credentials are validated and login button is clicked
        document.getElementById("mybtn").addEventListener("click", function(){
          window.location.href = "http://127.0.0.1:5500/Web/dashboard.html";
        });
    } else {
        // Show error messages
        document.getElementById("error-message").textContent = "Invalid username or password";
        document.getElementById("error-message").style.display = "block";
    }
});

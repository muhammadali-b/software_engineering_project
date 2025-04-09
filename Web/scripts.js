document.getElementById("mybtn").addEventListener("click", function(e) {
    e.preventDefault();

    // Get the input values
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    // Validate username and password. Remember to replace with an authentication process)
    if (username === "admin" && password === "password123") {
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

document.getElementById("myinput").addEventListener("click", function(e) {
  alert("Button worked");
  // Declare variables
  //var input, filter, table, tr, td, i, txtValue;
  //input = document.getElementById("myInput");
  //filter = input.value.toUpperCase();
  //table = document.getElementById("myTable");
  //tr = table.getElementsByTagName("tr");
//
  //// Loop through all table rows, and hide those who don't match the search query
  //for (i = 0; i < tr.length; i++) {
  //  td = tr[i].getElementsByTagName("td")[0];
  //  if (td.innerHTML.toUpperCase().indexOf(filter)) {
  //    txtValue = td.textContent || td.innerText;
  //    if (txtValue.toUpperCase().indexOf(filter) > -1) {
  //      tr[i].style.display = "";
  //    } 
  //    else {
  //      tr[i].style.display = "none";
  //    }
  //  }
  //}
});
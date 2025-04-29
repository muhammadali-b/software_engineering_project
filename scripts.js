  const loginForm = document.querySelector('.form');

  loginForm.addEventListener('submit', async (event) => {
    event.preventDefault();

    const formData = new FormData(loginForm);
    const data = Object.fromEntries(formData);

    try {
      const response = await fetch('https://softwareengineeringproject-production.up.railway.app/api/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
      });

      const result = await response.json();

      if (response.ok) {
        console.log('Login successful:', result);

        window.location.href = '/Web/employee_user_manage.html';
      } else {
        alert(result.message || 'Invalid email or password.');
      }
    } catch (error) {
      console.error('Login failed:', error);
      alert('Something went wrong. Please try again.');
    }
  });

async function loadEmployees() {
  try {
    const res = await fetch("https://softwareengineeringproject-production.up.railway.app/api/approved-employees");
    const data = await res.json();

    const employees = data.data;

    if (!Array.isArray(employees)) {
      throw new Error("Expected an array but got: " + JSON.stringify(employees));
    }

    const tbody = document.querySelector("tbody");
    employees.forEach(employee => {
      const row = `
        <tr>
          <td>${employee.id}</td>
          <td>${employee.f_name}</td>
          <td>${employee.l_name}</td>
          <td>${employee.email}</td>
        </tr>
      `;
      tbody.insertAdjacentHTML("beforeend", row);
    });
  } catch (error) {
    console.error("Error loading employees:", error);
  }
}

loadEmployees();
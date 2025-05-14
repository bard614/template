document.addEventListener('DOMContentLoaded', () => {
    const userTableBody = document.querySelector('#userTable tbody');
    const addUserForm = document.getElementById('addUserForm');

    // Function to fetch and display users
    const fetchUsers = async () => {
        try {
            const response = await fetch('/api/users');
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const users = await response.json();
            userTableBody.innerHTML = ''; // Clear existing rows
            users.forEach(user => {
                const row = userTableBody.insertRow();
                row.innerHTML = `
                    <td>${user.id}</td>
                    <td>${user.username}</td>
                    <td>${user.role}</td>
                    <td>
                        <button class="delete-btn" data-id="${user.id}">刪除</button>
                    </td>
                `;
            });
        } catch (error) {
            console.error('Error fetching users:', error);
            // Redirect to login page if unauthorized (status 401 or 403)
            if (error.message.includes('status: 401') || error.message.includes('status: 403')) {
                window.location.href = '/login';
            }
        }
    };

    // Event listener for add user form submission
    addUserForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        const formData = new FormData(addUserForm);
        const userData = Object.fromEntries(formData.entries());

        try {
            const response = await fetch('/api/users', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(userData)
            });
            if (!response.ok) {
                 // Check if the response is HTML (e.g., login page redirect)
                const contentType = response.headers.get("content-type");
                if (contentType && contentType.indexOf("text/html") !== -1) {
                     window.location.href = '/login'; // Redirect to login page
                }
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            addUserForm.reset();
            fetchUsers(); // Refresh user list
        } catch (error) {
            console.error('Error adding user:', error);
        }
    });

    // Event listener for delete buttons (using event delegation)
    userTableBody.addEventListener('click', async (event) => {
        if (event.target.classList.contains('delete-btn')) {
            const userId = event.target.dataset.id;
            try {
                const response = await fetch(`/api/users/${userId}`, {
                    method: 'DELETE'
                });
                 if (!response.ok) {
                     // Check if the response is HTML (e.g., login page redirect)
                    const contentType = response.headers.get("content-type");
                    if (contentType && contentType.indexOf("text/html") !== -1) {
                         window.location.href = '/login'; // Redirect to login page
                    }
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                fetchUsers(); // Refresh user list
            } catch (error) {
                console.error('Error deleting user:', error);
            }
        }
    });

    // Initial fetch of users
    fetchUsers();
});
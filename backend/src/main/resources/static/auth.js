let isLogin = true;
let isLoggedIn = false;
let currentUsername = null;

function updateAuthDisplay() {
    const loginButton = document.getElementById("login-button");
    const usernameDisplay = document.getElementById("username-display");

    if (isLoggedIn) {
        loginButton.style.display = "none";
        usernameDisplay.style.display = "block";
        usernameDisplay.innerHTML = `Logged in as: ${currentUsername} <button onclick="logout()">Logout</button>`;
    } else {
        loginButton.style.display = "block";
        usernameDisplay.style.display = "none";
    }
}

function storeLogin(username, userId) {
    isLoggedIn = true;
    currentUsername = username;
    localStorage.setItem("isLoggedIn", "true");
    localStorage.setItem("username", username);
    localStorage.setItem("userId", userId);
    updateAuthDisplay();
    window.location.reload();
}

function logout() {
    isLoggedIn = false;
    currentUsername = null;
    localStorage.removeItem("isLoggedIn");
    localStorage.removeItem("username");
    localStorage.removeItem("userId");
    updateAuthDisplay();
    window.location.reload();
}

function toggleAuthModal() {
    const modal = document.getElementById("auth-modal");
    if (modal) {
        modal.style.display = modal.style.display === "block" ? "none" : "block";
    } else {
        console.error("Auth modal not found");
    }
}

function switchAuthMode() {
    isLogin = !isLogin;
    document.getElementById("modal-title").textContent = isLogin ? "Login" : "Sign Up";
    document.getElementById("auth-toggle-text").innerHTML = isLogin
        ? `Don't have an account? <a href="#" onclick="switchAuthMode()">Sign up</a>`
        : `Already have an account? <a href="#" onclick="switchAuthMode()">Login</a>`;

    const emailInput = document.getElementById("email");
    if (emailInput) {
        emailInput.style.display = isLogin ? "none" : "block";
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const storedLogin = localStorage.getItem("isLoggedIn");
    const storedUsername = localStorage.getItem("username");

    if (storedLogin === "true" && storedUsername) {
        isLoggedIn = true;
        currentUsername = storedUsername;
    }

    updateAuthDisplay();

    const authForm = document.getElementById("auth-form");
    if (authForm) {
        authForm.addEventListener("submit", function (e) {
            e.preventDefault();

            const username = document.getElementById("username").value;
            const password = document.getElementById("password").value;
            const email = document.getElementById("email").value;

            if (!username || !password || (!isLogin && !email)) {
                alert("Please fill all fields.");
                return;
            }

            if (isLogin) {
                login(username, password);
            } else {
                signup(username, password, email);
            }
        });
    } else {
        console.error("Auth form not found");
    }
});

function login(username, password) {
    fetch("/api/users/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ username, password }),
    })
        .then((response) => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then((data) => {
            if (data.message === "Login successful") {
                storeLogin(username, data.userId);
                toggleAuthModal();
            } else {
                alert(data.message);
            }
        })
        .catch((error) => {
            console.error("Error:", error);
            alert("Something went wrong!");
        });
}

function signup(username, password, email) {
    const userData = {
        username: username,
        password: password,
        email: email
    };

    fetch("/api/users/signup", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(userData)
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => {
                    throw new Error(err.message || "Signup failed.");
                });
            }
            return response.json();
        })
        .then(data => {
            storeLogin(username, data.id || 0);
            toggleAuthModal();
        })
        .catch(error => {
            console.error("Error during signup:", error);
            const errorMessageEl = document.getElementById("signup-error-message");
            if (errorMessageEl) {
                errorMessageEl.textContent = error.message;
                errorMessageEl.classList.remove("hidden");
            } else {
                alert(error.message);
            }
        });
}
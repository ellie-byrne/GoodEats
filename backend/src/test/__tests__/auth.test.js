const localStorageMock = (function() {
    let store = {};
    return {
        getItem: jest.fn(key => store[key] || null),
        setItem: jest.fn((key, value) => {
            store[key] = value.toString();
        }),
        removeItem: jest.fn(key => {
            delete store[key];
        }),
        clear: jest.fn(() => {
            store = {};
        })
    };
})();
Object.defineProperty(window, 'localStorage', { value: localStorageMock });

document.body.innerHTML = `
  <button id="login-button">Login</button>
  <div id="username-display" style="display: none;"></div>
  <div id="auth-modal" class="modal">
    <div class="modal-content">
      <h2 id="modal-title">Login</h2>
      <form id="auth-form">
        <input type="text" id="username" name="username" value="testuser" />
        <input type="password" id="password" name="password" value="password123" />
        <input type="email" id="email" name="email" style="display: none;" />
      </form>
      <p id="auth-toggle-text">Don't have an account? <a href="#" onclick="switchAuthMode()">Sign up</a></p>
    </div>
  </div>
`;

const updateAuthDisplay = () => {
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
};

const storeLogin = (username, userId) => {
    isLoggedIn = true;
    currentUsername = username;
    localStorage.setItem("isLoggedIn", "true");
    localStorage.setItem("username", username);
    localStorage.setItem("userId", userId);
    updateAuthDisplay();
};

const logout = () => {
    isLoggedIn = false;
    currentUsername = null;
    localStorage.removeItem("isLoggedIn");
    localStorage.removeItem("username");
    localStorage.removeItem("userId");
    updateAuthDisplay();
};

const toggleAuthModal = () => {
    const modal = document.getElementById("auth-modal");
    if (modal) {
        modal.style.display = modal.style.display === "block" ? "none" : "block";
    }
};

const switchAuthMode = () => {
    isLogin = !isLogin;
    document.getElementById("modal-title").textContent = isLogin ? "Login" : "Sign Up";
    document.getElementById("auth-toggle-text").innerHTML = isLogin
        ? `Don't have an account? <a href="#" onclick="switchAuthMode()">Sign up</a>`
        : `Already have an account? <a href="#" onclick="switchAuthMode()">Login</a>`;

    const emailInput = document.getElementById("email");
    if (emailInput) {
        emailInput.style.display = isLogin ? "none" : "block";
    }
};

let isLogin = true;
let isLoggedIn = false;
let currentUsername = null;

describe('Auth Module', () => {
    beforeEach(() => {
        // Reset state before each test
        isLogin = true;
        isLoggedIn = false;
        currentUsername = null;
        localStorage.clear();

        document.getElementById("login-button").style.display = "block";
        document.getElementById("username-display").style.display = "none";
        document.getElementById("auth-modal").style.display = "none";
    });

    test('updateAuthDisplay shows username when logged in', () => {
        isLoggedIn = true;
        currentUsername = "testuser";

        updateAuthDisplay();

        expect(document.getElementById("login-button").style.display).toBe("none");
        expect(document.getElementById("username-display").style.display).toBe("block");
        expect(document.getElementById("username-display").innerHTML).toContain("testuser");
    });

    test('storeLogin sets localStorage and updates display', () => {
        storeLogin("testuser", "123");

        expect(localStorage.setItem).toHaveBeenCalledWith("isLoggedIn", "true");
        expect(localStorage.setItem).toHaveBeenCalledWith("username", "testuser");
        expect(localStorage.setItem).toHaveBeenCalledWith("userId", "123");
        expect(isLoggedIn).toBe(true);
        expect(currentUsername).toBe("testuser");
    });

    test('logout clears localStorage and updates display', () => {
        isLoggedIn = true;
        currentUsername = "testuser";

        logout();

        expect(localStorage.removeItem).toHaveBeenCalledWith("isLoggedIn");
        expect(localStorage.removeItem).toHaveBeenCalledWith("username");
        expect(localStorage.removeItem).toHaveBeenCalledWith("userId");
        expect(isLoggedIn).toBe(false);
        expect(currentUsername).toBeNull();
    });

    test('toggleAuthModal toggles modal visibility', () => {
        const modal = document.getElementById("auth-modal");
        modal.style.display = "none";

        toggleAuthModal();
        expect(modal.style.display).toBe("block");

        toggleAuthModal();
        expect(modal.style.display).toBe("none");
    });

    test('switchAuthMode toggles between login and signup', () => {
        isLogin = true;

        switchAuthMode();

        expect(isLogin).toBe(false);
        expect(document.getElementById("modal-title").textContent).toBe("Sign Up");
        expect(document.getElementById("email").style.display).toBe("block");

        switchAuthMode();

        expect(isLogin).toBe(true);
        expect(document.getElementById("modal-title").textContent).toBe("Login");
        expect(document.getElementById("email").style.display).toBe("none");
    });
});
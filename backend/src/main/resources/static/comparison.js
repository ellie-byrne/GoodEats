document.addEventListener("DOMContentLoaded", () => {
    const darkModeToggle = document.getElementById("dark-mode-toggle");

    // Setup dark mode
    setupDarkMode(darkModeToggle);

    // Function to set up dark mode
    function setupDarkMode(toggle) {
        const enabled = localStorage.getItem("darkModeEnabled") === "true";
        if (enabled) {
            document.body.classList.add("dark-mode");
            toggle.checked = true;
        }

        toggle.addEventListener("change", () => {
            document.body.classList.toggle("dark-mode", toggle.checked);
            localStorage.setItem("darkModeEnabled", toggle.checked ? "true" : "false");
        });
    }
});
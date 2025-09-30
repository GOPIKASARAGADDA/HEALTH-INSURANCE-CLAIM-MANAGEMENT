// Simulate login state
let isLoggedIn = false;
let currentUser = {}; // e.g., {name: "John", role: "Policyholder"}

// Load Navbar/Footer/Modules
$(function() {

	// Load saved user from localStorage
	  const savedUser = JSON.parse(localStorage.getItem("user"));
	  if (savedUser) {
	    isLoggedIn = true;
	    currentUser = savedUser;
	  }

  $("#navbar").load("/components/navbar.html", function(response, status, xhr) {
    if (status === "error") console.error("Navbar load failed:", xhr.status, xhr.statusText);
    renderNavbar(); // call render after navbar is loaded
  });

  $("#modules").load("/components/modules.html");
  $("#footer").load("/components/footer.html");
});

// ---------------------------
// Dark Mode
// ---------------------------
function toggleDarkMode() {
  document.body.classList.toggle("dark-mode");
}

// ---------------------------
// Role-based Navbar
// ---------------------------
function renderNavbar() {
  if (!$("#navbar").length) return; // ensure navbar exists

  if (isLoggedIn) {
    $(".auth-only").addClass("d-none"); // hide login/register
    $(".user-only").removeClass("d-none"); // show profile
    $("#userName").text(currentUser.name + " (" + currentUser.role + ")");

    // Hide all role-specific links first
    $(".role-policyholder, .role-agent, .role-claimadjuster, .role-admin").addClass("d-none");

    // Show links based on role
    switch ((currentUser.role || "").toLowerCase()) {
      case "policyholder":
        $(".role-policyholder").removeClass("d-none");
        break;
      case "agent":
        $(".role-agent").removeClass("d-none");
        break;
      case "claim_adjuster":
        $(".role-claimadjuster").removeClass("d-none");
        break;
      case "admin":
        $(".role-admin").removeClass("d-none");
        break;
    }
  } else {
    $(".auth-only").removeClass("d-none");
    $(".user-only").addClass("d-none");
    $(".role-policyholder, .role-agent, .role-claimadjuster, .role-admin").addClass("d-none");
  }
}

// ---------------------------
// Login/Logout Simulation
// ---------------------------
function login(name, role) {
  isLoggedIn = true;
  currentUser = { name, role };

  // Since navbar is dynamically loaded, call render after login
  renderNavbar();
}

function logout() {
  isLoggedIn = false;
  currentUser = {};
  renderNavbar();
}

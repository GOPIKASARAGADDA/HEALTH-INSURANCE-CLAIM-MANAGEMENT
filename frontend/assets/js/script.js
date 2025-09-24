     $(function(){
      $("#navbar").load("/frontend/components/navbar.html",function () { renderNavbar(); });
      $("#modules").load("/frontend/components/modules.html");
      $("#footer").load("/frontend/components/footer.html");
    });

    function toggleDarkMode() {
      document.body.classList.toggle("dark-mode");
    }

    // Simulating login state (later from backend/authentication)
let isLoggedIn = false;
let currentUser = { name: "John", role: "Agent" };

if (currentUser.role === "Admin" || currentUser.role === "Agent" || currentUser.role === "Policyholder" || currentUser.role === "Claim Adjuster") {
  isLoggedIn = true;
}

// Render navbar based on login state (yet pending to be CHANGE frontend necessarily in navbar.html)
// function renderNavbar() {
//   if (isLoggedIn) {
//     $(".auth-only").addClass("d-none"); // hide login/register
//     $(".user-only").removeClass("d-none"); // show profile
//     $("#userName").text(currentUser.name + " (" + currentUser.role + ")");
//   } else {
//     $(".auth-only").removeClass("d-none");
//     $(".user-only").addClass("d-none");
//   }
// }
 
// function login(name, role) {
//   isLoggedIn = true;
//   currentUser = { name, role };
//   renderNavbar();
// }
 
// function logout() {
//   isLoggedIn = false;
//   currentUser = {};
//   renderNavbar();
// }
 
// function switchRole(role) {
//   currentUser.role = role;
//   renderNavbar();
// }
 
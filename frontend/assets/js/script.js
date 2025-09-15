     $(function(){
      $("#navbar").load("/frontend/components/navbar.html");
      $("#modules").load("/frontend/components/modules.html");
      $("#footer").load("/frontend/components/footer.html");
      $("#claimFormModalContainer").load("/frontend/pages/ClaimForm.html");
    });

    function toggleDarkMode() {
      document.body.classList.toggle("dark-mode");
    }

    // Simulating login state (later from backend/authentication)
let isLoggedIn = true;
let currentUser = { name: "John", role: "Policyholder" };
 
function renderNavbar() {
  if (isLoggedIn) {
    $(".auth-only").addClass("d-none"); // hide login/register
    $(".user-only").removeClass("d-none"); // show profile
    $("#userName").text(currentUser.name + " (" + currentUser.role + ")");
  } else {
    $(".auth-only").removeClass("d-none");
    $(".user-only").addClass("d-none");
  }
}
 
function login(name, role) {
  isLoggedIn = true;
  currentUser = { name, role };
  renderNavbar();
}
 
function logout() {
  isLoggedIn = false;
  currentUser = {};
  renderNavbar();
}
 
function switchRole(role) {
  currentUser.role = role;
  renderNavbar();
}
 
// On page load
$(document).ready(() => {
  renderNavbar();
});
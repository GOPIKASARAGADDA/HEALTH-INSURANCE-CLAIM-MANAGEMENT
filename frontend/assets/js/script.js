     $(function(){
      $("#navbar").load("/frontend/components/navbar.html");
      $("#modules").load("/frontend/components/modules.html");
      $("#footer").load("/frontend/components/footer.html");
    });

    function toggleDarkMode() {
      document.body.classList.toggle("dark-mode");
    }
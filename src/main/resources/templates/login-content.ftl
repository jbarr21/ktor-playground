<center>
  <p>
    <h4>Welcome, Athlete!</h4>
    <p>Click below to login to visualize your stats.</p>
    <button id="login-btn" class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--primary">
      Login with Strava
    </button>
  </p>
</center>

<script>
  $("#login-btn").on('click', (e) => window.location = '/strava/login');
</script>
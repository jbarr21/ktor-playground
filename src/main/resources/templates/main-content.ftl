<!--
<center>
  <p>
    <div>
      <img src="${props.profileImage}" style="vertical-align:middle"/>
      <span class="display-2" style="vertical-align:middle" >Welcome, ${props.name}!</span>
    </div>
    <p># of activities: ${props.activityCount}</p>
  </p>
</center>
-->

<center>
  <#include "activity-table.ftl">
  <br>
  <button id="logout-btn" class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--primary">
    Logout
  </button>
<center>

<script>
  $("#logout-btn").on('click', (e) => window.location = '/strava/logout');
</script>

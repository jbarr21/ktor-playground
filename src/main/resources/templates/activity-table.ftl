<div class="mdl-grid">
  <table class="mdl-data-table mdl-js-data-table mdl-shadow--2dp mdl-cell mdl-cell--12-col">
    <thead>
    <tr>
      <th class="mdl-data-table__cell--non-numeric">Name</th>
      <th>Distance</th>
      <th>Elevation</th>
      <th>Speed</th>
      <th>Power</th>
    </tr>
    </thead>
    <tbody>
      <#list props.activities as activity>
        <tr>
          <td class="mdl-data-table__cell--non-numeric">${activity.name}</td>
          <td>${activity.distance}</td>
          <td>${activity.elevation}</td>
          <td>${activity.speed}</td>
          <td>${activity.power}</td>
        </tr>
      </#list>
    </tbody>
  </table>
</div>
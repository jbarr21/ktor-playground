<#include "base.ftl">

<#macro page_header>
  <a class="mdl-navigation__link" href="/strava/logout">Login</a>
</#macro>

<#macro page_content>
  <#include "login-content.ftl">
</#macro>

<@display_page/>
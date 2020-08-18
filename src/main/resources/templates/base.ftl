<#macro page_head>
  <#include "head.ftl">
</#macro>

<#macro page_header>
  <a class="mdl-navigation__link" href="">Link</a>
</#macro>

<#macro page_content>
  <h1>Basic Page</h1>
  <p>This is the body of the page!</p>
</#macro>

<#macro display_page>
  <!doctype html>
  <html>
    <@page_head/>

    <body class="mdl-base">
      <div class="mdl-layout mdl-js-layout mdl-layout--fixed-header">

        <!-- header-drawer -->
        <header class="mdl-layout__header">
          <div class="mdl-layout__header-row">
            <span class="mdl-layout-title">StravaStats</span>
            <div class="mdl-layout-spacer"></div>
            <nav class="mdl-navigation mdl-layout--large-screen-only">
              <@page_header/>
            </nav>
          </div>
        </header>

        <div class="mdl-layout__drawer">
          <span class="mdl-layout-title">StravaStats</span>
          <nav class="mdl-navigation">
            <@page_header/>
          </nav>
        </div>

        <main class="mdl-layout__content">
          <div class="page-content">
            <@page_content/>
          </div>
        </main>
      </div>
    </body>
  </html>
</#macro>

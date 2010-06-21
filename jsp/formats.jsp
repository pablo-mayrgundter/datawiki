<html>
  <head>
    <script src="/Util.js" type="text/javascript"></script>
    <script src="/Tabs.js" type="text/javascript"></script>
    <link rel="stylesheet" href="/formats.css" type="text/css"/>
    <script>
      function showCreate(button) {
        button.style.display = 'none';
        var mf = get('metaform');
        mf.style.display = 'block';
      }
      function setTarget(elt) {
        var link = get('link');
        link.href = elt.value;
      }
    </script>
  </head>
  <body>
    <jsp:include page="nav.jsp"/>
    <div class="mainPanel">
      <table>
        <tr>
          <td>
            <jsp:include page="featured.jsp"/>
          </td>
          <td class="createFormat">
            <h3>Create a new dataset format from scratch...</h3>
            <div id="metaform" style="display: none">
              Format Name: <input id="target" value="" onchange="setTarget(this)"/>
              <a id="link" href="#">Go</a>
            </div>
            <button class="button plus text" onclick="showCreate(this)"><div></div>New Format</button>

            <h3>or use an existing XML template</h3>
            <form action="/wiki/formats" method="POST" enctype="multipart/form-data">
              <textarea name="xml" cols="80" rows="20"></textarea><br/>
              <input type="submit"/><input type="reset"/>
            </form>
          </td>
        </tr>
      </table>
    </div>
  </body>
</html>

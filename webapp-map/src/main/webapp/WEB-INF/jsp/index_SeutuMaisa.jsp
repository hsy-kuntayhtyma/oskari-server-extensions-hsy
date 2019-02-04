<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html>
<head>
  <title>SeutuMaisa</title>

  <script type="text/javascript" src="/Oskari/libraries/jquery/jquery-1.10.2.js">
  </script>
  <script>
  $(function() {
  jQuery('input#submit[type=submit]').on('click', function(e) {
		e.preventDefault();
		jQuery.ajax({url: '/j_security_check',
			type: 'POST',
			data: jQuery('form').serialize(),
			success: function(data, status, jqXHR) {
				var d = jQuery(data);
				var login = d.find('#login');
				if(login.find('p.error').length > 0) {
					jQuery('#login p.error').remove();
					jQuery('#login').prepend('<p class="error">Virheellinen käyttäjätunnus tai salasana!!</p>');
				} else {
					location.reload();
				}
			},
			error: function(data, status) {
				
			}
		});
	});

	jQuery('#login a').on('click', function(e) {
		e.preventDefault();
		jQuery.ajax({url: '/logout',
			type: 'POST',
			data: jQuery('form').serialize(),
			success: function(data, status, jqXHR) {
				location.reload();
			},
			error: function(data, status) {
				
			}
		});
	});
  });
	  
  </script>
  <!-- ############# css ################# -->
<link
        rel="stylesheet"
        type="text/css"
        href="/Oskari/resources/css/forms.css"/>
<link
        rel="stylesheet"
        type="text/css"
        href="/Oskari/resources/css/portal.css"/>
<link
        rel="stylesheet"
        type="text/css"
        href="/Oskari${path}/icons.css"/>
<link
        rel="stylesheet"
        type="text/css"
        href="/Oskari${path}/css/SeutuMaisa_overwritten.css"/>

  <style type="text/css">
    @media screen {
      body {
        margin: 0;
        padding: 0;
      }

      #mapdiv {
        width: 100%;
      }

      #maptools {
        background-color: #333438;
        height: 100%;
        position: absolute;
        top: 0;
        width: 153px;
        z-index: 2;
      }

      #contentMap {
        height: 100%;
        margin-left: 170px;
      }

      #login {
        margin-left: 5px;
      }

      #login input[type="text"], #login input[type="password"] {
        width: 90%;
        margin-bottom: 5px;
        background-image: url("/Oskari${path}/images/forms/input_shadow.png");
        background-repeat: no-repeat;
        padding-left: 5px;
        padding-right: 5px;
        border: 1px solid #B7B7B7;
        border-radius: 4px 4px 4px 4px;
        box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1) inset;
        color: #878787;
        font: 13px/100% Arial,sans-serif;
      }
      #login input[type="submit"] {
        width: 90%;
        margin-bottom: 5px;
        padding-left: 5px;
        padding-right: 5px;
        border: 1px solid #B7B7B7;
        border-radius: 4px 4px 4px 4px;
        box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1) inset;
        color: #878787;
        font: 13px/100% Arial,sans-serif;
      }
      #login p.error {
        font-weight: bold;
        color : red;
        margin-bottom: 10px;
      }

      #login a {
        color: #FFF;
        padding: 5px;
      }
    }
  </style>
  <!-- ############# /css ################# -->
</head>
<body>

<nav id="maptools">
  <div id="logobar"></div>
  <div id="loginbar">
  </div>
  <div id="menubar">
  </div>
  <div id="divider">
  </div>
  <div id="toolbar">
  </div>
  <div id="login">
    <c:choose>
      <c:when test="${!empty loginState}">
        <p class="error"><spring:message code="invalid_password_or_username" text="Invalid password or username!" /></p>
      </c:when>
    </c:choose>
    <c:choose>
      <%-- If logout url is present - so logout link --%>
      <c:when test="${!empty _logout_uri}">
        <a href="${pageContext.request.contextPath}${_logout_uri}"><spring:message code="logout" text="Logout" /></a>
      </c:when>
      <%-- Otherwise show appropriate logins --%>
      <c:otherwise>
        <c:if test="${!empty _login_uri_saml}">
          <a href="${pageContext.request.contextPath}${_login_uri_saml}"><spring:message code="login.sso" text="SSO login" /></a><hr />
        </c:if>
        <c:if test="${!empty _login_uri && !empty _login_field_user}">
          <p style="color: #2C2A29 ; padding-bottom: 5px;">Ylläpidon kirjautuminen</p>
          <form action='${pageContext.request.contextPath}${_login_uri}' method="post" accept-charset="UTF-8">
            <input size="16" id="username" name="${_login_field_user}" type="text" placeholder="<spring:message code="username" text="Username" />" autofocus
                   required>
            <input size="16" id="password" name="${_login_field_pass}" type="password" placeholder="<spring:message code="password" text="Password" />" required>
            <input type="submit" id="submit" value="<spring:message code="login" text="Log in" />">
          </form>
        </c:if>
      </c:otherwise>
    </c:choose>
  </div>
  <div id="logosDiv">
  <div id="logos">
  <a id="helsinki_vaakuna" href="https://www.hel.fi/helsinki/"><img class="logo" src="/Oskari/resources/images/helsinki_logo.png"></a>
  <a id="espoo_vaakuna" href="https://www.espoo.fi/"><img class="logo" src="/Oskari/resources/images/espoo_logo.png"></a>
  <a id="vantaa_vaakuna" href="https://www.vantaa.fi/"><img class="logo" src="/Oskari/resources/images/vantaa_logo.png"></a>
  </div>
  </div>
</nav>
<div id="contentMap" class="oskariui container-fluid">
  <div id="menutoolbar" class="container-fluid"></div>
  <div class="row-fluid oskariui-mode-content" style="height: 100%; background-color:white;">
    <div class="oskariui-left"></div>
    <div class="span12 oskariui-center" style="height: 100%; margin: 0;">
      <div id="mapdiv"></div>
    </div>
    <div class="oskari-closed oskariui-right">
      <div id="mapdivB"></div>
    </div>
  </div>
</div>


<!-- ############# Javascript ################# -->

<!--  OSKARI -->

<script type="text/javascript">
    var ajaxUrl = '${ajaxUrl}';
    var controlParams = ${controlParams};
</script>

<script type="text/javascript"
        src="/Oskari/bundles/bundle.js">
</script>

<c:if test="${preloaded}">
  <!-- Pre-compiled application JS, empty unless created by build job -->
  <script type="text/javascript"
          src="/Oskari${path}/oskari.min.js">
  </script>
  <!-- Minified CSS for preload -->
  <link
          rel="stylesheet"
          type="text/css"
          href="/Oskari${path}/oskari.min.css"
          />
  <%--language files --%>
  <script type="text/javascript"
          src="/Oskari${path}/oskari_lang_${language}.js">
  </script>
</c:if>

<script type="text/javascript"
        src="/Oskari${path}/index.js">
</script>


<!-- ############# /Javascript ################# -->
</body>
</html>
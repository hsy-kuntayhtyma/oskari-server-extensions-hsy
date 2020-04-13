<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html>
<head>
    <title>${viewName}</title>

    <!-- ############# css ################# -->
    <link
            rel="stylesheet"
            type="text/css"
            href="/Oskari${path}/icons.css"/>
    <link
            rel="stylesheet"
            type="text/css"
            href="/Oskari${path}/oskari.min.css"/>
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
                background-color: #FFFFFF;
                height: 100%;
                position: absolute;
                top: 0;
                width: 153px;
                z-index: 2;
            }

   .oskari-tile {
        background: #9e9e9e !important;
		height: auto !important;
		min-height: 31px !important;
        line-height: 15px !important;
    }
    .oskari-tile-closed {
        background-color: white !important;
        border-bottom: 1px solid #c8c9ca !important;
        border-top: 1px solid white !important;
    }

    .oskari-tile-attached {
        background-color: #2d2d2d !important;
        border-bottom: 1px solid white !important;
        border-top: 1px solid #c8c9ca !important;
    }

    .oskari-tile-attached .oskari-tile-title {
		color: white !important;
	}

	.oskari-tile-closed .oskari-tile-title {
		color: black !important;
	}

    .oskari-flyout-title {
        color: white !important;
    }

    .oskari-flyouttoolbar {
    	background-color: #c8c9ca !important;
		background-position: -170px -23px !important;
        background-repeat: no-repeat;
    }

    .oskari-flyoutheading {
		background-color: #9e9e9e !important;
		border-top: 0px !important;
		border-bottom: 0px !important;
		height: 14px !important;
		width: 100% !important;
	}

    #toolbar {
        margin: 30px 5px 30px 5px !important;
        width: auto !important;
        border: 1px black solid;
        border-radius: 5px;
        box-shadow: 2px 2px 3px 0px rgba(0,0,0,0.75);
    }

    #toolbar div.toolrow {
        border: 0px;
    }

    .divmanazerpopup h3.popupHeader {
        background-color: #9e9e9e !important;
        color: white;
    }

    #logobar {
        background-image: url('/Oskari${path}/oskari_rgb_72.png') !important;
    }

    .oskari-tile.statsgrid .statsgrid-functionality .text {
        color: white;
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
                background-image: url("/Oskari/${version}/resources/images/forms/input_shadow.png");
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
    <div id="logobar">
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
                <form action="${pageContext.request.contextPath}${_logout_uri}" method="POST" id="logoutform">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <a href="${pageContext.request.contextPath}${_logout_uri}" style="color: #000000;" onClick="jQuery('#logoutform').submit();return false;"><spring:message code="logout" text="Logout" /></a>
                </form>
                <%-- oskari-profile-link id is used by the personaldata bundle - do not modify --%>
                <a href="${pageContext.request.contextPath}${_registration_uri}" style="color: #000000;" id="oskari-profile-link"><spring:message code="account" text="Account" /></a>
            </c:when>
            <%-- Otherwise show appropriate logins --%>
            <c:otherwise>
                <c:if test="${!empty _login_uri && !empty _login_field_user}">
                    <form action='${pageContext.request.contextPath}${_login_uri}' method="post" accept-charset="UTF-8">
                        <input size="16" id="username" name="${_login_field_user}" type="text" placeholder="<spring:message code="username" text="Username" />" autofocus
                               required>
                        <input size="16" id="password" name="${_login_field_pass}" type="password" placeholder="<spring:message code="password" text="Password" />" required>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <input type="submit" id="submit" value="<spring:message code="login" text="Log in" />">
                    </form>
                </c:if>
            </c:otherwise>
        </c:choose>
    </div>
            <div id="demolink">
                <a href="#" style="margin: 10px; color: #3c3c3c;"
                onClick="changeAppsetup()">EPSG:3857</a>
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

<script>
function changeAppsetup() {
    var appsetup = Oskari.app.getSystemDefaultViews().filter(function (appsetup) {
        return  appsetup.name === 'World';
    });

    window.location=window.location.pathname + '?uuid=' + appsetup[0].uuid;
    return false;
}
</script>
<!--  OSKARI -->

<script type="text/javascript">
    var ajaxUrl = '${ajaxUrl}';
    var controlParams = ${controlParams};
</script>
<%-- Pre-compiled application JS, empty unless created by build job --%>
<script type="text/javascript"
        src="/Oskari${path}/oskari.min.js">
</script>
<%--language files --%>
<script type="text/javascript"
        src="/Oskari${path}/oskari_lang_${language}.js">
</script>

<script type="text/javascript"
        src="/Oskari${path}/index.js">
</script>


<!-- ############# /Javascript ################# -->
</body>
</html>

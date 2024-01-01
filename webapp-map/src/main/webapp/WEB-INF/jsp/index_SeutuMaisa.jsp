<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html>
<head>
    <title>SeutuMassa</title>
    <!-- ############# css ################# -->

    <link
            rel="stylesheet"
            type="text/css"
            href="/Oskari${path}/icons.css"/>


    <style type="text/css">
        @media screen {
            body {
                margin: 0;
                padding: 0;
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
                font: 13px/100% Arial, sans-serif;
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
                font: 13px/100% Arial, sans-serif;
            }

            #login p.error {
                font-weight: bold;
                color: red;
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

<script type="text/javascript">
    var resetOskariState = function(){
        var rb = Oskari.requestBuilder('StateHandler.SetStateRequest');
        if (rb) {
            Oskari.getSandbox().request('MainMapModule', rb());
        }
        jQuery('div.oskari-flyout.ui-draggable.oskari-attached div.oskari-flyouttoolbar').find('.icon-close').trigger('click');
    };
</script>

<nav id="maptools">
    <div id="toolsDiv">
        <div id="logobar" onclick="resetOskariState();return false;"></div>
        <div id="loginbar"></div>
        <div id="menubar"></div>
        <div id="divider"></div>
        <div id="toolbar"></div>
        <div id="login">
            <c:choose>
                <c:when test="${!empty loginState}">
                    <p class="error"><spring:message code="invalid_password_or_username"
                                                     text="Invalid password or username!"/></p>
                </c:when>
            </c:choose>
            <c:choose>
                <%-- If logout url is present - so logout link --%>
                <c:when test="${!empty _logout_uri}">
                    <form action="${pageContext.request.contextPath}${_logout_uri}" method="POST" id="logoutform">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <a href="${pageContext.request.contextPath}${_logout_uri}"
                           onClick="jQuery('#logoutform').submit();return false;"><spring:message code="logout"
                                                                                                  text="Logout"/></a>
                    </form>
                </c:when>
                <%-- Otherwise show appropriate logins --%>
                <c:when test="${!empty _logout_uri}">
                    <a href="${pageContext.request.contextPath}${_logout_uri}"><spring:message code="logout"
                                                                                               text="Logout"/></a>
                </c:when>
                <%-- Otherwise show appropriate logins --%>
                <c:otherwise>
                    <c:if test="${!empty _login_uri_saml}">
                        <a href="${pageContext.request.contextPath}${_login_uri_saml}"><spring:message code="login.sso"
                                                                                                       text="SSO login"/></a>
                        <hr/>
                    </c:if>
                    <c:if test="${!empty _login_uri && !empty _login_field_user}">
                        <p class="login_header">Ylläpidon kirjautuminen</p>
                        <form action='${pageContext.request.contextPath}${_login_uri}' method="post" accept-charset="UTF-8">
                            <input size="16" id="username" name="${_login_field_user}" type="text"
                                   placeholder="<spring:message code="username" text="Username" />" autofocus
                                   required>
                            <input size="16" id="password" name="${_login_field_pass}" type="password"
                                   placeholder="<spring:message code="password" text="Password" />" required>
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <input type="submit" id="submit" value="<spring:message code="login" text="Log in" />">
                        </form>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <div id="logosDiv">
        <div id="logos">
            <table>
                <tr>
                    <td style="width:33%;vertical-align:middle;">
                        <div class="vaakuna_logo helsinki" onclick="window.open('https://www.hel.fi');" style="cursor:pointer;margin:auto;"></div>
                    </td>
                    <td style="width:33%;vertical-align:middle;">
                        <div class="vaakuna_logo espoo" onclick="window.open('https://www.espoo.fi');" style="cursor:pointer;margin:auto;"></div>
                    </td>
                    <td style="width:33%;vertical-align:middle;">
                        <div class="vaakuna_logo vantaa" onclick="window.open('https://www.vantaa.fi');" style="cursor:pointer;margin:auto;"></div>
                    </td>
                </tr>
            </table>
        </div>
    </div>
</nav>

<div id="contentMap">
</div>


<!-- ############# Javascript ################# -->

<!--  OSKARI -->

<script type="text/javascript">
    var ajaxUrl = '${ajaxUrl}';
    var controlParams = ${controlParams};
</script>


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


<script type="text/javascript"
        src="/Oskari${path}/index.js">
</script>

<link
        rel="stylesheet"
        type="text/css"
        href="/Oskari${path}/css/SeutuMaisa_overwritten.css"/>

<!-- ############# /Javascript ################# -->
<jsp:useBean id="props" class="fi.nls.oskari.util.PropertyUtil"/>
<c:set var="ajaxLogin" scope="page" value="${props.getOptional('hsy.use.spring.login.fix')}"/>
<c:if test="${!empty ajaxLogin}">
    <script>
        jQuery(function () {
            jQuery('input#submit[type=submit]').on('click', function (e) {
                var oldLocation = location.href;
                setTimeout(function () {
                    window.location.href = oldLocation;
                }, 400);

            });

            jQuery('#login a').on('click', function (e) {
                var oldLocation = location.href;
                setTimeout(function () {
                    window.location.href = oldLocation;
                }, 400);
            });
        });

    </script>
</c:if>

<!-- ############# RIBBON ############# -->
<c:set var="ribbon" scope="page" value="${props.getOptional('page.ribbon.seutumaisa')}" />
<c:if test="${!empty ribbon}">
    <div id="ribbon" class="seutumaisa">${ribbon}</div>
</c:if>

</body>
</html>

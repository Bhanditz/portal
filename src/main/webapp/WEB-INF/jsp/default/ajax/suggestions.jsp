<%@ page session="false" language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>{"suggestions":[<c:forEach items="${model.results}" var="entry" varStatus="status">"${entry}"<c:if test="${!status.last}">,</c:if></c:forEach>]}

<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>


<openmrs:require privilege="Run Reports" otherwise="/login.htm" redirect="/module/amrsreports/parametizedReport.form"/>

<%@ include file="template/localHeader.jsp" %>
<h2>Pharmacy Reports</h2>

<style>
    fieldset.visualPadding {
        padding: 1em;
    }

    .right {
        text-align: right;
    }

    input.hasDatepicker {
        width: 14em;
    }
</style>

<script type="text/javascript">

    var reportDate;
    var scheduleDate;
    var evaluationEndDate;

    $j(document).ready(function () {

        reportDate = new DatePicker("<openmrs:datePattern/>", "evaluationDate", {});
        reportDate.setDate(new Date());


    });

</script>


<b class="boxHeader">Reports</b>

<div class="box" style=" width:99%; height:auto;  overflow-x: auto;">
    <form method="POST" id="rpt">
        <fieldset class="visualPadding">
            <table cellpadding="2" cellspacing="0">
                <tr>
                    <td>The patient who made any visit between this date up to today is considered as Active Patient</td>
                    <td>
                        <input type="text" id="evaluationDate" name="evaluationDate"/>
                    </td>
                </tr>
            </table>
            <legend>Monthly Report</legend>
            <table cellspacing="0" cellpadding="2">
                <tr>
                    <td colspan="2">
                        <label><b>Click button to generate report</b></label>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <input type="submit"  name="monthlyReport" id="monthlyReport" value="  Generate  " />
                    </td>
                </tr>

            </table>
        </fieldset>

    </form>

</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>
